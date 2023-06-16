const Hapi = require("@hapi/hapi");
const { Storage } = require("@google-cloud/storage");
const axios = require("axios");
const sharp = require("sharp");
const { nanoid } = require("nanoid");
const admin = require("firebase-admin");
const { classification } = require("./classes");

const projectId = "capstone-sortify";
const servingUrl = "https://sortify-model-6x3vm2nioq-et.a.run.app";
const bucketName = "sortify-app";
const audioOrganik = "audio/organik-voice.mp3";
const audioAnorganik = "audio/anorganik-voice.mp3";

// GCS config
const gcs = new Storage({
  projectId: projectId,
  keyFilename: "servicekey.json",
});

// firestore config
const path = require("path");
const serviceAccount = require(path.join(
  __dirname,
  "servicekey-firestore.json"
));

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

// handler
const uploadImageHandler = async (request, h) => {
  try {
    const id = nanoid(8);
    const imageFile = request.payload.file;
    const filename = `${Date.now()}_${id}.jpg`;

    await gcs.bucket(bucketName).upload(imageFile.path, {
      destination: filename,
    });
    await compressImageHandler(bucketName, filename);
    const imageUrl = `https://storage.googleapis.com/${bucketName}/${filename}`;

    return imageUrl;
  } catch (error) {
    console.error("Error uploading image:", error);
    return h.response({ error: "Failed to upload image" }).code(500);
  }
};

const compressImageHandler = async (bucketName, imageName) => {
  try {
    const bucket = gcs.bucket(bucketName);
    const file = bucket.file(imageName);
    const [fileBuffer] = await file.download();
    const image = sharp(fileBuffer).jpeg({ quality: 70 });

    const compressedImageBuffer = await image.toBuffer();
    await file.save(compressedImageBuffer, {
      metadata: {
        contentType: "image/jpeg",
      },
    });

    console.log("Image compressed successfully.");
  } catch (error) {
    console.error("Error compressing image:", error);
  }
};
const getHistoryData = async () => {
  try {
    const snapshot = await db.collection("data").orderBy("timestamp", "desc").get();
    const data = [];
    snapshot.forEach((doc) => {
      data.push({ id: doc.id, ...doc.data() });
    });

    return { data };
  } catch (error) {
    console.error("Error getting data:", error);
    return { error: "Failed to get data" };
  }
};

const predictImgHandler = async (request, h) => {
  try {
    const imageUrl = await uploadImageHandler(request, h);
    let descriptions;
    let classResult;
    let audio;

    const payload = { url: imageUrl };
    const respon = await axios.post(`${servingUrl}/predict`, payload);
    const predictions = respon.data.predictions[0];
    const classIndex = predictions.indexOf(Math.max(...predictions));

    const isOrganic = [classification.CARDBOARD, classification.PAPER].includes(
      classIndex
    );

    const isAnorganic = [
      classification.GLASS,
      classification.METAL,
      classification.PLASTIC,
    ].includes(classIndex);

    if (isOrganic) {
      audio = audioOrganik;
      classResult = "Organik";
      descriptions =
        "Sampah organik adalah jenis sampah yang berasal dari bahan-bahan yang dapat terurai secara alami, seperti sisa makanan, daun, rumput, kulit buah, dan lain sebagainya. Daur ulang sampah organik melibatkan proses pengolahan kembali sisa-sisa organik tersebut menjadi bahan yang berguna. Metode daur ulang sampah organik antara lain meliputi pengomposan dan pembuatan biogas. Daur ulang sampah organik membantu mengurangi volume sampah, mengurangi emisi gas rumah kaca, serta menghasilkan bahan yang dapat digunakan kembali, seperti kompos yang berguna sebagai pupuk organik atau biogas yang dapat digunakan sebagai sumber energi alternatif.";
    } else if (isAnorganic) {
      audio = audioAnorganik;
      classResult = "Anorganik";
      descriptions =
        "Sampah anorganik adalah sampah yang tidak dapat terurai secara alami oleh mikroorganisme. Ini termasuk bahan seperti plastik, kaca, logam, dan kertas yang terkontaminasi dengan bahan kimia. Daur ulang sampah anorganik melibatkan proses pemrosesan kembali bahan-bahan ini untuk menghasilkan produk baru atau untuk mengurangi penggunaan bahan mentah baru. Beberapa metode daur ulang sampah anorganik meliputi penghancuran, pemisahan bahan, peleburan, dan proses kimia lainnya. Daur ulang sampah anorganik membantu mengurangi penggunaan sumber daya alam dan mengurangi volume sampah yang berakhir di tempat pembuangan akhir.";
    }

    //simpan hasil ke firestore
    const currentTime = Date.now();
    const gmtPlus7Time = new Date(currentTime).toLocaleString("en-US", {
      timeZone: "Asia/Jakarta",
    });
    const data = { classResult, imageUrl, timestamp: gmtPlus7Time };
    const collection = db.collection("data");
    await collection.add(data);
    return h.response({ classResult, descriptions, audio, imageUrl }).code(200);
  } catch (error) {
    console.log(error);
    return h.response({ message: "Gagal melakukan klasifikasi" }).code(500);
  }
};
//server
const init = async () => {
  const server = Hapi.server({
    port: 3000,
    host: "localhost",
    routes: {
      cors: {
        origin: ["*"],
      },
    },
  });
  //routes
  server.route([
    {
      method: "GET",
      path: "/api",
      options: {
        handler: () => {
          return {
            status: "running",
          };
        },
      },
    },
    {
      method: "GET",
      path: "/api/data",
      options: {
        handler: getHistoryData,
      },
    },
    {
      method: "POST",
      path: "/api/classify",
      options: {
        handler: predictImgHandler,
        payload: {
          allow: "multipart/form-data",
          maxBytes: 209715200,
          multipart: true,
          output: "file",
        },
      },
    },
  ]);
  await server.start();
  console.log(`Server berjalan pada ${server.info.uri}`);
};

//start server
init().catch((error) => {
  console.error(error);
  process.exit(1);
});

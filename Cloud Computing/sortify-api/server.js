const Hapi = require("@hapi/hapi");
const { Storage } = require("@google-cloud/storage");
const axios = require("axios");
const sharp = require("sharp");
const fs = require("fs");
const { nanoid } = require("nanoid");

const projectId = "feisty-truth-381413";
const servingUrl = "";
const bucketName = "sortify-img";
const audioOrganik = "audio/organik-voice.mp3";
const audioAnorganik = "audio/anorganik-voice.mp3";
const gcs = new Storage({
  projectId: projectId,
  keyFilename: "servicekey.json",
});

const uploadImageHandler = async (request, h) => {
  try {
    const id = nanoid(8);
    const imageFile = request.payload.file;
    const filename = `${Date.now()}_${id}.jpg`;

    await gcs.bucket(bucketName).upload(imageFile.path, {
      destination: filename,
    });
    compressImageHandler(bucketName, filename);
    const imageUrl = `https://storage.googleapis.com/sortify-img/${filename}`;

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
    const image = sharp(fileBuffer).jpeg({ quality: 30 });

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

const predictImgHandler = async (request, h) => {
  try {
    const imageUrl = await uploadImageHandler(request, h);
    let descriptions;
    let classResult;
    let audio;
    const image = request.payload.file;

    const respon = await axios.post(
      `${servingUrl}/path/to/model:predict`,
      image
    );

    const predictions = respon.data.predictions[0];
    // const respon = request.payload;
    // const predictions = respon.predictions[0];
    const classIndex = predictions.indexOf(Math.max(...predictions));

    switch (classIndex) {
      case 0:
        audio = audioOrganik;
        classResult = "Organik";
        descriptions =
          "Sampah organik adalah jenis sampah yang berasal dari bahan-bahan yang dapat terurai secara alami, seperti sisa makanan, daun, rumput, kulit buah, dan lain sebagainya. Daur ulang sampah organik melibatkan proses pengolahan kembali sisa-sisa organik tersebut menjadi bahan yang berguna. Metode daur ulang sampah organik antara lain meliputi pengomposan dan pembuatan biogas. Daur ulang sampah organik membantu mengurangi volume sampah, mengurangi emisi gas rumah kaca, serta menghasilkan bahan yang dapat digunakan kembali, seperti kompos yang berguna sebagai pupuk organik atau biogas yang dapat digunakan sebagai sumber energi alternatif.";
        break;
      case 1:
        audio = audioAnorganik;
        classResult = "Anorganik";
        descriptions =
          "Sampah anorganik adalah sampah yang tidak dapat terurai secara alami oleh mikroorganisme. Ini termasuk bahan seperti plastik, kaca, logam, dan kertas yang terkontaminasi dengan bahan kimia. Daur ulang sampah anorganik melibatkan proses pemrosesan kembali bahan-bahan ini untuk menghasilkan produk baru atau untuk mengurangi penggunaan bahan mentah baru. Beberapa metode daur ulang sampah anorganik meliputi penghancuran, pemisahan bahan, peleburan, dan proses kimia lainnya. Daur ulang sampah anorganik membantu mengurangi penggunaan sumber daya alam dan mengurangi volume sampah yang berakhir di tempat pembuangan akhir.";
        break;
    }

    return h.response({ classResult, descriptions, audio, imageUrl }).code(200);
  } catch (error) {
    console.log(error);
    return h.response({ message: "Gagal melakukan klasifikasi" }).code(500);
  }
};

const init = async () => {
  const server = Hapi.server({
    port: 5000,
    host: "localhost",
    routes: {
      cors: {
        origin: ["*"],
      },
    },
  });

  server.route([
    {
      method: "POST",
      path: "/",
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

init().catch((error) => {
  console.error(error);
  process.exit(1);
});

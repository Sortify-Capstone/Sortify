package com.dicoding.sortify.ui.camera

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.sortify.databinding.ActivityCameraBinding
import com.dicoding.sortify.ui.clasify.AddClassifyActivity
import com.dicoding.sortify.utils.Helper

class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var launchGalery: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGallery()

        supportActionBar?.hide()

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        binding.btnGallery.setOnClickListener{
            openGalery()
        }

        binding.btnShutter.setOnClickListener{
            takePhoto()
        }

        binding.btnSwitch.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            openCamera()
        }

        openCamera()
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(480,720))
                .build()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().setTargetResolution(Size(480,720)).build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (e: Exception){
                Toast.makeText(this, "Fail to launch camera : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun initGallery(){
        launchGalery = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if (result.resultCode == RESULT_OK){
                Log.i("TEST_GALERY", "Galeri berhasil dipilih dan akan mengarah ke new")
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = Helper.uriToFile(selectedImg, this)

                val intent = Intent(this, AddClassifyActivity::class.java)
                intent.putExtra(AddClassifyActivity.EXTRA_PHOTO_RESULT, myFile)
                intent.putExtra(
                    AddClassifyActivity.EXTRA_CAMERA_MODE,
                    cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                )
                this.finish()
                startActivity(intent)
            }
        }
    }

    private fun openGalery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launchGalery.launch(chooser)
    }

    private fun takePhoto(){
        val imageCapture = imageCapture ?: null
        val photoFile = Helper.createFile(application)
        val  outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
//                    Toast.makeText(this, getString(R.string.UI_error_camera_take_photo), Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    val intent = Intent(this@CameraActivity, AddClassifyActivity::class.java)
                    intent.putExtra(AddClassifyActivity.EXTRA_PHOTO_RESULT, photoFile)
                    intent.putExtra(
                        AddClassifyActivity.EXTRA_CAMERA_MODE,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    this@CameraActivity.finish()
                    startActivity(intent)
                }
            }
        )
    }
}
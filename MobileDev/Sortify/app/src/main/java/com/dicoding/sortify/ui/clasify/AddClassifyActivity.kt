package com.dicoding.sortify.ui.clasify

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.sortify.R
import com.dicoding.sortify.databinding.ActivityAddClassifyBinding
import com.dicoding.sortify.ui.viewmodel.AddClassifyViewModel
import com.dicoding.sortify.utils.Helper
import java.io.File

class AddClassifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddClassifyBinding
    private val addClassifyViewModel: AddClassifyViewModel by viewModels()

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClassifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = "Add new classify"
//            this.title = getString(R.string.menu_tilte_new_classify)
        }

        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = Helper.rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )

        binding.classifyImage.setImageBitmap(rotatedBitmap)

        binding.btnUpload.setOnClickListener {
            uploadImage(myFile)
        }

        addClassifyViewModel.let { vm ->
            vm.classfyResult.observe(this) { result ->
                Toast.makeText(this, getString(R.string.API_success_upload_image), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ResultClassifyActivity::class.java)
                intent.putExtra(ResultClassifyActivity.EXTRA_RESULT, result.classResult)
                intent.putExtra(ResultClassifyActivity.EXTRA_IMGURL, result.imageUrl)
                intent.putExtra(ResultClassifyActivity.EXTRA_DESC, result.descriptions)
                startActivity(intent)
//                if (it) {
//                    Toast.makeText(this, getString(R.string.API_success_upload_image), Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, ResultClassifyActivity::class.java))
//                }
            }
            vm.loading.observe(this) {
                binding.loading.visibility = it
            }
            vm.error.observe(this) {
                if (it.isNotEmpty()) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(image: File) {
        val file = Helper.reduceFileImage(image)
        addClassifyViewModel.uploadNewClassify(this, file)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
package com.dicoding.sortify.ui.clasify

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.sortify.MainActivity
import com.dicoding.sortify.R
import com.dicoding.sortify.databinding.ActivityResultClassifyBinding


class ResultClassifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultClassifyBinding

    private var class_result : String? = null
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val EXTRA_IMGURL = "extra_imgurl"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_DESC = "extra_desc"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultClassifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* toolbar */
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = "Classify Result"
        }



        class_result = intent.getStringExtra(EXTRA_RESULT)
        playSound(class_result)

        if (class_result == "Organik"){
            binding.resultBanner.background = getDrawable(R.drawable.card_bg1)
        } else {
            binding.resultBanner.background = getDrawable(R.drawable.card_bg2)
        }
        binding.resultText.text = class_result
        Glide.with(binding.root)
            .load(intent.getStringExtra(EXTRA_IMGURL))
            .into(binding.classImage)
//        binding.storyDesc.text =
//            intent.getData(Constanta.ClassifyDetail.ContentDescription.name, "Caption")

        binding.resultBanner.setOnClickListener {
            playSound(class_result)
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        android.R.id.home -> {
            navigateToHome()
            return true
        }
    }
    return super.onOptionsItemSelected(item)
}

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun playSound(class_result: String?) {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
            }
        }

        if (class_result == "Organik"){
            mediaPlayer = MediaPlayer.create(this, R.raw.organik_voice)
        } else{
            mediaPlayer = MediaPlayer.create(this, R.raw.anorganik_voice)
        }

        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
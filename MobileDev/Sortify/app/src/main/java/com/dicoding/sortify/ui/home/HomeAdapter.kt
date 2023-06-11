package com.dicoding.sortify.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.sortify.R
import com.dicoding.sortify.data.model.Sampah
import com.dicoding.sortify.databinding.ItemHistoryBinding
import com.dicoding.sortify.ui.clasify.ResultClassifyActivity

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var data = mutableListOf<Sampah>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sampah = data[position]
        holder.bind(sampah)
    }

    fun initData(story: List<Sampah>) {
        data.clear()
        data = story.toMutableList()
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(sampah: Sampah) {
            with(binding) {
                tvItemDescription.text = sampah.classResult
                Glide.with(itemView)
                    .load(sampah.imageUrl)
                    .into(imgItemPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, ResultClassifyActivity::class.java)
                    intent.putExtra(ResultClassifyActivity.EXTRA_RESULT, sampah.classResult)
                    intent.putExtra(ResultClassifyActivity.EXTRA_IMGURL, sampah.imageUrl)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

}
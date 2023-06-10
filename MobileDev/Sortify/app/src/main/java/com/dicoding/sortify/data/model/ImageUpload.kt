package com.dicoding.sortify.data.model

import com.google.gson.annotations.SerializedName

data class ImageUpload(
    @field:SerializedName("classResult")
    val classResult: String,

    @field:SerializedName("descriptions")
    val descriptions: String,

    @field:SerializedName("audio")
    val audio: String,

    @field:SerializedName("imageUrl")
    val imageUrl: String
)
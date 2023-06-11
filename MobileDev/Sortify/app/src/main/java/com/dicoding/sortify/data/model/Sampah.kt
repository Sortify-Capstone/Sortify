package com.dicoding.sortify.data.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Sampah(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("classResult")
    val classResult: String,

    @field:SerializedName("imageUrl")
    val imageUrl: String,
)
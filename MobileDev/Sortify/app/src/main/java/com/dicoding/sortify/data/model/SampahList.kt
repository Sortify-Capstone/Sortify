package com.dicoding.sortify.data.model

import com.google.gson.annotations.SerializedName

data class SampahList(
    @field:SerializedName("data")
    val data: List<Sampah>,
)
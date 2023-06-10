package com.dicoding.sortify.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.sortify.R
import com.dicoding.sortify.data.api.ApiConfig
import com.dicoding.sortify.data.model.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(val context: Context): ViewModel() {
    val status = MutableLiveData<Status>()
    var error = MutableLiveData("")
    private val TAG = HomeViewModel::class.simpleName

    fun cekStatus(){
        val client = ApiConfig.getApiService().getStatus()
        client.enqueue(object : Callback<Status> {
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (response.isSuccessful) {
                    status.postValue(response.body())
                } else {
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.API_error_fetch_data)} : ${t.message}")
            }
        })
    }
}
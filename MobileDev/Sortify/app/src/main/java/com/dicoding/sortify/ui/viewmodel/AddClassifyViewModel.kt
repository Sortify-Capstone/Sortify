package com.dicoding.sortify.ui.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.sortify.R
import com.dicoding.sortify.data.api.ApiConfig
import com.dicoding.sortify.data.model.ImageUpload
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddClassifyViewModel : ViewModel() {
    var loading = MutableLiveData(View.GONE)
//    var isSuccessUploadStory = MutableLiveData(false)
    var error = MutableLiveData("")
    val classfyResult = MutableLiveData<ImageUpload>()
    val isError = MutableLiveData(true)

    private val TAG = AddClassifyViewModel::class.simpleName

    fun uploadNewClassify(context: Context, image: File) {
        loading.postValue(View.VISIBLE)
        "${image.length() / 1024 / 1024} MB" // manual parse from bytes to Mega Bytes
//        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val requestImageFile = image.asRequestBody("text/plain".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            requestImageFile
        )
//        val imageMultipart = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("file", image.name, requestImageFile.)
        print(imageMultipart)
        val client =
            ApiConfig.getApiService().uploadImage(imageMultipart)
        client.enqueue(object : Callback<ImageUpload> {
            override fun onResponse(call: Call<ImageUpload>, response: Response<ImageUpload>) {
                when (response.code()) {
                    413 -> error.postValue(context.getString(R.string.API_error_large_payload))
                    200 -> classfyResult.postValue(response.body())
                    else -> error.postValue("Error ${response.code()} : ${response.message()}")
                }
//                if (response.isSuccessful) {
//                    classfyResult.postValue(response.body())
//                } else {
//                    response.errorBody()?.let {
//                        val errorResponse = JSONObject(it.string())
//                        val errorMessages = errorResponse.getString("message")
//                        error.postValue("LOGIN ERROR : $errorMessages")
//                    }
//                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<ImageUpload>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.API_error_send_payload)} : ${t.message}")
            }
        })
    }
}
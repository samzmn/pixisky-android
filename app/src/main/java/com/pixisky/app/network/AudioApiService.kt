package com.pixisky.app.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface AudioApiService {
    @Streaming
    @GET
    suspend fun getAudio(@Url absoluteUrl: String): Call<ResponseBody>
}
package com.pixisky.app.data

import com.pixisky.app.network.AudioApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface AudioRepository {
    suspend fun getAudio(url: String) : Response<ResponseBody>
}

class NetworkAudioRepository(private val audioApiService: AudioApiService) : AudioRepository {

    override suspend fun getAudio(url: String): Response<ResponseBody> {
        val modifiedUrl = url.replace("pixiskyaudioqrcode", "pixiskyapplicationaudioqrcode")
        val call: Call<ResponseBody> = audioApiService.getAudio(url)
        return call.execute()
    }

}
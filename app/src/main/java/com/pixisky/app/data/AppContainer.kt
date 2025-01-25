package com.pixisky.app.data

import android.content.Context
import com.pixisky.app.network.AudioApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val pixiRepository: PixiRepository
    val audioRepository: AudioRepository
}


/**
 * [AppContainer] implementation that provides instance of [OfflinePixiRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    private val BASE_URL = "http://pixisky.com"
    /**
     * Implementation for [PixiRepository]
     */
    override val pixiRepository: PixiRepository by lazy {
        OfflinePixiRepository(PixiDatabase.getDatabase(context).pixiDao())
    }

    private val audioApiService: AudioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AudioApiService::class.java)
    }

    override val audioRepository: AudioRepository by lazy {
        NetworkAudioRepository(audioApiService)
    }
}

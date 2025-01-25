package com.pixisky.app.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pixisky.app.PermissionManager.Companion.REQUEST_STORAGE_PERMISSION
import com.pixisky.app.PixiSkyApplication
import com.pixisky.app.data.AudioRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface PixiAudioUiState {
    data class Success(val audioFilePath: String) : PixiAudioUiState
    data class Error(val message: String) : PixiAudioUiState
    object Loading : PixiAudioUiState
    object Failure : PixiAudioUiState
}

class PixiViewModel(private val audioRepository: AudioRepository) : ViewModel() {
    var pixiAudioUiState: PixiAudioUiState by mutableStateOf(PixiAudioUiState.Loading)
        private set

    var audioFileName: String? by mutableStateOf(null)
        private set

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PixiSkyApplication)
                val audioRepository = application.getContainer().audioRepository
                PixiViewModel(audioRepository = audioRepository)
            }
        }
    }

    fun getAudio(url: String, context: Context, saveAudio: Boolean = true) {
        viewModelScope.launch {
            pixiAudioUiState = PixiAudioUiState.Loading
            if (!saveAudio) {
                pixiAudioUiState = PixiAudioUiState.Success(url)
                return@launch
            }
            if (!hasStoragePermission(context)) {
                requestStoragePermission(context)
                pixiAudioUiState = PixiAudioUiState.Error("Storage permission required")
                return@launch
            }
            pixiAudioUiState = try {
                val response: Response<ResponseBody> = audioRepository.getAudio(url)
                if (response.isSuccessful) {
                    val fileName = response.headers()["Audio-File-Name"]?.let {
                        Regex("filename=\"(.+)\"").find(it)?.groupValues?.get(1)
                    } ?: "downloaded_audio.mp3"
                    audioFileName = fileName

                    response.body()?.byteStream()?.let { inputStream ->
                        val file = File(context.getExternalFilesDir(null), fileName)
                        FileOutputStream(file).use { outputStream ->
                            val buffer = ByteArray(1024)
                            var len: Int
                            while (inputStream.read(buffer).also { len = it } != -1) {
                                outputStream.write(buffer, 0, len)
                            }
                        }
                        inputStream.close()
                        PixiAudioUiState.Success(file.absolutePath)
                    } ?: PixiAudioUiState.Error("Failed to save audio file")
                } else {
                    PixiAudioUiState.Error("Response not successful")
                }
            } catch (e: IOException) {
                PixiAudioUiState.Error(e.message ?: "IOException occurred")
            } catch (e: HttpException) {
                PixiAudioUiState.Error(e.message ?: "HttpException occurred")
            }
        }
    }

    private fun hasStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission(context: Context) {
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION
        )
    }

}

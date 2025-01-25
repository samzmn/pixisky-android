package com.pixisky.app.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.pixisky.app.PixiSkyAppBar
import com.pixisky.app.R

@Composable
fun QrCodeResultScreen(url: String?, onNavigateBack: () -> Unit) {

        if (url != null) {
            if (validateUrl(url)){
                val newUrl = url.replace("pixiskyaudioqrcode", "pixiskyapplicationaudioqrcode")
                AudioPlayerScreen(newUrl, onNavigateBack)
            } else {
                ScanResultScreen(url, onNavigateBack)
            }
        } else {
            Text(text = "No URL Found")
        }

}

private fun validateUrl(url: String): Boolean {
    return url.startsWith("https://pixisky.com", ignoreCase = true) && url.contains("type=pixiskyaudioqrcode")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(url: String, onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiSkyAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateBack
            )
        }
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Scan Result:")
            OutlinedTextField(value = url ?: "", onValueChange = {}, readOnly = true)
        }
    }
}
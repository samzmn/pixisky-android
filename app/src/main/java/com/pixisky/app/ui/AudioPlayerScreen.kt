package com.pixisky.app.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pixisky.app.PixiSkyAppBar
import com.pixisky.app.R
import com.pixisky.app.ui.theme.PixiSkyTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(url: String, onNavigateBack: () -> Unit) {
    val pixiViewModel: PixiViewModel = viewModel(factory = PixiViewModel.Factory)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { PixiSkyAppBar(
            title = pixiViewModel.audioFileName ?: stringResource(R.string.app_name),
            canNavigateBack = true,
            scrollBehavior = scrollBehavior,
            navigateUp = onNavigateBack)
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val audioState = pixiViewModel.pixiAudioUiState) {
                is PixiAudioUiState.Loading -> {
                    pixiViewModel.getAudio(url, context = LocalContext.current, saveAudio = false)
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }
                is PixiAudioUiState.Error -> ErrorScreen(retryAction = {  })
                PixiAudioUiState.Failure -> ErrorScreen(retryAction = {  })
                is PixiAudioUiState.Success -> {
                    MediaPlayerScreen(audioState.audioFilePath)
                }
            }
        }
    }
}

/**
 * The home screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MediaPlayerScreen(url: String) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            prepare()
            playWhenReady = true // Start playing immediately
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                //if (events.contains(Player.EVENT_TRACKS_CHANGED))
                //if (events.contains(Player.EVENT_RENDERED_FIRST_FRAME))
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Convert Bitmap to Drawable
    val bgImg = BitmapDrawable(context.resources, BitmapFactory.decodeResource(context.resources, R.drawable.media_player_background))

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // PlayerView
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    defaultArtwork = bgImg // Set default artwork
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setShutterBackgroundColor(Color.TRANSPARENT)
                }
            },
            modifier = Modifier.matchParentSize()
        )
    }
}


@Preview
@Composable
fun MediaPlayerScreenPreview() {
    PixiSkyTheme {
        MediaPlayerScreen("")
    }
}
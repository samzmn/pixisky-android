package com.pixisky.app.ui

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixisky.app.PixiSkyAppBar
import com.pixisky.app.R
import com.pixisky.app.ui.navigation.NavigationDestination
import com.pixisky.app.ui.theme.PixiSkyTheme

object HomeDestination : NavigationDestination() {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToQrCode: () -> Unit,
    modifier: Modifier = Modifier
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    // val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PixiSkyAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
    ){ innerPadding ->
        HomeBody(
            navigateToQrCode = navigateToQrCode,
            modifier = modifier.padding(innerPadding),
            contentPadding = innerPadding
        )
    }
}

@Composable
fun HomeBody(
    navigateToQrCode: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    //val scrollState = rememberScrollState()
    val context = LocalContext.current
    val designLink = stringResource(R.string.design_url)

    Image(
        painter = painterResource(id = R.drawable.starry_background),
        contentDescription = "Background Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .background(color = Color.Black)
            .fillMaxSize()
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            //.verticalScroll(state = scrollState)
            .padding(16.dp)
            .fillMaxSize()

    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Add space at the top
        NavButton(
            icon = R.drawable.design_button,
            text = R.string.design_title,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(designLink))
                context.startActivity(intent) }
        )
        NavButton(
            icon = R.drawable.qrcode,
            text = R.string.qrcode_title,
            onClick = navigateToQrCode
        )
        NavButton(
            icon = R.drawable.pixisky_text_logo,
            text = R.string.live_sky_title,
            onClick = {  }

        )
        NavButton(
            icon = R.drawable.pixisky_text_logo,
            text = R.string.pixi_art_title,
            onClick = {  }
        )
    }

}

@Composable
fun NavButton(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            Color.Transparent
        ),
        contentPadding = PaddingValues(start = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .background(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(bottomStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(text),
                modifier = Modifier.width(80.dp).height(80.dp),
                contentScale = ContentScale.Inside
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(stringResource(text), style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview(apiLevel = 35)
@Composable
fun HomeScreenPreview(){
    PixiSkyTheme (darkTheme = false) {
        HomeScreen(
            navigateToQrCode = { }
        )
    }
}

@Preview(apiLevel = 35)
@Composable
fun HomeScreenPreviewDark(){
    PixiSkyTheme (darkTheme = true) {
        HomeScreen(
            navigateToQrCode = { }
        )
    }
}
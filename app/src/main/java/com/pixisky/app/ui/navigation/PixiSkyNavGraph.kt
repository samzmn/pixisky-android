package com.pixisky.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pixisky.app.ui.HomeDestination
import com.pixisky.app.ui.HomeScreen
import com.pixisky.app.ui.QrCodeDestination
import com.pixisky.app.ui.QrCodeResultScreen
import com.pixisky.app.ui.QrCodeScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PixiSkyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    val backStackEntry by navController.currentBackStackEntryAsState()

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier,
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToQrCode = { navController.navigate(QrCodeDestination.route) },
            )
        }
        composable(route = QrCodeDestination.route) {
            QrCodeScreen(
                scanCode = { qrCodeUrl ->
                    // Handle the QR code URL
                    val encodedUrl = URLEncoder.encode(qrCodeUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate("result?url=$encodedUrl")
                }
            )
        }
        composable(route = "result?url={url}") { backStackEntry ->
            QrCodeResultScreen(
                backStackEntry.arguments?.getString("url"),
                onNavigateBack = { navController.navigate(HomeDestination.route) }
            )
        }
    }


}
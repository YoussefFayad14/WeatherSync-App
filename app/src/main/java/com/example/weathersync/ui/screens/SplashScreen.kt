package com.example.weathersync.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.weathersync.R
import com.example.weathersync.navigation.ScreenRoute
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1500)
        showText = true
        delay(1500)
        navController.navigate(ScreenRoute.HomeScreenRoute.route) {
            popUpTo(ScreenRoute.SplashScreenRoute.route) { inclusive = true }
        }
    }

    val textAlpha by animateFloatAsState(
        targetValue = if (showText) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = stringResource(R.string.appName),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.graphicsLayer(alpha = textAlpha)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
   // SplashScreen()
}

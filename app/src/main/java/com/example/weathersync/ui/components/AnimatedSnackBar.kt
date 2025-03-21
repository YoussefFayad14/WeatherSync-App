package com.example.weathersync.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedSnackBar(message: String?) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            isVisible = true
            snackbarHostState.showSnackbar(message)
            delay(5000)
            isVisible = false
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -10 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -10 })
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = Color.Red,
                contentColor = Color.White,
                actionColor = Color.Yellow,
                shape = SnackbarDefaults.shape,
                modifier = Modifier.fillMaxWidth().padding(0.dp)
            )
        }
    }
}

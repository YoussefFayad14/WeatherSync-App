package com.example.weathersync.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weathersync.ui.theme.DarkGreen
import kotlinx.coroutines.delay

@Composable
fun AnimatedSnackBar(message: String?, type: String = "Error") {
    val snackbarHostState = remember { SnackbarHostState() }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            isVisible = true
            snackbarHostState.showSnackbar(message)
            delay(5000)
            isVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.fillMaxWidth()
            ) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = if (type == "Error") Color.Red else DarkGreen,
                    contentColor = Color.White,
                    actionColor = Color.Yellow,
                    shape = SnackbarDefaults.shape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

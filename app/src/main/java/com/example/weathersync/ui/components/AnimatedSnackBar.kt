package com.example.weathersync.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weathersync.R
import com.example.weathersync.ui.theme.DarkGreen
import kotlinx.coroutines.delay

@Composable
fun AnimatedSnackBar(
    message: String?,
    type: String = "Error",
    showUndoButton: Boolean = false,
    onUndoClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            isVisible = true
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (showUndoButton) "Undo" else null,
                duration = SnackbarDuration.Short
            )
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
            ) { data ->
                Snackbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    containerColor = if (type == "Error") Color.Red else DarkGreen,
                    contentColor = Color.White,
                    actionContentColor = Color.Yellow,
                    action = {
                        if (showUndoButton) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                TextButton(
                                    onClick = {
                                        onUndoClick()
                                        isVisible = false
                                    }
                                ) {
                                    Text(
                                        stringResource(R.string.undo),
                                        color = Color.White,
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    }
}
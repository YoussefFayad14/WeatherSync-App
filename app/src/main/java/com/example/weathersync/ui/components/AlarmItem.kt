package com.example.weathersync.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersync.R
import com.example.weathersync.data.model.local.AlarmEntity
import com.example.weathersync.ui.theme.DarkRed
import com.example.weathersync.ui.theme.Green1
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AlarmItem(
    day: String,
    time: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            when (state) {
                SwipeToDismissBoxValue.EndToStart -> {
                    scope.launch {
                        delay(500)
                        onDelete()
                    }
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                targetValue = when (swipeToDismissBoxState.currentValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.Transparent
                },
                label = "Background Color"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(backgroundColor, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.delete),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f))
        ) {
            ListItem(
                headlineContent = {
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.calendar_icon),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = day,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Text(
                            text = time,
                            fontSize = 48.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                                .align (Alignment.CenterVertically)
                        )
                    }
                },
                trailingContent = {
                   Image(
                       painter = painterResource(id = R.drawable.ic_notifications),
                       contentDescription = null,
                       contentScale = ContentScale.Crop,
                   )
                }
            )
            HorizontalDivider()
        }
    }
}


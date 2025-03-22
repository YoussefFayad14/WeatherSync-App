package com.example.weathersync.ui.components

import com.example.weathersync.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersync.navigation.ScreenRoute
import com.example.weathersync.ui.theme.LightSeaGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FavoriteItem(
    item: String,
    navigateTo: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            when (state) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    scope.launch {
                        delay(1000)
                        navigateTo()
                    }
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    scope.launch {
                        delay(1000)
                        onRemove()
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
                    SwipeToDismissBoxValue.StartToEnd -> LightSeaGreen
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
                    .padding(4.dp)
            ) {
                Text(
                    text = if (swipeToDismissBoxState.currentValue == SwipeToDismissBoxValue.StartToEnd)
                        stringResource(R.string.weather_details) else stringResource(R.string.delete),
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center),
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
                    Row {
                        Text(text = item, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Description", fontSize = 16.sp)
                    }
                },
                trailingContent = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Navigate",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavoritesItemScreenPreview(){
    FavoriteItem(item = "New York", navigateTo = {}, onRemove = {})
}

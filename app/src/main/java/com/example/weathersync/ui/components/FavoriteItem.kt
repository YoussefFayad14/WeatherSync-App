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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersync.data.model.local.FavoriteEntity
import com.example.weathersync.utils.WeatherUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FavoriteItem(
    item: FavoriteEntity,
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
                    text = stringResource(R.string.delete),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.address.split(", ").lastOrNull() ?: "Unknown City",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.address,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(2f)
                        )
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = WeatherUtils.getFormattedTemperature(item.weatherEntity?.temp ?:0.0 , context),
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier.offset(y = (-4).dp)
                                ) {
                                    Text(
                                        text = WeatherUtils.getTemperatureUnitSymbol(context),
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }

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
    //FavoriteItem(item = "New York", navigateTo = {}, onRemove = {})
}

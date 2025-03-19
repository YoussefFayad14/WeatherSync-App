package com.example.weathersync.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weathersync.R

@Composable
fun HourlyForecastItem(time: String, temp: String, iconCode: Int){
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, Color.White, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)

    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = time, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = iconCode),
                contentDescription = "Snow"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$temp°C", color = Color.White)
        }
    }
}

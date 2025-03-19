package com.example.weathersync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.res.stringResource
import com.example.weathersync.R
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen

@Composable
fun SettingsScreen() {
    val backgroundColor = if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsSection(iconRes = R.drawable.language_icon, title = stringResource(R.string.language), options = listOf(
            stringResource(R.string.arabic),
            stringResource(R.string.english),
            stringResource(R.string.default_language)
        ))
        SettingsSection(iconRes = R.drawable.temperature_icon, title = stringResource(R.string.temp_unit), options = listOf(
            stringResource(R.string.celsius_c),
            stringResource(R.string.kelvin_k),
            stringResource(R.string.fahrenheit_f)
        ))
        SettingsSection(iconRes = R.drawable.map_icon, title = stringResource(R.string.location), options = listOf(
            stringResource(R.string.gps),
            stringResource(R.string.map)
        ))
        SettingsSection(iconRes = R.drawable.speed_icon, title = stringResource(R.string.wind_speed_unit), options = listOf(
            stringResource(R.string.meter_sec),
            stringResource(R.string.mile_hour)
        ))
    }
}

@Composable
fun SettingsSection(iconRes: Int, title: String, options: List<String>) {
    var selectedOption by remember { mutableStateOf(options.first()) }
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor =
            if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.1f) else Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (isSystemInDarkTheme()) Color.Cyan else Color.DarkGray
                            )
                        )
                        Text(text = option, color = textColor)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}

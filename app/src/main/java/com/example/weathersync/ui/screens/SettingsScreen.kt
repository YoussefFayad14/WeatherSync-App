package com.example.weathersync.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weathersync.R
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.LocaleHelper
import com.example.weathersync.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val backgroundColor = if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen

    val currentLanguage by  settingsViewModel.selectedLanguage.collectAsState()
    val currentTempUnit by settingsViewModel.selectedTempUnit.collectAsState()
    val currentLocationType by settingsViewModel.selectedLocationType.collectAsState()
    val currentWindSpeed by settingsViewModel.selectedWindSpeedUnit.collectAsState()

    var needsRecreation by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(needsRecreation) {
        if (needsRecreation) {
            (context as? Activity)?.recreate()
            needsRecreation = false
        }
    }

    LaunchedEffect(Unit) {
        settingsViewModel.reloadSettings(context)
    }


    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SettingsSection(
                iconRes = R.drawable.language_icon,
                title = stringResource(R.string.language),
                options = listOf(
                    stringResource(R.string.arabic),
                    stringResource(R.string.english),
                    stringResource(R.string.default_language)
                ),
                selectedOption = currentLanguage,
                onOptionSelected = {
                    settingsViewModel.setLanguage(context, it)
                    needsRecreation = true
                }
            )
        }
        item {
            SettingsSection(
                iconRes = R.drawable.temperature_icon,
                title = stringResource(R.string.temp_unit),
                options = listOf(
                    stringResource(R.string.celsius_c),
                    stringResource(R.string.kelvin_k),
                    stringResource(R.string.fahrenheit_f)
                ),
                selectedOption = currentTempUnit,
                onOptionSelected = {
                    settingsViewModel.setTempUnit(context, it)
                }
            )
        }

        item {
            SettingsSection(
                iconRes = R.drawable.map_icon,
                title = stringResource(R.string.location),
                options = listOf(
                    stringResource(R.string.gps),
                    stringResource(R.string.map)
                ),
                selectedOption = currentLocationType,
                onOptionSelected = {
                    settingsViewModel.setLocationType(context, it)
                }
            )
        }

        item {
            SettingsSection(
                iconRes = R.drawable.speed_icon,
                title = stringResource(R.string.wind_speed_unit),
                options = listOf(
                    stringResource(R.string.meter_sec),
                    stringResource(R.string.mile_hour)
                ),
                selectedOption = currentWindSpeed,
                onOptionSelected = {
                    settingsViewModel.setWindSpeedUnit(context, it)
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    iconRes: Int,
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme())
                Color.Black.copy(alpha = 0.1f)
            else
                Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Column {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { onOptionSelected(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (isSystemInDarkTheme()) Color.Cyan else Color.DarkGray
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = option,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    val fakeViewModel = SettingsViewModel(context = LocalContext.current)
    SettingsScreen(fakeViewModel)
}

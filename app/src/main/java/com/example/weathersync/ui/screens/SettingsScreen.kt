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
import androidx.navigation.NavController
import com.example.weathersync.R
import com.example.weathersync.navigation.ScreenRoute.MapScreenRoute
import com.example.weathersync.ui.components.AnimatedSnackBar
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.LightSeaGreen
import com.example.weathersync.utils.LocaleHelper
import com.example.weathersync.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel, mapMessage: String?) {
    val context = LocalContext.current
    val backgroundColor = if (isSystemInDarkTheme()) DeepNavyBlue else LightSeaGreen

    val currentLanguage by  settingsViewModel.selectedLanguage.collectAsState()
    val currentTempUnit by settingsViewModel.selectedTempUnit.collectAsState()
    val currentLocationType by settingsViewModel.selectedLocationType.collectAsState()
    val currentWindSpeed by settingsViewModel.selectedWindSpeedUnit.collectAsState()
    val message by settingsViewModel.message.collectAsStateWithLifecycle()

    var needsRecreation by rememberSaveable { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showSnackBar by remember { mutableStateOf(false) }

    LaunchedEffect(message, mapMessage) {
        message?.let {
            snackbarMessage = Pair(it.first, it.second)
            showSnackBar = true
        }
        mapMessage?.let {
            snackbarMessage = if (snackbarMessage == null) {
                Pair(it, "Success")
            } else {
                Pair("${snackbarMessage?.first} | $it", "Success")
            }
            showSnackBar = true
        }
    }

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
                    if(currentLanguage == it) return@SettingsSection
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
                    if(currentTempUnit == it) return@SettingsSection
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
                    if(currentLocationType == it) return@SettingsSection
                    settingsViewModel.setLocationType(context, it)
                    settingsViewModel.handleSetLocationType(context) {
                        navController.navigate(MapScreenRoute.createRoute(null, null, true))
                    }
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
                    if(currentWindSpeed == it) return@SettingsSection
                    settingsViewModel.setWindSpeedUnit(context, it)
                }
            )
        }
    }
    if (showSnackBar && snackbarMessage != null) {
        AnimatedSnackBar(snackbarMessage!!.first, snackbarMessage!!.second)

        LaunchedEffect(snackbarMessage) {
            delay(3000)
            showSnackBar = false
            snackbarMessage = null
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
    val fakeNavController = NavController(LocalContext.current)
    SettingsScreen(fakeNavController,fakeViewModel,null)
}

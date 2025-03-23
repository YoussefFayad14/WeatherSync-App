package com.example.weathersync.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersync.R
import com.example.weathersync.ui.theme.DarkGreen
import com.example.weathersync.ui.theme.DarkRed
import com.example.weathersync.ui.theme.Green1
import com.google.android.gms.maps.model.LatLng

@Composable
fun BottomSheetContent(
    selectedLocation: LatLng,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.selected_location), fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = selectedLocation.latitude.toString(),
            onValueChange = {},
            label = { Text(stringResource(R.string.latitude)) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = selectedLocation.longitude.toString(),
            onValueChange = {},
            label = { Text(stringResource(R.string.longitude)) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) DarkRed else Color.Red
                )
            ) {
                Text(
                    stringResource(R.string.cancel),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White                )
            }
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) DarkGreen else Green1
                )
            ) {
                Text(
                    text = stringResource(R.string.save),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

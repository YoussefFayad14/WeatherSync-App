package com.example.weathersync.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersync.R
import com.example.weathersync.ui.theme.DarkGreen
import com.example.weathersync.ui.theme.DarkRed
import com.example.weathersync.ui.theme.DeepNavyBlue
import com.example.weathersync.ui.theme.Green1
import com.example.weathersync.ui.theme.LightSeaGreen
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerBottomSheet(
    context: Context,
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int, String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        var selectedHour by remember { mutableStateOf(0) }
        var selectedMinute by remember { mutableStateOf(0) }
        var selectedDay by remember { mutableStateOf("Monday") }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.set_alarm),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Day Picker TextField
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSystemInDarkTheme()) Color.White else LightSeaGreen,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .clickable{
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                selectedDay = "$dayOfMonth/${month + 1}/$year"
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }

            ) {
                Text(
                    text ="Selected Day: $selectedDay",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSystemInDarkTheme()) DeepNavyBlue else Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSystemInDarkTheme()) Color.White else LightSeaGreen,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .clickable {
                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                selectedHour = hour
                                selectedMinute = minute
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        ).show()
                    }
            ) {
                Text(
                    text ="Selected Time: $selectedHour:$selectedMinute",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSystemInDarkTheme()) DeepNavyBlue else Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) DarkRed else Color.Red
                    )
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        onTimeSelected(selectedHour, selectedMinute, selectedDay)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) DarkGreen else Green1
                    )
                ) {
                    Text(
                        stringResource(R.string.confirm),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

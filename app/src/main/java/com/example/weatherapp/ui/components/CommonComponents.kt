package com.example.weatherapp.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading weather data...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onErrorContainer,
                    contentColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again")
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    onAction: () -> Unit,
    actionText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onAction) {
            Text(actionText)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 200)
@Composable
fun LoadingIndicatorPreview() {
    WeatherAppTheme {
        LoadingIndicator()
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 300)
@Composable
fun ErrorMessagePreview() {
    WeatherAppTheme {
        ErrorMessage(
            message = "Unable to fetch weather data. Please check your internet connection.",
            onRetry = { }
        )
    }
}

@Composable
fun LocationConfirmationDialog(
    isVisible: Boolean,
    locationInfo: LocationInfo?,
    onGetWeather: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible && locationInfo != null) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Current Location",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Location Details
                    // Address
                    if (locationInfo.address.isNotEmpty()) {
                        LocationDetailRow(
                            label = "Address:",
                            value = locationInfo.address
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Coordinates
                    LocationDetailRow(
                        label = "Latitude:",
                        value = "%.6f°".format(locationInfo.latitude)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LocationDetailRow(
                        label = "Longitude:",
                        value = "%.6f°".format(locationInfo.longitude)
                    )


                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Would you like to get weather information for this location?",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                onGetWeather()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Get Weather")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
    }
}

// Data class for location information
data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

@Preview(showBackground = true, widthDp = 360, heightDp = 200)
@Composable
fun EmptyStatePreview() {
    WeatherAppTheme {
        EmptyState(
            message = "Search for a city or use your current location to get started!",
            actionText = "Search London",
            onAction = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 400)
@Composable
fun LocationConfirmationDialogPreview() {
    WeatherAppTheme {
        LocationConfirmationDialog(
            isVisible = true,
            locationInfo = LocationInfo(
                latitude = 51.5085,
                longitude = -0.1257,
                address = "London, Greater London, England, United Kingdom"
            ),
            onGetWeather = { },
            onDismiss = { }
        )
    }
}

@Composable
fun GpsWarningDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Location is Disabled",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "To use your current location for weather information, please enable GPS/Location Services in your device settings.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onOpenSettings()
                        onDismiss()
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            },
            modifier = modifier
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 300)
@Composable
fun GpsWarningDialogPreview() {
    WeatherAppTheme {
        GpsWarningDialog(
            isVisible = true,
            onDismiss = { },
            onOpenSettings = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 300, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GpsWarningDialogDarkPreview() {
    WeatherAppTheme {
        GpsWarningDialog(
            isVisible = true,
            onDismiss = { },
            onOpenSettings = { }
        )
    }
}

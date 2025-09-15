package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.components.*
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                WeatherApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp() {
    val viewModel: WeatherViewModel = viewModel()
    val weatherState = viewModel.weatherState
    val context = LocalContext.current

    // Initialize location manager
    LaunchedEffect(Unit) {
        viewModel.initializeLocationManager(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text("Weather App") },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.getCurrentLocationWeather(context)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Get current location weather"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LocationPermissionHandler(
            onLocationPermissionGranted = {
                // Permission granted, you can now use location
            }
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                SearchBar(
                    onSearch = { cityName ->
                        viewModel.getCurrentWeatherByCity(cityName)
                    }
                )
                
                // Content based on state
                when {
                    weatherState.isLoading -> {
                        LoadingIndicator()
                    }
                    
                    weatherState.error != null -> {
                        ErrorMessage(
                            message = weatherState.error,
                            onRetry = {
                                viewModel.clearError()
                                // Try to refresh or show empty state
                                viewModel.refreshWeather()
                            }
                        )
                    }
                    
                    weatherState.currentWeather != null -> {
                        // Current Weather
                        CurrentWeatherCard(weather = weatherState.currentWeather)
                        
                        // Forecast (if available)
                        weatherState.forecast?.let { forecast ->
                            ForecastCard(forecast = forecast)
                        }
                    }
                    
                    else -> {
                        EmptyState(
                            message = "Search for a city or use your current location to get started!",
                            actionText = "Search Dhaka",
                            onAction = {
                                viewModel.getCurrentWeatherByCity("Dhaka")
                            }
                        )
                    }
                }
            }
        }
    }
}

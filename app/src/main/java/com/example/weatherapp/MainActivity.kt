package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.components.*
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.google.accompanist.permissions.*

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun WeatherApp() {
    val viewModel: WeatherViewModel = viewModel()
    val weatherState = viewModel.weatherState
    val context = LocalContext.current

    // Initialize location manager
    LaunchedEffect(Unit) {
        viewModel.initializeLocationManager(context)
    }
    
    // Dynamic permission request handler for IconButton clicks
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.onPermissionGranted(context)
        } else {
            viewModel.onPermissionDenied()
        }
    }
    
    // Handle permission request trigger
    LaunchedEffect(weatherState.showPermissionRequest) {
        if (weatherState.showPermissionRequest) {
            locationPermissionState.launchMultiplePermissionRequest()
            viewModel.dismissPermissionRequest()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text("Weather App") },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.showLocationDialog(context)
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
                        
                        // OpenWeather Credit
                        OpenWeatherCredit()
                    }
                    
                    else -> {
                        val lastCity = viewModel.getLastSearchedCity(context)
                        EmptyState(
                            message = "Search for a city or use your current location to get started!",
                            actionText = "Search $lastCity",
                            onAction = {
                                viewModel.getCurrentWeatherByCity(lastCity)
                            }
                        )
                    }
                }
                
                // Location Confirmation Dialog
                LocationConfirmationDialog(
                    isVisible = weatherState.showLocationDialog,
                    locationInfo = weatherState.locationInfo,
                    onGetWeather = {
                        viewModel.getWeatherForCurrentLocation()
                    },
                    onDismiss = {
                        viewModel.dismissLocationDialog()
                    }
                )
                
                // GPS Warning Dialog
                GpsWarningDialog(
                    isVisible = weatherState.showGpsWarningDialog,
                    onDismiss = {
                        viewModel.dismissGpsWarningDialog()
                    },
                    onOpenSettings = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun WeatherAppPreview() {
//    WeatherAppTheme {
//        WeatherApp()
//    }
//}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherAppWithDataPreview() {
    WeatherAppTheme {
        WeatherAppPreviewContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppPreviewContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text("Weather App") },
                actions = {
                    IconButton(onClick = { }) {
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
            SearchBar(onSearch = { })
            
            // Show sample weather data
            CurrentWeatherCard(weather = sampleWeatherResponsePreview())
            ForecastCard(forecast = sampleForecastResponsePreview())
            
            // OpenWeather Credit
            OpenWeatherCredit()
        }
    }
}

// Sample data for preview
private fun sampleWeatherResponsePreview(): com.example.weatherapp.data.WeatherResponse {
    return com.example.weatherapp.data.WeatherResponse(
        coord = com.example.weatherapp.data.Coord(lon = -0.1257, lat = 51.5085),
        weather = listOf(
            com.example.weatherapp.data.Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        base = "stations",
        main = com.example.weatherapp.data.Main(
            temp = 22.5,
            feelsLike = 23.1,
            tempMin = 18.0,
            tempMax = 26.0,
            pressure = 1013,
            humidity = 65
        ),
        visibility = 10000,
        wind = com.example.weatherapp.data.Wind(speed = 3.5, deg = 220),
        clouds = com.example.weatherapp.data.Clouds(all = 0),
        dt = 1694678400,
        sys = com.example.weatherapp.data.Sys(
            country = "GB",
            sunrise = 1694661000,
            sunset = 1694708000
        ),
        timezone = 3600,
        id = 2643743,
        name = "London",
        cod = 200
    )
}

private fun sampleForecastResponsePreview(): com.example.weatherapp.data.ForecastResponse {
    return com.example.weatherapp.data.ForecastResponse(
        cod = "200",
        message = 0,
        cnt = 40,
        list = listOf(
            com.example.weatherapp.data.ForecastItem(
                dt = 1694678400,
                main = com.example.weatherapp.data.Main(
                    temp = 22.5,
                    feelsLike = 23.1,
                    tempMin = 18.0,
                    tempMax = 26.0,
                    pressure = 1013,
                    humidity = 65
                ),
                weather = listOf(
                    com.example.weatherapp.data.Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")
                ),
                clouds = com.example.weatherapp.data.Clouds(all = 0),
                wind = com.example.weatherapp.data.Wind(speed = 3.5, deg = 220),
                visibility = 10000,
                pop = 0.0,
                sys = com.example.weatherapp.data.ForecastSys(pod = "d"),
                dtTxt = "2024-09-14 12:00:00"
            ),
            com.example.weatherapp.data.ForecastItem(
                dt = 1694764800,
                main = com.example.weatherapp.data.Main(
                    temp = 25.0,
                    feelsLike = 25.5,
                    tempMin = 20.0,
                    tempMax = 28.0,
                    pressure = 1015,
                    humidity = 60
                ),
                weather = listOf(
                    com.example.weatherapp.data.Weather(id = 801, main = "Clouds", description = "few clouds", icon = "02d")
                ),
                clouds = com.example.weatherapp.data.Clouds(all = 20),
                wind = com.example.weatherapp.data.Wind(speed = 2.8, deg = 180),
                visibility = 10000,
                pop = 0.1,
                sys = com.example.weatherapp.data.ForecastSys(pod = "d"),
                dtTxt = "2024-09-15 12:00:00"
            ),
            com.example.weatherapp.data.ForecastItem(
                dt = 1694851200,
                main = com.example.weatherapp.data.Main(
                    temp = 19.0,
                    feelsLike = 19.5,
                    tempMin = 15.0,
                    tempMax = 22.0,
                    pressure = 1010,
                    humidity = 75
                ),
                weather = listOf(
                    com.example.weatherapp.data.Weather(id = 500, main = "Rain", description = "light rain", icon = "10d")
                ),
                clouds = com.example.weatherapp.data.Clouds(all = 80),
                wind = com.example.weatherapp.data.Wind(speed = 4.2, deg = 240),
                visibility = 8000,
                pop = 0.8,
                sys = com.example.weatherapp.data.ForecastSys(pod = "d"),
                dtTxt = "2024-09-16 12:00:00"
            )
        ),
        city = com.example.weatherapp.data.City(
            id = 2643743,
            name = "London",
            coord = com.example.weatherapp.data.Coord(lon = -0.1257, lat = 51.5085),
            country = "GB",
            population = 8982000,
            timezone = 3600,
            sunrise = 1694661000,
            sunset = 1694708000
        )
    )
}

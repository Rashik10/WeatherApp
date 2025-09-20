package com.example.weatherapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.location.LocationManager
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.components.LocationInfo
import com.example.weatherapp.utils.LocationUtils
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    
    private val repository = WeatherRepository()
    private var locationManager: LocationManager? = null
    private var sharedPreferences: SharedPreferences? = null
    
    companion object {
        private const val PREFS_NAME = "weather_app_prefs"
        private const val KEY_LAST_CITY = "last_city"
        private const val DEFAULT_CITY = "London"
    }
    
    var weatherState by mutableStateOf(WeatherState())
        private set
    
    fun initializeLocationManager(context: Context) {
        locationManager = LocationManager(context)
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getLastSearchedCity(context: Context): String {
        // Initialize sharedPreferences if not already done
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
        return sharedPreferences?.getString(KEY_LAST_CITY, DEFAULT_CITY) ?: DEFAULT_CITY
    }
    
    private fun saveLastSearchedCity(cityName: String) {
        sharedPreferences?.edit {
            putString(KEY_LAST_CITY, cityName)
        }
    }
    
    fun getCurrentWeatherByCity(cityName: String) {
        viewModelScope.launch {
            weatherState = weatherState.copy(isLoading = true, error = null)
            
            repository.getCurrentWeatherByCity(cityName)
                .onSuccess { weather ->
                    weatherState = weatherState.copy(
                        currentWeather = weather,
                        isLoading = false
                    )
                    // Save the city name for future use
                    saveLastSearchedCity(weather.name)
                    // Also fetch forecast for this city
                    getForecastByCity(cityName)
                }
                .onFailure { exception ->
                    weatherState = weatherState.copy(
                        error = exception.message ?: "Unknown error occurred",
                        isLoading = false
                    )
                }
        }
    }
    
    private fun getForecastByCity(cityName: String) {
        viewModelScope.launch {
            repository.getForecastByCity(cityName)
                .onSuccess { forecast ->
                    weatherState = weatherState.copy(forecast = forecast)
                }
                .onFailure { exception ->
                    // Don't update error state for forecast, as current weather is more important
                    println("Failed to fetch forecast: ${exception.message}")
                }
        }
    }
    
    fun clearError() {
        weatherState = weatherState.copy(error = null)
    }
    
    fun refreshWeather() {
        weatherState.currentWeather?.let { weather ->
            getCurrentWeatherByCity(weather.name)
        }
    }
    
    // New methods for location dialog
    fun showLocationDialog(context: Context) {
        if (!LocationUtils.hasLocationPermission(context)) {
            // Trigger permission request instead of showing error
            weatherState = weatherState.copy(
                showPermissionRequest = true,
                error = null,
                isLoading = false
            )
            return
        }
        
        if (!LocationUtils.isLocationEnabled(context)) {
            // Show GPS warning dialog instead of error message
            showGpsWarningDialog()
            return
        }
        
        viewModelScope.launch {
            weatherState = weatherState.copy(isLoading = true, error = null)
            
            try {
                val locationDetails = locationManager?.getCurrentLocationDetails()
                if (locationDetails != null) {
                    val (lat, lon, address) = locationDetails
                    weatherState = weatherState.copy(
                        isLoading = false,
                        locationInfo = LocationInfo(lat, lon, address),
                        showLocationDialog = true
                    )
                } else {
                    // Re-check GPS status if location is null
                    weatherState = weatherState.copy(isLoading = false)
                    if (!LocationUtils.isLocationEnabled(context)) {
                        showGpsWarningDialog()
                    } else {
                        weatherState = weatherState.copy(
                            error = "Unable to get current location",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Re-check GPS status on any exception
                weatherState = weatherState.copy(isLoading = false)
                if (!LocationUtils.isLocationEnabled(context)) {
                    showGpsWarningDialog()
                } else {
                    weatherState = weatherState.copy(
                        error = "Failed to get location: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun dismissLocationDialog() {
        weatherState = weatherState.copy(
            showLocationDialog = false,
            locationInfo = null
        )
    }
    
    fun showGpsWarningDialog() {
        weatherState = weatherState.copy(showGpsWarningDialog = true)
    }
    
    fun dismissGpsWarningDialog() {
        weatherState = weatherState.copy(showGpsWarningDialog = false)
    }
    
    fun dismissPermissionRequest() {
        weatherState = weatherState.copy(showPermissionRequest = false)
    }
    
    fun onPermissionGranted(context: Context) {
        weatherState = weatherState.copy(showPermissionRequest = false)
        // After permission is granted, proceed with location dialog
        showLocationDialog(context)
    }
    
    fun onPermissionDenied() {
        weatherState = weatherState.copy(
            showPermissionRequest = false,
            error = "Location permission is required to get current location weather"
        )
    }
    
    fun getWeatherForCurrentLocation() {
        weatherState.locationInfo?.let { locationInfo ->
            // Use the LocationManager to get city name from coordinates
            viewModelScope.launch {
                try {
                    val cityName = locationManager?.getCityNameFromLocation(
                        locationInfo.latitude, 
                        locationInfo.longitude
                    )
                    if (cityName != null) {
                        getCurrentWeatherByCity(cityName)
                    } else {
                        // Fallback to extracting from address
                        val addressParts = locationInfo.address.split(", ")
                        val city = addressParts.find { part ->
                            // Try to find a part that looks like a city (not a street number/name)
                            part.isNotBlank() && !part.matches(Regex("\\d+.*"))
                        } ?: addressParts.firstOrNull { it.isNotBlank() } ?: "Unknown"
                        getCurrentWeatherByCity(city)
                    }
                } catch (e: Exception) {
                    weatherState = weatherState.copy(
                        error = "Failed to get weather: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class WeatherState(
    val currentWeather: WeatherResponse? = null,
    val forecast: ForecastResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showLocationDialog: Boolean = false,
    val locationInfo: LocationInfo? = null,
    val showGpsWarningDialog: Boolean = false,
    val showPermissionRequest: Boolean = false
)
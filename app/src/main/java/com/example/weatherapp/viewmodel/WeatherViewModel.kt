package com.example.weatherapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.location.LocationManager
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.utils.LocationUtils
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    
    private val repository = WeatherRepository()
    private var locationManager: LocationManager? = null
    
    var weatherState by mutableStateOf(WeatherState())
        private set
    
    fun initializeLocationManager(context: Context) {
        locationManager = LocationManager(context)
    }
    
    fun getCurrentLocationWeather(context: Context) {
        if (!LocationUtils.hasLocationPermission(context)) {
            weatherState = weatherState.copy(
                error = "Location permission is required",
                isLoading = false
            )
            return
        }
        
        if (!LocationUtils.isLocationEnabled(context)) {
            weatherState = weatherState.copy(
                error = "Please enable location services",
                isLoading = false
            )
            return
        }
        
        viewModelScope.launch {
            weatherState = weatherState.copy(isLoading = true, error = null)
            
            try {
                val location = locationManager?.getCurrentLocation()
                if (location != null) {
                    getCurrentWeather(location.latitude, location.longitude)
                } else {
                    weatherState = weatherState.copy(
                        error = "Unable to get current location",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                weatherState = weatherState.copy(
                    error = "Failed to get location: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun getCurrentWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherState = weatherState.copy(isLoading = true, error = null)
            
            repository.getCurrentWeather(lat, lon)
                .onSuccess { weather ->
                    weatherState = weatherState.copy(
                        currentWeather = weather,
                        isLoading = false
                    )
                    // Also fetch forecast
                    getForecast(lat, lon)
                }
                .onFailure { exception ->
                    weatherState = weatherState.copy(
                        error = exception.message ?: "Unknown error occurred",
                        isLoading = false
                    )
                }
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
    
    private fun getForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getForecast(lat, lon)
                .onSuccess { forecast ->
                    weatherState = weatherState.copy(forecast = forecast)
                }
                .onFailure { exception ->
                    // Don't update error state for forecast, as current weather is more important
                    println("Failed to fetch forecast: ${exception.message}")
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
            getCurrentWeather(weather.coord.lat, weather.coord.lon)
        }
    }
}

data class WeatherState(
    val currentWeather: WeatherResponse? = null,
    val forecast: ForecastResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
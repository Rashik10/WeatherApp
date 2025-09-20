package com.example.weatherapp.repository

import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.network.WeatherApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherRepository {
    
    private val apiService = WeatherApiClient.apiService
    
    // You need to get your API key from OpenWeatherMap
    // Visit: https://openweathermap.org/api
    private val apiKey = "657834c9d87eeabc78e34afd18b59aed" // Replace with your actual API key
    
    suspend fun getCurrentWeatherByCity(cityName: String): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentWeatherByCity(cityName, apiKey)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch weather data: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getForecastByCity(cityName: String): Result<ForecastResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getForecastByCity(cityName, apiKey)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch forecast data: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
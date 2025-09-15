package com.example.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object WeatherUtils {
    
    fun getWeatherIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/$iconCode@2x.png"
    }
    
    fun formatTemperature(temp: Double): String {
        return "${temp.roundToInt()}°C"
    }
    
    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }
    
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        return format.format(date)
    }
    
    fun formatDateTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        return format.format(date)
    }
    
    fun capitalizeFirst(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
            }
        }
    }
    
    fun getWindDirection(degrees: Int): String {
        return when (degrees) {
            in 0..22, in 338..360 -> "N"
            in 23..67 -> "NE"
            in 68..112 -> "E"
            in 113..157 -> "SE"
            in 158..202 -> "S"
            in 203..247 -> "SW"
            in 248..292 -> "W"
            in 293..337 -> "NW"
            else -> "N"
        }
    }
}
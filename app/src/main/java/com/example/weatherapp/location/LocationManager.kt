package com.example.weatherapp.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            // Always request a fresh location for accuracy
            requestFreshLocation(continuation)
        }
    }
    
    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(continuation: kotlin.coroutines.Continuation<Location?>) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMaxUpdateDelayMillis(15000)
            .build()
        
        var isResumed = false
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                if (!isResumed) {
                    isResumed = true
                    continuation.resume(locationResult.lastLocation)
                }
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            
            // Set a timeout to avoid waiting indefinitely
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                fusedLocationClient.removeLocationUpdates(locationCallback)
                if (!isResumed) {
                    isResumed = true
                    continuation.resume(null)
                }
            }, 15000) // 15 seconds timeout
            
        } catch (e: Exception) {
            if (!isResumed) {
                isResumed = true
                continuation.resume(null)
            }
        }
    }
    
    suspend fun getCityNameFromLocation(latitude: Double, longitude: Double): String? {
        val (cityName, _) = getLocationInfo(latitude, longitude)
        return cityName
    }
    
    suspend fun getCurrentLocationDetails(): Triple<Double, Double, String>? {
        val location = getCurrentLocation()
        return if (location != null) {
            val (_, detailedAddress) = getLocationInfo(location.latitude, location.longitude)
            Triple(location.latitude, location.longitude, detailedAddress)
        } else {
            null
        }
    }
    
    private suspend fun getLocationInfo(latitude: Double, longitude: Double): Pair<String?, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            
            addresses?.firstOrNull()?.let { address ->
                // Extract city name with fallback hierarchy
                val cityName = address.locality 
                    ?: address.subLocality
                    ?: address.adminArea
                    ?: address.countryName
                
                // Build detailed address string
                val addressParts = mutableListOf<String>()
                address.subThoroughfare?.let { addressParts.add(it) }
                address.thoroughfare?.let { addressParts.add(it) }
                address.locality?.let { addressParts.add(it) }
                address.subAdminArea?.let { addressParts.add(it) }
                address.adminArea?.let { addressParts.add(it) }
                address.countryName?.let { addressParts.add(it) }
                
                val detailedAddress = if (addressParts.isNotEmpty()) {
                    addressParts.joinToString(", ")
                } else {
                    "Unknown location"
                }
                
                Pair(cityName, detailedAddress)
            } ?: Pair(null, "Unknown location")
        } catch (e: Exception) {
            Pair(null, "Unable to get address")
        }
    }
    
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 seconds
        ).build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
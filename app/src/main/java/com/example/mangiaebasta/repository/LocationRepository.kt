package com.example.mangiaebasta.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.mangiaebasta.model.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.tasks.await

class LocationRepository(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            val androidLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null
            ).await()

            androidLocation?.let {
                Location(lat = it.latitude, lng = it.longitude)
            }
        } catch (e: Exception) {
            Log.e("LocationRepository", "Errore nel recupero della posizione: ${e.message}")
            null
        }
    }

}

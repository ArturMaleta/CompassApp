package com.example.compassapp.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel

class CompassAppViewModel : ViewModel() {
    private lateinit var userPoint: Location
    private var destinationPoint = Location("")

    fun setUserPoint(userLocation: Location) {
        userPoint = userLocation
    }

    fun setDestinationPoint(destinationLocation: Location) {
        destinationPoint = destinationLocation
    }

    fun calculateDistance(): Float {
        return userPoint.distanceTo(destinationPoint)
    }
}
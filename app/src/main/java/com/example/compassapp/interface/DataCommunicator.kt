package com.example.compassapp.`interface`

import android.location.Location

interface DataCommunicator {
    fun passData(destination: Location)
}
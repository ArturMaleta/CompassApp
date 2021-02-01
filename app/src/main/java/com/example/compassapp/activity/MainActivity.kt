package com.example.compassapp.activity

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.compassapp.R
import com.example.compassapp.`interface`.DataCommunicator
import com.example.compassapp.fragment.DestinationFragment
import com.example.compassapp.util.Compass
import com.example.compassapp.util.Constans
import com.example.compassapp.viewmodel.CompassAppViewModel
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), DataCommunicator {
    private lateinit var compassIcon: ImageView
    private lateinit var destinationCoordinatesTv: TextView
    private lateinit var locationTv: TextView
    private lateinit var locationRequest: LocationRequest
    private lateinit var destinationBtn: Button

    private var currentDegree = 0f

    private val compass by inject<Compass>()
    private val compassAppViewModel by viewModel<CompassAppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        compassIcon = findViewById(R.id.compass_icon)

        destinationCoordinatesTv = findViewById(R.id.destination_coordinates)
        locationTv = findViewById(R.id.current_location)
        destinationBtn = findViewById(R.id.destination_btn)
        destinationBtn.setOnClickListener {
            openDestinationDialog()
        }

        setupCompass()
    }

    override fun onStop() {
        super.onStop()
        compass.stopCompass()
    }

    override fun onResume() {
        super.onResume()
        compass.startCompass()
    }

    private fun setupCompass() {
        val compassListener = getCompassListener()
        compass.setListener(compassListener)
    }

    private fun getCompassListener(): Compass.CompassListener? {
        return Compass.CompassListener { azimuth ->
            runOnUiThread {
                adjustArrow(azimuth)
            }
        }
    }

    private fun adjustArrow(azimuth: Float) {
        val rotateAnimation = RotateAnimation(
            currentDegree,
            -azimuth,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotateAnimation.duration = 210
        rotateAnimation.fillAfter = true

        compassIcon.startAnimation(rotateAnimation)
        currentDegree = -azimuth
    }


    private fun openDestinationDialog() {
        val fragmentManager = supportFragmentManager
        val destinationFragment = DestinationFragment()
        destinationFragment.show(fragmentManager, DestinationFragment.TAG)
    }

    private fun getUserLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        locationRequest.interval = Constans.UPDATE_INTERVAL
        locationRequest.fastestInterval = Constans.FASTEST_INTERVAL

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        onLocationChanged(locationResult.lastLocation)
                    }
                },
                Looper.myLooper())
            }
        }
    }

    private fun onLocationChanged(location: Location) {
        compassAppViewModel.setUserPoint(location)
        var distance = compassAppViewModel.calculateDistance()
        if (distance > 1000) {
            destinationCoordinatesTv.text = DecimalFormat("##.##").format(distance / 1000) + " km"
        } else {
            destinationCoordinatesTv.text = DecimalFormat("##.##").format(distance) + " m"
        }
    }

    override fun passData(destination: Location) {
        getUserLocation()
        compassAppViewModel.setDestinationPoint(destination)
    }
}

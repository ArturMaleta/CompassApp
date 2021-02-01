package com.example.compassapp.fragment

import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.compassapp.R
import com.example.compassapp.`interface`.DataCommunicator

class DestinationFragment : DialogFragment() {
    private var screenWidth: Int = 0
    private var screeHeight: Int = 0

    private lateinit var latEt: EditText
    private lateinit var longEt: EditText
    private lateinit var destinationBtn: Button

    lateinit var communicator: DataCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getScreenMetrics()
        communicator = activity as DataCommunicator

        return LayoutInflater.from(activity).inflate(R.layout.set_destination_fragment_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        latEt = view.findViewById(R.id.latitude_et)
        longEt = view.findViewById(R.id.longitude_et)

        destinationBtn = view.findViewById(R.id.set_destination_btn)
        destinationBtn.setOnClickListener {
            if (validateCoordinates("${latEt.text}", "${longEt.text}")) {
                val dest = Location("")
                dest.latitude = latEt.text.toString().toDouble()
                dest.longitude = longEt.text.toString().toDouble()

                communicator.passData(dest)
                this.dismiss()
            } else {
                Toast.makeText(activity, "Incorrect coordinates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setDialogParams()
    }

    private fun setDialogParams() {
        dialog!!.window?.let {
            val dm = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(dm)
            val params = it.attributes
            params.width = (screenWidth * 0.85).toInt()
            params.height = (screeHeight * 0.4).toInt()
            it.attributes = params
        }
    }

    private fun getScreenMetrics() {
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        screenWidth = dm.widthPixels
        screeHeight = dm.heightPixels
    }

    private fun validateCoordinates(lat: String, long: String): Boolean {
        val pattern =
            Regex("^(-?([0-8]?[0-9](\\.\\d+)?|90(.[0]+)?)\\s?\\s?)+(-?([1]?[0-7]?[0-9](\\.\\d+)?|180((.[0]+)?)))\$")
        return pattern.matches("$lat $long")
    }

    companion object {
        const val TAG = "DESTINATION_FRAGMENT_TAG"
    }
}
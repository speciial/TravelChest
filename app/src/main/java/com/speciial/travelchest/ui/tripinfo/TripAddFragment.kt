package com.speciial.travelchest.ui.tripinfo

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.MainActivity.Companion.PREF_NAME
import com.speciial.travelchest.PreferenceHelper.customPreference
import com.speciial.travelchest.PreferenceHelper.tripId
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.Location
import com.speciial.travelchest.model.Trip
import org.jetbrains.anko.doAsync
import java.time.LocalDateTime


class TripAddFragment : Fragment() {

    private lateinit var locationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        prefs = customPreference(activity as MainActivity, PREF_NAME)
        val now = LocalDateTime.now()
        var date = "${now.year}-${now.monthValue}-${now.dayOfMonth}"
        getLastLocation()
        val root = inflater.inflate(R.layout.fragment_trip_add, container, false)
        root.findViewById<RadioGroup>(R.id.trip_add_radiogroup_location)
            .setOnCheckedChangeListener { _, checkedId ->
                val editTextCity = root.findViewById<EditText>(R.id.trip_add_city)
                when (checkedId) {
                    R.id.trip_radio_location -> {
                        editTextCity.isEnabled = false
                        editTextCity.isFocusableInTouchMode = false
                    }
                    R.id.trip_radio_city -> {
                        editTextCity.isEnabled = true
                        editTextCity.isFocusableInTouchMode = true
                    }
                }
            }
        root.findViewById<Button>(R.id.trip_add_datepicker).setOnClickListener {

            val dp = DatePickerDialog(activity as MainActivity)
            dp.updateDate(now.year, now.monthValue, now.dayOfMonth)

            dp.setOnDateSetListener { _, year, month, dayOfMonth ->
                date = "$year-$month-$dayOfMonth"
                Log.d("DBG", date)
            }
            dp.show()


        }

        root.findViewById<Button>(R.id.trip_add_button).setOnClickListener {
            val db = TravelChestDatabase.get(activity as MainActivity)
            val tripName = root.findViewById<EditText>(R.id.trip_add_name).text.toString()
            var tripCity = ""
            var location: Location ?= null
            var okay = true
            if (root.findViewById<RadioButton>(R.id.trip_radio_location).isChecked) {
                getLastLocation()
                if(lastLocation == null && root.findViewById<RadioButton>(R.id.trip_radio_location).isChecked) {
                    Toast.makeText(
                        activity as MainActivity,
                        "You should have location activated to use your location",
                        Toast.LENGTH_LONG
                    ).show()
                    okay = false
                } else{
                    location = lastLocation!!
                    tripCity = getCityFromLocation(location)!!
                }

            } else {
                tripCity = root.findViewById<EditText>(R.id.trip_add_city).text.toString()
                val loca:Location
                try {
                    loca = getLocationFromCity(tripCity)!!
                    location = loca
                }catch (e:java.lang.Exception){
                    Toast.makeText(
                        activity as MainActivity,
                        "Can't find a location with that city",
                        Toast.LENGTH_LONG
                    ).show()
                    okay = false
                }

            }
            if(okay) {
                var idTrip: Long
                doAsync {
                    try{
                        val lastTrip = db.tripDao().get(prefs.tripId)
                        if(lastTrip.endDate != ON_TRIP)
                            lastTrip.endDate = "${now.year}-${now.month}-${now.dayOfMonth}"
                        db.tripDao().update(lastTrip)
                    }catch (e:java.lang.Exception){}
                    idTrip = db.tripDao()
                        .insert(Trip(0, tripName, tripCity, location!!, "", date, ON_TRIP))
                    prefs.tripId = idTrip
                }
                findNavController().navigate(R.id.nav_home)
            }
        }

        return root
    }

    private fun getLocationFromCity(cityName: String): Location? {
        val coder = Geocoder(activity)
        val addressList: List<Address>
        var address: Address? = null

        try {
            addressList = coder.getFromLocationName(cityName, 5)
            if (addressList == null) {
                return null
            }
            address = addressList[0]

        } catch (e: Exception) {
        }
        return Location(address!!.latitude, address.longitude)
    }

    private fun getCityFromLocation(location: Location): String? {
        val coder = Geocoder(activity)
        val addressList: List<Address>
        var address: Address? = null

        try {
            addressList = coder.getFromLocation(location.latitude, location.longitude, 5)
            if (addressList == null) {
                return null
            }
            address = addressList[0]

        } catch (e: Exception) {
        }
        return address!!.locality
    }

    private fun getLastLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)
        locationClient.lastLocation.addOnCompleteListener(activity as MainActivity) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = Location(task.result!!.latitude, task.result!!.longitude)
            }
        }
    }
    companion object{
        const val ON_TRIP = "On trip"
    }

}
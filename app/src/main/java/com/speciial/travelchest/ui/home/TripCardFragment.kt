package com.speciial.travelchest.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.speciial.travelchest.R
import com.speciial.travelchest.model.Trip

class TripCardFragment(private val trip: Trip, private val tripCardListener: TripCardAdapter.TripCardListener, private val cardIndex: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trip_card, container, false)

        root.findViewById<TextView>(R.id.home_card_title).text = trip.name
        root.findViewById<TextView>(R.id.home_card_subtitle).text = trip.tripCiy
        root.findViewById<TextView>(R.id.home_card_date).text = "${trip.startDate} - ${trip.endDate}"

        root.findViewById<MaterialCardView>(R.id.home_card_view).setOnClickListener {
            tripCardListener.onTripCardClick(cardIndex)
        }

        return root
    }


}

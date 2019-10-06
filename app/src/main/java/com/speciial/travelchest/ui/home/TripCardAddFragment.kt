package com.speciial.travelchest.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.speciial.travelchest.R

class TripCardAddFragment(private val tripCardListener: TripCardAdapter.TripCardListener) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trip_card_add, container, false)

        root.findViewById<MaterialCardView>(R.id.home_card_view_add).setOnClickListener {
            tripCardListener.onTripCardClick(-1)
        }

        return root
    }


}

package com.speciial.travelchest.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.speciial.travelchest.model.Trip

class TripCardAdapter(
    fm: FragmentManager,
    private val tripList: List<Trip>,
    private val tripCardListener: TripCardListener
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            TripCardAddFragment(tripCardListener)
        } else {
            TripCardFragment(tripList[position - 1], tripCardListener)
        }
    }

    override fun getCount(): Int = (tripList.size + 1)

    interface TripCardListener {
        fun onTripCardClick(cardID: Long)
    }
}
package com.speciial.travelchest.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TripCardAdapter(fm: FragmentManager, private val tripCardListener: TripCardListener) : FragmentStatePagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        return if(position == 0) {
            TripCardAddFragment(tripCardListener, position)
        }else {
            TripCardFragment(tripCardListener, position)
        }
    }

    override fun getCount(): Int = 5

    interface TripCardListener {
        fun onTripCardClick(cardIndex: Int)
    }
}
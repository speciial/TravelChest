package com.speciial.travelchest.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TripCardAdapter(fragment: FragmentManager) : FragmentStatePagerAdapter(fragment){

    override fun getItem(position: Int): Fragment {
        return TripCard()
    }

    override fun getCount(): Int = 5

}
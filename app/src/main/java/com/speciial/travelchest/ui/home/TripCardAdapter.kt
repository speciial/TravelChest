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

    private val fragmentCache: ArrayList<Fragment> = ArrayList()

    init {
        fragmentCache.ensureCapacity(tripList.size + 1)
        fragmentCache.add(TripCardAddFragment(tripCardListener, 0))
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            TripCardAddFragment(tripCardListener, 0)
        } else {
            TripCardFragment(tripList[position - 1], tripCardListener, position)
        }
    }

    override fun getCount(): Int = (tripList.size + 1)

    interface TripCardListener {
        fun onTripCardClick(cardIndex: Int)
    }
}
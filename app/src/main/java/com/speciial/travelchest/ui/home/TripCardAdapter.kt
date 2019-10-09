package com.speciial.travelchest.ui.home

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.speciial.travelchest.model.Trip


class TripCardAdapter(
    fm: FragmentManager,
    private val tripList: List<Trip>
) : FragmentStatePagerAdapter(fm) {

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        try {
            super.restoreState(state, loader)
        } catch (e: Exception) {
            // TODO: this is a hack and needs improvement
            // Log.e("TAG", "Error Restore State of Fragment : " + e.message, e)
        }

    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            TripCardAddFragment()
        } else {
            TripCardFragment.newInstance(tripList[position - 1].uid)
        }
    }

    override fun getCount(): Int = (tripList.size + 1)

    interface TripCardListener {
        fun onTripCardClick(tripID: Long)
    }
}
package com.speciial.travelchest.ui.tripinfo

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.speciial.travelchest.MainActivity.Companion.TAG
import com.speciial.travelchest.ui.tripinfo.lists.AudioListFragment
import com.speciial.travelchest.ui.tripinfo.lists.ImageListFragment
import com.speciial.travelchest.ui.tripinfo.lists.VideoListFragment

class ListPagerAdapter(
    fm: FragmentManager,
    tripID: Long
) : FragmentStatePagerAdapter(fm) {

    private val imageListFragment = ImageListFragment.newInstance(tripID)
    private val videoListFragment = VideoListFragment.newInstance(tripID)
    private val audioListFragment = AudioListFragment.newInstance(tripID)

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> imageListFragment
            1 -> videoListFragment
            2 -> audioListFragment
            else -> {
                Log.d(TAG, "Pager out of bounds")
                Fragment()
            }
        }
    }

    override fun getCount(): Int = 3

}
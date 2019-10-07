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
    private val imageList: ArrayList<String>,
    private val videoList: ArrayList<String>,
    private val audioList: ArrayList<String>
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ImageListFragment(imageList)
            1 -> VideoListFragment(videoList)
            2 -> AudioListFragment(audioList)
            else -> {
                Log.d(TAG, "Pager out of bounds")
                Fragment()
            }
        }
    }

    override fun getCount(): Int = 3

}
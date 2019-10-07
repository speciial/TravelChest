package com.speciial.travelchest.ui.tripinfo.lists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.R

class VideoListFragment(private val videoList: ArrayList<String>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_video_list, container, false)

        val rvAdapter = VideoRVAdapter(videoList, context!!)
        val rvManager = LinearLayoutManager(context!!)

        root.findViewById<RecyclerView>(R.id.video_list_rv).apply {
            setHasFixedSize(true)
            adapter = rvAdapter
            layoutManager = rvManager
        }

        return root
    }


}

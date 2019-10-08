package com.speciial.travelchest.ui.home.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.speciial.travelchest.R


class VideoViewerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.viewer_video, container, false)
        val vw = root.findViewById<VideoView>(R.id.video_viewer)
        vw.setVideoPath(arguments?.getString("path"))
        vw.start()
        return root
    }




}
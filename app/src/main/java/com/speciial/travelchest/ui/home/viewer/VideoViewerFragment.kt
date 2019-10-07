package com.speciial.travelchest.ui.home.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ui.PlayerView
import com.speciial.travelchest.R


class VideoViewerFragment : Fragment() {

    private lateinit var playerView:PlayerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.viewer_video, container, false)
        root.findViewById<VideoView>(R.id.video_viewer).setVideoPath(arguments?.getString("path"))
        root.findViewById<VideoView>(R.id.video_viewer).start()
        return root
    }




}
package com.speciial.travelchest.ui.home.viewer

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.speciial.travelchest.R


class ImageViewerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.viewer_image, container, false)
        val imageBitmap= BitmapFactory.decodeFile(arguments?.getString("path"))
        root.findViewById<ImageView>(R.id.image_viewer).setImageBitmap(imageBitmap)
        return root
    }


}
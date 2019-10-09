package com.speciial.travelchest.ui.tripinfo.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.speciial.travelchest.FileHelper
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class ImageViewerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.viewer_image, container, false)
        doAsync {
            val bitmap = FileHelper.getBitmapFromPath(
                activity as MainActivity,
                arguments?.getString("path")!!
            )
            uiThread {
                root.findViewById<ImageView>(R.id.image_viewer).setImageBitmap(bitmap)
            }
        }
        return root
    }


}
package com.speciial.travelchest.ui.tripinfo.lists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.R

class ImageListFragment(private val imageList: ArrayList<String>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_image_list, container, false)

        val rvAdapter = ImageRVAdapter(imageList, context!!)
        val rvManager = GridLayoutManager(context!!, 3)

        root.findViewById<RecyclerView>(R.id.image_list_rv).apply {
            setHasFixedSize(true)
            adapter = rvAdapter
            layoutManager = rvManager
        }

        return root
    }

}

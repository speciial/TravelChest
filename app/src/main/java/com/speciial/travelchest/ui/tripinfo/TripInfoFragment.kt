package com.speciial.travelchest.ui.tripinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.R

class TripInfoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trip_info, container, false)

        val gridLayoutManager = GridLayoutManager(context!!, 4)

        viewManager = gridLayoutManager

        viewAdapter = ImageRVAdapter(context!!)

        recyclerView = root.findViewById(R.id.image_rv)
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = viewAdapter

            layoutManager = viewManager
        }

        return root
    }


}

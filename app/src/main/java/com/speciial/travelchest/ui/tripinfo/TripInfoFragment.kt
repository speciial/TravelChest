package com.speciial.travelchest.ui.tripinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class TripInfoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trip_info, container, false)

        val tripID = arguments!!.getLong("tripID")
        doAsync {
            val trip = TravelChestDatabase.get(activity as MainActivity).tripDao().getTripByID(tripID)
            uiThread {
                root.findViewById<TextView>(R.id.trip_info_title).text = trip.name
                root.findViewById<TextView>(R.id.trip_info_subtitle).text = trip.tripCiy
                root.findViewById<TextView>(R.id.trip_info_date).text = "${trip.startDate} - ${trip.endDate}"
            }
        }

        val gridLayoutManager = GridLayoutManager(context!!, 3)

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

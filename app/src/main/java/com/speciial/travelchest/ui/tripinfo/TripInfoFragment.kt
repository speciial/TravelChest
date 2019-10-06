package com.speciial.travelchest.ui.tripinfo


import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.MainActivity.Companion.TAG
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.Type
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class TripInfoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val imageList: ArrayList<String> = ArrayList()
    private val videoList: ArrayList<String> = ArrayList()
    private val audioList: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trip_info, container, false)

        fillView(root)

        return root
    }

    private fun fillView(root: View){
        val tripID = arguments!!.getLong("tripID")

        doAsync {
            val trip = TravelChestDatabase.get(activity as MainActivity).tripDao().get(tripID)

            if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
                for(item in trip.fileList){
                    if(item.type == Type.IMAGE){
                        imageList.add(item.path)
                    }

                    Log.d(TAG, item.path)
                }
            }

            viewManager = GridLayoutManager(context!!, 3)
            viewAdapter = ImageRVAdapter(imageList, context!!)

            uiThread {
                root.findViewById<TextView>(R.id.trip_info_title).text = trip.name
                root.findViewById<TextView>(R.id.trip_info_subtitle).text = trip.tripCiy
                root.findViewById<TextView>(R.id.trip_info_date).text = "${trip.startDate} - ${trip.endDate}"

                recyclerView = root.findViewById(R.id.image_rv)
                recyclerView.apply {
                    setHasFixedSize(true)
                    adapter = viewAdapter
                    layoutManager = viewManager
                }
            }

        }
    }

}

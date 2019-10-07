package com.speciial.travelchest.ui.tripinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/*

private lateinit var recyclerView: RecyclerView
private lateinit var viewAdapter: RecyclerView.Adapter<*>
private lateinit var viewManager: RecyclerView.LayoutManager

viewManager = GridLayoutManager(context!!, 3)
viewAdapter = ImageRVAdapter(imageList, context!!)

recyclerView = root.findViewById(R.id.image_rv)
recyclerView.apply {
setHasFixedSize(true)
adapter = viewAdapter
layoutManager = viewManager
}
 */

class TripInfoFragment : Fragment() {

    private lateinit var listPager: ViewPager

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

            uiThread {
                root.findViewById<TextView>(R.id.trip_info_title).text = trip.name
                root.findViewById<TextView>(R.id.trip_info_subtitle).text = trip.tripCiy
                root.findViewById<TextView>(R.id.trip_info_date).text = "${trip.startDate} - ${trip.endDate}"

                listPager = root.findViewById(R.id.trip_info_pager)
                listPager.adapter = ListPagerAdapter(activity!!.supportFragmentManager, tripID)

                root.findViewById<Button>(R.id.btn_image_list).setOnClickListener { listPager.setCurrentItem(0, true) }
                root.findViewById<Button>(R.id.btn_video_list).setOnClickListener { listPager.setCurrentItem(1, true) }
                root.findViewById<Button>(R.id.btn_audio_list).setOnClickListener { listPager.setCurrentItem(2, true) }
            }

        }
    }

}

package com.speciial.travelchest.ui.tripinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.File
import com.speciial.travelchest.model.Trip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.time.LocalDateTime


class TripInfoFragment : Fragment(), ViewPager.OnPageChangeListener {

    private lateinit var listPager: ViewPager
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_trip_info, container, false)

        fillView()

        return root
    }

    private fun fillView() {
        val tripID = arguments!!.getLong("tripID")

        doAsync {
            val trip = TravelChestDatabase.get(activity as MainActivity).tripDao().get(tripID)

            uiThread {
                root.findViewById<TextView>(R.id.trip_info_title).text = trip.name
                root.findViewById<TextView>(R.id.trip_info_subtitle).text = trip.tripCiy
                root.findViewById<TextView>(R.id.trip_info_date).text =
                    getString(R.string.date_template, trip.startDate, trip.endDate)
                root.findViewById<MaterialButton>(R.id.trip_info_end_trip).setOnClickListener {
                    endTrip(trip)
                }

                listPager = root.findViewById(R.id.trip_info_pager)
                listPager.adapter = ListPagerAdapter(it.childFragmentManager, tripID)
                listPager.addOnPageChangeListener(it)

                root.findViewById<Button>(R.id.btn_image_list)
                    .setOnClickListener { listPager.setCurrentItem(0, true) }
                root.findViewById<Button>(R.id.btn_video_list)
                    .setOnClickListener { listPager.setCurrentItem(1, true) }
                root.findViewById<Button>(R.id.btn_audio_list)
                    .setOnClickListener { listPager.setCurrentItem(2, true) }
                highlightButton(0)
            }

        }
    }

    private fun endTrip(trip: Trip) {
        doAsync {
            val db = TravelChestDatabase.get(activity as MainActivity)
            val now = LocalDateTime.now()
            trip.endDate = "${now.year}-${now.monthValue}-${now.dayOfMonth}"
            db.tripDao().update(trip)
        }
    }

    private fun highlightButton(index: Int) {
        root.findViewById<Button>(R.id.btn_image_list)
            .setBackgroundColor(context!!.getColor(R.color.secondaryColor))
        root.findViewById<Button>(R.id.btn_video_list)
            .setBackgroundColor(context!!.getColor(R.color.secondaryColor))
        root.findViewById<Button>(R.id.btn_audio_list)
            .setBackgroundColor(context!!.getColor(R.color.secondaryColor))

        when (index) {
            0 -> root.findViewById<Button>(R.id.btn_image_list).setBackgroundColor(
                context!!.getColor(
                    R.color.secondaryDarkColor
                )
            )
            1 -> root.findViewById<Button>(R.id.btn_video_list).setBackgroundColor(
                context!!.getColor(
                    R.color.secondaryDarkColor
                )
            )
            2 -> root.findViewById<Button>(R.id.btn_audio_list).setBackgroundColor(
                context!!.getColor(
                    R.color.secondaryDarkColor
                )
            )
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        highlightButton(position)
    }

    interface FileClickListener {
        fun onFileClicked(file: File)
    }

}

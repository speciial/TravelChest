package com.speciial.travelchest.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.speciial.travelchest.FileHelper
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class TripCardFragment : Fragment() {

    private var tripCardListener: TripCardAdapter.TripCardListener? = null
    private var tripID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tripID = it.getLong("tripID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trip_card, container, false)

        doAsync {
            val trip = TravelChestDatabase.get(activity as MainActivity).tripDao().get(tripID!!)

            uiThread {
                var bitmap: Bitmap?= null
                try{
                    bitmap =  FileHelper.getBitmapFromPath(context!!,trip.pathThumbnail)
                } catch (e:Exception){}

                if (bitmap != null) {
                    val bitmapScaled = bitmap.scale(bitmap.width / 4, bitmap.height / 4, false)
                    root.findViewById<ImageView>(R.id.home_card_thumbnail).setImageBitmap(bitmapScaled)
                }

                root.findViewById<TextView>(R.id.home_card_title).text = trip.name
                root.findViewById<TextView>(R.id.home_card_subtitle).text = trip.tripCiy
                root.findViewById<TextView>(R.id.home_card_date).text =
                    "${trip.startDate} - ${trip.endDate}"

                root.findViewById<MaterialCardView>(R.id.home_card_view).setOnClickListener {
                    tripCardListener?.onTripCardClick(trip.uid)
                }
            }
        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TripCardAdapter.TripCardListener) {
            tripCardListener = context
        } else {
            throw RuntimeException("$context must implement TripCardListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        tripCardListener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(tripID: Long) = TripCardFragment().apply {
            arguments = Bundle().apply {
                putLong("tripID", tripID)
            }
        }
    }
}
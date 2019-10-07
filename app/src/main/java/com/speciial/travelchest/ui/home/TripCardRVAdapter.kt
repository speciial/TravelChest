package com.speciial.travelchest.ui.home

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.speciial.travelchest.R
import com.speciial.travelchest.model.Trip

class TripCardRVAdapter(
    private val tripList: List<Trip>,
    private val cardClickListener: CardClickListener
) : RecyclerView.Adapter<TripCardRVAdapter.TripCardRVViewHolder>() {

    class TripCardRVViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val cardView: MaterialCardView = item.findViewById(R.id.home_card_view)
        val title: TextView = item.findViewById(R.id.home_card_title)
        val subtitle: TextView = item.findViewById(R.id.home_card_subtitle)
        val date: TextView = item.findViewById(R.id.home_card_date)
        val image: ImageView = item.findViewById(R.id.home_card_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripCardRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_card, parent, false) as FrameLayout

        return TripCardRVViewHolder(view)
    }

    override fun getItemCount(): Int = tripList.size

    override fun onBindViewHolder(holder: TripCardRVViewHolder, position: Int) {

        val bitmap = BitmapFactory.decodeFile(tripList[position].pathThumbnail)
        if (bitmap != null) {
            val bitmapScaled = bitmap.scale(bitmap.width / 4, bitmap.height / 4, false)
            holder.image.setImageBitmap(bitmapScaled)
        }

        holder.title.text = tripList[position].name
        holder.subtitle.text = tripList[position].tripCiy
        holder.date.text = "${tripList[position].startDate} - ${tripList[position].endDate}"

        holder.cardView.setOnClickListener {
            cardClickListener.onCardClick(tripList[position].uid)
        }
    }

    interface CardClickListener {
        fun onCardClick(tripID: Long)
    }

}
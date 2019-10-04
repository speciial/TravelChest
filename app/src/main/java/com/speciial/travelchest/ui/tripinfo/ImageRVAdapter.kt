package com.speciial.travelchest.ui.tripinfo

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.R

class ImageRVAdapter(private val context: Context) : RecyclerView.Adapter<ImageRVAdapter.ImageRVViewHolder>() {

    private val bitmap = BitmapFactory.decodeStream(context.assets.open("images/wallpaper.jpg"))

    class ImageRVViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val image: ImageView = item.findViewById(R.id.grid_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_info_image, parent, false) as FrameLayout

        return ImageRVViewHolder(view)
    }

    override fun getItemCount(): Int = 100

    override fun onBindViewHolder(holder: ImageRVViewHolder, position: Int) {
        holder.image.setImageBitmap(bitmap)
    }

}
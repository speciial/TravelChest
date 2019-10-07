package com.speciial.travelchest.ui.tripinfo.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.R

class VideoRVAdapter(private val videoList: ArrayList<String>, private val context: Context) : RecyclerView.Adapter<VideoRVAdapter.VideoRVViewHolder>() {

    class VideoRVViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val name: TextView = item.findViewById(R.id.trip_info_video_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_info_video, parent, false) as LinearLayout

        return VideoRVViewHolder(view)
    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: VideoRVViewHolder, position: Int) {
        holder.name.text = videoList[position]
    }

}
package com.speciial.travelchest.ui.tripinfo.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.R
import com.speciial.travelchest.model.File
import com.speciial.travelchest.ui.tripinfo.TripInfoFragment

class AudioRVAdapter(
    private val audioList: List<File>,
    private val fileClickListener: TripInfoFragment.FileClickListener,
    private val context: Context
) : RecyclerView.Adapter<AudioRVAdapter.AudioRVViewHolder>() {

    class AudioRVViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val name: TextView = item.findViewById(R.id.trip_info_audio_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_info_audio, parent, false) as LinearLayout

        return AudioRVViewHolder(view)
    }

    override fun getItemCount(): Int = audioList.size

    override fun onBindViewHolder(holder: AudioRVViewHolder, position: Int) {
        holder.name.text = audioList[position].path
        holder.name.setOnClickListener {
            fileClickListener.onFileClicked(audioList[position])
        }
    }

}
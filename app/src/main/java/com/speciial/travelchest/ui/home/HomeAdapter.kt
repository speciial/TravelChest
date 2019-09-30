package com.speciial.travelchest.ui.home

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.R
import com.speciial.travelchest.model.File
import kotlinx.android.synthetic.main.item_home_picture.view.*
import kotlinx.android.synthetic.main.item_home_video.view.*

class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


class HomeAdapter(list:List<File>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mediacontroller: MediaController? = null
    private var videoUri: Uri? = null

    val fileList = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            1 -> return PictureViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_home_picture,
                    parent,
                    false
                ) as LinearLayout
            )
            2 -> return VideoViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_home_video,
                    parent,
                    false
                ) as LinearLayout
            )
        }
        return PictureViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            1 -> {
                val picture = fileList[position]
                val pictureHolder = holder as PictureViewHolder
                pictureHolder.itemView.home_imageview.setImageBitmap(BitmapFactory.decodeFile(picture.path))
            }
            2 -> {
                val video = fileList[position]
                val videoHolder = holder as VideoViewHolder
                val uriPath = video.path
                videoUri = Uri.fromFile(java.io.File(uriPath))

                val vv = videoHolder.itemView.home_videoView

                vv.setMediaController(mediacontroller)
                vv.setVideoURI(videoUri)
                vv.requestFocus()
                vv.start()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return fileList[position].type
    }

}
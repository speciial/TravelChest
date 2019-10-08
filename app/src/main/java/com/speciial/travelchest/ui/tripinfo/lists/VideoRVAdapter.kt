package com.speciial.travelchest.ui.tripinfo.lists

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.model.File
import com.speciial.travelchest.ui.tripinfo.TripInfoFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class VideoRVAdapter(private val videoList: List<File>, private val fileClickListener: TripInfoFragment.FileClickListener, private val context: Context) : RecyclerView.Adapter<VideoRVAdapter.VideoRVViewHolder>() {

    class VideoRVViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val image: ImageView = item.findViewById(R.id.grid_image)
        val cardView : MaterialCardView = item.findViewById(R.id.grid_image_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_info_video, parent, false) as FrameLayout

        return VideoRVViewHolder(view)
    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: VideoRVViewHolder, position: Int) {
        doAsync {

            val mMMR = MediaMetadataRetriever()
            mMMR.setDataSource(context, Uri.parse(videoList[position].path))
            val bitmap = mMMR.frameAtTime

            val scaledBitmap = bitmap!!.scale(bitmap.width / 4, bitmap.height / 4, false)
            Log.d(MainActivity.TAG, "Bitmap created")

            uiThread {
                holder.image.setImageBitmap(scaledBitmap)
            }
        }
        holder.cardView.setOnClickListener {
            fileClickListener.onFileClicked(videoList[position])
        }
    }
    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            else
                mediaMetadataRetriever.setDataSource(videoPath)
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

}
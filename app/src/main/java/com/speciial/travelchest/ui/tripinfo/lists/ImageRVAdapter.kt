package com.speciial.travelchest.ui.tripinfo.lists

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.speciial.travelchest.MainActivity.Companion.TAG
import com.speciial.travelchest.R
import com.speciial.travelchest.model.File
import com.speciial.travelchest.ui.tripinfo.TripInfoFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ImageRVAdapter(private val imageList: List<File>, private val fileClickListener: TripInfoFragment.FileClickListener, private val context: Context) : RecyclerView.Adapter<ImageRVAdapter.ImageRVViewHolder>() {

    class ImageRVViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val image: ImageView = item.findViewById(R.id.grid_image)
        val cardView : MaterialCardView = item.findViewById(R.id.grid_image_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_info_image, parent, false) as FrameLayout

        return ImageRVViewHolder(view)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ImageRVViewHolder, position: Int) {
        doAsync {
            //val bitmap = BitmapFactory.decodeFile(imageList[position].path)
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(imageList[position].path))
            val scaledBitmap = bitmap.scale(bitmap.width / 4, bitmap.height / 4, false)
            Log.d(TAG, "Bitmap created")

            uiThread {
                holder.image.setImageBitmap(scaledBitmap)
            }
        }
        holder.cardView.setOnClickListener {
            fileClickListener.onFileClicked(imageList[position])
        }
    }

}
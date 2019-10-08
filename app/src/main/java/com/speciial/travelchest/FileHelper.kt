package com.speciial.travelchest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.net.HttpURLConnection
import java.net.URL


object FileHelper {
    fun getBitmapFromPath(context: Context, path:String): Bitmap {
        var bitmap:Bitmap
        if(!path.startsWith("http"))
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(path))
        else
        {
            val url = URL(path)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            bitmap = BitmapFactory.decodeStream(input)
        }
        return bitmap
    }
    fun getThumbnailFromVideoPath(context:Context,path: String):Bitmap{
        val mMMR = MediaMetadataRetriever()
        mMMR.setDataSource(context, Uri.parse(path))
        return mMMR.frameAtTime
    }
}
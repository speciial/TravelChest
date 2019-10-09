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

    /*private fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap{
        val rotationMatrix = Matrix()
        rotationMatrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotationMatrix, true)
    }

    private fun adjustImageOrientation(inputStream: InputStream): Bitmap {
        val exif = ExifInterface(inputStream)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        val tempBitmap = BitmapFactory.decodeStream(inputStream)

        val result = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                Log.d("IMAGE_ROTATION", "90")
                rotateBitmap(tempBitmap, 90.0f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                Log.d("IMAGE_ROTATION", "180")
                rotateBitmap(tempBitmap, 180.0f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                Log.d("IMAGE_ROTATION", "270")
                rotateBitmap(tempBitmap, 270.0f)
            }
            else -> {
                Log.d("IMAGE_ROTATION", "0")
                tempBitmap
            }
        }

        return result
    }*/

    fun getBitmapFromPath(context: Context, path: String): Bitmap {
        val bitmap: Bitmap
        if (!path.startsWith("http")) {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(path))
        }
        else {
            val url = URL(path)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            bitmap = BitmapFactory.decodeStream(input)
        }
        return bitmap
    }

    fun getThumbnailFromVideoPath(context: Context, path: String): Bitmap {
        val mMMR = MediaMetadataRetriever()
        mMMR.setDataSource(context, Uri.parse(path))
        return mMMR.frameAtTime
    }

}
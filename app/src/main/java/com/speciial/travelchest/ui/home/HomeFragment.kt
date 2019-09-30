package com.speciial.travelchest.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.Location
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_VIDEO_CAPTURE = 2
    var mCurrentPhotoPath: String = ""
    var mCurrentVideoPath: String = ""
    private var lastLocation: android.location.Location? = null
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        root.findViewById<RecyclerView>(R.id.home_recyclerview).layoutManager = LinearLayoutManager(activity as MainActivity)



        getLastLocation()

        val db = TravelChestDatabase.get(activity as MainActivity)

        doAsync {
            val tab = db.fileDao().getAll()
            uiThread {
                tab.observe(activity as MainActivity, Observer {
                    it.forEach {
                        Log.e("DBG_FILE",it.toString())
                    }
                    root.findViewById<RecyclerView>(R.id.home_recyclerview).adapter = HomeAdapter(it)
                })

            }



        }
        root.findViewById<Button>(R.id.home_picture).setOnClickListener{
            buttonPictureListener()
        }
        root.findViewById<Button>(R.id.home_video).setOnClickListener{
            buttonVideoListener()
        }

        return root
    }


    fun buttonPictureListener(){
        getLastLocation()
        val fileName= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm:s"))
        val imgPath= activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var imageFile= File.createTempFile(fileName, ".jpg", imgPath)
        mCurrentPhotoPath= imageFile!!.absolutePath
        val photoURI: Uri = FileProvider.getUriForFile(activity as MainActivity,
            "com.speciial.travelchest.ui.home",
            imageFile)
        val myIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(myIntent.resolveActivity(activity!!.packageManager) != null) {
            myIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(myIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun buttonVideoListener(){
        getLastLocation()
        val fileName= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm:s"))
        val imgPath= activity?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        var imageFile= File.createTempFile(fileName, ".webm", imgPath)
        mCurrentVideoPath= imageFile!!.absolutePath
        val videoURI: Uri = FileProvider.getUriForFile(activity as MainActivity,
            "com.speciial.travelchest.ui.home",
            imageFile)
        val myIntent= Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if(myIntent.resolveActivity(activity!!.packageManager) != null) {
            myIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI )
            startActivityForResult(myIntent, REQUEST_VIDEO_CAPTURE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, recIntent: Intent?) {
        val db = TravelChestDatabase.get(activity as MainActivity)
        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode== Activity.RESULT_OK) {
            doAsync {
                val id = db.fileDao().insert(
                    com.speciial.travelchest.model.File(0, 1, mCurrentPhotoPath, Location(0, lastLocation!!.latitude, lastLocation!!.longitude))
                )
            }
        }
        if(requestCode== REQUEST_VIDEO_CAPTURE && resultCode== Activity.RESULT_OK) {
            doAsync {
                val id = db.fileDao().insert(
                    com.speciial.travelchest.model.File(0, 2, mCurrentVideoPath, Location(0, lastLocation!!.latitude, lastLocation!!.longitude))
                )
            }
        }
    }

    private fun getLastLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)
        locationClient.lastLocation.addOnCompleteListener(activity as MainActivity) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result

            }
        }
    }
}
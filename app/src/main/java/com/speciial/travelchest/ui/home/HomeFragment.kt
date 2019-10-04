package com.speciial.travelchest.ui.home

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.MainActivity.Companion.TAG
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.Location
import com.speciial.travelchest.model.Type
import com.speciial.travelchest.ui.home.audio.AudioDialog
import com.speciial.travelchest.ui.home.audio.PlayAudio
import com.speciial.travelchest.ui.home.audio.Record
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment(), TripCardAdapter.TripCardListener {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_VIDEO_CAPTURE = 2
    }

    private lateinit var cardViewPager: ViewPager
    private lateinit var cardViewTabs: TabLayout

    private var mCurrentPhotoPath: String = ""
    private var mCurrentVideoPath: String = ""
    private var mCurrentSoundPath: String = ""

    private var imageFile:File ?= null
    private var movieFile:File ?= null
    private var soundFile:File ?= null

    private var lastLocation: android.location.Location? = null
    private lateinit var locationClient: FusedLocationProviderClient

    private var mThreadRecord:Thread ?= null
    private var mThreadPlay:Thread ?= null
    private var record:Record ?= null

    private var currentUser: FirebaseUser ?= null
    private lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage

    private var db:TravelChestDatabase ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        currentUser = auth.currentUser

        getLastLocation()

        db = TravelChestDatabase.get(activity as MainActivity)

        doAsync {
            val fileListLiveData = db!!.fileDao().getAll()
            uiThread {
                fileListLiveData.observe(activity as MainActivity, Observer { fileList ->
                    fileList.forEach {file ->
                        Log.e("DBG_FILE",file.toString())
                    }
                })
            }
        }
        root.findViewById<ImageButton>(R.id.home_picture).setOnClickListener{
            buttonPictureListener()
        }
        root.findViewById<ImageButton>(R.id.home_video).setOnClickListener{
            buttonVideoListener()
        }
        root.findViewById<ImageButton>(R.id.home_audio).setOnClickListener{
            buttonAudioListener()
        }

        cardViewPager = root.findViewById(R.id.home_card_pager)
        cardViewPager.adapter = TripCardAdapter(activity!!.supportFragmentManager, this)
        cardViewPager.setCurrentItem(1, false)
        cardViewTabs = root.findViewById(R.id.home_card_pager_tabs)
        cardViewTabs.setupWithViewPager(cardViewPager, true)

        return root
    }

    override fun onTripCardClick(cardIndex: Int) {
        when(cardIndex){
            0 -> {
                // TODO: create new trip
                Log.d(TAG, "Create new trip")
            }
            else -> {
                Log.d(TAG, "Look at trip $cardIndex")

                findNavController().navigate(R.id.nav_trip_info)
            }
        }
    }

    private fun buttonPictureListener(){

        getLastLocation()
        val fileName= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm:s"))
        val imgPath= activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        imageFile= File(imgPath.toString()+"/" + fileName + ".jpg")
        mCurrentPhotoPath= imageFile!!.absolutePath
        val photoURI: Uri = FileProvider.getUriForFile(activity as MainActivity,
            "com.speciial.travelchest.ui.home",
            imageFile!!)
        val myIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(myIntent.resolveActivity(activity!!.packageManager) != null) {
            myIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(myIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun buttonVideoListener(){
        getLastLocation()
        val fileName= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm:s"))
        val imgPath= activity?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        movieFile= File(imgPath.toString()+"/" + fileName + ".webm")
        mCurrentVideoPath= movieFile!!.absolutePath
        val videoURI: Uri = FileProvider.getUriForFile(activity as MainActivity,
            "com.speciial.travelchest.ui.home",
            movieFile!!)
        val myIntent= Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if(myIntent.resolveActivity(activity!!.packageManager) != null) {
            myIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI )
            startActivityForResult(myIntent, REQUEST_VIDEO_CAPTURE)
        }
    }

    private fun buttonAudioListener(){
        val dialog = AudioDialog(
            activity as MainActivity,
            "Record an audio",
            "Press the start button to record, Press stop when you have finished"
        )
        val storageDir= activity?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        dialog.startButton!!.setOnClickListener {
            record = Record(activity as MainActivity)
            mThreadRecord= Thread(record)
            mThreadRecord!!.start()
        }
        dialog.stopButton!!.setOnClickListener {
            if(record != null)
                record!!.stopRecord()
        }
        dialog.playButton!!.setOnClickListener {
            try {
                val fileName= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm:s"))
                soundFile = File(storageDir.toString() + "/" + fileName + ".raw")
                mCurrentSoundPath = soundFile!!.absolutePath

                val inputStream = FileInputStream(soundFile!!)
                val myRunnable = PlayAudio(inputStream)
                mThreadPlay = Thread(myRunnable)
                mThreadPlay!!.start()
            } catch (ex: IOException) {
                Toast.makeText(activity as MainActivity, "You should first record an audio", Toast.LENGTH_LONG).show()
            }
        }
        dialog.saveButton!!.setOnClickListener {

            try {
                if(soundFile == null)
                    Toast.makeText(activity as MainActivity, "No audio saved", Toast.LENGTH_LONG).show()
                else {
                    dialog.dismiss()

                    if(currentUser != null){
                        upload(Type.SOUND, soundFile!!)
                    } else {
                        doAsync {
                            db!!.fileDao().insert(
                                com.speciial.travelchest.model.File(0, 3, mCurrentSoundPath, Location(0, lastLocation!!.latitude, lastLocation!!.longitude)
                                )
                            )
                        }
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(activity as MainActivity, "You should first record an audio", Toast.LENGTH_LONG).show()
            }

        }
        dialog.cancelButton!!.setOnClickListener {

            if(soundFile !=null){
                try {
                    soundFile!!.delete()
                } catch (e:Exception){

                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, recIntent: Intent?) {

        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode== Activity.RESULT_OK) {
            if(currentUser != null){
                upload(Type.PICTURE, imageFile!!)
            } else {
                doAsync {
                    db!!.fileDao().insert(
                        com.speciial.travelchest.model.File(0, 1, mCurrentPhotoPath, Location(0, lastLocation!!.latitude, lastLocation!!.longitude))
                    )
                }
            }

        }

        if(requestCode== REQUEST_VIDEO_CAPTURE && resultCode== Activity.RESULT_OK) {
            if(currentUser != null){
                upload(Type.VIDEO,movieFile!!)
            } else {
                doAsync {
                    db!!.fileDao().insert(
                        com.speciial.travelchest.model.File(0, 2, mCurrentVideoPath, Location(0, lastLocation!!.latitude, lastLocation!!.longitude)
                        )
                    )
                }
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




    private fun upload(type:Int, fileToDelete:File){
        var typeString = "null"
        var path = "null"

        when(type){
            Type.PICTURE -> {
                typeString = Type.PICTURE_STRING
                path = mCurrentPhotoPath
            }
            Type.VIDEO -> {
                typeString = Type.VIDEO_STRING
                path = mCurrentVideoPath
            }
            Type.SOUND -> {
                typeString = Type.SOUND_STRING
                path = mCurrentSoundPath
            }
        }
        val storageRef = storage.reference
        val file = Uri.fromFile(File(path))
        val pictureRef = storageRef.child(currentUser!!.uid + "/${typeString}s/${file.lastPathSegment}")
        val uploadTask = pictureRef.putFile(file)
        val progressDoalog = createProgressDialog(typeString)
        uploadTask.addOnSuccessListener {
            Log.e(MainActivity.TAG, "$typeString success upload")
        }
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                progressDoalog.progress = progress.toInt()
            }.addOnPausedListener {

            }
            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation pictureRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    progressDoalog.dismiss()
                    fileToDelete.delete()
                    doAsync {
                        db!!.fileDao().insert(
                            com.speciial.travelchest.model.File(0, type, downloadUri.toString(), Location(0, lastLocation!!.latitude, lastLocation!!.longitude))
                        )
                    }
                }
            }
    }

    private fun createProgressDialog(type:String):ProgressDialog{
        val progressDoalog = ProgressDialog(activity as MainActivity)
        progressDoalog.max = 100
        progressDoalog.setMessage("Uploading $type..")
        progressDoalog.setTitle("Upload $type to the cloud")
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDoalog.show()
        return progressDoalog
    }
}
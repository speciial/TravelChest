package com.speciial.travelchest.ui.home

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
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
import com.speciial.travelchest.MainActivity.Companion.PREF_NAME
import com.speciial.travelchest.MainActivity.Companion.TAG
import com.speciial.travelchest.PreferenceHelper.customPreference
import com.speciial.travelchest.PreferenceHelper.save_online
import com.speciial.travelchest.PreferenceHelper.tripId
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


class HomeFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_VIDEO_CAPTURE = 2
        private const val READ_REQUEST_CODE = 42
    }

    private lateinit var cardViewPager: ViewPager
    private lateinit var cardViewTabs: TabLayout

    private var mCurrentPhotoPath: Uri ?= null
    private var mCurrentVideoPath: Uri ?= null
    private var mCurrentSoundPath: Uri ?= null

    private lateinit var imageFile: File
    private lateinit var movieFile: File
    private lateinit var soundFile: File

    private var lastLocation: android.location.Location? = null
    private lateinit var locationClient: FusedLocationProviderClient

    private var mThreadRecord: Thread? = null
    private var mThreadPlay: Thread? = null
    private var record: Record? = null

    private var currentUser: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage

    private lateinit var db: TravelChestDatabase
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        currentUser = auth.currentUser


        prefs = customPreference(activity as MainActivity, PREF_NAME)
        getLastLocation()

        db = TravelChestDatabase.get(activity as MainActivity)

        doAsync {
            val tripListLiveData = db.tripDao().getAll()
            val fileListLiveData = db.fileDao().getAll()
            uiThread {
                tripListLiveData.observe(activity as MainActivity, Observer { fileList ->
                    fileList.forEach { trip ->
                        Log.e("DBG_TRIP", trip.toString())

                    }
                })
                fileListLiveData.observe(activity as MainActivity, Observer { fileList ->
                    fileList.forEach { file ->
                        Log.e("DBG_FILE", file.toString())

                    }
                })
            }
        }
        root.findViewById<ImageButton>(R.id.home_picture).setOnClickListener {
            buttonPictureListener()
        }
        root.findViewById<ImageButton>(R.id.home_video).setOnClickListener {
            buttonVideoListener()
        }
        root.findViewById<ImageButton>(R.id.home_audio).setOnClickListener {
            buttonAudioListener()
        }
        root.findViewById<ImageButton>(R.id.home_ar).setOnClickListener {
            findNavController().navigate(R.id.nav_ar)
        }
        root.findViewById<ImageButton>(R.id.home_file).setOnClickListener {
            buttonFileListener()
        }

        setupTripCards(root)


        return root
    }

    private fun setupTripCards(root: View) {
        cardViewPager = root.findViewById(R.id.home_card_pager)
        doAsync {
            val tripListLiveDate = db.tripDao().getAll()
            uiThread {
                tripListLiveDate.observe(activity as MainActivity, Observer { tripList ->
                    cardViewPager.adapter = TripCardAdapter(
                        it.childFragmentManager,
                        tripList
                    )
                    if (tripList.isNotEmpty()) {
                        cardViewPager.setCurrentItem(1, false)
                    }
                })
            }
        }

        // Setting up the dots at the bottom of the view pager
        cardViewTabs = root.findViewById(R.id.home_card_pager_tabs)
        cardViewTabs.setupWithViewPager(cardViewPager, true)
    }

    private fun buttonPictureListener() {

        getLastLocation()
        imageFile = createFile(Type.IMAGE)

        val photoURI: Uri = FileProvider.getUriForFile(
            activity as MainActivity,
            "com.speciial.travelchest.ui.home",
            imageFile
        )
        mCurrentPhotoPath = photoURI
        val myIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (myIntent.resolveActivity(activity!!.packageManager) != null) {
            myIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(myIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun buttonVideoListener() {
        getLastLocation()
        movieFile = createFile(Type.VIDEO)

        val videoURI: Uri = FileProvider.getUriForFile(
            activity as MainActivity,
            "com.speciial.travelchest.ui.home",
            movieFile
        )
        mCurrentVideoPath = videoURI
        val myIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (myIntent.resolveActivity(activity!!.packageManager) != null) {
            myIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
            startActivityForResult(myIntent, REQUEST_VIDEO_CAPTURE)
        }
    }

    private fun buttonAudioListener() {
        val dialog = AudioDialog(
            activity as MainActivity,
            "Record an audio",
            "Press the start button to record, Press stop when you have finished"
        )
        var recFile:File ?= null
        val recFileName= "temprecord.raw"
        val storageDir= activity?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

        dialog.startButton!!.setOnClickListener {
            record = Record(activity as MainActivity)
            mThreadRecord = Thread(record)
            mThreadRecord!!.start()
        }

        dialog.stopButton!!.setOnClickListener {
            if (record != null)
                record!!.stopRecord()
        }

        dialog.playButton!!.setOnClickListener {
            try {
                recFile = File(storageDir.toString() + "/" + recFileName)
                val inputStream = FileInputStream(recFile)
                val myRunnable = PlayAudio(inputStream)
                mThreadPlay = Thread(myRunnable)
                mThreadPlay!!.start()
            } catch (ex: IOException) {
                Toast.makeText(
                    activity as MainActivity,
                    "You should first record an audio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        dialog.saveButton!!.setOnClickListener {

            try {
                recFile = File(storageDir.toString() + "/" + recFileName)
                if (recFile == null)
                    Toast.makeText(
                        activity as MainActivity,
                        "No audio saved",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    soundFile = createFile(Type.AUDIO)
                    val audioURI: Uri = FileProvider.getUriForFile(
                        activity as MainActivity,
                        "com.speciial.travelchest.ui.home",
                        soundFile
                    )
                    mCurrentSoundPath = audioURI
                    recFile?.copyTo(soundFile)
                    recFile?.delete()
                    dialog.dismiss()
                    save(Type.AUDIO, soundFile, mCurrentSoundPath!!)
                }
            } catch (e: IOException) {
                Toast.makeText(
                    activity as MainActivity,
                    "You should first record an audio",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        dialog.cancelButton!!.setOnClickListener {

            if (soundFile != null) {
                try {
                    soundFile.delete()
                } catch (e: Exception) {

                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun buttonFileListener() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, recIntent: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            save(Type.IMAGE, imageFile, mCurrentPhotoPath!!)
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            save(Type.VIDEO, movieFile, mCurrentVideoPath!!)

        }
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            recIntent?.data?.also { uri ->
                Log.i(TAG, "Uri: $uri")
                val file = File(uri.path!!)
                var type = 0
                if (file.name.startsWith("image"))
                    type = Type.IMAGE
                if (file.name.startsWith("video"))
                    type = Type.VIDEO
                if (file.name.startsWith("audio"))
                    type = Type.AUDIO
                saveFile(type, uri)


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

    private fun createFile(type: Int): File {
        val fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm:s"))
        var imgPath: File? = null
        var ext = ""
        when (type) {
            Type.IMAGE -> {
                imgPath = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ext = ".jpg"
            }
            Type.VIDEO -> {
                imgPath = activity?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                ext = ".webm"
            }
            Type.AUDIO -> {
                imgPath = activity?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                ext = ".raw"
            }
        }
        return File(imgPath.toString() + "/" + fileName + ext)
    }


    private fun save(type: Int, file: File, path: Uri) {

        if (currentUser != null && prefs.save_online)
            upload(type, file)
        else
            saveFile(type, path)
    }


    private fun saveFile(type: Int, path: Uri) {
        doAsync {
            val trip = db.tripDao().get(prefs.tripId)
            if(trip.getFilesByType(Type.IMAGE).isEmpty() && type == Type.IMAGE)
                trip.pathThumbnail = path.toString()
            trip.fileList.add(com.speciial.travelchest.model.File(
                0,
                type,
                path.toString(),
                Location(lastLocation!!.latitude, lastLocation!!.longitude)
            ))
            db.tripDao().update(trip)
        }
    }


    private fun upload(type: Int, fileToDelete: File) {
        var typeString = "null"
        var path:Uri ?= null

        when (type) {
            Type.IMAGE -> {
                typeString = Type.PICTURE_STRING
                path = mCurrentPhotoPath!!
            }
            Type.VIDEO -> {
                typeString = Type.VIDEO_STRING
                path = mCurrentVideoPath!!
            }
            Type.AUDIO -> {
                typeString = Type.SOUND_STRING
                path = mCurrentSoundPath!!
            }
        }
        val storageRef = storage.reference
        val pictureRef =
            storageRef.child(currentUser!!.uid + "/${typeString}s/${path!!.lastPathSegment}")
        val uploadTask = pictureRef.putFile(path)
        val progressDoalog = createProgressDialog(typeString)
        uploadTask.addOnSuccessListener {
            Log.e(TAG, "$typeString success upload")
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
                    saveFile(type, downloadUri!!)
                }
            }
    }

    private fun createProgressDialog(type: String): ProgressDialog {
        val progressDoalog = ProgressDialog(activity as MainActivity)
        progressDoalog.max = 100
        progressDoalog.setMessage("Uploading $type..")
        progressDoalog.setTitle("Upload $type to the cloud")
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDoalog.show()
        return progressDoalog
    }


}
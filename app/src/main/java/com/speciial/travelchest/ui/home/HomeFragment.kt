package com.speciial.travelchest.ui.home

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.*
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.Location
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_VIDEO_CAPTURE = 2
    var mCurrentPhotoPath: String = ""
    var mCurrentVideoPath: String = ""
    private var lastLocation: android.location.Location? = null
    private lateinit var locationClient: FusedLocationProviderClient

    private var recRunning:Boolean = false
    private var mThreadRecord:Thread ?= null
    private var mThreadPlay:Thread ?= null
    private var currentUser: FirebaseUser ?= null
    private lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage


    private var imageFile:File ?= null
    private var  movieFile:File ?= null

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
        root.findViewById<RecyclerView>(R.id.home_recyclerview).layoutManager = LinearLayoutManager(activity as MainActivity)

        getLastLocation()

        db = TravelChestDatabase.get(activity as MainActivity)

        doAsync {
            val fileListLiveData = db!!.fileDao().getAll()
            uiThread {_ ->
                fileListLiveData.observe(activity as MainActivity, Observer { fileList ->
                    fileList.forEach {file ->
                        Log.e("DBG_FILE",file.toString())

                    }
                    //root.findViewById<RecyclerView>(R.id.home_recyclerview).adapter = HomeAdapter(fileList)
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

        return root
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
        val dialog = AudioDialog(activity as MainActivity, "Record an audio","Press the start button to record, Press stop when you have finished")
        var recFile:File ?= null
        val recFileName= "temprecord.raw"
        val storageDir= activity?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        dialog.startButton!!.setOnClickListener {
            val myRunnable= Record()
            mThreadRecord= Thread(myRunnable)
            mThreadRecord!!.start()
        }
        dialog.stopButton!!.setOnClickListener {
            recRunning = false
        }
        dialog.playButton!!.setOnClickListener {
            try {
                recFile = File(storageDir.toString() + "/" + recFileName)
                val inputStream = FileInputStream(recFile!!)
                val myRunnable = PlayAudio(inputStream)
                mThreadPlay = Thread(myRunnable)
                mThreadPlay!!.start()
            } catch (ex: IOException) {
                Toast.makeText(activity as MainActivity, "You should first record an audio", Toast.LENGTH_LONG).show()
            }
        }
        dialog.saveButton!!.setOnClickListener {
            val fileName= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm:s"))
            try {
                if(recFile == null)
                    Toast.makeText(activity as MainActivity, "No audio saved", Toast.LENGTH_LONG).show()
                else {
                    dialog.dismiss()
                    val filesave = File(storageDir.toString() + "/" + fileName + ".raw")
                    recFile?.copyTo(filesave)
                    recFile?.delete()
                    if(currentUser != null){
                        val storageRef = storage.reference
                        val file = Uri.fromFile(File(mCurrentPhotoPath))
                        val pictureRef = storageRef.child(currentUser!!.uid + "/images/${file.lastPathSegment}")
                        val uploadTask = pictureRef.putFile(file)
                        val progressDoalog = createProgressDialog("sound")
                        uploadTask.addOnSuccessListener {
                            Log.e(MainActivity.TAG, "Sound success upload")
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
                                    filesave.delete()
                                    doAsync {
                                        db!!.fileDao().insert(
                                            com.speciial.travelchest.model.File(0, 3, downloadUri.toString(), Location(0, lastLocation!!.latitude, lastLocation!!.longitude))
                                        )
                                    }
                                }
                            }
                    } else {
                        doAsync {
                            db!!.fileDao().insert(
                                com.speciial.travelchest.model.File(0, 3, recFile!!.path, Location(0, lastLocation!!.latitude, lastLocation!!.longitude)
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

            if(recFile !=null){
                try {
                    recFile!!.delete()
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
                val storageRef = storage.reference
                val file = Uri.fromFile(File(mCurrentPhotoPath))
                val pictureRef = storageRef.child(currentUser!!.uid + "/images/${file.lastPathSegment}")
                val uploadTask = pictureRef.putFile(file)
                val progressDoalog = createProgressDialog("picture")
                uploadTask.addOnSuccessListener {
                    Log.e(MainActivity.TAG, "Image success upload")
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
                            imageFile!!.delete()
                            doAsync {
                                db!!.fileDao().insert(
                                    com.speciial.travelchest.model.File(0, 1, downloadUri.toString(), Location(0, lastLocation!!.latitude, lastLocation!!.longitude))
                                )
                            }
                        }
                    }
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
                val storageRef = storage.reference
                val file = Uri.fromFile(File(mCurrentVideoPath))
                val pictureRef = storageRef.child(currentUser!!.uid + "/movies/${file.lastPathSegment}")
                val uploadTask = pictureRef.putFile(file)
                val progressDoalog = createProgressDialog("movie")
                uploadTask.addOnSuccessListener {
                    Log.e(MainActivity.TAG, "Video success upload")
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
                            movieFile!!.delete()
                            doAsync {
                                db!!.fileDao().insert(
                                    com.speciial.travelchest.model.File(0, 2, downloadUri.toString(), Location(0, lastLocation!!.latitude, lastLocation!!.longitude))
                                )
                            }
                        }
                    }
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



    private inner class Record: Runnable{
        override fun run() {
            recRunning = true
            var recFile:File ?= null

            val recFileName= "temprecord.raw"
            val storageDir= activity?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            try{
                recFile = File(storageDir.toString() + "/"+ recFileName)
            } catch(ex: IOException) {
            }
            try {
                val outputStream = FileOutputStream(recFile!!)
                val bufferedOutputStream = BufferedOutputStream(outputStream)
                val dataOutputStream = DataOutputStream(bufferedOutputStream)
                val minBufferSize= AudioRecord.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT)
                val aFormat= AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
                val recorder= AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(aFormat)
                    .setBufferSizeInBytes(minBufferSize)
                    .build()
                val audioData= ByteArray(minBufferSize)
                recorder.startRecording()
                while(recRunning) {
                    val numofBytes= recorder.read(audioData, 0, minBufferSize)
                    if(numofBytes>0) {
                        dataOutputStream.write(audioData)
                    }
                }
                recorder.stop()
                dataOutputStream.close()
            } catch(e: IOException) {
                //e.printStackTrace()
            }
        }

    }
    private inner class PlayAudio(val inputStream: InputStream):Runnable{
        override fun run() {
            val track1: AudioTrack?
            val minBufferSize= AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT)
            val aBuilder= AudioTrack.Builder()
            val aAttr: AudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val aFormat: AudioFormat= AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(44100)
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .build()
            track1 = aBuilder.setAudioAttributes(aAttr)
                .setAudioFormat(aFormat)
                .setBufferSizeInBytes(minBufferSize)
                .build()
            track1.setVolume(10.0f)
            track1.play()
            var i:Int
            val buffer= ByteArray(minBufferSize)
            try{
                i = inputStream.read(buffer, 0, minBufferSize)
                while(i != -1) {
                    track1.write(buffer, 0, i)
                    i = inputStream.read(buffer, 0, minBufferSize)
                }
            } catch(e: IOException) {
                e.printStackTrace()
            }
            try{
                //inputStream.close()
            } catch(e: IOException) {
                e.printStackTrace()
            }
            track1.stop()
            track1.release()
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
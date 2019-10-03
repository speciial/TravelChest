package com.speciial.travelchest.ui.home.audio

import android.media.*
import android.os.Environment
import com.speciial.travelchest.MainActivity
import java.io.*





class Record(private val activity:MainActivity): Runnable{
    private var recRunning: Boolean = false


    fun stopRecord(){

    }
    override fun run() {
        recRunning = true
        var recFile: File?= null

        val recFileName= "temprecord.raw"
        val storageDir= activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
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

class PlayAudio(val inputStream: InputStream):Runnable{
    override fun run() {
        val track1: AudioTrack?
        val minBufferSize= AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT)
        val aBuilder= AudioTrack.Builder()
        val aAttr: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val aFormat: AudioFormat = AudioFormat.Builder()
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
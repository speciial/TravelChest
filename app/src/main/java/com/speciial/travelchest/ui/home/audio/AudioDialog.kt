package com.speciial.travelchest.ui.home.audio

import android.app.Activity
import android.app.Dialog
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.speciial.travelchest.R


class AudioDialog(activity: Activity, title: String, subTitle:String) : Dialog(activity) {

    var startButton:Button? = null
    var stopButton:Button? = null
    var playButton:Button? = null
    var saveButton:Button? = null
    var cancelButton:Button? = null

    init {
        setContentView(R.layout.dialog_audio)
        findViewById<TextView>(R.id.audio_title).text = title
        findViewById<TextView>(R.id.audio_subtitle).text = subTitle
        startButton = findViewById(R.id.audio_start)
        stopButton = findViewById(R.id.audio_stop)
        saveButton = findViewById(R.id.audio_save)
        playButton = findViewById(R.id.audio_play)
        cancelButton = findViewById(R.id.audio_cancel)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp


    }
}
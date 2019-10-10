package com.speciial.travelchest.ui.ar.renderables

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.scale
import com.google.android.material.button.MaterialButton
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.rendering.ViewSizer
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.speciial.travelchest.FileHelper
import com.speciial.travelchest.R
import com.speciial.travelchest.model.Trip

class ARBillboard(
    private val context: Context,
    transformationSystem: TransformationSystem,
    private val anchorNode: Node,
    private val trip: Trip,
    private val billboardEventListener: BillboardEventListener
) {

    var placementNode: TransformableNode = TransformableNode(transformationSystem)

    private lateinit var renderable: ViewRenderable

    private var isVisible: Boolean = true

    init {
        val temp = LayoutInflater.from(context)
        val view = temp.inflate(R.layout.default_billboard_view, null)
        fillView(view)

        val cardWidthInMeterScaled = (250.0f / 250.0f) * 0.3f
        val cardHeightInMeterScaled = (190.0f / 250.0f) * 0.3f

        ViewRenderable.builder()
            .setView(context, view)
            .build()
            .thenAccept {
                renderable = it
                renderable.sizer = ViewSizer {
                    Vector3(cardWidthInMeterScaled, cardHeightInMeterScaled, 1.0f)
                }
                renderable.horizontalAlignment = ViewRenderable.HorizontalAlignment.CENTER
                renderable.verticalAlignment = ViewRenderable.VerticalAlignment.BOTTOM

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                toggleVisibility()

                placementNode.worldRotation = Quaternion.eulerAngles(Vector3(0.0f, 0.0f, 0.0f))
                placementNode.localPosition = Vector3(0.0f, 0.15f, 0.0f)
            }
    }

    private fun fillView(view: View) {
        var bitmap: Bitmap? = null
        try {
            bitmap = FileHelper.getBitmapFromPath(context, trip.pathThumbnail)
        } catch (e: Exception) {
        }

        if (bitmap != null) {
            val bitmapScaled = bitmap.scale(bitmap.width / 4, bitmap.height / 4, false)
            view.findViewById<ImageView>(R.id.ar_card_thumbnail).setImageBitmap(bitmapScaled)
        }

        view.findViewById<TextView>(R.id.ar_card_title).text = trip.name
        view.findViewById<TextView>(R.id.ar_card_subtitle).text = trip.tripCiy
        view.findViewById<TextView>(R.id.ar_card_date).text =
            context.getString(R.string.date_template, trip.startDate, trip.endDate)

        view.findViewById<MaterialButton>(R.id.ar_card_show_trip).setOnClickListener {
            billboardEventListener.onTripInfoButtonClick(trip.uid)
        }
    }

    fun adjustOrientation(camPos: Vector3) {
        // TODO: implement the billboard rotation correctly
        //
        //      Note: We've been trying to make this work
        //      but for some reason the initially calculated
        //      rotation sometimes creates weird angles, so
        //      we decided to not implement it.
        val bb = placementNode.worldPosition

        val lookDir = Vector3.subtract(bb, camPos)
        val lookRot = Quaternion.lookRotation(lookDir, Vector3.up())

        placementNode.localRotation = lookRot
    }

    fun toggleVisibility() {
        if (isVisible) {
            placementNode.renderable = null
            isVisible = false
        } else {
            placementNode.renderable = renderable
            isVisible = true
        }
    }

    interface BillboardEventListener {
        // Go to trip info view
        fun onTripInfoButtonClick(tripID: Long)
    }

}
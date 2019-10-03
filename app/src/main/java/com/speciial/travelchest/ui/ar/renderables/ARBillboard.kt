package com.speciial.travelchest.ui.ar.renderables

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.widget.ImageView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.rendering.ViewSizer
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.speciial.travelchest.R

class ARBillboard(
    context: Context,
    transformationSystem: TransformationSystem,
    anchorNode: Node
) {

    var placementNode: TransformableNode = TransformableNode(transformationSystem)

    private var isVisibil: Boolean = true

    private lateinit var renderable: ViewRenderable

    init {
        // TODO(@speciial): adjust the position of the billboard so that
        //                  it floats on top of the marker

        val temp = LayoutInflater.from(context)
        val view = temp.inflate(R.layout.default_billboard_view, null)

        val input = context.assets.open("images/wallpaper.jpg")
        view.findViewById<ImageView>(R.id.iv_thumbnail)
            .setImageBitmap(BitmapFactory.decodeStream(input))

        val cardWidthInMeterScaled = (250.0f / 250.0f) * 0.2f
        val cardHeightInMeterScaled = (190.0f / 250.0f) * 0.2f

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

                // placementNode.scaleController.

                placementNode.worldRotation = Quaternion.eulerAngles(Vector3(0.0f, 0.0f, 0.0f))
                placementNode.localPosition = Vector3(0.0f, 0.15f, 0.0f)
            }
    }

    fun adjustOrientation(camPos: Vector3) {
        // TODO: improve the rotation
        val bb = placementNode.worldPosition

        val lookDir = Vector3.subtract(bb, camPos)
        val lookRot = Quaternion.lookRotation(lookDir, Vector3.up())

        placementNode.localRotation = lookRot
    }

    fun toggleVisibility() {
        // TODO(@speciial): this is more or less a hack and should be improved
        if (isVisibil) {
            placementNode.renderable = null
            isVisibil = false
        } else {
            placementNode.renderable = renderable
            isVisibil = true
        }
    }

}
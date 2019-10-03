package com.speciial.travelchest.ui.ar.renderables

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.speciial.travelchest.MainActivity.Companion.TAG

class ARMarker(
    context: Context,
    transformationSystem: TransformationSystem,
    anchorNode: Node
) {

    var placementNode: TransformableNode = TransformableNode(transformationSystem)

    private lateinit var renderable: ModelRenderable

    private var eventListener: MarkerEventListener? = null

    init {
        ModelRenderable.builder()
            .setSource(context, Uri.parse("models/marker.sfb"))
            .build()
            .thenAccept {
                renderable = it

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                placementNode.scaleController.isEnabled = false
                placementNode.setOnTapListener { _, _ ->
                    if (eventListener != null) {
                        Log.d(TAG, "Marker Clicked")
                        eventListener!!.onMarkerClick(0, placementNode)
                    }
                }
            }
    }

    fun setEventListener(el: MarkerEventListener) {
        eventListener = el
    }

    fun updateTranslation(pos: Vector3) {
        placementNode.localPosition = pos
    }

    fun adjustOrientation(from: Vector3, to: Vector3) {
        placementNode.localRotation = Quaternion.rotationBetweenVectors(from, to)
    }

    fun scale(factor: Float) {
        placementNode.localScale = Vector3(factor, factor, factor)
    }

    interface MarkerEventListener {
        fun onMarkerClick(markerID: Int, node: Node)
    }
}
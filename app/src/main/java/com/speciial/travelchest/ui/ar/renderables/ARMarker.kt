package com.speciial.travelchest.ui.ar.renderables

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

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
            .setSource(context, Uri.parse("marker.sfb"))
            .build()
            .thenAccept {
                renderable = it

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                placementNode.scaleController.isEnabled = false
                placementNode.setOnTapListener { hitTestResult, motionEvent ->
                    if (eventListener != null) {
                        eventListener!!.onMarkerClick(0, placementNode)
                    }
                }
            }
    }

    fun setEventListener(el: MarkerEventListener) {
        eventListener = el
    }

    fun updateTranslation(pos: Vector3) {
        placementNode.localPosition = pos.normalized().scaled(0.95f)
        placementNode.localScale = Vector3(0.9f, 0.9f, 0.9f)
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
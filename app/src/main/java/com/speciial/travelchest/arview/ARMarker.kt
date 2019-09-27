package com.speciial.travelchest.arview

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
    anchorNode: Node,
    name: String
) {

    var placementNode: TransformableNode = TransformableNode(transformationSystem)

    private lateinit var renderable: ModelRenderable

    init {
        ModelRenderable.builder()
            .setSource(context, Uri.parse("marker.sfb"))
            .build()
            .thenAccept {
                renderable = it

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                placementNode.scaleController.minScale = 1.0f
                placementNode.name = name
            }
    }

    fun translate(x: Float, y: Float, z: Float) {
        placementNode.localPosition = Vector3(x, y, z)
    }

    fun rotate(rot: Quaternion){
        placementNode.localRotation = rot
    }

    fun scale(factor: Float) {
        placementNode.localScale = Vector3(factor, factor, factor)
    }

}
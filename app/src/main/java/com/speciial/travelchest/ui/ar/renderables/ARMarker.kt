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

    init {
        ModelRenderable.builder()
            .setSource(context, Uri.parse("marker.sfb"))
            .build()
            .thenAccept {
                renderable = it

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                placementNode.scaleController.minScale = 1.0f
            }
    }

    fun updateTranslation(pos: Vector3) {
        placementNode.localPosition = pos
    }

    fun adjustOrientation(from: Vector3, to: Vector3){
        placementNode.localRotation = Quaternion.rotationBetweenVectors(from, to)
    }

    fun scale(factor: Float) {
        placementNode.localScale = Vector3(factor, factor, factor)
    }

}
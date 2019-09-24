package com.speciial.travelchest.arview

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class ARGlobe(
    context: Context,
    transformationSystem: TransformationSystem,
    anchorNode: Node
) {

    var placementNode: TransformableNode = TransformableNode(transformationSystem)

    private lateinit var renderable: ModelRenderable

    init {
        ModelRenderable.builder()
            .setSource(context, Uri.parse("earth_10x10.sfb"))
            .build()
            .thenAccept {
                renderable = it

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                // placementNode.localScale = Vector3(0.01f, 0.01f, 0.01f)
                placementNode.worldScale= Vector3(0.01f, 0.01f, 0.01f)
            }
    }

    fun translatePosition(x: Float, y: Float, z: Float) {
        placementNode.localPosition = Vector3(x, y, z)
    }

}
package com.speciial.travelchest.arview

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.speciial.travelchest.MainActivity.Companion.TAG

class ARGlobe(
    context: Context,
    transformationSystem: TransformationSystem,
    anchorNode: Node,
    name: String
) {

    var placementNode: TransformableNode = TransformableNode(transformationSystem)
    var currentScaleValue = 1.0f

    private lateinit var renderable: ModelRenderable

    init {
        ModelRenderable.builder()
            .setSource(context, Uri.parse("sphere_10x10.sfb"))
            .build()
            .thenAccept {
                renderable = it

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                placementNode.scaleController.minScale = 1.0f
                placementNode.scaleController.maxScale = 2.0f
                placementNode.name = name

                placementNode.addTransformChangedListener { new, old ->
                    if (new.name == old.name) {
                        if (new.localScale.x != currentScaleValue) {
                            Log.d(TAG, new.localScale.toString())
                            currentScaleValue = new.localScale.x
                        }
                    }
                }
            }
    }

    fun translatePosition(x: Float, y: Float, z: Float) {
        placementNode.localPosition = Vector3(x, y, z)
    }

    fun scale(factor: Float) {
        placementNode.localScale = Vector3(factor, factor, factor)
    }

    companion object{
        const val DEFAULT_RADIUS = 0.1f // 5cm radius for the sphere model
    }

}
package com.speciial.travelchest.arview

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ar.sceneform.Node
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
            .setSource(context, Uri.parse("sphere_100x100.sfb"))
            .build()
            .thenAccept {
                renderable = it

                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
                placementNode.scaleController.isEnabled = false
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

    companion object{
        const val DEFAULT_RADIUS = 0.5f // 50cm radius for the sphere model
    }

}
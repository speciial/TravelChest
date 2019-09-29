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
        // TODO(@speciial): optimize loading the model.
        //                  texture seems to big and takes long time to display
        ModelRenderable.builder()
            .setSource(context, Uri.parse("globe_textured_8k.sfb"))
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

                Log.d(TAG, placementNode.worldPosition.toString())
                Log.d(TAG, placementNode.localPosition.toString())
            }
    }

    companion object {
        const val DEFAULT_RADIUS = 0.5f // 50cm radius for the sphere model
    }

}
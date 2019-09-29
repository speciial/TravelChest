package com.speciial.travelchest.ui.ar.renderables

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class ARGlobe(
    context: Context,
    transformationSystem: TransformationSystem,
    anchorNode: Node
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

                /*
                TODO(@speciial): add the ability to scale the globe back in
                placementNode.addTransformChangedListener { new, old ->
                    if (new.name == old.name) {
                        if (new.localScale.x != currentScaleValue) {
                            Log.d(TAG, new.localScale.toString())
                            currentScaleValue = new.localScale.x
                        }
                    }
                }
                 */
            }
    }

    companion object {
        const val DEFAULT_RADIUS = 0.5f // 50cm radius for the sphere model
    }

}
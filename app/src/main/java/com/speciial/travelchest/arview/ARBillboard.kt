package com.speciial.travelchest.arview

import android.content.Context
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R

class ARBillboard(
    context: Context,
    transformationSystem: TransformationSystem,
    anchorNode: Node
) {

    var placementNode: TransformableNode = TransformableNode(transformationSystem)

    private lateinit var renderable: ViewRenderable

    init {
        ViewRenderable.builder()
            .setView(context, R.layout.default_billboard_view)
            .build()
            .thenAccept{
                renderable = it
                placementNode.setParent(anchorNode)
                placementNode.renderable = renderable
            }
    }

}
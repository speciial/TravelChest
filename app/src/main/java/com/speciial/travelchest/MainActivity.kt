package com.speciial.travelchest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import com.speciial.travelchest.arview.ARBillboard

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    private lateinit var billboard: ARBillboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            Log.d(TAG, "Plane tapped!")

            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(arFragment.arSceneView.scene)

            billboard = ARBillboard(this, arFragment.transformationSystem, anchorNode,this)
        }
    }

    companion object {
        const val TAG = "TRAVEL_CHEST"
    }

}

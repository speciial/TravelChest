package com.speciial.travelchest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import com.speciial.travelchest.arview.ARGlobe
import com.speciial.travelchest.arview.ARMarker


class MainActivity : AppCompatActivity() {

    /*
    TODO(@speciial):
        [ ] create a small marker to place
        [ ] scale the globe correctly
     */
    private lateinit var arFragment: ArFragment

    private lateinit var globe: ARGlobe
    private lateinit var tinyGlobe: ARMarker

    private lateinit var locationClient: FusedLocationProviderClient

    private var x: Float = 0.0f
    private var y: Float = 0.0f
    private var z: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionChecker.checkPermissions(this)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            Log.d(TAG, "Plane tapped!")

            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(arFragment.arSceneView.scene)

            globe = ARGlobe(arFragment.context!!, arFragment.transformationSystem, anchorNode, "globe")
            tinyGlobe =
                ARMarker(arFragment.context!!, arFragment.transformationSystem, globe.placementNode, "tinyglobe")
            // TODO(@speciial): Check if a new location was set before changing the translation
            tinyGlobe.translatePosition(x, y + (ARGlobe.DEFAULT_RADIUS * globe.currentScaleValue), z)
            tinyGlobe.scale(0.1f)
        }

    /*
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient.lastLocation.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                val lat = task.result!!.latitude
                val lng = task.result!!.longitude

                // TODO(@speciial): check if this conversion is right!
                x = (cos(lng) * sin(lat)).toFloat()
                y = (sin(lng) * sin(lat)).toFloat()
                z = cos(lat).toFloat()

                Log.d(TAG, "X: $x, Y: $y, Z: $z")
            }
        }
    */
    }

    companion object {
        const val TAG = "TRAVEL_CHEST"
    }

}

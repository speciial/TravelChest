package com.speciial.travelchest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.ux.ArFragment
import com.speciial.travelchest.arview.ARGlobe
import com.speciial.travelchest.arview.ARMarker

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    private lateinit var globe: ARGlobe
    private lateinit var marker: ARMarker

    private lateinit var locationClient: FusedLocationProviderClient

    private var x: Float = 0.0f
    private var y: Float = 0.0f
    private var z: Float = 0.0f
    private var rot: Quaternion? = null

    private fun calcMarkerRotation() {
        // TODO(@speciial): rotation calculation is wrong but I'm to lazy to
        //                  find out how to do it correctly now
        val p1 = globe.placementNode.localPosition
        val p2 = marker.placementNode.localPosition

        rot = Quaternion.lookRotation(p2, p1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionChecker.checkPermissions(this)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            Log.d(TAG, "Plane tapped!")

            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(arFragment.arSceneView.scene)

            globe =
                ARGlobe(arFragment.context!!, arFragment.transformationSystem, anchorNode, "globe")
            marker =
                ARMarker(
                    arFragment.context!!,
                    arFragment.transformationSystem,
                    globe.placementNode,
                    "marker"
                )

            // TODO(@speciial): Check if a new location was set before changing the translation
            // TODO(@speciial): Fix the offset on the y axis, subtract the height of the marker
            marker.translate(x, (y + ARGlobe.DEFAULT_RADIUS), z)
        }

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient.lastLocation.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {

                // converting lat and lng to radians
                val lat = task.result!!.latitude * (kotlin.math.PI / 180)
                val lng = task.result!!.longitude * (kotlin.math.PI / 180)

                // calculating the xzy coordinates on the unit sphere
                x = (kotlin.math.cos(lat) * kotlin.math.cos(lng)).toFloat() / 2
                y = (kotlin.math.cos(lat) * kotlin.math.sin(lng)).toFloat() / 2
                z = kotlin.math.sin(lat).toFloat() / 2

                Log.d(TAG, "X: $x, Y: $y, Z: $z")
            }
        }
    }

    companion object {
        const val TAG = "TRAVEL_CHEST"
    }

}

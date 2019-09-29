package com.speciial.travelchest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import com.speciial.travelchest.arview.ARGlobe
import com.speciial.travelchest.arview.ARMarker

class MainActivity : AppCompatActivity() {

    // TODO(speciial): Clean up all the imported models and move to code
    //                 to their own functions and classes

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
        val globeCenter= Vector3(0.0f, 0.5f, 0.0f)
        val markerPos = marker.placementNode.localPosition

        val rotDir = Vector3.subtract(globeCenter, markerPos)
        rot = Quaternion.axisAngle(rotDir, 0.0f)
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
            marker.translate(x, (y + ARGlobe.DEFAULT_RADIUS), z)

            calcMarkerRotation()
            marker.rotate(rot!!)
        }

        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient.lastLocation.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {

                // converting lat and lng to radians
                val lat = task.result!!.latitude.toFloat() //  * kotlin.math.PI / 180.0f
                val lng = task.result!!.longitude.toFloat()//  * kotlin.math.PI / 180.0f

                val phi = ((90 - lat) * (kotlin.math.PI / 180.0f)).toFloat()
                val theta = ((lng + 180) * (kotlin.math.PI / 180.0f)).toFloat()

                // calculating the xzy coordinates on the unit sphere
                x = -(0.5f * kotlin.math.sin(phi) * kotlin.math.cos(theta))
                y = 0.5f * kotlin.math.cos(phi)
                z = (0.5f * kotlin.math.sin(phi) * kotlin.math.sin(theta))


                Log.d(TAG, "Lat: ${task.result!!.latitude},Lng: ${task.result!!.longitude}")
                Log.d(TAG, "X: $x, Y: $y, Z: $z")
            }
        }
    }

    companion object {
        const val TAG = "TRAVEL_CHEST"
    }

}

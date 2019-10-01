package com.speciial.travelchest.ui.ar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.MainActivity.Companion.TAG
import com.speciial.travelchest.R
import com.speciial.travelchest.ui.ar.renderables.ARBillboard
import com.speciial.travelchest.ui.ar.renderables.ARGlobe
import com.speciial.travelchest.ui.ar.renderables.ARMarker


class ArFragment : Fragment(), ARMarker.MarkerEventListener {

    private lateinit var arFragment: ArFragment

    private var earthAnchorNode: AnchorNode? = null

    private var globe: ARGlobe? = null
    private var marker: ARMarker? = null
    private var billboard: ARBillboard? = null

    private lateinit var locationClient: FusedLocationProviderClient
    private var locationInCoordSpace: Vector3? = null

    private fun getLastLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)
        locationClient.lastLocation.addOnCompleteListener(activity as MainActivity) { task ->
            if (task.isSuccessful && task.result != null) {

                val lat = task.result!!.latitude
                val lng = task.result!!.longitude

                // calculating the xzy coordinates on a sphere with radius = 0.5m
                val phi = (90 - lat) * (kotlin.math.PI / 180.0f)
                val theta = (lng + 180) * (kotlin.math.PI / 180.0f)

                val x = -(0.5 * kotlin.math.sin(phi) * kotlin.math.cos(theta))
                val y = (0.5 * kotlin.math.cos(phi)) + ARGlobe.DEFAULT_RADIUS
                val z = (0.5 * kotlin.math.sin(phi) * kotlin.math.sin(theta))
                locationInCoordSpace = Vector3(x.toFloat(), y.toFloat(), z.toFloat())

                // TODO(@speciial): clean up
                Log.d(TAG, "Lat: ${task.result!!.latitude},Lng: ${task.result!!.longitude}")
                Log.d(TAG, "X: $x, Y: $y, Z: $z")
            }
        }
    }

    override fun onMarkerClick(markerID: Int, node: Node) {
        // TODO(@speciial): figure out which marker called this and add a billboard
        Log.d(TAG, "Marker $markerID has been clicked")

        if (billboard == null) {
            billboard = ARBillboard(arFragment.context!!, arFragment.transformationSystem, node)
            billboard!!.adjustOrientation(arFragment.arSceneView.scene.camera.worldPosition)
        } else {
            billboard!!.toggleVisibility()
            billboard!!.adjustOrientation(arFragment.arSceneView.scene.camera.worldPosition)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ar, container, false)

        getLastLocation()

        arFragment = childFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            earthAnchorNode = AnchorNode(hitResult.createAnchor())
            earthAnchorNode!!.setParent(arFragment.arSceneView.scene)

            globe =
                ARGlobe(
                    arFragment.context!!,
                    arFragment.transformationSystem,
                    earthAnchorNode!!
                )
            marker =
                ARMarker(
                    arFragment.context!!,
                    arFragment.transformationSystem,
                    globe!!.placementNode
                )
            marker!!.setEventListener(this)

            // TODO(@speciial): Move the location tracking out to its own provider and
            //                  only place the marker when a location is available
            if (locationInCoordSpace != null) {
                marker!!.updateTranslation(locationInCoordSpace!!)
                marker!!.adjustOrientation(Vector3(0.0f, 0.5f, 0.0f), locationInCoordSpace!!)
            }
        }

        return root
    }
}
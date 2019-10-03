package com.speciial.travelchest.ui.ar

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
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

    private fun convertLocationToXYZ(lat: Double, lng: Double) {
        // calculating the xzy coordinates on a sphere with radius = 0.5m
        val phi = (90 - lat) * (kotlin.math.PI / 180.0f)
        val theta = (lng + 180) * (kotlin.math.PI / 180.0f)

        val x = -(kotlin.math.sin(phi) * kotlin.math.cos(theta))
        val y = (kotlin.math.cos(phi))
        val z = (kotlin.math.sin(phi) * kotlin.math.sin(theta))
        locationInCoordSpace = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
        locationInCoordSpace = locationInCoordSpace!!.normalized().scaled(ARGlobe.DEFAULT_RADIUS - 0.005f)
        locationInCoordSpace!!.y += ARGlobe.DEFAULT_RADIUS

        // TODO(@speciial): Clean up
        Log.d(TAG, "X: $x, Y: $y, Z: $z")
    }

    private fun getLocationFromCity(cityName: String) {
        val coder = Geocoder(activity)
        val address: List<Address>

        try {
            address = coder.getFromLocationName(cityName, 5)
            if (address == null) {
                return
            }
            for (a in address) {
                Log.d(TAG, a.toString())
            }

            convertLocationToXYZ(address[0].latitude, address[0].longitude)
        } catch (e: Exception) {
            // LOG
        }
    }

    private fun getLastLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)
        locationClient.lastLocation.addOnCompleteListener(activity as MainActivity) { task ->
            if (task.isSuccessful && task.result != null) {
                convertLocationToXYZ(task.result!!.latitude, task.result!!.longitude)

                // TODO(@speciial): clean up
                Log.d(TAG, "Lat: ${task.result!!.latitude},Lng: ${task.result!!.longitude}")
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

    fun onUpdate(frameTime: FrameTime) {
        // TODO(@speciial): Why is it so stupidly hard to persist a scene
        //                  through a orientation change event??
        // arFragment.arSceneView.arFrame!!.

    }

    override fun onResume() {
        super.onResume()

        arFragment.arSceneView.session!!.resume()
    }

    override fun onPause() {
        super.onPause()

        arFragment.arSceneView.session!!.pause()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ar, container, false)

        // getLastLocation()
        getLocationFromCity("Miami")

        arFragment = childFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        var session = Session(context)
        var config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        session.configure(config)

        arFragment.arSceneView.setupSession(session)

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            earthAnchorNode = AnchorNode(hitResult.createAnchor())
            earthAnchorNode!!.setParent(arFragment.arSceneView.scene)

            arFragment.arSceneView.session!!.createAnchor(earthAnchorNode!!.anchor!!.pose)

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
        arFragment.arSceneView.scene.addOnUpdateListener {
            onUpdate(it)
        }

        return root
    }
}
package com.speciial.travelchest.ui.ar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.ui.ar.renderables.ARGlobe
import com.speciial.travelchest.ui.ar.renderables.ARMarker


class ArFragment : Fragment(), ARMarker.MarkerEventListener {

    private lateinit var arFragment: ArFragment

    private var earthAnchorNode: AnchorNode? = null

    private var globe: ARGlobe? = null

    private fun placeTripMarker() {
        val tripList = TravelChestDatabase.get(activity as MainActivity).tripDao().getAll()
        tripList.observe(activity as MainActivity, Observer {
            for (trip in it) {
                val position = convertLocationToXYZ(trip.location.latitude, trip.location.longitude)

                Log.d(TAG, trip.toString())

                val marker =
                    ARMarker(
                        arFragment.context!!,
                        arFragment.transformationSystem,
                        globe!!.placementNode,
                        trip
                    )
                marker.setEventListener(this@ArFragment)
                marker.updateTranslation(position)
                marker.adjustOrientation(Vector3(0.0f, 0.5f, 0.0f), position)
            }
        })

    }

    private fun convertLocationToXYZ(lat: Double, lng: Double): Vector3 {
        // calculating the xzy coordinates on a sphere with radius = 0.5m
        val phi = (90 - lat) * (kotlin.math.PI / 180.0f)
        val theta = (lng + 180) * (kotlin.math.PI / 180.0f)

        val x = -(kotlin.math.sin(phi) * kotlin.math.cos(theta))
        val y = (kotlin.math.cos(phi))
        val z = (kotlin.math.sin(phi) * kotlin.math.sin(theta))

        val result = Vector3(x.toFloat(), y.toFloat(), z.toFloat()).normalized()
            .scaled(ARGlobe.DEFAULT_RADIUS - 0.005f)
        result.y += ARGlobe.DEFAULT_RADIUS

        Log.d(TAG, "X: $x, Y: $y, Z: $z")

        return result
    }

    override fun onMarkerClick(markerID: Int, node: Node) {
        Log.d(TAG, "Marker $markerID has been clicked")
    }

    private fun onUpdate(frameTime: FrameTime) {
        // TODO(@speciial): Why is it so stupidly hard to persist a scene
        //                  through a orientation change event??
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

        arFragment = childFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        val session = Session(context)
        val config = Config(session)
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

            placeTripMarker()
        }
        arFragment.arSceneView.scene.addOnUpdateListener {
            onUpdate(it)
        }

        return root
    }
}
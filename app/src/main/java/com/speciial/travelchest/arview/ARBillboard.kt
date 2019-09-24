package com.speciial.travelchest.arview

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.MainActivity.Companion.TAG
import com.speciial.travelchest.R

class ARBillboard(
    context: Context,
    transformationSystem: TransformationSystem,
    anchorNode: AnchorNode,
    activity: MainActivity
): OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG,"Map")
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        placementNode.renderable = renderable
    }

    private var placementNode: TransformableNode = TransformableNode(transformationSystem)

    private lateinit var renderable: ViewRenderable

    init {
        ViewRenderable.builder()
            .setView(context, R.layout.map_fragment)
            .build()
            .thenAccept{
                renderable = it
                placementNode.setParent(anchorNode)
                val mapFragment = activity.supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
    }

}
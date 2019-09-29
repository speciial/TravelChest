package com.speciial.travelchest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import com.speciial.travelchest.arview.ARGlobe
import com.speciial.travelchest.arview.ARMarker
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var arFragment: ArFragment

    private var globe: ARGlobe? = null
    private var marker: ARMarker? = null

    private lateinit var locationClient: FusedLocationProviderClient

    private var locationInCoordSpace: Vector3? = null

    private fun setupARView() {
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(arFragment.arSceneView.scene)

            globe =
                ARGlobe(arFragment.context!!, arFragment.transformationSystem, anchorNode)
            marker =
                ARMarker(
                    arFragment.context!!,
                    arFragment.transformationSystem,
                    globe!!.placementNode
                )
            if(locationInCoordSpace != null) {
                marker!!.updateTranslation(locationInCoordSpace!!)
                marker!!.adjustOrientation(Vector3(0.0f, 0.5f, 0.0f), locationInCoordSpace!!)
            }
        }
    }

    private fun getLastLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient.lastLocation.addOnCompleteListener(this) { task ->
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

                // TODO(@speciial): remove!
                Log.d(TAG, "Lat: ${task.result!!.latitude},Lng: ${task.result!!.longitude}")
                Log.d(TAG, "X: $x, Y: $y, Z: $z")
            }
        }
    }
  
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_ar
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        const val TAG = "TRAVEL_CHEST"
    }

}

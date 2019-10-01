package com.speciial.travelchest

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionChecker.checkPermissions(this)

        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_ar, R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        val currentUser = auth.currentUser
        updateUI(currentUser)
        super.onStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        const val TAG = "TRAVEL_CHEST"
    }
    private fun updateUI(user: FirebaseUser?) {
        val header = (findViewById<View>(R.id.nav_view) as NavigationView).getHeaderView(0)
        if (user != null) {
            header.findViewById<TextView>(R.id.nav_title).text = user.displayName
            header.findViewById<TextView>(R.id.nav_subtitle).text = user.email
            Glide.with(this).load(user.photoUrl).override(200, 200).apply(RequestOptions.circleCropTransform()).into(header.findViewById<ImageView>(R.id.nav_imageView))
        } else {
            header.findViewById<TextView>(R.id.nav_title).setText(R.string.not_connected)
            header.findViewById<TextView>(R.id.nav_subtitle).text = null
            header.findViewById<ImageView>(R.id.nav_imageView).setImageResource(android.R.color.transparent)

        }
    }





}

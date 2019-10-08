package com.speciial.travelchest

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.speciial.travelchest.PreferenceHelper.customPreference
import com.speciial.travelchest.PreferenceHelper.dark_theme
import com.speciial.travelchest.PreferenceHelper.save_online
import com.speciial.travelchest.model.File
import com.speciial.travelchest.model.Type
import com.speciial.travelchest.ui.ar.renderables.ARBillboard
import com.speciial.travelchest.ui.home.TripCardAdapter
import com.speciial.travelchest.ui.tripinfo.TripInfoFragment


class MainActivity : AppCompatActivity(), TripCardAdapter.TripCardListener, TripInfoFragment.FileClickListener, ARBillboard.BillboardEventListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var prefs:SharedPreferences
    private lateinit var navController:NavController

    companion object {
        const val TAG = "TRAVEL_CHEST"
        const val PREF_NAME = "TRAVEL_CHEST_PREF"
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         prefs = customPreference(this, PREF_NAME)

        when(prefs.dark_theme){
            true -> setTheme(R.style.DarkTheme_NoActionBar)
            false -> setTheme(R.style.AppTheme_NoActionBar)
        }

        PermissionChecker.checkPermissions(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        findViewById<SignInButton>(R.id.profile_signIn).setOnClickListener {
            signIn()
        }
        findViewById<Button>(R.id.profile_signOut).setOnClickListener {
            signOut()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_ar, R.id.nav_preferences
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onTripInfoButtonClick(tripID: Long) {
        val bundle = bundleOf((Pair("tripID", tripID)))
        findNavController(R.id.nav_host_fragment).navigate(R.id.nav_trip_info, bundle)
    }

    override fun onTripCardClick(tripID: Long) {
        when (tripID) {
            (-1L) -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.nav_trip_add)
                Log.d(TAG, "Create new trip")
            }
            else -> {
                Log.d(TAG, "Look at trip $tripID")

                val bundle = bundleOf((Pair("tripID", tripID)))
                findNavController(R.id.nav_host_fragment).navigate(R.id.nav_trip_info, bundle)
            }
        }
    }

    override fun onFileClicked(file: File) {
        Log.d(TAG, file.toString())
        when(file.type){
            Type.IMAGE -> {
                val bundle = Bundle()
                bundle.putString("path",file.path)
                navController.navigate(R.id.nav_image_viewer,bundle)
            }
            Type.VIDEO -> {
                val bundle = Bundle()
                bundle.putString("path",file.path)
                navController.navigate(R.id.nav_video_viewer,bundle)
            }

        }

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


    private fun updateUI(user: FirebaseUser?) {
        val header = (findViewById<View>(R.id.nav_view) as NavigationView).getHeaderView(0)
        if (user != null) {
            findViewById<SignInButton>(R.id.profile_signIn).visibility = View.GONE
            findViewById<Button>(R.id.profile_signOut).visibility = View.VISIBLE
            header.findViewById<TextView>(R.id.nav_title).text = user.displayName
            header.findViewById<TextView>(R.id.nav_subtitle).text = user.email
            Glide.with(this).load(user.photoUrl).override(200, 200).apply(RequestOptions.circleCropTransform()).into(header.findViewById<ImageView>(R.id.nav_imageView))
        } else {
            findViewById<SignInButton>(R.id.profile_signIn).visibility = View.VISIBLE
            findViewById<Button>(R.id.profile_signOut).visibility = View.GONE
            header.findViewById<TextView>(R.id.nav_title).setText(R.string.not_connected)
            header.findViewById<TextView>(R.id.nav_subtitle).text = null
            header.findViewById<ImageView>(R.id.nav_imageView).setImageResource(android.R.color.transparent)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase

                prefs.save_online = true
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Authentication Failed.", Toast.LENGTH_SHORT ).show()
                    updateUI(null)
                }

            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        prefs.save_online = false
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }





}

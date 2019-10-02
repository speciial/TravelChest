package com.speciial.travelchest.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity as MainActivity, gso)

        auth = FirebaseAuth.getInstance()
        root.findViewById<SignInButton>(R.id.profile_signIn).setOnClickListener {
            signIn()
        }
        root.findViewById<Button>(R.id.profile_signOut).setOnClickListener {
            signOut()
        }



        return root
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(activity as MainActivity,"Authentication Failed.", Toast.LENGTH_SHORT ).show()
                    updateUI(null)
                }

            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(activity as MainActivity) {
            updateUI(null)
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(activity as MainActivity) {
            updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val header = (activity!!.findViewById<View>(R.id.nav_view) as NavigationView).getHeaderView(0)
        if (user != null) {
            view!!.findViewById<TextView>(R.id.profile_name).text = user.displayName

            view!!.findViewById<SignInButton>(R.id.profile_signIn).visibility = View.GONE
            view!!.findViewById<Button>(R.id.profile_signOut).visibility = View.VISIBLE
            header.findViewById<TextView>(R.id.nav_title).text = user.displayName
            header.findViewById<TextView>(R.id.nav_subtitle).text = user.email
            //val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, user.photoUrl)

            Glide.with(this).load(user.photoUrl).override(200, 200).apply(RequestOptions.circleCropTransform()).into(header.findViewById<ImageView>(R.id.nav_imageView))
        } else {
            view!!.findViewById<TextView>(R.id.profile_name).setText(R.string.signed_out)

            view!!.findViewById<SignInButton>(R.id.profile_signIn).visibility = View.VISIBLE
            view!!.findViewById<Button>(R.id.profile_signOut).visibility = View.GONE
            header.findViewById<TextView>(R.id.nav_title).setText(R.string.not_connected)
            header.findViewById<TextView>(R.id.nav_subtitle).text = null
            header.findViewById<ImageView>(R.id.nav_imageView).setImageResource(android.R.color.transparent)
        }
    }


    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

}
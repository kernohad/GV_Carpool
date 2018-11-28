package com.example.dndMobile.gvcarpool

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_feed_detail.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.math.sign

class LoginActivity : AppCompatActivity() {
    // Reference to firebase authenticator
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize firebase reference
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener{ _ ->
            // TODO: Make and call firebase login function. For now, just launch main activity
            signIn()
        }

        registerLabel.setOnClickListener { _ ->
            // Make intent to the register activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            auth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful){
                            //Sign in success. Start app
                            val currentUser: FirebaseUser = auth?.currentUser!!
                            launchApp(currentUser)
                        }else{
                            Snackbar.make(loginRoot, "Authentication Failed", Snackbar.LENGTH_SHORT).show()
                        }
                    }
        }else{
            Snackbar.make(loginRoot, "Please fill out both fields.", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly
        if(auth?.currentUser != null) {
            val currentUser: FirebaseUser = auth?.currentUser!!
            launchApp(currentUser)
        }
    }

    private fun launchApp(user: FirebaseUser){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

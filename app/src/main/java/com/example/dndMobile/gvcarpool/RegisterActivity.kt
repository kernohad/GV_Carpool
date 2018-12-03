package com.example.dndMobile.gvcarpool

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    // Reference to firebase authenticator
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize firebase reference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        registerButton.setOnClickListener { _ ->
            createUser()
        }
    }

    private fun createUser(){
        val fullName = fullNameField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        val confirmPassword = confirmPasswordField.text.toString()

        // Check that fields are not empty
        if(!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)){

            // Check that passwords are the same
            if(password == confirmPassword){
                // Fields all validated, create the user
                auth!!
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val currentUser: FirebaseUser = auth?.currentUser!!
                                val userId = currentUser.uid

                                //Verify Email
                                verifyEmail()

                                //update user profile information
                                val currentUserDb = databaseReference!!.child(userId)
                                currentUserDb.child("fullName").setValue(fullName)
                                currentUserDb.child("bio").setValue("This is your bio")
                                currentUserDb.child("commonDep").setValue("GVSU")
                                currentUserDb.child("commonArr").setValue("GVSU")

                                // Go back to login screen while user verifies email
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                            } else {
                                // If sign in fails, display a message to the user.
                                Snackbar.make(registerRoot, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
            }else{
                // Passwords do not match
                Snackbar.make(registerRoot, "Passwords do not match", Snackbar.LENGTH_SHORT).show()

            }
        }else{
            // All fields are not filled out.
            Snackbar.make(registerRoot, "Please fill out all fields.", Snackbar.LENGTH_SHORT).show()

        }
    }

    private fun verifyEmail(){
        val user = auth!!.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,
                                "Verification email sent to " + user.email,
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
}

package com.example.dndMobile.gvcarpool

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    // Reference to firebase authenticator abd DB
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null

    //Database vars
    private var fullName: String? = null
    private var about: String? = null
    private var departure: String? = null
    private var arrival: String? = null
    private var ridesGiven: Int? = null
    private var ridesTaken: Int? = null
    private var photoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Get the extra (DB data) from the intent
        val extras = intent.extras ?: return
        val userId = extras.getString("userId")

       // Use data from DB and set all the fields
        //nameTextView.text = userId

        // Initialize firebase reference
        auth = FirebaseAuth.getInstance()

        // Set DB refernece to Users DB
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        var toast = Toast.makeText(this,"Loading Profile..", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 170)
        toast.show()

        // Get reference to current Users DB
        val userReference = databaseReference!!.child(userId)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.value != null) {
                    fullName = snapshot.child("fullName").value as String
                    about = snapshot.child("bio").value as String
                    departure = snapshot.child("commonDep").value as String
                    arrival = snapshot.child("commonArr").value as String
                    ridesGiven = (snapshot.child("ridesGiven").value as Long).toInt()
                    ridesTaken = (snapshot.child("ridesTaken").value as Long).toInt()
                    photoUrl = snapshot.child("photoUrl").value as String

                    nameTextView.text = fullName
                    aboutEditText.setText(about)
                    departureText.text = departure
                    arrivalText.text = arrival
                    ridesGivenText.text = ridesGiven.toString()
                    ridesTakenText.text = ridesTaken.toString()

                    //if the user has not set a profile picture, don't try to load a non-existent url
                    //note: without this if statement, the app will crash.
                    if(photoUrl != "") {
                        //Picasso to load image.
                        Picasso.get().load(photoUrl).into(profilePicture)
                    }
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }
}

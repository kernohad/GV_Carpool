package com.example.dndMobile.gvcarpool

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_feed_detail.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedDetailActivity : AppCompatActivity() {
    private var databaseReference: DatabaseReference? = null
    private var auth: FirebaseAuth? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_detail)

        // Set DB refernece to Rides DB
        databaseReference = FirebaseDatabase.getInstance().reference.child("Rides")
        auth = FirebaseAuth.getInstance()


        // Get the rideId from the intent and use to get ride info
        val extras = intent.extras ?: return
        val rideId = extras.getString("rideId")

        val rideReference = databaseReference!!.child(rideId!!)

        var type = ""
        var userId = ""
        var seatsAvailable = 0
        var notes = ""

        // Get User name and Profile Picture
        rideReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the User ID for name and picture
                // Set DB reference to Users and get the Users info
                databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

                userId = snapshot.child("user").value as String

                val userReference = databaseReference!!.child(userId)

                // Get User name and Profile Picture
                userReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        nameHeader.text = snapshot.child("fullName").value as String
                        var photoUrl = snapshot.child("photoUrl").value as String

                        //if the user has not set a profile picture, don't try to load a non-existent url
                        //note: without this if statement, the app will crash.
                        if(photoUrl != "") {
                            //Picasso to load image.
                            Picasso.get().load(photoUrl).into(profilePicture)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })

                // Set ride into UI fields with info from DB
                typeField.text = snapshot.child("type").value as String

                type = typeField.text.toString()

                toField.text = snapshot.child("to").value as String
                fromField.text = snapshot.child("from").value as String
                whenField.text = snapshot.child("when").value as String
                totalSeatsField.text = snapshot.child("total_seats").value as String
                seatsAvailableField.text = snapshot.child("seats_available").value as String
                gasMoneyField.text = snapshot.child("gas_money").value as String
                notesField.text = snapshot.child("notes").value as String

                seatsAvailable = (snapshot.child("seats_available").value as String).toInt()

                // Check that the current user has not accepted it already OR no seats available
                databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
                val acceptedReference = databaseReference!!.child(auth!!.currentUser!!.uid).child("accepted_rides")

                acceptedReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // If current user has not accepted the ride, continue.
                        if(snapshot.hasChild(rideId) || seatsAvailable <= 0){
                            // Set accept button background to neutral
                            acceptButton.setBackgroundResource(R.drawable.neutral_background)
                            acceptButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })


                // Set the type background
                if(type == "Offer"){
                    typeField.setBackgroundResource(R.drawable.offer_background)
                }else if(type == "Request"){
                    typeField.setBackgroundResource(R.drawable.request_background)
                }

                // Set activity title
                supportActionBar!!.title = "$type Details"
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })



        // Click listener for the accept button
        acceptButton.setOnClickListener{ _ ->

            // Check that seats are available
            if(seatsAvailable > 0){

                // Check that the current user has not accepted it already
                databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
                val acceptedReference = databaseReference!!.child(auth!!.currentUser!!.uid).child("accepted_rides")

                acceptedReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // If current user has not accepted the ride, continue.
                        if(!snapshot.hasChild(rideId)){
                            // Decrement available seats
                            seatsAvailable--

                            // Save new available seats value to database
                            databaseReference = FirebaseDatabase.getInstance().reference.child("Rides")
                            val rideReference = databaseReference!!.child(rideId)
                            rideReference.child("seats_available").setValue(seatsAvailable.toString())

                            // Save ride to current users accepted_rides list
                            databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
                            val userReference = databaseReference!!.child(auth!!.currentUser!!.uid).child("accepted_rides")
                            userReference.child(rideId).setValue(true)

                            Snackbar.make(feedDetailRoot, "You chose to accept this $type!", Snackbar.LENGTH_SHORT).show()
                        }else{
                            // User has accepted the ride
                            acceptButton.setBackgroundResource(R.drawable.neutral_background)
                            acceptButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })


            }
        }

        // Click listener for profile Picture
        profilePicture.setOnClickListener { _ ->
            // Make intent to Profile Activity
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", userId)

            startActivity(intent)

        }
    }
}

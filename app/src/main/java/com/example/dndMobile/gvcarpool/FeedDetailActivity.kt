package com.example.dndMobile.gvcarpool

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_feed_detail.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedDetailActivity : AppCompatActivity() {
    private var databaseReference: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_detail)

        // Set DB refernece to Rides DB
        databaseReference = FirebaseDatabase.getInstance().reference.child("Rides")

        // Get the rideId from the intent and use to get ride info
        val extras = intent.extras ?: return
        val rideId = extras.getString("rideId")

        val rideReference = databaseReference!!.child(rideId!!)

        var type = ""
        var userId = ""

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
                        //TODO: Add user photo URI to user DB entry and pull it down here
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
            //TODO: Do database logic here to modify the request/offer total seats and available seats.
            //TODO: Also maybe notify user that made the entry? For now, show snackbar.
            Snackbar.make(feedDetailRoot, "You chose to accept this $type!", Snackbar.LENGTH_SHORT).show()

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

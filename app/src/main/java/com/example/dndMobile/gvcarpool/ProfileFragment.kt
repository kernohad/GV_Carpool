package com.example.dndMobile.gvcarpool

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {
    private var root: View? = null   // create a global variable which will hold your layout


    // Reference to firebase authenticator abd DB
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null

    /*******************
     * On Create View
     *******************/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize firebase reference
        auth = FirebaseAuth.getInstance()

        // Set DB refernece to Users DB
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")


        //TODO: Find out of profile belongs to current user or not
        //      Allow edits to profile if so1

        //TODO: Pull profile info from db and populate fields
        //      :Name
        //      :bio
        //      :photo, if exists.  if not, use default
        //      :common routes
        // EXAMPLE of pull info from data passed to fragment and populating field. DB entry will be stored as argument to fragment

        // Get reference to current Users DB
        val currentUser: FirebaseUser = auth?.currentUser!!
        val userId = currentUser.uid
        val userReference = databaseReference!!.child(userId)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nameTextView.text = snapshot.child("fullName").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //TODO: Stop focus when clicked off of bio edit text

        //TODO: Figure out if we want common departures/arrivals to be decided by actual data or user specified

        return root
    }

    /********************
     * On View Created
     *******************/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: Find out of profile belongs to current user or not
        //      Allow edits to profile if so

        //TODO: Pull profile info from db and populate fields
        //      :Name
        //      :bio
        //      :photo, if exists.  if not, use default
        //      :common routes
        // EXAMPLE of pull info from data passed to fragment and populating field. DB entry will be stored as argument to fragment

        // Get reference to current Users DB
        val currentUser: FirebaseUser = auth?.currentUser!!
        val userId = currentUser.uid
        val userReference = databaseReference!!.child(userId)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nameTextView.text = snapshot.child("fullName").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //TODO: Stop focus when clicked off of bio edit text

        //TODO: Figure out if we want common departures/arrivals to be decided by actual data or user specified

        //Is the user editing their profile or nah?
        var editMode = false

        editButton.setOnClickListener{_ ->
            editMode = true

            aboutEditText.isEnabled = true

            saveButton.visibility = View.VISIBLE
            editButton.visibility = View.INVISIBLE

        }

        profilePicture.setOnClickListener{_ ->
            //TODO: image select/take photo intent

        }

        saveButton.setOnClickListener{_ ->
            //TODO: Store changes in firebase
            aboutEditText.isEnabled = false

            saveButton.visibility = View.INVISIBLE
            editButton.visibility = View.INVISIBLE

        }
    }




}

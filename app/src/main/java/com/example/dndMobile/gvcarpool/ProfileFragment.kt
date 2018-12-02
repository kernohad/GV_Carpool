package com.example.dndMobile.gvcarpool

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*

//Request code for photo picker
private const val READ_REQUEST_CODE: Int = 42

class ProfileFragment : Fragment() {
    private var root: View? = null   // create a global variable which will hold your layout



    // Reference to firebase authenticator abd DB
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var profileUpdates: UserProfileChangeRequest? = null;

    //Making sure profile picture was changed
    private var picChanged = false;

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


        return root
    }

    /********************
     * On View Created
     *******************/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Get reference to current Users DB
        val currentUser: FirebaseUser = auth?.currentUser!!
        val userId = currentUser.uid
        val userReference = databaseReference!!.child(userId)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nameTextView.text = snapshot.child("fullName").value as String
                profilePicture.setImageURI(auth!!.currentUser?.photoUrl)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //Disable profile picture onClick
        profilePicture.isEnabled = false


        //TODO: Stop focus when clicked off of bio edit text

        //TODO: Figure out if we want common departures/arrivals to be decided by actual data or user specified

        /**EDIT Button Listener*/
        editButton.setOnClickListener{_ ->

            //Make things editable
            aboutEditText.isEnabled = true
            saveButton.visibility = View.VISIBLE
            editButton.visibility = View.INVISIBLE
            aboutEditText.setBackgroundResource(R.drawable.login_input_box)
            profilePicture.isEnabled = true
            editImage.visibility = View.VISIBLE
        }

        /** PROFILE PICTURE Listener **/
        profilePicture.setOnClickListener{_ ->
            selectImageInAlbum()
        }

        /**SAVE Button Listener */
        saveButton.setOnClickListener{_ ->
            //TODO: Store changes in firebase

            //If the profile picture has changed, update firebase profile
            if(picChanged) {
                auth!!.currentUser?.updateProfile(profileUpdates!!)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }
            }

            //Make things not editable
            aboutEditText.isEnabled = false
            aboutEditText.setBackgroundResource(R.color.transparent)
            saveButton.visibility = View.INVISIBLE
            editButton.visibility = View.VISIBLE
            profilePicture.isEnabled = false
            editImage.visibility = View.INVISIBLE

            var toast = Toast.makeText(context,"Profile Updated!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM, 0, 170)
            toast.show()

            picChanged = false;
        }
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            picChanged = true;
            resultData?.data?.also { uri ->
                Log.i(TAG, "Uri: $uri")
                profilePicture.setImageURI(uri)
                // Create a Storage Ref w/ username
                profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()
            }
        }
    }

}


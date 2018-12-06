package com.example.dndMobile.gvcarpool

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

//Request code for photo picker
private const val READ_REQUEST_CODE: Int = 42

class ProfileFragment : Fragment() {
    private var root: View? = null   // create a global variable which will hold your layout



    // Reference to firebase authenticator abd DB
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var profileUpdates: UserProfileChangeRequest? = null

    //Firebase Storage for profile pictures
//    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var uriForStorage: Uri? = null

    //Database vars
    private var fullName: String? = null
    private var about: String? = null
    private var departure: String? = null
    private var arrival: String? = null
    private var ridesGiven: Int? = null
    private var ridesTaken: Int? = null
    private var photoUrl: String? = ""

    //Making sure profile picture was changed
    private var picChanged = false

    /*******************
     * On Create View
     *******************/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false)


        // Initialize firebase reference
        auth = FirebaseAuth.getInstance()

        // Set DB reference to Users DB
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        // Set storage reference
        storageReference = FirebaseStorage.getInstance().reference

        var toast = Toast.makeText(context,"Loading Profile..", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 170)
        toast.show()

        // Get reference to current Users DB
        val currentUser: FirebaseUser = auth?.currentUser!!
        val userId = currentUser.uid
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
                    departureText.setText(departure)
                    arrivalText.setText(arrival)
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

        //Disable profile picture onClick
        profilePicture.isEnabled = false


        //TODO: Stop focus when clicked off of bio edit text

        /**EDIT Button Listener*/
        editButton.setOnClickListener{_ ->

            //Make things editable
            aboutEditText.isEnabled = true
            saveButton.visibility = View.VISIBLE
            editButton.visibility = View.INVISIBLE
            aboutEditText.setBackgroundResource(R.drawable.login_input_box)
            profilePicture.isEnabled = true
            editImage.visibility = View.VISIBLE
            departureText.isEnabled = true
            departureText.setBackgroundResource(R.drawable.login_input_box)
            arrivalText.isEnabled = true
            arrivalText.setBackgroundResource(R.drawable.login_input_box)
        }

        /** PROFILE PICTURE Listener **/
        profilePicture.setOnClickListener{_ ->
            selectImageInAlbum()
        }

        /**SAVE Button Listener */
        saveButton.setOnClickListener{_ ->

            var waitingForPhoto = false
            //If the profile picture has changed, update firebase profile
            if(picChanged) {
                waitingForPhoto = true
                auth!!.currentUser?.updateProfile(profileUpdates!!)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")

                                //TODO: Upload photo to firebase storage
                                var uploadTask = storageReference!!.child("$userId/profilePicture").putFile(uriForStorage!!)

                                uploadTask.addOnFailureListener {
                                    val toast = Toast.makeText(context,"Photo Upload Failed", Toast.LENGTH_SHORT)
                                    toast.setGravity(Gravity.BOTTOM, 0, 170)
                                    toast.show()
                                    waitingForPhoto = false
                                }.addOnSuccessListener {
                                    val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                        if (!task.isSuccessful) {
                                            task.exception?.let {
                                                throw it
                                            }
                                        }
                                        return@Continuation storageReference!!.child("$userId/profilePicture").downloadUrl
                                    }).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {

                                            //Set the photo uri to the database
                                            val downloadUri = task.result
                                            userReference.child("photoUrl").setValue(downloadUri.toString())
                                        }else {
                                            val toast = Toast.makeText(context,"Photo Upload Failed", Toast.LENGTH_SHORT)
                                            toast.setGravity(Gravity.BOTTOM, 0, 170)
                                            toast.show()
                                        }
                                        waitingForPhoto = false
                                    }
                                }
                            }
                        }
            }

            //busy wait until asynch photo stuff is complete
            //while(waitingForPhoto){}

            userReference.child("bio").setValue(aboutEditText.text.toString())
            userReference.child("commonArr").setValue(arrivalText.text.toString())
            userReference.child("commonDep").setValue(departureText.text.toString())

            //Make things not editable
            aboutEditText.isEnabled = false
            aboutEditText.setBackgroundResource(R.color.transparent)
            saveButton.visibility = View.INVISIBLE
            editButton.visibility = View.VISIBLE
            profilePicture.isEnabled = false
            editImage.visibility = View.INVISIBLE
            departureText.isEnabled = false
            departureText.setBackgroundResource(R.color.transparent)
            arrivalText.isEnabled = false
            arrivalText.setBackgroundResource(R.color.transparent)

            val toast = Toast.makeText(context,"Profile Updated!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM, 0, 170)
            toast.show()

            picChanged = false;
        }
    }

    /**
     * Select an image from the device's album
     */
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
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
                uriForStorage = uri
                profilePicture.setImageURI(uri)
                // Create a Storage Ref w/ username
                profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()

            }

        }
    }

}


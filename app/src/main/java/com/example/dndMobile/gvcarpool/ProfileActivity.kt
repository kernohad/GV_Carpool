package com.example.dndMobile.gvcarpool

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set the fragment to be profile fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.profileFragmentContainer, ProfileFragment())
                .commit()

        //TODO: Use DB entry from intent and create a profileFragment object and pass it the info to set the fields. Or something
    }
}

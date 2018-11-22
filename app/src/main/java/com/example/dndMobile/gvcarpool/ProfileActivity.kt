package com.example.dndMobile.gvcarpool

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Get the extra (DB data) from the intent
        val extras = intent.extras ?: return
        val name = extras.getString("data")

        // Create a new intance of the profile fragment and pass it the data
        //TODO: Right now we only pass it the name as the data. Make this the entire DB object
        val profileFragment = ProfileFragment.newInstance(name)

        // Set the fragment to be profile fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.profileFragmentContainer, profileFragment)
                .commit()

    }
}

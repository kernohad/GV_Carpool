package com.example.dndMobile.gvcarpool

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Get the extra (DB data) from the intent
        val extras = intent.extras ?: return
        val userId = extras.getString("userId")

       // TODO: Use data from DB and set all the fields
        nameTextView.text = userId
    }
}

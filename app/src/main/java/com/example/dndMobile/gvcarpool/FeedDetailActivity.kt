package com.example.dndMobile.gvcarpool

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_feed_detail.*
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_detail)

        // Get the type (offer/request) from the intent and set the typeField
        val extras = intent.extras ?: return
        val type = extras.getString("data")

        // Set the type text
        typeField.text = type

        // Set the type background
        if(type == "Offer"){
            typeField.setBackgroundResource(R.drawable.offer_background)
        }else if(type == "Request"){
            typeField.setBackgroundResource(R.drawable.request_background)
        }

        // Set activity title
        this.supportActionBar!!.title = "$type Details"

        // Click listener for the accept button
        acceptButton.setOnClickListener{ _ ->
            //TODO: Do database logic here to modify the request/offer total seats and available seats.
            //TODO: Also maybe notify user that made the entry? For now, show snackbar.
            Snackbar.make(feedDetailRoot, "You chose to accept this $type!", Snackbar.LENGTH_SHORT).show()

        }
    }
}

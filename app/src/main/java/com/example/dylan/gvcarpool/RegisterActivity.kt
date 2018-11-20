package com.example.dylan.gvcarpool

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton.setOnClickListener { view ->
            //TODO: Change clickListener to call validation function if that passes then make intent.
            //TODO:     Probably put conditional logic to call intent inside the validate function. Only have function call here.

            // Intent back to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    //TODO: Functions to validate credentials
}

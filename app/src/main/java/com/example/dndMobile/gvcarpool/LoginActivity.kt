package com.example.dndMobile.gvcarpool

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener{view ->
            // TODO: Make and call firebase login function. For now, just launch main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        registerLabel.setOnClickListener { view ->
            // Make intent to the register activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}

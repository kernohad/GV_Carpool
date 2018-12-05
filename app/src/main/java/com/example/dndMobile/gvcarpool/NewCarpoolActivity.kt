package com.example.dndMobile.gvcarpool

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType.TYPE_NULL
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_new_carpool.*
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import java.text.SimpleDateFormat
import java.util.*


class NewCarpoolActivity : AppCompatActivity() {
    // Reference to firebase authenticator and DB
    private var auth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var type = "Offer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_carpool)

        // Set type based on default value (offer)
        toggleType()

        // Set click listners to toggle type
        offerButton.setOnClickListener { _ ->
            type = "Offer"
            toggleType()
        }

        requestButton.setOnClickListener { _ ->
            type = "Request"
            toggleType()
        }

        // Initialize firebase reference
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Rides")

        // Focus Listener for 'When' Field. It starts pulls up the Date Time picker
        // Reference to Librfary Used: https://github.com/Kunzisoft/Android-SwitchDateTimePicker
        whenField.setOnFocusChangeListener { _ , hasFocus ->
            if(hasFocus){
                whenField.inputType = TYPE_NULL

                // Initialize
                var dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        "When do you want to leave?",
                        "OK",
                        "Cancel"
                )

                // Assign values
                dateTimeDialogFragment.startAtCalendarView()
                dateTimeDialogFragment.setHighlightAMPMSelection(true)
                dateTimeDialogFragment.minimumDateTime = GregorianCalendar(2015, Calendar.JANUARY, 1).getTime()
                dateTimeDialogFragment.maximumDateTime = GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime()
                dateTimeDialogFragment.setDefaultDateTime(Date())

                // Define new day and month format
                try {
                    dateTimeDialogFragment.simpleDateMonthAndDayFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
                } catch (e: SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException) {
                }


                // Set listener
                dateTimeDialogFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonClickListener {
                    override fun onPositiveButtonClick(date: Date) {

                        val simpleFormat = SimpleDateFormat("MM/dd/yyyy 'at' hh:mm aaa", Locale.getDefault())
                        val currentDate = simpleFormat.format(date)


                        whenField.setText(currentDate)
                        totalSeatsField.requestFocus()
                    }

                    override fun onNegativeButtonClick(date: Date) {
                        totalSeatsField.requestFocus()
                    }
                })

                // Show Date Time Picker
                dateTimeDialogFragment.show(supportFragmentManager, "dialog_time")
            }
        }

        submitButton.setOnClickListener{ _ ->

            // TODO: Field Validation. Make sure required fields are not empty.

            // Set DB reference specific ride and save data
            val rideDb = databaseReference!!.child(Date().toString())        // Get Current Date time and use as Ride ID
            rideDb.child("user").setValue(auth?.currentUser!!.uid)
            rideDb.child("type").setValue(type)
            rideDb.child("to").setValue(toField.text.toString())
            rideDb.child("from").setValue(fromField.text.toString())
            rideDb.child("when").setValue(whenField.text.toString())
            rideDb.child("total_seats").setValue(totalSeatsField.text.toString())
            rideDb.child("gas_money").setValue(gasMoneyField.text.toString())
            rideDb.child("seats_available").setValue(totalSeatsField.text.toString())
            rideDb.child("notes").setValue(notesField.text.toString())

            // Display message and finish activity
            Toast.makeText(this,
                    "New $type was created.",
                    Toast.LENGTH_SHORT).show()
            finish()

        }

        cancelButton.setOnClickListener { _ ->
            // Finish the activity and return to previous activity (Home)
            finish()
        }
    }

    private fun toggleType(){
        // Set type colors
        if(type == "Offer"){
            offerButton.setBackgroundResource(R.drawable.offer_background)
            requestButton.setBackgroundResource(R.drawable.neutral_background)
        }else if(type == "Request"){
            offerButton.setBackgroundResource(R.drawable.neutral_background)
            requestButton.setBackgroundResource(R.drawable.request_background)
        }
    }
}


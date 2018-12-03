package com.example.dndMobile.gvcarpool

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType.TYPE_NULL
import kotlinx.android.synthetic.main.activity_new_carpool.*
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class NewCarpoolActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_carpool)




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
    }
}

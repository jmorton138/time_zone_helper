package com.authenticator.timezonehelper

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val pickTimeTextView = getActivity()?.findViewById<TextView>(R.id.pickTimeTextView)
        if (pickTimeTextView != null) {
            val dateToday = LocalDate.now().toString()
            var minuteString: String = minute.toString()
            if (minute < 10) {
                minuteString = "0$minuteString"
            }
            val timeSelected: String = "$hourOfDay:$minuteString:00"
            val sourceDateTime = "$dateToday $timeSelected"
            pickTimeTextView.setText(sourceDateTime)
        }

    }
}
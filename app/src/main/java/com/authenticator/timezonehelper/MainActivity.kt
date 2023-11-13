package com.authenticator.timezonehelper

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.authenticator.timezonehelper.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sourceCityAutocompleteTextView: AutoCompleteTextView
    private lateinit var destCityAutocompleteTextView: AutoCompleteTextView
    private lateinit var pickTimeTextView: EditText
    private lateinit var clearInputsButton: Button
    private lateinit var destinationTimeTextView: TextView
    private lateinit var tzSourceState: String
    private lateinit var tzDestState: String
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            with(savedInstanceState) {
                // Restore value of members from saved state.
                tzSourceState = getString("tz_source_state").toString()
                Log.e("state restored", tzSourceState)
                tzDestState = getString("tz_dest_state").toString()
            }
        } else  {
            tzSourceState = ""
            tzDestState = ""
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val alertDialogBuilder = AlertDialog.Builder(this)
        sourceCityAutocompleteTextView = findViewById<View>(R.id.cityAutoCompleteTextViewSource) as AutoCompleteTextView
        destCityAutocompleteTextView = findViewById<View>(R.id.cityAutoCompleteTextViewDest) as AutoCompleteTextView
        pickTimeTextView = findViewById<EditText>(R.id.pickTimeTextView)
        clearInputsButton = findViewById<Button>(R.id.clearInputsButton)
        val convertButton = findViewById<Button>(R.id.convertTime)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line
        )
        sourceCityAutocompleteTextView.setAdapter(adapter)
        destCityAutocompleteTextView.setAdapter(adapter)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        mainViewModel = (applicationContext as TimeZoneHelperApplication).appContainer.mainViewModelFactory.create()
        coroutineScope.launch {
            val filteredSuggestions = mainViewModel.getCityNames()
                .map { it }
                .toTypedArray()

            // Update the adapter on the main thread
            withContext(Dispatchers.Main) {
                adapter.clear()
                adapter.addAll(*filteredSuggestions)
                adapter.notifyDataSetChanged()
            }
        }
        sourceCityAutocompleteTextView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            coroutineScope.launch {
                val lat = mainViewModel.getLatitude(item)
                val lng = mainViewModel.getLongitude(item)
                val tz = mainViewModel.getTimeZoneFromCoordinates(lat, lng)
                withContext(Dispatchers.Main) {
                    tzSourceState = tz
                }
                val time = mainViewModel.getTimezoneCurrentTime(tz)
            }

        }
        destCityAutocompleteTextView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            coroutineScope.launch {
                val lat = mainViewModel.getLatitude(item)
                val lng = mainViewModel.getLongitude(item)
                val tz = mainViewModel.getTimeZoneFromCoordinates(lat, lng)
                withContext(Dispatchers.Main) {
                    tzDestState = tz
                }
                val time = mainViewModel.getTimezoneCurrentTime(tz)
            }

        }
        pickTimeTextView.setOnClickListener {
            TimePickerFragment().show(supportFragmentManager, "timePicker")
            pickTimeTextView.error = null
        }
        convertButton.setOnClickListener(View.OnClickListener {
            val sourceTimeZoneText = tzSourceState
            val destTimeZoneText = tzDestState
            val sourceTimeSelected = pickTimeTextView.text
            if (validateInputs(sourceTimeZoneText, destTimeZoneText, sourceTimeSelected)) {
                val destTime = mainViewModel.convertToDestinationTime(sourceTimeZoneText, destTimeZoneText, sourceTimeSelected)
                alertDialogBuilder.setTitle("Destination Time")
                val sourceCity = sourceCityAutocompleteTextView.text
                val destCity = destCityAutocompleteTextView.text
                val message = "When it is $sourceTimeSelected in $sourceCity it is $destTime in $destCity"
                alertDialogBuilder.setMessage(destTime)
                alertDialogBuilder.setNegativeButton("Close") { dialog, which ->
//                    Toast.makeText(applicationContext,
//                        "Close", Toast.LENGTH_SHORT).show()
                }
                alertDialogBuilder.show()

            }
        })
        clearInputsButton.setOnClickListener(View.OnClickListener {
            tzSourceState = ""
            sourceCityAutocompleteTextView.text = null
            tzDestState = ""
            destCityAutocompleteTextView.text = null
            pickTimeTextView.text = null
        })

    }
    override fun onSaveInstanceState(outState: Bundle) {
        Log.e("state saved", tzSourceState)
        outState?.run {
            outState.putString("tz_source_state", tzSourceState)
            outState.putString("tz_dest_state", tzDestState)
        }
        // Always call the superclass so it can save the view hierarchy state.
        super.onSaveInstanceState(outState)
    }

    private fun validateInputs(source: CharSequence, dest: CharSequence, sourceTime: CharSequence): Boolean {
        var validated:Boolean = true
        if(TextUtils.isEmpty(source)) {
            sourceCityAutocompleteTextView.error = "Please select a city"
            sourceCityAutocompleteTextView.requestFocus()
            validated = false
        }
        if(TextUtils.isEmpty(dest)) {
            destCityAutocompleteTextView.error = "Please select a city"
            destCityAutocompleteTextView.requestFocus()
            validated = false
        }
        if(TextUtils.isEmpty(sourceTime)) {
            pickTimeTextView.error = "Please select a time"
            pickTimeTextView.requestFocus()
            validated = false
        }
        return validated
    }

}
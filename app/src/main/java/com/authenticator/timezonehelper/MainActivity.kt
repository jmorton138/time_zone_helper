package com.authenticator.timezonehelper

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.authenticator.timezonehelper.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cityDao: CityDao
    private lateinit var cities : List<String>
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sourceCityAutocompleteTextView: AutoCompleteTextView
    private lateinit var destCityAutocompleteTextView: AutoCompleteTextView
    private lateinit var pickTimeTextView: EditText
    private lateinit var clearInputsButton: Button
    private lateinit var destinationTimeTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val alertDialogBuilder = AlertDialog.Builder(this)

        sourceCityAutocompleteTextView = findViewById<View>(R.id.cityAutoCompleteTextViewSource) as AutoCompleteTextView
        destCityAutocompleteTextView = findViewById<View>(R.id.cityAutoCompleteTextViewDest) as AutoCompleteTextView
        pickTimeTextView = findViewById<EditText>(R.id.pickTimeTextView)
        destinationTimeTextView = findViewById<TextView>(R.id.convertedTime)
        clearInputsButton = findViewById<Button>(R.id.clearInputsButton)
        val convertButton = findViewById<Button>(R.id.convertTime)
        val sourceTimeZone = findViewById<TextView>(R.id.sourceTimeZone)
        val destTimeZone  = findViewById<TextView>(R.id.destTimeZone)
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
                    sourceTimeZone.text = tz
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
                    destTimeZone.setText(tz)
                }
                val time = mainViewModel.getTimezoneCurrentTime(tz)
            }

        }
        pickTimeTextView.setOnClickListener {
            TimePickerFragment().show(supportFragmentManager, "timePicker")
            pickTimeTextView.error = null
        }
        convertButton.setOnClickListener(View.OnClickListener {

            val sourceTimeZoneText = sourceTimeZone.text
            val destTimeZoneText = destTimeZone.text
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
            sourceTimeZone.text = ""
            sourceCityAutocompleteTextView.text = null
            destTimeZone.text = ""
            destCityAutocompleteTextView.text = null
            pickTimeTextView.text = null
            destinationTimeTextView.text = ""
        })

    }
    fun validateInputs(source: CharSequence, dest: CharSequence, sourceTime: CharSequence): Boolean {
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
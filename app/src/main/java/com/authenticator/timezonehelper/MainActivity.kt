package com.authenticator.timezonehelper

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sourceCityAutocompleteTextView = findViewById<View>(R.id.cityAutoCompleteTextViewSource) as AutoCompleteTextView
        val destCityAutocompleteTextView = findViewById<View>(R.id.cityAutoCompleteTextViewDest) as AutoCompleteTextView
        val pickTimeView = findViewById<TextView>(R.id.pickTime)
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
                Log.e("clicked", lat.toString())
                Log.e("clicked", lng.toString())
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
                Log.e("clicked", time.toString())
            }

        }
        pickTimeView.setOnClickListener {
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
        convertButton.setOnClickListener(View.OnClickListener {
            val sourceTimeZoneText = sourceTimeZone.text
            val destTimeZoneText = destTimeZone.text
            val sourceTimeSelected = pickTimeView.text
            val destTime = mainViewModel.convertToDestinationTime(sourceTimeZoneText, destTimeZoneText, sourceTimeSelected)
            findViewById<TextView>(R.id.convertedTime).text = destTime

        })

    }

}
package com.authenticator.timezonehelper

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.authenticator.timezonehelper.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cityDao: CityDao
    private lateinit var cities : List<String>
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val textView = findViewById<View>(R.id.inputCityAutoComplete) as AutoCompleteTextView
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line
        )
        textView.setAdapter(adapter)
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

    }

}
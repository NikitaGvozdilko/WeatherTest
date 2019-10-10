package com.example.weathertest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.weathertest.fragments.mainFragment.MainFragment
import com.example.weathertest.utils.AppPref
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Arrays.asList

private const val AUTOCOMPLETE_REQUEST_CODE = 1
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var mainFragment: MainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            mainFragment = MainFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, mainFragment!!).commit()
        } else {
            mainFragment = supportFragmentManager.fragments[0] as MainFragment?
        }

        toolbar.title = ""
        setSupportActionBar(toolbar)
        imageLocation.setOnClickListener {
            mainFragment?.getCurrentLocationWeather()
        }
        val placeName = AppPref.getPlaceName(this)
        if (placeName != null) textToolbarTitle?.text = placeName
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.d(TAG, "Place: " + place.name + ", " + place.id)
                    if (place.latLng != null) {
                        textToolbarTitle?.text = place.name!!
                        AppPref.savePlaceName(this@MainActivity, place.name!!)
                        AppPref.saveCoords(this@MainActivity, place.latLng!!)
                        mainFragment?.updateWeather(place.latLng!!.latitude, place.latLng!!.longitude)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.d(TAG, status.statusMessage)
                }
                Activity.RESULT_CANCELED -> {
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_my_location) {
            val fields = asList(Place.Field.ID, Place.Field.NAME)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
            startActivityForResult(intent, 1)
        }

        return true
    }
}

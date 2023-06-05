package com.vicryfahreza.storyapp.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.vicryfahreza.storyapp.R
import com.vicryfahreza.storyapp.data.StoryMarker
import com.vicryfahreza.storyapp.databinding.ActivityMapsBinding
import com.vicryfahreza.storyapp.model.MapsViewModel
import com.vicryfahreza.storyapp.model.UserPreference
import com.vicryfahreza.storyapp.model.ViewModelFactory
import com.vicryfahreza.storyapp.response.ListStoryItem

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        mapsViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MapsViewModel::class.java]

        mapsViewModel.getUser().observe(this) {
                userToken: String ->
            accessMapsActivity(userToken)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun accessMapsActivity(token: String) {
        if (token.isEmpty()) {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        } else {
            mapsViewModel.markerStory(token)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        mapsViewModel.markerStory.observe(this) { marker ->
            addUserMarker(marker)
        }
        val locationNow = LatLng(-6.0957813, 106.69922)
        mMap.addMarker(MarkerOptions().position(locationNow).title("Marker Of Home"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locationNow))

        getMyLocation()
        setMapStyle()
    }

    private fun addUserMarker(storyMarkerResponse: ArrayList<ListStoryItem>) {
        val storyMarker = ArrayList<StoryMarker>()
        for(story in storyMarkerResponse){
            storyMarker.add(
                StoryMarker(story.name, story.description ,story.lat, story.lon)
            )
        }
        storyMarker.forEach { currentLocation ->
            val latLng = LatLng(currentLocation.lat, currentLocation.long)
            mMap.addMarker(MarkerOptions().position(latLng).title(currentLocation.name).snippet(currentLocation.desc))
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this@MapsActivity, "Style parsing failed.", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this@MapsActivity, "Can't find style. Error: $exception", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }


    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}
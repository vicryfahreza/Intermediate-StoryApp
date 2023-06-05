package com.vicryfahreza.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vicryfahreza.storyapp.R
import com.vicryfahreza.storyapp.databinding.ActivityAddStoryBinding
import com.vicryfahreza.storyapp.model.AddStoryViewModel
import com.vicryfahreza.storyapp.model.UserPreference
import com.vicryfahreza.storyapp.model.ViewModelFactory
import com.vicryfahreza.storyapp.response.AddStoryResponse
import com.vicryfahreza.storyapp.service.ApiConfig
import com.vicryfahreza.storyapp.setting.createCustomTempFile
import com.vicryfahreza.storyapp.setting.reduceFileImage
import com.vicryfahreza.storyapp.setting.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var location: Location
    private lateinit var fusedLocation: FusedLocationProviderClient

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AddStoryViewModel::class.java]

        binding.cameraButton.setOnClickListener { startCamera() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocation.lastLocation.addOnSuccessListener { locs: Location? ->
                if (locs != null) {
                    location = locs
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val photoFile = File(currentPhotoPath)

            photoFile.let { file ->
                getFile = file
                binding.previewImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK){
            val selectedImg = it.data?.data as Uri
            selectedImg.let { uri ->
                val photoFile = uriToFile(uri , this@AddStoryActivity)
                getFile = photoFile
                binding.previewImg.setImageURI(uri)
            }
        }
    }

    private fun uploadImage(){
        showLoading(true)
        if(getFile != null){
            val file = reduceFileImage(getFile as File)
            val description = binding.edDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val lat = location.latitude.toFloat()
            val long = location.longitude.toFloat()
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            addStoryViewModel.getUser().observe(this) {
                userToken: String ->
                val clientUpload = ApiConfig.getApiService()
                    .uploadStories("Bearer $userToken", imageMultipart, description, lat, long)
                clientUpload.enqueue(object: Callback<AddStoryResponse> {
                    override fun onResponse(
                        call: Call<AddStoryResponse>,
                        response: Response<AddStoryResponse>
                    ) {
                        val responseBody = response.body()
                        showLoading(false)
                        if (response.isSuccessful) {
                            if (responseBody != null && response.isSuccessful) {
                                Toast.makeText(this@AddStoryActivity, responseBody.message, Toast.LENGTH_SHORT)
                                    .show()
                                intentAllStory()
                            }
                        } else {
                            Toast.makeText(this@AddStoryActivity, response.message(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                        showLoading(false)
                        Toast.makeText(this@AddStoryActivity, t.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            }

        } else {
            showLoading(false)
            Toast.makeText(this@AddStoryActivity, getString(R.string.add_story_failed), Toast.LENGTH_SHORT).show()
        }
    }


    fun intentAllStory() {
        val mainIntent = Intent(this@AddStoryActivity, MainActivity::class.java)
        val intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        mainIntent.flags = intentFlag
        startActivity(mainIntent)
        finish()
    }

    private fun startGallery() {
        val intentGallery = Intent()
        intentGallery.action = ACTION_GET_CONTENT
        intentGallery.type = "image/*"
        val intentChooser = Intent.createChooser(intentGallery, "choose a picture")
        launcherIntentGallery.launch(intentChooser)
    }

    private fun startCamera() {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentCamera.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.vicryfahreza.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intentCamera)
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }



}

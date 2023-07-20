package dev.munderstand.pc_epsi_mspr_app.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewPlantActivity : AppCompatActivity() {

    private lateinit var capturePhotoButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var selectedPhotoUri: Uri
    private lateinit var currentPhotoPath: String

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plant)

        capturePhotoButton = findViewById(R.id.capturePhotoButton)
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        uploadButton = findViewById(R.id.uploadButton)

        // Check camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

        capturePhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val sharedPreferences = this.getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences.getString("accountInfo", "")

        val jsonObject = JSONObject(accountInfo.toString())
        val accountId = jsonObject.getInt("id").toString()
//        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)

        // Retrieve the token from SharedPreferences
        val token = sharedPreferences?.getString("token", "")
        uploadButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            // Upload the photo and form data to the API
            uploadPlantData(name, description, accountId, token.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with the camera operation
            } else {
                // Camera permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile: File? = tryCreateImageFile()
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "dev.munderstand.pc_epsi_mspr_app.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.e(TAG, "Error capturing photo", ex)
        }
    }

    private fun tryCreateImageFile(): File? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            ).apply {
                currentPhotoPath = absolutePath
            }
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.e(TAG, "Error creating image file", ex)
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Photo capture succeeded, handle the photo as needed
            val photoFile = File(currentPhotoPath)
            selectedPhotoUri = Uri.fromFile(photoFile)
            // Display the captured photo if desired
            displayCapturedPhoto()
        } else {
            // Photo capture failed or was canceled, handle the error or cancellation
        }
    }

    private fun displayCapturedPhoto() {
        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setImageURI(selectedPhotoUri)
    }

    private fun uploadPlantData(name: String, description: String, user_id: String, token: String) {
        // Disable the upload button to prevent multiple clicks
        uploadButton.isEnabled = false

        // Create a multipart request body with the photo file
        val photoFile = File(currentPhotoPath)
        val requestFile: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), photoFile)
        val photoPart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)

        // Create other form data fields
        val userIdPart: RequestBody = user_id.toRequestBody("text/plain".toMediaTypeOrNull())
        val namePart: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart: RequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

        // Create the request body
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(photoPart)
            .addPart(MultipartBody.Part.createFormData("user_id", null, userIdPart))
            .addPart(MultipartBody.Part.createFormData("name", null, namePart))
            .addPart(MultipartBody.Part.createFormData("description", null, descriptionPart))
            .build()

        val url = ApiConfig.PLANT_ADD_ENDPOINT

        // Create the request
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        Log.e("WS", photoFile.absolutePath)

        // Send the request asynchronously
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle the request failure
                runOnUiThread {
                    Toast.makeText(
                        this@NewPlantActivity,
                        "Failed to upload plant data",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Enable the upload button again
                    uploadButton.isEnabled = true
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                // Handle the request success
                val responseBody = response.body?.string()
                try {
                    val jsonObject = JSONObject(responseBody)
                    Log.e("WS", jsonObject.toString())
                    // Handle the response data if needed
                    runOnUiThread {
                        Toast.makeText(
                            this@NewPlantActivity,
                            "Plant uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Finish the activity and return to PlantesFragment
                        finish()
                    }
                } catch (e: JSONException) {
                    // Error occurred while parsing the response
                    runOnUiThread {
                        Toast.makeText(
                            this@NewPlantActivity,
                            "Failed to upload plant data",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Enable the upload button again
                        uploadButton.isEnabled = true
                    }
                }
            }
        })
    }

    companion object {
        private const val TAG = "NewPlantActivity"
    }
}

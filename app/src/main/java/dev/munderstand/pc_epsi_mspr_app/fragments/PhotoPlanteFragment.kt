package dev.munderstand.pc_epsi_mspr_app.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.CreateAnnonceActivity
import dev.munderstand.pc_epsi_mspr_app.activities.PlantIdentifyActivity
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoPlanteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoPlanteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private lateinit var selectedPhotoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_plante, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonScan = view.findViewById<Button>(R.id.buttonScan)
        val uploadButton = view.findViewById<Button>(R.id.uploadButton)
        val sharedPreferences = context?.getSharedPreferences("account", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "")

        buttonScan.setOnClickListener {
            dispatchTakePictureIntent()
        }

        uploadButton.setOnClickListener {
            uploadPlantData(token.toString())
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
        val imageView: ImageView = requireActivity().findViewById(R.id.imageView)
        imageView.setImageURI(selectedPhotoUri)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile: File? = tryCreateImageFile()
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireActivity().application,
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
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )
            val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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

//    private fun uploadPlantData(token: String) {
//        // Create a multipart request body with the photo file
//        val photoFile = File(currentPhotoPath)
//        val requestFile: RequestBody =
//            RequestBody.create("image/*".toMediaTypeOrNull(), photoFile)
//        val photoPart: MultipartBody.Part =
//            MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
//
//        // Create the request body
//        val requestBody: RequestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addPart(photoPart)
//            .build()
//
//        val url = ApiConfig.PLANT_SCAN_ENDPOINT
//
//        // Create the request
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url(url)
//            .header("Authorization", "Bearer $token")
//            .post(requestBody)
//            .build()
//
//        // Send the request asynchronously
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                // Handle the request failure
//                activity?.runOnUiThread {
//                    Toast.makeText(
//                        context,
//                        "Failed to upload plant data",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                // Handle the request success
//                val responseBody = response.body?.string()
//                try {
//                    val jsonObject = JSONObject(responseBody)
//                    Log.e("WS", jsonObject.toString())
//                    // Handle the response data if needed
//                    activity?.runOnUiThread {
//                        Toast.makeText(
//                            context,
//                            "img uploaded successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        val intent = Intent(activity?.applicationContext, PlantIdentifyActivity::class.java)
//                        intent.putExtra("urlImg", jsonObject.getString("img"))
//                        startActivity(intent)
//                    }
//                } catch (e: JSONException) {
//                    // Error occurred while parsing the response
//                    activity?.runOnUiThread {
//                        Toast.makeText(
//                            context,
//                            "Failed to upload plant data",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        })
//    }

    private fun uploadPlantData(token: String) {
        val photoFile = File(currentPhotoPath)

        if (!photoFile.exists() || !photoFile.isFile) {
            // The photo file does not exist or is not a valid file
            Toast.makeText(
                context,
                "Error: Photo file not found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val requestFile: RequestBody =
            photoFile.asRequestBody("image/*".toMediaTypeOrNull())
        val photoPart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(photoPart)
            .build()

        val url = ApiConfig.PLANT_SCAN_ENDPOINT

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle the request failure
                activity?.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Failed to upload plant data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseBody = response.body?.string()
                Log.e("WS", responseBody.toString())
                if (response.isSuccessful) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val imgUrl = jsonObject.optString("img")
                        if (!imgUrl.isNullOrEmpty()) {
                            // Handle the successful response with imgUrl
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    context,
                                    "Image uploaded successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    activity?.applicationContext,
                                    PlantIdentifyActivity::class.java
                                )
                                intent.putExtra("urlImg", imgUrl)
                                startActivity(intent)
                            }
                        } else {
                            // Handle the case when "img" is missing or empty in the response
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    context,
                                    "Error: Invalid response from server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: JSONException) {
                        // Error occurred while parsing the response JSON
                        activity?.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Error: Failed to parse server response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Handle non-successful response (e.g., HTTP status code is not 200)
                    activity?.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Error: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoPlanteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoPlanteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val TAG = "NewPlantActivity"
    }
}
package dev.munderstand.pc_epsi_mspr_app.activities.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SignUpActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var zipcodeEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        latitudeEditText = findViewById(R.id.latitudeEditText)
        longitudeEditText = findViewById(R.id.longitudeEditText)
        cityEditText = findViewById(R.id.cityEditText)
        zipcodeEditText = findViewById(R.id.zipcodeEditText)
        addressEditText = findViewById(R.id.addressEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val latitude = latitudeEditText.text.toString().toDoubleOrNull() ?: 0.0
            val longitude = longitudeEditText.text.toString().toDoubleOrNull() ?: 0.0
            val city = cityEditText.text.toString()
            val zipcode = zipcodeEditText.text.toString()
            val address = addressEditText.text.toString()

            // Make API request to register user
            register(firstName, lastName, username, email, password, latitude, longitude, city, zipcode, address)
        }
    }

    private fun register(firstName: String, lastName: String, username: String, email: String, password: String, latitude: Double, longitude: Double, city: String, zipcode: String, address: String) {
        val client = OkHttpClient()

        val url = ApiConfig.REGISTER_ENDPOINT

        val jsonBody = JSONObject()
        jsonBody.put("firstName", firstName)
        jsonBody.put("lastName", lastName)
        jsonBody.put("username", username)
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        jsonBody.put("latitude", latitude)
        jsonBody.put("longitude", longitude)
        jsonBody.put("city", city)
        jsonBody.put("zipcode", zipcode)
        jsonBody.put("address", address)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SignUpActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                try {
                    val jsonObject = JSONObject(responseBody)

                    if (response.isSuccessful) {
                        if (jsonObject.has("message")) {
                            val message = jsonObject.getString("message") // Extract the message from the response

                            // Show the message or perform any desired action
                            runOnUiThread {
                                Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle the case when "message" key is missing
                            runOnUiThread {
                                Toast.makeText(this@SignUpActivity, "Invalid response from the server", Toast.LENGTH_SHORT).show()
                            }
                            Log.e("SignUpActivity", "Invalid response from the server")
                        }
                    } else {
                        if (jsonObject.has("errors")) {
                            val errorsArray = jsonObject.getJSONArray("errors")

                            // Concatenate all error messages
                            val errorMessage = StringBuilder()
                            for (i in 0 until errorsArray.length()) {
                                errorMessage.append(errorsArray.getString(i))
                                if (i != errorsArray.length() - 1) {
                                    errorMessage.append("\n")
                                }
                            }

                            runOnUiThread {
                                Toast.makeText(this@SignUpActivity, errorMessage.toString(), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle the case when "errors" key is missing
                            runOnUiThread {
                                Toast.makeText(this@SignUpActivity, "Invalid response from the server", Toast.LENGTH_SHORT).show()
                            }
                            Log.e("SignUpActivity", "Invalid response from the server")
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "Failed to parse response", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("SignUpActivity", "Failed to parse response", e)
                }
            }
        })
    }


}

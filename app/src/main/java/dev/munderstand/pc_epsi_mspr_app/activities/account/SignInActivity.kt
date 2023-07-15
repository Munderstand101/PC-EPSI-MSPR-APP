package dev.munderstand.pc_epsi_mspr_app.activities.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.MainActivity
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SignInActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        usernameEditText = findViewById(R.id.et_username)
        passwordEditText = findViewById(R.id.et_password)

        usernameEditText.setText("test")
        passwordEditText.setText("testtest")

        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Disable the login button to prevent multiple clicks
            loginButton.isEnabled = false

            // Make API request to get the token
            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        val client = OkHttpClient()

        val url = ApiConfig.LOGIN_ENDPOINT

        val jsonBody = JSONObject()
        jsonBody.put("username", username)
        jsonBody.put("password", password)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SignInActivity, "Login failed", Toast.LENGTH_SHORT).show()

                    // Enable the login button again
                    loginButton.isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                try {
                    val jsonObject = JSONObject(responseBody)
                    val token = jsonObject.getString("token") // Extract the token from the response

                    // Store the token or use it for further API requests
                    val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token", token)
                    editor.apply()

                    // Get account information
                    getAccountInfo(token)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@SignInActivity, "Failed to parse response", Toast.LENGTH_SHORT).show()
                    }
                    // Enable the login button again
                    runOnUiThread {
                        loginButton.isEnabled = true
                    }
                }
            }
        })
    }

    private fun getAccountInfo(token: String) {
        val client = OkHttpClient()

        val url = ApiConfig.ACCOUNT_ENDPOINT

        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SignInActivity, "Failed to get account information", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    try {
                        val jsonResponse = responseBody?.let { JSONObject(it) }
                        val accountInfo = jsonResponse.toString()

                        // Store the account information in shared preferences
                        val editor = sharedPreferences.edit()
                        editor.putString("accountInfo", accountInfo)
                        editor.apply()

                        // Show a success message or perform any desired action
                        runOnUiThread {
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@SignInActivity, "Failed to parse account information", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@SignInActivity, "Failed to get account information", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun fRegister_Click(view: View) {
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        startActivity(intent)
    }
}

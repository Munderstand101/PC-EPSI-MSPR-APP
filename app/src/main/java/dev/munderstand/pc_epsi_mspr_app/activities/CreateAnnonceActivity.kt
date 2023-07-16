package dev.munderstand.pc_epsi_mspr_app.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import dev.munderstand.pc_epsi_mspr_app.fragments.AcceuilFragment
import dev.munderstand.pc_epsi_mspr_app.fragments.Plant
import dev.munderstand.pc_epsi_mspr_app.fragments.PlantAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CreateAnnonceActivity : AppCompatActivity() {

    private val TAG = "CreateAnnonceActivity"
    private val items = mutableListOf<Plant>()
    private val plantes = mutableMapOf<Int, Plant>()
    private lateinit var spinnerPlantes: Spinner
    private lateinit var token: String
    private lateinit var accountId: String
    private lateinit var editTextRequestTitle: EditText
    private lateinit var editTextRequestDescription: EditText
    private lateinit var editTextAddress: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_annonce)

        initializeViews()
        populatePlants()
        setupCreateAnnonceButton()
    }

    private fun initializeViews() {
        val pageTitle = findViewById<TextView>(R.id.tv_title)
        pageTitle.text = "Créer une demande de garde"

        val imageViewBack = findViewById<ImageView>(R.id.iv_Left_Arrow)
        imageViewBack.setOnClickListener {
            finish()
        }

        spinnerPlantes = findViewById(R.id.spinnerPlantes)
        editTextRequestTitle = findViewById(R.id.editTextRequestTitle)
        editTextRequestDescription = findViewById(R.id.editTextRequestDescription)
        editTextAddress = findViewById(R.id.editTextAddress)
    }

    private fun populatePlants() {
        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        token = sharedPreferences?.getString("token", "") ?: ""

        accountId = getAccountId(accountInfo)

        if (accountId.isEmpty()) {
            // Handle the case when there is no account info
            Toast.makeText(this, "No account info available", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.PLANTS_ENDPOINT + accountId
        val adapter = PlantAdapter(items)
        val queue = Volley.newRequestQueue(this)

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val id = item.getInt("id")
                    val name = item.getString("name")
                    val description = item.getString("description")
                    val pictureUrl = ApiConfig.BASE_URL_PHOTOS + item.getString("photo")
                    val newPlant = Plant(id, name, description, pictureUrl)
                    items.add(newPlant)
                    plantes[id] = newPlant
                    Log.e("WS", item.toString())
                }

                Log.e("WS", items.toString())
                adapter.notifyDataSetChanged()
                populateSpinner()
            },
            { error ->
                Log.e(AcceuilFragment.TAG, "Error fetching data", error)
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        queue.add(jsonArrayRequest)
    }

    private fun getAccountId(accountInfo: String?): String {
        if (accountInfo.isNullOrEmpty()) {
            return ""
        }

        return try {
            val jsonObject = JSONObject(accountInfo)
            jsonObject.getInt("id").toString()
        } catch (e: JSONException) {
            e.printStackTrace()
            ""
        }
    }

    private fun populateSpinner() {
        val plantes = items.map { it.id.toString() + " " + it.titre }
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, plantes)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlantes.adapter = arrayAdapter
    }

    private fun setupCreateAnnonceButton() {
        val buttonCreateAnnonce = findViewById<Button>(R.id.buttonCreateAnnonce)
        buttonCreateAnnonce.setOnClickListener {
            val selectedPlantId = spinnerPlantes.selectedItem.toString().split(" ")[0].toInt()
            val selectedPlant = plantes[selectedPlantId]

            val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
            val accountInfo = sharedPreferences?.getString("accountInfo", "")
            token = sharedPreferences?.getString("token", "") ?: ""

            if (selectedPlant != null) {
                val client = OkHttpClient()
                val url = ApiConfig.REQUESTS_ADD_ENDPOINT
                val requestBody = createRequestBody(selectedPlantId)

                val request = okhttp3.Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(
                                this@CreateAnnonceActivity,
                                "Creation requests failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()

                        try {
                            val jsonObject = JSONObject(responseBody)

                            if (response.isSuccessful) {
                                if (jsonObject.has("message")) {
                                    val message =
                                        jsonObject.getString("message") // Extract the message from the response

                                    // Show the message or perform any desired action
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@CreateAnnonceActivity,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // Handle the case when "message" key is missing
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@CreateAnnonceActivity,
                                            "Invalid response from the server",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    Log.e(
                                        "CreateAnnonceActivity",
                                        "Invalid response from the server"
                                    )
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
                                        Toast.makeText(
                                            this@CreateAnnonceActivity,
                                            errorMessage.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // Handle the case when "errors" key is missing
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@CreateAnnonceActivity,
                                            "Invalid response from the server",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    Log.e(
                                        "CreateAnnonceActivity",
                                        "Invalid response from the server"
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            runOnUiThread {
                                Toast.makeText(
                                    this@CreateAnnonceActivity,
                                    "Failed to parse response",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Log.e("CreateAnnonceActivity", "Failed to parse response", e)
                        }
                    }
                })
            } else {
                Toast.makeText(
                    this@CreateAnnonceActivity,
                    "No plant selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createRequestBody(selectedPlantId: Int): RequestBody {
        val jsonBody = JSONObject()
        jsonBody.put("title", editTextRequestTitle.text)
        jsonBody.put("description", editTextRequestDescription.text)
        jsonBody.put("address", editTextAddress.text)
        jsonBody.put("plant_id", selectedPlantId)
        jsonBody.put("user_id", accountId)
        return RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())
    }

}


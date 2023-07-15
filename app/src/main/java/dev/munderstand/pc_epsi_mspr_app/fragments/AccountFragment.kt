package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.munderstand.pc_epsi_mspr_app.R
import org.json.JSONException
import org.json.JSONObject

class AccountFragment : Fragment() {

    private lateinit var accountIdTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var zipcodeTextView: TextView
    private lateinit var cityTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
//
//        accountIdTextView = view.findViewById(R.id.tv_account_id)
//        emailTextView = view.findViewById(R.id.tv_email)
//        addressTextView = view.findViewById(R.id.tv_address)
//        zipcodeTextView = view.findViewById(R.id.tv_zipcode)
//        cityTextView = view.findViewById(R.id.tv_city)
//
//        // Retrieve account info from shared preferences
//        val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
//        val accountInfo = sharedPreferences?.getString("accountInfo", "")
//
//        if (accountInfo != null && accountInfo.isNotEmpty()) {
//            try {
//                val jsonObject = JSONObject(accountInfo)
//                val accountId = jsonObject.getInt("id")
//                val email = jsonObject.getString("email")
//                val address = jsonObject.getString("address")
//                val zipcode = jsonObject.getString("zipcode")
//                val city = jsonObject.getString("city")
//
//                // Set the account info to the respective TextViews
//                accountIdTextView.text = accountId.toString()
//                emailTextView.text = email
//                addressTextView.text = address
//                zipcodeTextView.text = zipcode
//                cityTextView.text = city
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        }

        return view
    }
}

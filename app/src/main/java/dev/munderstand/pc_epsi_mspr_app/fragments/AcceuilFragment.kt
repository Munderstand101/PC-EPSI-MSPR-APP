package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.AnnoncesActivity
import dev.munderstand.pc_epsi_mspr_app.activities.CreateAnnonceActivity
import dev.munderstand.pc_epsi_mspr_app.activities.MyRequestsActivity
import dev.munderstand.pc_epsi_mspr_app.activities.NewPlantActivity
import dev.munderstand.pc_epsi_mspr_app.activities.account.CGUActivity
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import org.json.JSONException
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AcceuilFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_acceuil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messagesButton = view.findViewById<Button>(R.id.messagesButton)
        val newRequestButton = view.findViewById<Button>(R.id.newRequestButton)
        val getRequestsButton = view.findViewById<Button>(R.id.getRequestsButton)

        val myScrollView = view.findViewById<ScrollView>(R.id.scrollView)
        myScrollView.post {
            myScrollView.fullScroll(View.FOCUS_DOWN)
        }

        messagesButton.setOnClickListener {

        }

        newRequestButton.setOnClickListener {
            val intent = Intent(activity?.applicationContext, CreateAnnonceActivity::class.java)
            startActivity(intent)
        }

        getRequestsButton.setOnClickListener {
            val intent = Intent(activity?.applicationContext, AnnoncesActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Tab1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        const val TAG = "AcceuilFragment"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AcceuilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

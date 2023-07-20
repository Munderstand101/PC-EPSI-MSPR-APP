package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.Annonce
import dev.munderstand.pc_epsi_mspr_app.activities.AnnonceAdapter
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnnoncesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnnoncesFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        return inflater.inflate(R.layout.fragment_annonces, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val annonces = arrayListOf<Annonce>()

        val recyclerViewAnnonces = view.findViewById<RecyclerView>(R.id.recyclerViewAnnonces)
        recyclerViewAnnonces.layoutManager = LinearLayoutManager(view.context)
        val annonceAdapter = AnnonceAdapter(annonces)
        recyclerViewAnnonces.adapter = annonceAdapter

        val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "")

        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        val mRequestUrl = ApiConfig.REQUESTS_ENDPOINT
        val request =
            Request.Builder().url(mRequestUrl).header("Authorization", "Bearer $token").cacheControl(CacheControl.FORCE_NETWORK).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()

                val jsonArray = JSONArray(data)

                for (i in 0 until jsonArray.length()) {
                    val jsAnnonce = jsonArray.getJSONObject(i)
                    val annonce = Annonce(
                        jsAnnonce.getString("title"),
                        jsAnnonce.getString("description"),
                        jsAnnonce.getJSONObject("plant").getString("photo"),
                        jsAnnonce.getString("address"),
                        jsAnnonce.getJSONObject("user").getString("username"),
                        jsAnnonce.getJSONObject("plant").getString("name")
                    )
                    annonces.add(annonce)
                }
                activity?.runOnUiThread(Runnable {
                    annonceAdapter.notifyDataSetChanged()
                })
                if (data != null) {
                    //Log.e("WS", data)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread(Runnable {
                    Toast.makeText(activity!!.application, e.message, Toast.LENGTH_SHORT).show()
                })
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
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnnoncesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


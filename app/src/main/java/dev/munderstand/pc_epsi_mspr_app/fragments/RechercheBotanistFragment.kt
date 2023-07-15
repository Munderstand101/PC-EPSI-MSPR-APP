package dev.munderstand.pc_epsi_mspr_app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dev.munderstand.pc_epsi_mspr_app.R
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "RechercheBotanistFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [RechercheBotanistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RechercheBotanistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BotanistAdapter
    private val items = mutableListOf<Botanist>()


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
        return inflater.inflate(R.layout.fragment_recherche_botanist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rcv_products)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = BotanistAdapter(items)
        recyclerView.adapter = adapter

//        fetchOffers()
    }

//    private fun fetchOffers() {
//        val queue = Volley.newRequestQueue(activity)
//        val url = "https://www.ugarit.online/epsi/offers.json"
//
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
//                try {
//                    val jsonArray = response.getJSONArray("items")
//                    for (i in 0 until jsonArray.length()) {
//                        val item = jsonArray.getJSONObject(i)
//                        val name = item.getString("name")
//                        val description = item.getString("description")
//                        val pictureUrl = item.getString("picture_url")
//                        items.add(Botanist(id,name, description, pictureUrl,pictureUrl,pictureUrl,pictureUrl,pictureUrl,pictureUrl,pictureUrl,pictureUrl,pictureUrl))
//
//                    }
//                    adapter.notifyDataSetChanged()
//                } catch (e: JSONException) {
//                //    Log.e(RechercheBotanistFragment.TAG, "Error parsing JSON", e)
//                }
//            },
//            { error ->
//             //   Log.e(RechercheBotanistFragment.TAG, "Error fetching data", error)
//            })
//
//        queue.add(jsonObjectRequest)
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RechercheBotanistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RechercheBotanistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
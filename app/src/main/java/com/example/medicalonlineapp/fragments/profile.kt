package com.example.medicalonlineapp.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.medicalonlineapp.R
import com.example.medicalonlineapp.adapters.PacienteAdapter
import com.example.medicalonlineapp.http.httpRequest
import com.example.medicalonlineapp.paciente.Paciente
import kotlinx.android.synthetic.main.fragment_citas.*
import org.json.JSONObject

class profile  : Fragment() {

    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private val HOSTING: String = "https://aquarossnok.000webhostapp.com/getPacientesInfo.php"
    private lateinit var charactersList: ArrayList<Paciente>
    private lateinit var adapter: PacienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId
        if(id== R.id.salir){
            Toast.makeText(this.activity, "Salir", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }



}
package com.example.medicalonlineapp.fragments

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.medicalonlineapp.R
import com.example.medicalonlineapp.http.httpRequest
import com.example.medicalonlineapp.paciente.Paciente
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_citas.*
import org.json.JSONObject
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.medicalonlineapp.adapters.PacienteAdapter


class Citas : Fragment() {

    var swipeRefreshLayout: SwipeRefreshLayout? = null
   private val HOSTING: String = "https://aquarossnok.000webhostapp.com/getPacientesInfo.php"
    private lateinit var charactersList: ArrayList<Paciente>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_citas, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefreshLayout = swipeToRefreshLayout

        recyclerCitas.setHasFixedSize(true)
        recyclerCitas.layoutManager = LinearLayoutManager(this.activity)
        cargarCitas()

        swipeRefreshLayout!!.setOnRefreshListener {
            cargarCitas()
        }
    }

    private fun isOnline(): Boolean{
        val on = (this.requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

        val activityNetwork: NetworkInfo? = on.activeNetworkInfo

        return activityNetwork != null && activityNetwork.isConnected
    }

    private fun cargarCitas() {

        if(isOnline()){
            try {
                val json = JSONObject()

                json.put("getPacientesInfo", true)

                val string = httpRequest{
                    if(it == null){
                        print("error conexion")
                        return@httpRequest
                    }
                    println("it $it")
                }.execute("POST", HOSTING, json.toString())

                val parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(string.get())
                val json2: JsonObject = parser.parse(stringBuilder) as JsonObject

                charactersList = ArrayList()

                if(json2.int("success") == 1){
                    val JsonFinal = JSONObject(string.get())
                    val charsInfo = JsonFinal.getJSONArray("cita")
                    Log.d("error", "$charsInfo")

                    for(i in 0 until charsInfo.length()){
                        val nombre: String = charsInfo.getJSONObject(i).getString("nombre")
                        val edad: String = charsInfo.getJSONObject(i).getString("edad").toString()
                        val NoSeguro: String = charsInfo.getJSONObject(i).getString("NoSeguro")
                        val alergias: String = charsInfo.getJSONObject(i).getString("alergias")
                        val fechaCita: String = charsInfo.getJSONObject(i).getString("fechaCita")
                        val horaCita: String = charsInfo.getJSONObject(i).getString("horaCita")

                        charactersList.add(Paciente(nombre, edad, NoSeguro,  alergias, fechaCita, horaCita))
                    }

                    val adapter = PacienteAdapter(this.requireActivity(), charactersList, this.requireActivity())
                    recyclerCitas.adapter = adapter
                }else if(json2.int("success") == 0){
                    Toast.makeText(linearLayout.context, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(linearLayout.context, "Problemas en la conexion", Toast.LENGTH_SHORT).show()
                }

            }catch (ex:Exception){
                Toast.makeText(this.activity, "ha ocurrido un problema", Toast.LENGTH_SHORT).show()
                ex.printStackTrace()
                swipeRefreshLayout!!.isRefreshing = false
            }

            swipeRefreshLayout!!.isRefreshing = false
        }else{
            swipeRefreshLayout!!.isRefreshing = false
        }
    }

}
package com.example.medicalonlineapp.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.medicalonlineapp.R
import com.example.medicalonlineapp.adapters.PacienteAdapter
import com.example.medicalonlineapp.adapters.citasAdapter
import com.example.medicalonlineapp.http.httpRequest
import com.example.medicalonlineapp.paciente.Paciente
import kotlinx.android.synthetic.main.fragment_citas.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CitasAnteriores.newInstance] factory method to
 * create an instance of this fragment.
 */
class CitasAnteriores : Fragment() {
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private val HOSTING: String = "https://rossworld.000webhostapp.com/GetInfoAnteriores.php"
    private lateinit var charactersList: ArrayList<Paciente>
    private lateinit var adapter: citasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id== R.id.salir){
            Toast.makeText(this.activity, "Cerrando sesión", Toast.LENGTH_SHORT).show()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_citas_anteriores, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val databundle = arguments
        val user = databundle!!.getString("user")

        //Toast.makeText(this.activity, "$user", Toast.LENGTH_SHORT).show()

        swipeRefreshLayout = swipeToRefreshLayout

        recyclerCitas.setHasFixedSize(true)
        recyclerCitas.layoutManager = LinearLayoutManager(this.activity)


        swipeRefreshLayout!!.setOnRefreshListener {
            cargarCitas(user)
        }

        editFilter.addTextChangedListener { nombreFilter ->
            val citasFiltradas = this.charactersList.filter { cita ->
                cita.getNombre().toLowerCase().contains(nombreFilter.toString().toLowerCase())
            }
            adapter.updateList(citasFiltradas)
        }

        cargarCitas(user)
    }

    private fun isOnline(): Boolean{
        val on = (this.requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

        val activityNetwork: NetworkInfo? = on.activeNetworkInfo

        return activityNetwork != null && activityNetwork.isConnected
    }

    private fun cargarCitas(user : String?) {

        if(isOnline()){
            try {
                val json = JSONObject()

                json.put("getPacientesInfo", true)
                json.put("User", user)

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
                        val nombre: String = charsInfo.getJSONObject(i).getString("Nombre_paciente")
                        val sexo: String = charsInfo.getJSONObject(i).getString("Sexo_paciente").toString()
                        val domicilio: String = charsInfo.getJSONObject(i).getString("Domicilio_paciente")
                        val localidad: String = charsInfo.getJSONObject(i).getString("Localidad_paciente")
                        val numero_telefono: String = charsInfo.getJSONObject(i).getString("Numero_telefono")
                        val observaciones: String = charsInfo.getJSONObject(i).getString("Observaciones_paciente")
                        val fecha_nacimiento: String = charsInfo.getJSONObject(i).getString("Fecha_nacimiento")
                        val noSeguro: String = charsInfo.getJSONObject(i).getString("No_seguro_social")
                        val fechaCita: String = charsInfo.getJSONObject(i).getString("Fecha_cita")
                        val horaCita: String = charsInfo.getJSONObject(i).getString("Hora_cita")

                        charactersList.add(Paciente(nombre, sexo, domicilio, localidad, numero_telefono,observaciones, fecha_nacimiento,
                            noSeguro, fechaCita, horaCita))
                    }

                    adapter = citasAdapter(this.requireActivity(), charactersList, this.requireActivity())
                    recyclerCitas.adapter = adapter

                    /*val decorator = DividerItemDecoration(this.activity,RecyclerView.VERTICAL)
                    ResourcesCompat.getDrawable(resources, R.drawable.card_view_divider, null)?.let {
                        decorator.setDrawable(it)
                    }
                    recyclerCitas.addItemDecoration(decorator)*/
                }else if(json2.int("success") == 0){
                    Toast.makeText(this.activity, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this.activity, "Problemas en la conexion", Toast.LENGTH_SHORT).show()
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
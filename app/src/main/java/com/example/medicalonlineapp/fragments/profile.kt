package com.example.medicalonlineapp.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
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
import com.example.medicalonlineapp.MainActivity
import com.example.medicalonlineapp.R
import com.example.medicalonlineapp.adapters.PacienteAdapter
import com.example.medicalonlineapp.http.httpRequest
import com.example.medicalonlineapp.paciente.Paciente
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_agendar.*
import kotlinx.android.synthetic.main.fragment_citas.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.cedule
import kotlinx.android.synthetic.main.fragment_profile.dependence
import kotlinx.android.synthetic.main.fragment_profile.password
import kotlinx.android.synthetic.main.fragment_profile.phone
import kotlinx.android.synthetic.main.fragment_profile.saveButton
import kotlinx.android.synthetic.main.fragment_profile.specialty
import kotlinx.android.synthetic.main.fragment_profile.username
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class profile  : Fragment() {

    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private val HOSTING: String = "https://rossworld.000webhostapp.com/GetMedicoInfo.php"
    private lateinit var charactersList: ArrayList<Paciente>
    private lateinit var adapter: PacienteAdapter
    private var progresAsyncTask: profile.ProgressAsyncTask? = null
    val hosting: String = "https://rossworld.000webhostapp.com/"

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

        var databundle = arguments
        val user = databundle!!.getString("user")

        // txtUser!!.text = "Bienvenide" + user
        //Toast.makeText(this.activity, "$user", Toast.LENGTH_SHORT).show()
        //return inflater.inflate(R.layout.fragment_home, container, false)

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val databundle = arguments
        val user = databundle!!.getString("user")

        cargarPerfil()

        saveButton.setOnClickListener{//aqui dentro es donde pueden ir las llamadas de los botones, los set on click listener y de mas
            if(!username.text.isNullOrEmpty() && !password.text.isNullOrEmpty()
                && !cedule.text.isNullOrEmpty() && !phone.text.isNullOrEmpty() &&
                !dependence.text.isNullOrEmpty() && !specialty.text.isNullOrEmpty()){

                val json = JSONObject()
                json.put("insertMedico", true)
                json.put("Nombre_medico",username.text.trim())
                json.put("Pass", password.text.trim())
                json.put("Cedula_profesional", cedule.text.trim())
                json.put("Especialidad_medico", specialty.text.trim())
                json.put("Numero_telefonico_medico", phone.text.trim())
                json.put("Dependencia", dependence.text.trim())

                progresAsyncTask = ProgressAsyncTask()
                progresAsyncTask!!.execute("POST", hosting +"UpdateMedico.php", json.toString())

            }else{
                Toast.makeText(this.activity, "Campo vacio, compruebe que todos los campos se hayan llenado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isOnline(): Boolean{
        val on = (this.requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

        val activityNetwork: NetworkInfo? = on.activeNetworkInfo

        return activityNetwork != null && activityNetwork.isConnected
    }

    private fun cargarPerfil() {

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

                    for(i in 0 until charsInfo.length()){
                        username.setText(charsInfo.getJSONObject(i).getString("Nombre_medico").toString())
                        password.setText(charsInfo.getJSONObject(i).getString("Pass").toString())
                        dependence.setText(charsInfo.getJSONObject(i).getString("Dependencia").toString())
                        cedule.setText(charsInfo.getJSONObject(i).getString("Cedula_profesional").toString())
                        specialty.setText(charsInfo.getJSONObject(i).getString("Especialidad_medico").toString())
                        phone.setText(charsInfo.getJSONObject(i).getString("Numero_telefonico_medico").toString())

                        /*charactersList.add(Paciente(nombre, sexo, domicilio, localidad, numero_telefono,observaciones, fecha_nacimiento,
                            noSeguro, fechaCita, horaCita))*/
                    }

                    /*adapter = PacienteAdapter(this.requireActivity(), charactersList, this.requireActivity())
                    recyclerCitas.adapter = adapter*/

                }else if(json2.int("success") == 0){
                    Toast.makeText(this.activity, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this.activity, "Problemas en la conexion", Toast.LENGTH_SHORT).show()
                }

            }catch (ex:Exception){
                Toast.makeText(this.activity, "ha ocurrido un problema", Toast.LENGTH_SHORT).show()
                ex.printStackTrace()
                //swipeRefreshLayout!!.isRefreshing = false
            }

            //swipeRefreshLayout!!.isRefreshing = false
        }else{
            //swipeRefreshLayout!!.isRefreshing = false
        }
    }

    inner class ProgressAsyncTask: AsyncTask<String, Unit, String>(){

        val TIMEOUT = 50000

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String? {

            val url = URL(params[1])
            val httpClient = url.openConnection() as HttpURLConnection
            httpClient.readTimeout = TIMEOUT
            httpClient.connectTimeout = TIMEOUT
            httpClient.requestMethod = params[0]

            if(params[0] == "POST"){
                httpClient.instanceFollowRedirects = false
                httpClient.doOutput = true
                httpClient.doInput = true
                httpClient.useCaches = false
                httpClient.setRequestProperty("Content-Type","application/json; charset-utf-8")
            }
            try{
                if (params[0] == "POST"){
                    httpClient.connect()
                    val os = httpClient.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(os,"UTF-8"))
                    writer.write(params[2])
                    writer.flush()
                    writer.close()
                    os.close()
                }
                Log.d("e",""+httpClient.responseCode)
                if(httpClient.responseCode == HttpURLConnection.HTTP_OK){
                    val stream = BufferedInputStream(httpClient.inputStream)
                    val data: String = readStream(inputStream = stream)
                    println("aqui estamos $data")

                    return data
                } else if((httpClient.responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT)){
                    println("Error${httpClient.responseCode}")
                }
                else{

                    println("Error ${httpClient.responseCode}")
                }
            }catch(e:Exception){
                e.printStackTrace()
            }finally {
                httpClient.disconnect()
            }
            return null
        }

        fun readStream(inputStream: BufferedInputStream): String{
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it)}
            println("aqui estamos ${stringBuilder.toString()}")
            return stringBuilder.toString()
        }

        override fun onProgressUpdate(vararg values: Unit?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            if(!result.isNullOrBlank() && !result.isNullOrEmpty()) {
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)

                val json2: JsonObject = parser.parse(stringBuilder) as JsonObject
                if (json2.int("success") == 1) {
                    Toast.makeText(activity, "Datos actualizados", Toast.LENGTH_SHORT).show()
                }else if (json2.int("success") == 0){
                    Toast.makeText(activity, "Ocurrio un problema.", Toast.LENGTH_SHORT).show()
                }
            }
            super.onPostExecute(result)
            /*username.setText("")
            password.setText("")
            cedule.setText("")
            specialty.setText("")
            dependence.setText("")*/
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }

}
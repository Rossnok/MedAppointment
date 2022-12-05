package com.example.medicalonlineapp.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.medicalonlineapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_agendar.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class Agendar : Fragment() {

    val hosting: String = "https://rossworld.000webhostapp.com/"
    private var progresAsyncTask: ProgressAsyncTask? = null
    private var fecha:String? = null

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
        return inflater.inflate(R.layout.fragment_agendar, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val databundle = arguments
        val user = databundle!!.getString("user")
        Toast.makeText(activity, "$user", Toast.LENGTH_SHORT).show()

        agendarCita.setOnClickListener{//aqui dentro es donde pueden ir las llamadas de los botones, los set on click listener y de mas
            if(!txtNombreAgendar.text.isNullOrEmpty() && txtSexo.selectedItem != ""
                && ! txtSeguroSocial.text.isNullOrEmpty() && ! txtDomicilio.text.isNullOrEmpty() && ! txtFechaCita.text.isNullOrEmpty()
                && txtHoraCita.selectedItem != "" && !txtTelefono.text.isNullOrEmpty() && !txtFechaNacimiento.text.isNullOrEmpty()
                && !txtLocalidad.text.isNullOrEmpty() && !txtObservaciones.text.isNullOrEmpty()) {

                val json = JSONObject()
                if (txtSexo.selectedItem.toString().equals("Mujer")) {
                    json.put("Sexo_paciente", 1)
                } else {
                    json.put("Sexo_paciente", 0)
                }
                json.put("insertCita", true)
                json.put("Nombre_paciente", txtNombreAgendar.text)
                json.put("Domicilio_paciente", txtDomicilio.text)
                json.put("Localidad_paciente", txtLocalidad.text)
                json.put("Numero_telefono", txtTelefono.text)
                json.put("Observaciones_paciente", txtObservaciones.text)
                json.put("Fecha_nacimiento", txtFechaNacimiento.text)
                json.put("No_seguro_social", txtSeguroSocial.text)
                json.put("Fecha_cita", txtFechaCita.text)
                json.put("Hora_cita", txtHoraCita.selectedItem.toString() + ":00")
                progresAsyncTask = ProgressAsyncTask()
                progresAsyncTask!!.execute("POST", hosting + "InsertCita.php", json.toString())
            }else{
                Toast.makeText(this.requireContext(), "Llene todos los campos e intentelo de nuevo", Toast.LENGTH_SHORT).show()
            }
        }

        txtFechaCita.setOnClickListener{
            showDialogPicker()
        }

        txtFechaNacimiento.setOnClickListener{
            showDialogPickerNacimiento()
        }
    }

    private fun showDialogPicker() {
        val dataPicker = DatePickerFragment {day, month, year -> onDateSelected(day, month, year)}
        fragmentManager?.let { dataPicker.show(it,"Fecha de la cita") }
    }

     private fun onDateSelected(day:Int, month:Int, year:Int){
       txtFechaCita.setText("$year-${month+1}-$day")
    }

    private fun showDialogPickerNacimiento() {
        val dataPicker = DatePickerFragment {day, month, year -> onDateSelectedNacimiento(day, month, year)}
        fragmentManager?.let { dataPicker.show(it,"Fecha de la cita") }
    }

    private fun onDateSelectedNacimiento(day:Int, month:Int, year:Int){
        txtFechaNacimiento.setText("$year-${month+1}-$day")
    }

    inner class ProgressAsyncTask: AsyncTask<String, Unit, String>(){

        val TIMEOUT = 50000

        override fun onPreExecute() {
            agendarCita.isEnabled = false
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

                        val parser: Parser = Parser()
                        val stringBuilder: StringBuilder = StringBuilder(data)

                        val json2: JsonObject = parser.parse(stringBuilder) as JsonObject
                        if(json2.int("success") == 1){

                        }

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
                    Toast.makeText(context, "El registro de su cita se relaizo con exito!", Toast.LENGTH_SHORT).show()
                }else if (json2.int("success") == 0){
                    Toast.makeText(context, "Ocurrio un problema durante el registro, contacte a sopporte tecnico", Toast.LENGTH_SHORT).show()
                }
            }
            super.onPostExecute(result)
            agendarCita.isEnabled = true
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }

}
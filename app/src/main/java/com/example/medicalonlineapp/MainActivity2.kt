package com.example.medicalonlineapp


import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kotlinx.android.synthetic.main.activity_main2.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity2 : AppCompatActivity() {

    val hosting: String = "https://rossworld.000webhostapp.com/"
    private var progresAsyncTask: ProgressAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        supportActionBar!!.hide()

        saveButton.setOnClickListener{
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
                progresAsyncTask!!.execute("POST", hosting +"InsertMedico.php", json.toString())

            }else{
                Toast.makeText(applicationContext, "Campo vacio, compruebe que todos los campos se hayan llenado correctamente", Toast.LENGTH_SHORT).show()
            }
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
                    Toast.makeText(applicationContext, "Te has registrado con exito", Toast.LENGTH_SHORT).show()
                    val intento: Intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intento)
                }else if (json2.int("success") == 0){
                    Toast.makeText(applicationContext, "Ocurrio un problema durante el registro.", Toast.LENGTH_SHORT).show()
                }
            }
            super.onPostExecute(result)
            username.setText("")
            password.setText("")
            cedule.setText("")
            specialty.setText("")
            dependence.setText("")
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }

}
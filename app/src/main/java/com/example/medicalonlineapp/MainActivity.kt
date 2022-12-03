package com.example.medicalonlineapp

import android.content.Intent
import android.opengl.Visibility
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.medicalonlineapp.principalView.PrincipalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_agendar.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var progressAsyncTask: ProgressAsyncTask? = null
    private val hosting = "https://rossworld.000webhostapp.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()
        loginButton.setOnClickListener(){
           if(!username.text.isNullOrEmpty() && !password.text.isNullOrEmpty()){
               val json = JSONObject()
               json.put("loginUser", true)
               json.put("Nombre_medico", username.text)
               json.put("Pass", password.text)

               progressAsyncTask = ProgressAsyncTask()
               progressAsyncTask!!.execute("POST", hosting +"UserLogin.php", json.toString())
           }else{
               Toast.makeText(applicationContext, "Asegurate de llenar los campos necesarios", Toast.LENGTH_SHORT).show()
           }
        }

        signupText.setOnClickListener(){
            register()
        }
    }

    private fun register (){
        val intento: Intent = Intent(applicationContext, MainActivity2::class.java)
        startActivity(intento)
    }

    inner class ProgressAsyncTask: AsyncTask<String, Unit, String>(){

        val TIMEOUT = 50000

        override fun onPreExecute() {
            loginButton.isEnabled = false
            progresLoginBar.visibility = View.VISIBLE
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
                    val jsonFinal = JSONObject(result)
                    val datosUsuario = jsonFinal.getJSONArray("credenciales")
                    val usuario = datosUsuario.getJSONObject(0).getString("Nombre_medico")
                    val contra = datosUsuario.getJSONObject(0).getString("Pass")
                    Log.e("user", usuario)
                    Log.e("user", contra)
                    Log.e("user", username.text.toString())
                    Log.e("user", password.text.toString())

                    if(username.text.toString() == usuario.toString() && password.text.toString() == contra.toString()){
                        Toast.makeText(applicationContext, "Bienvenido!", Toast.LENGTH_SHORT).show()
                        var intent: Intent = Intent(applicationContext, PrincipalView::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(applicationContext, "Comprueba tus credenciales e intentalo nuevamente", Toast.LENGTH_SHORT).show()
                    }
                }else if (json2.int("success") == 0 ){
                    Toast.makeText(applicationContext, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            super.onPostExecute(result)
            loginButton.isEnabled = true
            progresLoginBar.visibility = View.INVISIBLE
        }

        override fun onCancelled() {
            super.onCancelled()
            progresLoginBar.visibility = View.INVISIBLE
            loginButton.isEnabled = true
        }
    }
}
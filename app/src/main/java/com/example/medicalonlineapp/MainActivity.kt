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

    private var progressAsyncTask: ProgressAsyncTask? = null // variable de tipo ProgressAsyncTask
    private val hosting = "https://rossworld.000webhostapp.com/"// Direccion del hosting donde se encuentra la base de datos

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()//ocultar la action bar de las ventanas

        //listener del boton loginButton(ingresar) que manda llamar la inner class ProgressAsyncTask
        loginButton.setOnClickListener(){
           if(!username.text.isNullOrEmpty() && !password.text.isNullOrEmpty()){// si los campos contienen datos
               val json = JSONObject() //json que se envia al php contenido en el hosting de la base de datos
               json.put("loginUser", true)//campo login user con valor true
               json.put("Nombre_medico", username.text)//campo nombre medico con el nombre de usuario
               json.put("Pass", password.text)// campo pass con el password del usuario

               progressAsyncTask = ProgressAsyncTask()

               progressAsyncTask!!.execute(
                   "POST",
                   hosting +"UserLogin.php",
                   json.toString())// ejecucion del objeto de tipo ProgressAsyncTask

           }else{ // si algun campo de informacion esta vacio
               Toast.makeText(applicationContext, "Asegurate de llenar los campos necesarios", Toast.LENGTH_SHORT).show()
           }
        }

        // evento onclick del Textview signup
        signupText.setOnClickListener(){
            register()
        }
    }

    private fun register (){ // funcion de cambio de ventana a MainActivity2
        val intento: Intent = Intent(applicationContext, MainActivity2::class.java)
        startActivity(intento)
    }

        //inicio de la inner class ProgressAsykTask
    inner class ProgressAsyncTask: AsyncTask<String, Unit, String>(){

        val TIMEOUT = 50000 // tiempo limite de respuesta para el servidor

        override fun onPreExecute() {//antes de la ejecucion
            loginButton.isEnabled = false//loginButton desabilitado
            progresLoginBar.visibility = View.VISIBLE//progressLoginBar visible
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String? {//ejecucion de fondo

            val url = URL(params[1])// url del hosting
            val httpClient = url.openConnection() as HttpURLConnection // conexion Http
            httpClient.readTimeout = TIMEOUT// tiempo limite de lectura
            httpClient.connectTimeout = TIMEOUT // tiempo limite de respuesta de conexion
            httpClient.requestMethod = params[0] //metodo request usado (POST)

            if(params[0] == "POST"){ // si el metodo request = post
                httpClient.instanceFollowRedirects = false // redireccion no permitida
                httpClient.doOutput = true // realizar salidas permitido
                httpClient.doInput = true // realizar entradas permitido
                httpClient.useCaches = false // usar la memoria cache no permitido
                httpClient.setRequestProperty("Content-Type","application/json; charset-utf-8") // propiedades del metodo request
            }
            try{ //intentar
                if (params[0] == "POST"){ // si el metodo request es post
                    httpClient.connect() // conexion http con el cliente
                    val os = httpClient.outputStream // salida de datos
                    val writer = BufferedWriter(OutputStreamWriter(os,"UTF-8")) // charset utilizado en los datos
                    writer.write(params[2])
                    writer.flush()
                    writer.close()
                    os.close()
                }
                if(httpClient.responseCode == HttpURLConnection.HTTP_OK){ // si la respuesta del sevidoor fue OK
                    val stream = BufferedInputStream(httpClient.inputStream) // se leen los datos de entrada
                    val data: String = readStream(inputStream = stream) // se almacenan los datos de entrada

                    return data // retorno de los datos
                } else if((httpClient.responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT)){ // si el tiempo de respuesta se excedio
                    println("Error${httpClient.responseCode}") // se imprime el codigo de error
                }else{ //si no
                    println("Error ${httpClient.responseCode}") // se imprime el codigo de error
                }
            }catch(e:Exception){
                e.printStackTrace() // error recuperado en caso de error
            }finally {
                httpClient.disconnect()// al final se cierra la conexion
            }
            return null
        }

        fun readStream(inputStream: BufferedInputStream): String{ // lectura de datos recuperados desde el servidor
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it)}
            return stringBuilder.toString()
        }

        override fun onProgressUpdate(vararg values: Unit?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {// despues de la ejecucion
            if(!result.isNullOrBlank() && !result.isNullOrEmpty()) { // si los resultados no estan en blanco, son null y no estan vacios
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)

                val json2: JsonObject = parser.parse(stringBuilder) as JsonObject
                if (json2.int("success") == 1) {// si el campo success recuperado del servidor es = 1
                    val jsonFinal = JSONObject(result) // objeto json con la respuesta del servidor
                    val datosUsuario = jsonFinal.getJSONArray("credenciales") // datos obtenidos de credenciales
                    val usuario = datosUsuario.getJSONObject(0).getString("Nombre_medico") // nombre del usuario
                    val contra = datosUsuario.getJSONObject(0).getString("Pass") // password del usuario


                    if(username.text.trim().toString() == usuario.toString()
                        && password.text.toString().trim() == contra.toString()){ // si los nombres de usuario y passwords coinciden
                        Toast.makeText(applicationContext, "Bienvenido!", Toast.LENGTH_SHORT).show()// mensaje de inicio
                        var intent: Intent = Intent(applicationContext, PrincipalView::class.java).apply {// lleva a la interfaz Principal View
                            putExtra("user", username.text.toString())// envia el nombre de usuario a la siguiente ventana
                            startActivity(this)//se inicia la actividad
                        }

                    }else{// si los datos no coinciden
                        Toast.makeText(
                            applicationContext,
                            "Comprueba tus credenciales e intentalo nuevamente",
                            Toast.LENGTH_SHORT).show() // mensaje de error
                    }
                }else if (json2.int("success") == 0 ){// si el campo success es = 0
                    Toast.makeText(applicationContext, "Usuario no encontrado", Toast.LENGTH_SHORT).show() // mensaje de error
                }
            }
            super.onPostExecute(result)
            loginButton.isEnabled = true // loginButton habilitado
            progresLoginBar.visibility = View.INVISIBLE // progresLoginBar invisible
        }

        override fun onCancelled() {// si el proceso se cancela
            super.onCancelled()
            progresLoginBar.visibility = View.INVISIBLE
            loginButton.isEnabled = true
        }
    }
}
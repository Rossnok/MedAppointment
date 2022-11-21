package com.example.medicalonlineapp.http


import android.os.AsyncTask
import android.util.Log
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class httpRequest(callBack: (String?) -> Unit): AsyncTask<String, Unit,String>() {

    var callBack: (String) -> Unit = callBack //guardar constructor en una variable

    private val TIEMPO: Int = 5000

    override fun onPreExecute() {
        super.onPreExecute()
        Log.d("hilo", "se esta ejecutando")
    }

    //los parametros se reciben cuando se crea el constructor
    override fun doInBackground(vararg parametros: String?): String {
        var url = URL(parametros[1])
        var conexionWeb: HttpURLConnection? = url.openConnection() as HttpURLConnection //tranformacion de la url a conexion para el webService

        //propiedades de la conexion

        conexionWeb?.readTimeout = TIEMPO
        conexionWeb?.connectTimeout = TIEMPO //muerte de la conexion si se pasa del tiempo
        conexionWeb?.requestMethod = parametros[0]
        conexionWeb?.instanceFollowRedirects = false
        conexionWeb?.doInput = true
        conexionWeb?.doOutput = true
        conexionWeb?.useCaches = false
        conexionWeb?.setRequestProperty("Content-Type","application/json; charset-utf-8")

        //Envio de datos al iniciar la pagina, primero se envian los datos del json convertido a string a la conexion web
        return try {

            conexionWeb?.connect()

            val escribir: OutputStream? = conexionWeb?.outputStream

            val escritor = BufferedWriter(OutputStreamWriter(escribir, "UTF-8"))

            escritor.write(parametros[2])

            escritor.flush() // se usa para liberar memoria, temrmmina el envio de datos
            escritor.close()
            escribir?.close()

            if (conexionWeb?.responseCode == HttpURLConnection.HTTP_OK) {
                val stream = BufferedInputStream(conexionWeb?.inputStream) // buffered se utiliza para escribir
                val data:String = readStream(inputStream = stream)
                println("aqui estamos $data")
                return data
            } else if (conexionWeb?.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return "Exeption"
            } else {
                return "Exeption"
            }
        } catch (e: Exception) {
            e.printStackTrace().toString()
        } finally {
            conexionWeb?.disconnect()
        }

    }

    private fun readStream(inputStream: BufferedInputStream):String{
        return try {
            val lectorBuffer = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            lectorBuffer.forEachLine{
                stringBuilder.append(it)
            }
            stringBuilder.toString()
        }catch (ex:Exception){
            ex.printStackTrace()
            ""
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.d("Web service", "trajo: $result")

        if(result.equals("Exception", ignoreCase = true)
            || result.equals("unsuccessful", ignoreCase = true)
            || result.isNullOrEmpty()){
            Log.d("WEBSERVICE","NO SE QUE PONER")
        }
        else{
            callBack(result!!)
        }
    }
}
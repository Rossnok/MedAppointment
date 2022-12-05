package com.example.medicalonlineapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.medicalonlineapp.MainActivity
import com.example.medicalonlineapp.R
import com.example.medicalonlineapp.principalView.PrincipalView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class GoogleMapsInfo : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap

    private var latitud: String? = null
    private var longitud : String? = null
    private var getLocation: String? = null
    private var nombrePaciente :String? = null
    private lateinit var coordinates: LatLng

    private var start:String = ""
    private var end:String = ""

    private var progressAsyncTask: ProgressAsyncTask? = null
    private val hosting = "https://rossworld.000webhostapp.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_maps_info)

        supportActionBar!!.hide()

        getLocation = intent.getStringExtra("domicilio")+ " " + intent.getStringExtra("localidad")
        nombrePaciente = intent.getStringExtra("nombre")

        getCoordenatesLocation(getLocation!!)

        createFragment()
    }

    private fun getCoordenatesLocation(direccion:String) {
        val json = JSONObject()
        json.put("askLocation", true)
        json.put("address", direccion)

        progressAsyncTask = ProgressAsyncTask()
        progressAsyncTask!!.execute("POST", hosting +"GetGeoLocation.php", json.toString())
    }

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.googleMapsFragmentInfo) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()

    }

    private fun createMarker() {
            var marker = MarkerOptions().position(coordinates).title("Ubicacion de $nombrePaciente")
            map.addMarker(marker)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 16f), 3000, null
            )
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para activar la localizacion ve a ajustes y activa los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
        latitud = location.latitude.toString()
        longitud = location.longitude.toString()
        //aqui se pueden obtener las coordenadas de la ubicacion en tiempo real
    }

    fun createRute(){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRetrofit().create(ApiService::class.java)
                .getRoute("5b3ce3597851110001cf6248f3c825ce3ccd4c92b34f8f6ad3e53ba0", start, end)
            if(call.isSuccessful){
                //aqui van las coordenadas de inicio y final
                drawRoute(call.body())
            }else{

            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLine = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach{
            polyLine.add(LatLng(it[1], it[0]))
        }

        runOnUiThread {
            val poly = map.addPolyline(polyLine) }
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //obtencion de las coordenadas desde php en el servidor
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
                    val jsonFinal = JSONObject(result)
                    val  coordenadas = jsonFinal.getJSONArray("coordinates")
                    val latitud = coordenadas.getJSONObject(0).getString("latitude")
                    val longitud = coordenadas.getJSONObject(0).getString("longitude")
                    Log.e("coordinates", latitud)
                    Log.e("coordinates", longitud)

                    coordinates = LatLng(latitud.toDouble(), longitud.toDouble())
                    createMarker()

                }else if (json2.int("success") == 0 ){
                    Toast.makeText(applicationContext, "Error al obtener las coordenadas", Toast.LENGTH_SHORT).show()
                }
            }
            super.onPostExecute(result)
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }
}
package com.example.medicalonlineapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.medicalonlineapp.principalView.PrincipalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity() {
    private var confirmed:Boolean = false
    private var btnLogin: Button? = null
    //private var btnSingin: Button? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin = findViewById(R.id.btnLogin)
        //btnSingin = findViewById(R.id.btnRegistrar)

        supportActionBar!!.hide()


        btnLogin!!.setOnClickListener(){
            confirmedUser()
        }

        /*btnSingin!!.setOnClickListener(){
            register()
        }*/
    }

    private fun confirmedUser (){
        //aqui se pone la logica de comprobacion de usuario
        confirmed = true
        if(confirmed == true){
            var intent: Intent = Intent(applicationContext, PrincipalView::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun register (){
        val intento: Intent = Intent(applicationContext, MainActivity2::class.java)
        startActivity(intento)
        finish()
    }
}
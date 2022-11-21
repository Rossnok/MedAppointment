package com.example.medicalonlineapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.medicalonlineapp.R
import kotlinx.android.synthetic.main.fragment_agendar.*



class Agendar : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun mostrarMensaje(){
        Toast.makeText(layoutInflater.context.applicationContext, "este es el mensaje del boton", Toast.LENGTH_SHORT).show()
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

        agendarCita.setOnClickListener{//aqui dentro es donde pueden ir las llamadas de los botones, los set on click listener y de mas
            mostrarMensaje()
        }

        txtFechaCita.setOnClickListener{
            showDialogPicker()
        }
    }

    private fun showDialogPicker() {
        val dataPicker = DatePickerFragment {day, month, year -> onDateSelected(day, month, year)}
        fragmentManager?.let { dataPicker.show(it,"Fecha de la cita") }
    }

     private fun onDateSelected(day:Int, month:Int, year:Int){
        txtFechaCita.setText("$day/${month+1}/$year")
    }

}
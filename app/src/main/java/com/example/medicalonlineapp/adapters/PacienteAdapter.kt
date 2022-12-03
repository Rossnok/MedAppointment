package com.example.medicalonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalonlineapp.R
import com.example.medicalonlineapp.fragments.Agendar
import com.example.medicalonlineapp.paciente.Paciente
import com.gdacciaro.iOSDialog.iOSDialogBuilder


class PacienteAdapter (mCtx: Context, pacientesList:List<Paciente>, activity: Activity) :
    RecyclerView.Adapter<PacienteAdapter.ProductViewHolder>() {

    private val mCtx = mCtx
    private val activity:Activity

    private var pacienteList : List<Paciente>

    init{
        this.activity = activity

        this.pacienteList = pacientesList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProductViewHolder{
        val inflate : LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = inflate.inflate(R.layout.card_view_citas_programadas, parent,false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int){
        val paciente: Paciente = pacienteList[position]

        holder.nombre!!.text = paciente.getNombre()
        holder.edad!!.text = paciente.getEdad()
        holder.alergias!!.text = paciente.getAlergias()
        holder.fechaCita!!.text = paciente.getFechaCita()
        holder.horaCita!!.text = paciente.getHoraCita()

        holder.itemView.setOnClickListener(){
            val nombre = pacienteList[position].getNombre()
            val edad = pacienteList[position].getEdad()
            val alergias = pacienteList[position].getAlergias()
            val fechaCita = pacienteList[position].getFechaCita()
            val horaCita = pacienteList[position].getHoraCita()
            showDialog(nombre, edad, alergias, fechaCita, horaCita)
        }

    }

    override fun getItemCount(): Int {
        return pacienteList.size
    }

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var nombre: TextView? = null
        var edad: TextView? = null
        var noSeguro: TextView? = null
        var alergias: TextView? = null
        var fechaCita: TextView? = null
        var horaCita: TextView? = null

        init {
            //Inicializar las variables anteriores con findByViewId
            nombre = itemView.findViewById(R.id.nombrePacienteCardViewCitas)
            edad = itemView.findViewById(R.id.edadPacienteCardViewCitas)
           // noSeguro = itemView.findViewById(R.id.)
            alergias = itemView.findViewById(R.id.alergiasPacienteCardViewCitas)
            fechaCita = itemView.findViewById(R.id.fechaPacienteCardViewCitas)
            horaCita = itemView.findViewById(R.id.horaPacienteCardViewCitas)
        }

    }

    private fun showDialog(nombre:String, edad:String, alergias:String, fechaCita:String, horaCita:String) {
        iOSDialogBuilder(activity)
            .setTitle("Datos Del Paciente")
            .setSubtitle("Informacion:")
            .setBoldPositiveLabel(true)
            .setCancelable(true)
            .setPositiveListener("consultar"){ dialog ->

                val intent = Intent(activity, Agendar::class.java)
                intent.putExtra("nom", nombre)
                intent.putExtra("eda", edad)
                intent.putExtra("alerg", alergias)
                intent.putExtra("fecha", fechaCita)
                intent.putExtra("hora", horaCita)

                dialog.dismiss()
            }
            .setNegativeListener("Cancelar"){dialog ->
                dialog.dismiss()
            }
            .build().show()
    }

    fun updateList(pacientesList:List<Paciente>){
        this.pacienteList = pacientesList
        notifyDataSetChanged()
    }

}
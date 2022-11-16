package com.example.medicalonlineapp.paciente

class Paciente {

    private var nombre = ""
    private var edad = ""
    private var NoSeguro = ""
    private var alergias = ""
    private var fechaCita = ""
    private var horaCita = ""

    constructor(
        nombre:String,
        edad:String,
        NoSeguro:String,
        alergias:String,
        fechaCita:String,
        horaCita:String
    ){
        this.nombre = nombre
        this.edad = edad
        this.NoSeguro = NoSeguro
        this.alergias = alergias
        this.fechaCita = fechaCita
        this.horaCita = horaCita
    }

    fun getNombre(): String{
        return this.nombre
    }

    fun getEdad(): String{
        return this.edad
    }

    fun getNoSeguro(): String{
        return this.NoSeguro
    }

    fun getAlergias(): String{
        return this.alergias
    }

    fun getFechaCita(): String{
        return this.fechaCita
    }

    fun getHoraCita(): String{
        return this.horaCita
    }
}
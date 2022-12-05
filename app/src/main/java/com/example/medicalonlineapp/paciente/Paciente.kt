package com.example.medicalonlineapp.paciente

class Paciente {

    private var nombre = ""
    private var NoSeguro = ""
    private var observaciones = ""
    private var fechaCita = ""
    private var horaCita = ""
    private var sexo_paciente = ""
    private var numero_telefono = ""
    private var fecha_nacimiento = ""
    private var domicilio = ""
    private var localidad = ""

    constructor(
        nombre:String,
        sexo_paciente:String,
        domicilio:String,
        localidad:String,
        numero_telefono:String,
        observaciones:String,
        fecha_nacimiento:String,
        NoSeguro:String,
        fechaCita:String,
        horaCita:String,
    ){
        this.nombre = nombre
        this.NoSeguro = NoSeguro
        this.observaciones = observaciones
        this.fechaCita = fechaCita
        this.horaCita = horaCita
        this.sexo_paciente = sexo_paciente
        this.numero_telefono = numero_telefono
        this.fecha_nacimiento = fecha_nacimiento
        this.domicilio = domicilio
        this.localidad = localidad
    }

    fun  getNumeroTelefono(): String{
        return this.numero_telefono
    }

    fun getFechaNacimiento(): String{
        return this.fecha_nacimiento
    }

    fun getLocalidad(): String{
        return this.localidad
    }

    fun getDomicilio(): String{
        return this.domicilio
    }

    fun getSexo(): String{
        return this.sexo_paciente
    }

    fun getNombre(): String{
        return this.nombre
    }

    fun getNoSeguro(): String{
        return this.NoSeguro
    }

    fun getAlergias(): String{
        return this.observaciones
    }

    fun getFechaCita(): String{
        return this.fechaCita
    }

    fun getHoraCita(): String{
        return this.horaCita
    }
}
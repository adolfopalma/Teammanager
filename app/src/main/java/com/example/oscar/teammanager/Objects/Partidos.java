package com.example.oscar.teammanager.Objects;

import java.io.Serializable;

/**
 * Created by ptmarketing05 on 19/05/2017.
 */

public class Partidos implements Serializable{

    int id;
    String nombre;
    String horaPartido;
    String diaPartido;
    String rutaFoto;
    String administrador;
    String fechaCreacion;



    public Partidos(int id, String nombre,String administrador,String fechaCreacion, String diaPartido, String horaPartido, String rutaFoto) {
        this.id = id;
        this.nombre = nombre;
        this.administrador = administrador;
        this.fechaCreacion = fechaCreacion;
        this.diaPartido = diaPartido;
        this.horaPartido = horaPartido;
        this.rutaFoto = rutaFoto;
    }
}

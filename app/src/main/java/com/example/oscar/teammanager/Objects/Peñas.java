package com.example.oscar.teammanager.Objects;

import java.io.Serializable;

/**
 * Created by oscar on 27/04/2017.
 */

public class Peñas implements Serializable {
    int id;
    String nombre;
    String horaPartido;
    String diaPartido;
    String rutaFoto;
    String administrador;
    String nomAdministrador;
    String fechaCreacion;



    public Peñas(int id, String nombre,String administrador,String fechaCreacion, String diaPartido, String horaPartido, String rutaFoto, String nomAdministrador) {
        this.id = id;
        this.nombre = nombre;
        this.administrador = administrador;
        this.fechaCreacion = fechaCreacion;
        this.diaPartido = diaPartido;
        this.horaPartido = horaPartido;
        this.rutaFoto = rutaFoto;
        this.nomAdministrador = nomAdministrador;
    }

    public Peñas(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHoraPartido() {
        return horaPartido;
    }

    public void setHoraPartido(String horaPartido) {
        this.horaPartido = horaPartido;
    }

    public String getDiaPartido() {
        return diaPartido;
    }

    public void setDiaPartido(String diaPartido) {
        this.diaPartido = diaPartido;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public String getAdministrador() {
        return administrador;
    }

    public void setAdministrador(String administrador) {
        this.administrador = administrador;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNomAdministrador() {
        return nomAdministrador;
    }

    public void setNomAdministrador(String nomAdministrador) {
        this.nomAdministrador = nomAdministrador;
    }

}

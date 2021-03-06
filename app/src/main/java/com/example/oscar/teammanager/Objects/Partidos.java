package com.example.oscar.teammanager.Objects;

import java.io.Serializable;

/**
 * Created by oscar on 19/05/2017.
 */

public class Partidos implements Serializable{

    int id;
    int CodPeña;
    String nomPeña;
    String fechaPartido;
    String resultado;
    String ganador;
    String rutaFoto;

    public Partidos(int id, int CodPeña, String nomPeña, String fechaPartido, String resultado, String ganador, String rutaFoto) {
        this.id = id;
        this.CodPeña = CodPeña;
        this.nomPeña = nomPeña;
        this.fechaPartido = fechaPartido;
        this.resultado = resultado;
        this.ganador = ganador;
        this.rutaFoto = rutaFoto;
    }

    public Partidos() {
    }

    public String getNomPeña() {
        return nomPeña;
    }

    public void setNomPeña(String nomPeña) {
        this.nomPeña = nomPeña;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodPeña() {
        return CodPeña;
    }

    public void setCodPeña(int codPeña) {
        CodPeña = codPeña;
    }

    public String getFechaPartido() {
        return fechaPartido;
    }

    public void setFechaPartido(String fechaPartido) {
        this.fechaPartido = fechaPartido;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }
}

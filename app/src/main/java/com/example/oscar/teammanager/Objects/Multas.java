package com.example.oscar.teammanager.Objects;

import java.io.Serializable;

/**
 * Created by ptmarketing05 on 23/05/2017.
 */

public class Multas implements Serializable {

    int codMulta;
    int cantidad;
    String tipoMulta;
    String codigoJug;
    String estado;


    public Multas(int codMulta,int cantidad,String tipoMulta,String codigoJug,String estado) {
        this.codMulta = codMulta;
        this.cantidad = cantidad;
        this.tipoMulta = tipoMulta;
        this.codigoJug = codigoJug;
        this.estado = estado;
    }


    public Multas() {
    }

    public int getCodMulta() {
        return codMulta;
    }

    public void setCodMulta(int codMulta) {
        this.codMulta = codMulta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipoMulta() {
        return tipoMulta;
    }

    public void setTipoMulta(String tipoMulta) {
        this.tipoMulta = tipoMulta;
    }

    public String getCodigoJug() {
        return codigoJug;
    }

    public void setCodigoJug(String codigoJug) {
        this.codigoJug = codigoJug;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
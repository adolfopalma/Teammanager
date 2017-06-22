package com.example.oscar.teammanager.Objects;

import java.io.Serializable;

/**
 * Created by oscar on 17/05/2017.
 */

public class Estadisticas implements Serializable {
    String codjug, correoJug;
    int goles;
    int tarjetaAmarilla;
    int tarjetaRoja;
    int codPeña;
    int partidosJugados;
    int partidosGanados;
    int partidosPerdidos;
    int partidosEmpatados;
    int puntos;

    public Estadisticas(int goles, int tarjetaAmarilla, int tarjetaRoja, int codPeña, int partidosJugados, int partidosGanados, int partidosPerdidos, int partidosEmpatados, int puntos) {
        this.goles = goles;
        this.tarjetaAmarilla = tarjetaAmarilla;
        this.tarjetaRoja = tarjetaRoja;
        this.codPeña = codPeña;
        this.partidosJugados = partidosJugados;
        this.partidosGanados = partidosGanados;
        this.partidosPerdidos = partidosPerdidos;
        this.partidosEmpatados = partidosEmpatados;
        this.puntos = puntos;
    }



    public Estadisticas(String correoJug, int goles, int tarjetaAmarilla, int tarjetaRoja, int codPeña, int partidosJugados, int partidosGanados, int partidosPerdidos, int partidosEmpatados, int puntos) {
        this.correoJug = correoJug;
        this.goles = goles;
        this.tarjetaAmarilla = tarjetaAmarilla;
        this.tarjetaRoja = tarjetaRoja;
        this.codPeña = codPeña;
        this.partidosJugados = partidosJugados;
        this.partidosGanados = partidosGanados;
        this.partidosPerdidos = partidosPerdidos;
        this.partidosEmpatados = partidosEmpatados;
        this.puntos = puntos;
    }

    public Estadisticas() {
    }

    public String getCorreoJug() {
        return correoJug;
    }

    public void setCorreoJug(String correoJug) {
        this.correoJug = correoJug;
    }

    public String getCodjug() {
        return codjug;
    }

    public void setCodjug(String codjug) {
        this.codjug = codjug;
    }

    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        this.goles = goles;
    }

    public int getTarjetaAmarilla() {
        return tarjetaAmarilla;
    }

    public void setTarjetaAmarilla(int tarjetaAmarilla) {
        this.tarjetaAmarilla = tarjetaAmarilla;
    }

    public int getTarjetaRoja() {
        return tarjetaRoja;
    }

    public void setTarjetaRoja(int tarjetaRoja) {
        this.tarjetaRoja = tarjetaRoja;
    }

    public int getCodPeña() {
        return codPeña;
    }

    public void setCodPeña(int codPeña) {
        this.codPeña = codPeña;
    }

    public int getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(int partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public int getPartidosGanados() {
        return partidosGanados;
    }

    public void setPartidosGanados(int partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public int getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public void setPartidosPerdidos(int partidosPerdidos) {
        this.partidosPerdidos = partidosPerdidos;
    }

    public int getPartidosEmpatados() {
        return partidosEmpatados;
    }

    public void setPartidosEmpatados(int partidosEmpatados) {
        this.partidosEmpatados = partidosEmpatados;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }



}

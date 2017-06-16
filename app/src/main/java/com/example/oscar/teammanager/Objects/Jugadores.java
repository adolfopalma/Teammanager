package com.example.oscar.teammanager.Objects;

import java.io.Serializable;

/**
 * Created by oscar on 27/01/2017.
 */

public class Jugadores implements Serializable{
    int id;
    String nombre;
    String nick;
    String edad;
    String correo;
    String pass;
    String tipoJug;
    String rutaFoto;



    public Jugadores(int id, String nombre, String nick, String edad, String correo, String pass, String tipoJug, String rutaFoto) {
        this.id = id;
        this.nombre = nombre;
        this.nick = nick;
        this.edad = edad;
        this.correo = correo;
        this.pass = pass;
        this.tipoJug = tipoJug;
        this.rutaFoto = rutaFoto;
    }



    public Jugadores(){
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
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTipoJug() {
        return tipoJug;
    }

    public void setTipoJug(String tipoJug) {
        this.tipoJug = tipoJug;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }
}

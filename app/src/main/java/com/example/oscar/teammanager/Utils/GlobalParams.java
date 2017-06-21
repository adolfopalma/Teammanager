package com.example.oscar.teammanager.Utils;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;

import java.util.ArrayList;

/**
 * Created by ptmarketing04 on 31/03/2017.
 */

public class GlobalParams {

    //Datos conexion
    public static String IP_Server = "http://iesayala.ddns.net/19ramajo";
    //public static String IP_Server = "http://192.168.0.2";
    public static String url_consulta = IP_Server+"/consulta.php";
    public static String url_insert = IP_Server+"/prueba.php";

    //Datos usuario
    public static String correoUsuario = "";

    //Datos notificaciones
    public static ArrayList<Peñas> diaPartido = null;

    //Datos partido
    public static int MarcadorOscuro = 0;
    public static int MarcadorClaro = 0;
    public static ArrayList<Jugadores> equipo1 = null;
    public static ArrayList<Jugadores> equipo2 = null;

    //Datos peña en partido
    public static int codPeña = 0;

    //Datos multa
    public static int multaRetraso = 3;
    public static int multaFaltaAsistencia = 5;

    //Datos administradores
    public static ArrayList<String> administradores = null;

    //Datos editar equipo
    public static ArrayList<Jugadores> listaJugadores = null;
}

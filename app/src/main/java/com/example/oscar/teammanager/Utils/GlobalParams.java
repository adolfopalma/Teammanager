package com.example.oscar.teammanager.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by oscar on 31/03/2017.
 */

public class GlobalParams {

    private static int color;
    private static Paint paint;
    private static Rect rect;
    private static RectF rectF;
    private static Bitmap result;
    private static Canvas canvas;
    private  static float roundPx;

    //Datos conexion
    public static String IP_Server = "http://iesayala.ddns.net/19ramajo";
    public static String url_consulta = IP_Server+"/consulta.php";
    public static String url_insert = IP_Server+"/prueba.php";

    //Datos usuario
    public static String correoUsuario = "";
    public static String passUsu = "";
    public static Bitmap foto;

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


    //metodo para codificar la imagen en base 64 y pasarla como string a la base de datos
    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    //metodo para decodificar imagenes obtenidas
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    //metodo para dar forma redondeada a la imagenes
    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);

        color = 0xff424242;
        paint = new Paint();
        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        rectF = new RectF(rect);
        roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return result;
    }
}

package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.R;

/**
 * Created by ptmarketing05 on 11/05/2017.
 */

public class Item_jugador extends LinearLayout {
    TextView nombreJugador;
    ImageView imageJug;
    private Bitmap result;


    public Item_jugador(Context context) {
        super(context);
        inflate(context, R.layout.item_jugadores, this);

        nombreJugador = (TextView) findViewById(R.id.jugador_nombre);
        imageJug = (ImageView) findViewById(R.id.peña_img);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }



    public void setJugador(Jugadores j) {
        nombreJugador.setText(""+j.getNombre());
        result = decodeBase64(j.getRutaFoto());

        if(result != null) {
            imageJug.setImageBitmap(result);
        }else{
            imageJug.setImageResource(R.mipmap.ic_launcher_round);
        }

    }
}

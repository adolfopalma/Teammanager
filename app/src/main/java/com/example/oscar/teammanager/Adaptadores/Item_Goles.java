package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.R;

/**
 * Created by ptmarketing05 on 17/05/2017.
 */

public class Item_Goles extends LinearLayout {

    TextView nombreJugador, goles, amarillas, rojas, tvNum;
    int cont = 1;



    public Item_Goles(Context context) {
        super(context);
        inflate(context, R.layout.item_clasificacion_goles, this);

        nombreJugador = (TextView) findViewById(R.id.tvNombre);
        goles = (TextView) findViewById(R.id.tvGoles);
        amarillas = (TextView) findViewById(R.id.tvAmarilla);
        rojas = (TextView) findViewById(R.id.tvRoja);
        tvNum = (TextView)findViewById(R.id.tvNum);


    }




    public void setGoles(Estadisticas e) {
        nombreJugador.setText(""+e.getCorreoJug());
        goles.setText(""+e.getGoles());
        amarillas.setText(""+e.getTarjetaAmarilla());
        rojas.setText(""+e.getTarjetaRoja());
//        tvNum.setText(cont+1);

    }
}


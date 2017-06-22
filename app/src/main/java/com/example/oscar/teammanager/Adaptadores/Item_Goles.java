package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.R;

/**
 * Created by oscar on 17/05/2017.
 */

public class Item_Goles extends LinearLayout {

    TextView nombreJugador, goles, amarillas, rojas, tvNum;

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
    }
}


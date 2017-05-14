package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.R;

/**
 * Created by ptmarketing05 on 12/05/2017.
 */

public class Item_list_jugador_claros extends LinearLayout {
    TextView nombreJugador;



    public Item_list_jugador_claros(Context context) {
        super(context);
        inflate(context, R.layout.item_list_claro, this);

        nombreJugador = (TextView) findViewById(R.id.jugador_nombre_list);
    }


    public void setListJugador(Jugadores j) {
        nombreJugador.setText(""+j.getNombre());
    }
}

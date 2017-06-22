package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.R;

/**
 * Created by oscar on 14/05/2017.
 */

public class Item_list_jugador_oscuros extends LinearLayout {
    TextView nombreJugador;



    public Item_list_jugador_oscuros(Context context) {
        super(context);
        inflate(context, R.layout.item_list_oscuro, this);

        nombreJugador = (TextView) findViewById(R.id.jugador_nombre_list);
    }


    public void setListJugador(Jugadores j) {
        if(j.getNick().equals("")) {
            nombreJugador.setText("" + j.getNombre());
        }else{
            nombreJugador.setText("" + j.getNick());
        }
    }
}

package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.R;

/**
 * Created by ptmarketing05 on 22/05/2017.
 */

public class Item_puntos extends LinearLayout {

    TextView nombreJugador, jugados, ganados, perdidos, empatados, puntos,num;
    int cont = 1;



    public Item_puntos(Context context) {
        super(context);
        inflate(context, R.layout.item_clasificacion_puntos, this);

        nombreJugador = (TextView) findViewById(R.id.tvNombre);
        jugados = (TextView) findViewById(R.id.tvJugados);
        ganados = (TextView) findViewById(R.id.tvGanados);
        perdidos = (TextView) findViewById(R.id.tvPerdidos);
        empatados = (TextView)findViewById(R.id.tvEmpatados);
        puntos = (TextView)findViewById(R.id.tvPuntos);
        num = (TextView)findViewById(R.id.tvNum);

    }




    public void setPuntos(Estadisticas e) {
        nombreJugador.setText(""+e.getCorreoJug());
        jugados.setText(""+e.getPartidosJugados());
        ganados.setText(""+e.getPartidosGanados());
        perdidos.setText(""+e.getPartidosPerdidos());
        empatados.setText(""+e.getPartidosEmpatados());
        puntos.setText(""+e.getPuntos());
        //num.setText();

    }
}

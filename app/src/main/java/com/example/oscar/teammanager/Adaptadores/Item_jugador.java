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
import com.example.oscar.teammanager.Utils.GlobalParams;

/**
 * Created by oscar on 11/05/2017.
 */

public class Item_jugador extends LinearLayout {
    TextView nombreJugador, tipoJugador;
    ImageView imageJug;
    private Bitmap result;


    public Item_jugador(Context context) {
        super(context);
        inflate(context, R.layout.item_jugadores, this);

        nombreJugador = (TextView) findViewById(R.id.jugador_nombre);
        tipoJugador = (TextView) findViewById(R.id.jugador_tipo);
        imageJug = (ImageView) findViewById(R.id.peña_img);
    }

    public void setJugador(Jugadores j) {

        nombreJugador.setText(""+j.getNombre());
        tipoJugador.setText(""+j.getTipoJug());
        result = GlobalParams.decodeBase64(j.getRutaFoto());

        if(result != null) {
            imageJug.setImageBitmap(result);
        }else{
            imageJug.setImageResource(R.drawable.perfil);
        }

    }
}

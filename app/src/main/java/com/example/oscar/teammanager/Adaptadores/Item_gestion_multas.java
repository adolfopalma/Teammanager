package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.oscar.teammanager.Objects.Multas;
import com.example.oscar.teammanager.R;

/**
 * Created by oscar on 21/06/2017.
 */

public class Item_gestion_multas extends LinearLayout {

    TextView nomJug,tipo,cantidad;

    public Item_gestion_multas(Context context) {
        super(context);
        inflate(context, R.layout.item_list_gestion_multas, this);

        nomJug = (TextView) findViewById(R.id.tvNombre);
        tipo= (TextView) findViewById(R.id.tvTipo);
        cantidad = (TextView) findViewById(R.id.tvCantidad);
    }


    public void setGestionMulta(Multas m) {
        nomJug.setText(""+m.getNomJug());
        tipo.setText(""+m.getTipoMulta());
        cantidad.setText(""+m.getCantidad());
    }

}

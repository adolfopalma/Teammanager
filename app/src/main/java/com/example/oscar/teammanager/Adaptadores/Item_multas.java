package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.oscar.teammanager.Objects.Multas;
import com.example.oscar.teammanager.R;

/**
 * Created by ptmarketing05 on 23/05/2017.
 */

public class Item_multas extends LinearLayout {

    TextView nomJug,tipo, estado, cantidad;


    public Item_multas(Context context) {
        super(context);
        inflate(context, R.layout.item_list_multas, this);

        nomJug = (TextView) findViewById(R.id.tvNombre);
        tipo= (TextView) findViewById(R.id.tvTipo);
        estado = (TextView) findViewById(R.id.tvEstado);
        cantidad = (TextView) findViewById(R.id.tvCantidad);
    }


    public void setMulta(Multas m) {
        nomJug.setText(""+m.getCodigoJug());
        tipo.setText(""+m.getTipoMulta());
        cantidad.setText(""+m.getCantidad());
        if(m.getEstado().equals("No pagado")){
            estado.setText(""+m.getEstado());
            estado.setTextColor(Color.RED);
        }else{
            estado.setText(""+m.getEstado());
            estado.setTextColor(Color.GREEN);
        }



    }

}

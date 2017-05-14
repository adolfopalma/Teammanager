package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.R;
import com.example.oscar.teammanager.Utils.GlobalParams;

import java.util.ArrayList;

/**
 * Created by ptmarketing05 on 12/05/2017.
 */

public class Adapter_list_oscuros extends BaseAdapter {

    protected ArrayList<Jugadores> items;
    Activity actividad;
    int contador =0;

    public Adapter_list_oscuros(Activity activity, ArrayList<Jugadores> items) {
        this.items=items;
        this.actividad = activity;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }


    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        Item_list_jugador_oscuros view;
        if (convertView == null) {
            view = new Item_list_jugador_oscuros(parent.getContext());
        }else {
            view = (Item_list_jugador_oscuros) convertView;
        }

        view.setListJugador(items.get(position));
        ImageButton gol = (ImageButton)view.findViewById(R.id.bGol);
        gol.setFocusable(false);
        gol.setFocusableInTouchMode(false);

        gol.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contador ++;
                String num = String.valueOf(contador);
                GlobalParams.MarcadorOscuro = num;
            }
        });

        return view;
    }

}

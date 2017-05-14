package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.oscar.teammanager.Objects.Jugadores;

import java.util.ArrayList;

/**
 * Created by ptmarketing05 on 11/05/2017.
 */

public class GestionListAdapter  extends BaseAdapter {

    protected ArrayList<Jugadores> items;
    final Activity actividad;


    public GestionListAdapter(Activity a,ArrayList<Jugadores> items) {
        this.items=items;
        this.actividad = a;
    }



    @Override
    public int getCount() {
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

        Item_jugador view;
        if (convertView == null) {
            view = new Item_jugador(parent.getContext());
        }else {
            view = (Item_jugador) convertView;
        }

        view.setJugador(items.get(position));

        return view;
    }
}

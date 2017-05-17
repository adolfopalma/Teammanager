package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.R;

import java.util.ArrayList;

/**
 * Created by ptmarketing05 on 17/05/2017.
 */

public class ListaGolesAdapter extends BaseAdapter {

    protected ArrayList<Estadisticas> items;
    final Activity actividad;


    public ListaGolesAdapter(Activity a,ArrayList<Estadisticas> items) {
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
        return Long.parseLong(items.get(position).getCodjug());
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        Item_Goles view;
        if (convertView == null) {
            view = new Item_Goles(parent.getContext());
        }else {
            view = (Item_Goles) convertView;
        }

        view.setGoles(items.get(position));

        return view;
    }
}


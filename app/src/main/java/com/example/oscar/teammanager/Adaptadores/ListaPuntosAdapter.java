package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.oscar.teammanager.Objects.Estadisticas;

import java.util.ArrayList;

/**
 * Created by ptmarketing05 on 22/05/2017.
 */

public class ListaPuntosAdapter extends BaseAdapter {

    protected ArrayList<Estadisticas> items;
    final Activity actividad;


    public ListaPuntosAdapter(Activity a,ArrayList<Estadisticas> items) {
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

        Item_puntos view;
        if (convertView == null) {
            view = new Item_puntos(parent.getContext());
        }else {
            view = (Item_puntos) convertView;
        }

        view.setPuntos(items.get(position));

        return view;
    }
}



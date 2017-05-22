package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.oscar.teammanager.Objects.Partidos;
import com.example.oscar.teammanager.Objects.Peñas;

import java.util.ArrayList;

/**
 * Created by ptmarketing05 on 19/05/2017.
 */

/*public class Adapter_list_partidos extends BaseAdapter {

    protected ArrayList<Partidos> items;
    final Activity actividad;


    public Adapter_list_partidos(Activity a,ArrayList<Partidos> items) {
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

        Item_partidos view;
        if (convertView == null) {
            view = new Item_partidos(parent.getContext());
        }else {
            view = (Item_partidos) convertView;
        }

        view.setPartido(items.get(position));

        return view;
    }
}*/

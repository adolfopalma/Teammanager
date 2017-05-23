package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Objects.Multas;

import java.util.ArrayList;

/**
 * Created by ptmarketing05 on 23/05/2017.
 */

public class ListaMultasAdapter extends BaseAdapter {

    protected ArrayList<Multas> items;
    final Activity actividad;


    public ListaMultasAdapter(Activity a,ArrayList<Multas> items) {
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
        return items.get(position).getCodMulta();
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        Item_multas view;
        if (convertView == null) {
            view = new Item_multas(parent.getContext());
        }else {
            view = (Item_multas) convertView;
        }

        view.setMulta(items.get(position));

        return view;
    }
}

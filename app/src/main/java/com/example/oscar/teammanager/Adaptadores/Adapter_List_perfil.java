package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.oscar.teammanager.Objects.Peñas;
import java.util.ArrayList;

/**
 * Created by oscar on 27/05/2017.
 */

public class Adapter_List_perfil extends BaseAdapter {

    protected ArrayList<Peñas> items;
    final Activity actividad;


    public Adapter_List_perfil(Activity a,ArrayList<Peñas> items) {
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

        Item_equipos view;
        if (convertView == null) {
            view = new Item_equipos(parent.getContext());
        }else {
            view = (Item_equipos) convertView;
        }

        view.setEquipo(items.get(position));

        return view;
    }
}
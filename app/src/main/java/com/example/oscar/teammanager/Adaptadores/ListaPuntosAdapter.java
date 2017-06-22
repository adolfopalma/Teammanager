package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.R;

import java.util.ArrayList;

/**
 * Created by oscar on 22/05/2017.
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

        TextView pos = (TextView)view.findViewById(R.id.tvNum);
        pos.setText(String.valueOf(position+1));

        return view;
    }
}



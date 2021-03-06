package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.oscar.teammanager.Objects.Multas;
import com.example.oscar.teammanager.R;
import java.util.ArrayList;

/**
 * Created by oscar on 23/05/2017.
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

        TextView pos = (TextView)view.findViewById(R.id.tvNum);
        pos.setText(String.valueOf(position+1));

        return view;
    }
}

package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.oscar.teammanager.Objects.Peñas;
import java.util.ArrayList;


/**
 * Created by oscar on 09/05/2017.
 */

public class PeñaListAdapter extends BaseAdapter {

    protected ArrayList<Peñas> items;
    final Activity actividad;


    public PeñaListAdapter(Activity a,ArrayList<Peñas> items) {
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

        Item view;
        if (convertView == null) {
            view = new Item(parent.getContext());
        }else {
            view = (Item) convertView;
        }

        view.setPeña(items.get(position));

        return view;
    }
}

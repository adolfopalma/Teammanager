package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Multas;
import com.example.oscar.teammanager.R;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oscar on 21/06/2017.
 */

public class Adapter_gestion_multa extends BaseAdapter {

    protected ArrayList<Multas> items;
    final Activity actividad;
    protected int codMulta;
    private ClaseConexion devuelveJSON;


    public Adapter_gestion_multa(Activity a,ArrayList<Multas> items) {
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

        Item_gestion_multas view;
        if (convertView == null) {
            view = new Item_gestion_multas(parent.getContext());
        }else {
            view = (Item_gestion_multas) convertView;
        }

        devuelveJSON = new ClaseConexion();

        view.setGestionMulta(items.get(position));

        final Button bPagar = (Button) view.findViewById(R.id.bPagar);
        bPagar.setFocusable(false);
        bPagar.setFocusableInTouchMode(false);

        bPagar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                codMulta = items.get(position).getCodMulta();
                UpdateEstadoTask task = new UpdateEstadoTask();
                task.execute();
                bPagar.setText("Pagado");
                bPagar.setTextColor(Color.GREEN);
                bPagar.setEnabled(false);
            }
        });

        return view;
    }

    class UpdateEstadoTask extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE multas SET Estado = 'Pagado' WHERE CodMulta = "+codMulta);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}

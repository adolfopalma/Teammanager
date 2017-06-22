package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.R;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oscar on 14/05/2017.
 */

public class Adapter_list_claros extends BaseAdapter{

    protected ArrayList<Jugadores> items;
    protected Activity actividad;
    protected String correo;
    private ClaseConexion devuelveJSON;
    protected ImageButton amarilla, roja;
    protected TextView tvGol, tvAmarilla, tvRoja;


    public Adapter_list_claros(Activity activity, ArrayList<Jugadores> items) {
        this.items=items;
        this.actividad = activity;
    }

    @Override
    public int getCount()
    {
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

        final Item_list_jugador_claros view;
        if (convertView == null) {
            view = new Item_list_jugador_claros(parent.getContext());
        }else {
            view = (Item_list_jugador_claros) convertView;
        }


        devuelveJSON = new ClaseConexion();

        final LinearLayout fondo = (LinearLayout)view.findViewById(R.id.lnFondo);
        TextView pos = (TextView)view.findViewById(R.id.tvNum);
        pos.setText(String.valueOf(position+1));
        view.setListJugador(items.get(position));
        final ImageButton gol = (ImageButton)view.findViewById(R.id.bGol);
        gol.setFocusable(false);
        gol.setFocusableInTouchMode(false);

        gol.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tvAmarilla = (TextView)view.findViewById(R.id.tvAmarilla);
                tvRoja = (TextView)view.findViewById(R.id.tvRoja);
                int numAmarilla = Integer.parseInt(String.valueOf(tvAmarilla.getText()));
                int numRoja = Integer.parseInt(String.valueOf(tvRoja.getText()));
                if(numAmarilla == 2 || numRoja == 1){
                    Snackbar.make(view, R.string.jugador_exp, Snackbar.LENGTH_LONG).show();
                }else {
                    GlobalParams.MarcadorClaro = GlobalParams.MarcadorClaro + 1;
                    correo = items.get(position).getCorreo();
                    tvGol = (TextView) view.findViewById(R.id.tvGol);
                    int numGol = Integer.parseInt(String.valueOf(tvGol.getText()));
                    tvGol.setText(String.valueOf(numGol + 1));
                    UpdateGolesTask task = new UpdateGolesTask();
                    task.execute();
                }
            }
        });

        amarilla = (ImageButton)view.findViewById(R.id.btAmarilla);
        amarilla.setFocusable(false);
        amarilla.setFocusableInTouchMode(false);

        amarilla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tvAmarilla = (TextView)view.findViewById(R.id.tvAmarilla);
                tvRoja = (TextView)view.findViewById(R.id.tvRoja);
                int numAmarilla = Integer.parseInt(String.valueOf(tvAmarilla.getText()));
                int numRoja = Integer.parseInt(String.valueOf(tvRoja.getText()));

                if(numRoja < 1) {
                    if (numAmarilla < 2) {
                        correo = items.get(position).getCorreo();
                        tvAmarilla.setText(String.valueOf(numAmarilla + 1));
                        fondo.setBackgroundColor(Color.YELLOW);
                        UpdateAmarillaTask task = new UpdateAmarillaTask();
                        task.execute();
                    }
                    if (numAmarilla >= 1) {
                        tvRoja = (TextView) view.findViewById(R.id.tvRoja);
                        fondo.setBackgroundColor(Color.RED);
                        tvRoja.setText(String.valueOf(1));
                        tvAmarilla.setText(String.valueOf(2));
                        UpdateAmarillaTask task = new UpdateAmarillaTask();
                        task.execute();
                        UpdateRojaTask task2 = new UpdateRojaTask();
                        task2.execute();
                    }
                }


            }
        });

        roja = (ImageButton)view.findViewById(R.id.btRoja);
        roja.setFocusable(false);
        roja.setFocusableInTouchMode(false);

        roja.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tvAmarilla = (TextView)view.findViewById(R.id.tvAmarilla);
                tvRoja = (TextView) view.findViewById(R.id.tvRoja);
                int numAmarilla = Integer.parseInt(String.valueOf(tvAmarilla.getText()));
                int numRoja = Integer.parseInt(String.valueOf(tvRoja.getText()));

                if(numRoja < 1 || numAmarilla < 2) {
                    correo = items.get(position).getCorreo();
                    fondo.setBackgroundColor(Color.RED);
                    tvRoja.setText(String.valueOf(1));
                    UpdateRojaTask task = new UpdateRojaTask();
                    task.execute();
                }

            }
        });

        return view;
    }


    class UpdateAmarillaTask extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE estadisticas SET TarjetaAmarilla = TarjetaAmarilla+1 WHERE CodigoJug = "+"'"+correo+"' and codPeña = "+GlobalParams.codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    class UpdateRojaTask extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE estadisticas SET TarjetaRoja = TarjetaRoja+1 WHERE CodigoJug = "+"'"+correo+"' and codPeña = "+GlobalParams.codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    class UpdateGolesTask extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE estadisticas SET Goles = Goles+1 WHERE CodigoJug = "+"'"+correo+"' and codPeña = "+GlobalParams.codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}

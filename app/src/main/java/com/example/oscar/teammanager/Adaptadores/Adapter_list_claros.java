package com.example.oscar.teammanager.Adaptadores;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.PartidoActivity;
import com.example.oscar.teammanager.R;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oscar on 14/05/2017.
 */

public class Adapter_list_claros extends BaseAdapter{

    protected ArrayList<Jugadores> items;
    Activity actividad;
    int contador =0;
    String correo;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta, url_insert;
    private String IP_Server;
    int goles;


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

        Item_list_jugador_claros view;
        if (convertView == null) {
            view = new Item_list_jugador_claros(parent.getContext());
        }else {
            view = (Item_list_jugador_claros) convertView;
        }

        view.setListJugador(items.get(position));
        ImageButton gol = (ImageButton)view.findViewById(R.id.bGol);
        gol.setFocusable(false);
        gol.setFocusableInTouchMode(false);

        gol.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contador ++;
                String num = String.valueOf(contador);
                GlobalParams.MarcadorClaro = num;
                correo = items.get(position).getCorreo();
                ConsultaTask task = new ConsultaTask();
                task.execute();
            }
        });

        return view;
    }

    class ConsultaTask extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            IP_Server = "http://iesayala.ddns.net/19ramajo";
            url_consulta = IP_Server + "/consulta.php";
            devuelveJSON = new ClaseConexion();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Goles from estadisticas where CodigoJug  = "+"'"+correo+"' and codPeña = "+GlobalParams.codPeña);
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONArray json) {

            if (json != null) {
                for (int i = 0; i < json.length(); i++) {
                    try {
                        jsonObject = json.getJSONObject(i);
                        goles = jsonObject.getInt("Goles")+1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {

            }

            UpdateTask task = new UpdateTask();
            task.execute();
        }

        @Override
        protected void onCancelled() {

        }
    }

    class UpdateTask extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            IP_Server = "http://iesayala.ddns.net/19ramajo";
            url_insert = IP_Server + "/prueba.php";
            devuelveJSON = new ClaseConexion();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE estadisticas SET Goles = " +goles+ " WHERE CodigoJug = "+"'"+correo+"' and codPeña = "+GlobalParams.codPeña);
                devuelveJSON.sendRequest(url_insert, parametrosPosteriores);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONArray json) {

        }

        @Override
        protected void onCancelled() {

        }
    }

}

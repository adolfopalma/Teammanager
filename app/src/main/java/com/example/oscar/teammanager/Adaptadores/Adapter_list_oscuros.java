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
 * Created by oscar on 12/05/2017.
 */

public class Adapter_list_oscuros extends BaseAdapter{

    protected ArrayList<Jugadores> items;
    protected Activity actividad;
    protected String correo;
    private ClaseConexion devuelveJSON;
    private String  url_insert;
    private String IP_Server;
    protected ImageButton amarilla, roja, gol;
    protected TextView tvGol, tvAmarilla, tvRoja;


    public Adapter_list_oscuros(Activity activity, ArrayList<Jugadores> items) {
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

        final Item_list_jugador_oscuros view;
        if (convertView == null) {
            view = new Item_list_jugador_oscuros(parent.getContext());
        }else {
            view = (Item_list_jugador_oscuros) convertView;
        }

        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_insert = IP_Server + "/prueba.php";
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

                //Cada vez que pulso el boton gol incremento en 1 el marcador personal de cada jugador y el marcador global
                //y ejecuto la task que actualiza los los goles
                tvAmarilla = (TextView)view.findViewById(R.id.tvAmarilla);
                tvRoja = (TextView)view.findViewById(R.id.tvRoja);
                int num = Integer.parseInt(String.valueOf(tvAmarilla.getText()));
                int num2 = Integer.parseInt(String.valueOf(tvRoja.getText()));
                if(num == 2 || num2 == 1){
                    Snackbar.make(view, R.string.jugador_exp, Snackbar.LENGTH_LONG).show();

                }else {
                    GlobalParams.MarcadorOscuro = GlobalParams.MarcadorOscuro + 1;
                    correo = items.get(position).getCorreo();
                    tvGol = (TextView) view.findViewById(R.id.tvGol);
                    int num3 = Integer.parseInt(String.valueOf(tvGol.getText()));
                    tvGol.setText(String.valueOf(num3 + 1));
                    Adapter_list_oscuros.UpdateGolesTask task = new Adapter_list_oscuros.UpdateGolesTask();
                    task.execute();
                }
            }
        });

        amarilla = (ImageButton)view.findViewById(R.id.btAmarilla);
        amarilla.setFocusable(false);
        amarilla.setFocusableInTouchMode(false);

        amarilla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Cada vez que pulso el boton amarilla incremento su valor en 1, si llego a dos incremento el valor de roja
                //y inhabilito al jugador, ademas ejecuto la task para actualizar datos
                tvAmarilla = (TextView)view.findViewById(R.id.tvAmarilla);
                tvRoja = (TextView)view.findViewById(R.id.tvRoja);
                int num = Integer.parseInt(String.valueOf(tvAmarilla.getText()));
                int num2 = Integer.parseInt(String.valueOf(tvRoja.getText()));

                if(num2 < 1) {
                    if (num < 2) {
                        correo = items.get(position).getCorreo();
                        tvAmarilla.setText(String.valueOf(num + 1));
                        fondo.setBackgroundColor(Color.YELLOW);
                    }
                    if (num >= 1) {
                        tvRoja = (TextView) view.findViewById(R.id.tvRoja);
                        fondo.setBackgroundColor(Color.RED);
                        tvRoja.setText(String.valueOf(1));
                        tvAmarilla.setText(String.valueOf(2));
                    }

                    Adapter_list_oscuros.UpdateAmarillaTask task = new Adapter_list_oscuros.UpdateAmarillaTask();
                    task.execute();
                }


            }
        });

        roja = (ImageButton)view.findViewById(R.id.btRoja);
        roja.setFocusable(false);
        roja.setFocusableInTouchMode(false);

        roja.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Cuando pulso el boton roja incremento en 1 su valos, inhabilito el jugador con sus botones y ejecuto task para actualizar datos
                tvAmarilla = (TextView)view.findViewById(R.id.tvAmarilla);
                int num = Integer.parseInt(String.valueOf(tvAmarilla.getText()));

                if(num < 2) {
                    correo = items.get(position).getCorreo();
                    tvRoja = (TextView) view.findViewById(R.id.tvRoja);
                    fondo.setBackgroundColor(Color.RED);
                    tvRoja.setText(String.valueOf(1));
                    amarilla.setEnabled(false);
                    roja.setEnabled(false);
                    Adapter_list_oscuros.UpdateRojaTask task = new Adapter_list_oscuros.UpdateRojaTask();
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
                devuelveJSON.sendInsert(url_insert, parametrosPosteriores);
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
                devuelveJSON.sendInsert(url_insert, parametrosPosteriores);
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
                devuelveJSON.sendInsert(url_insert, parametrosPosteriores);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}

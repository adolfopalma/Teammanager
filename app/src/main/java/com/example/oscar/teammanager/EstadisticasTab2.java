package com.example.oscar.teammanager;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oscar.teammanager.Adaptadores.ListaGolesAdapter;
import com.example.oscar.teammanager.Adaptadores.ListaPuntosAdapter;
import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ptmarketing04 on 30/03/2017.
 */

public class EstadisticasTab2 extends Fragment {

    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta, url_insert;
    private String IP_Server;
    private Peñas peña;
    private Estadisticas estadisticas;
    private Jugadores jugadores;
    private ArrayList<Peñas> arrayPeñas;
    private ArrayList<Estadisticas> arrayListaEstadisticas;
    ListView lvEst;


    public EstadisticasTab2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        url_insert = IP_Server + "/prueba.php";
        devuelveJSON = new ClaseConexion();
        arrayListaEstadisticas = new ArrayList<>();
        arrayPeñas = new ArrayList<>();
        new ConsultaTask().execute();


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvEst = (ListView)view.findViewById(R.id.lvEstPunt);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.layout_estadisticas2, container, false);

    }

    //Creamos asynctask para obtener los datos
    class ConsultaTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select j.Nombre, e.CodigoJug, e.PartidosJugados, e.PartidosGanados, e.PartidosPerdidos, e.PartidosEmpatados, e.Puntos, e.codPeña from estadisticas e, jugadores j where e.codPeña = 1 and e.CodigoJug = j.Correo ORDER BY Puntos DESC");

                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                    Snackbar.make(getView(), "Error de conexion", Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONArray json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                for (int i = 0; i < json.length(); i++) {
                    try {
                        jsonObject = json.getJSONObject(i);
                        estadisticas = new Estadisticas();
                        estadisticas.setCorreoJug(jsonObject.getString("Nombre"));
                        estadisticas.setCodjug(jsonObject.getString("CodigoJug"));
                        estadisticas.setCodPeña(jsonObject.getInt("codPeña"));
                        estadisticas.setPuntos(jsonObject.getInt("Puntos"));
                        estadisticas.setPartidosJugados(jsonObject.getInt("PartidosJugados"));
                        estadisticas.setPartidosGanados(jsonObject.getInt("PartidosGanados"));
                        estadisticas.setPartidosPerdidos(jsonObject.getInt("PartidosPerdidos"));
                        estadisticas.setPartidosEmpatados(jsonObject.getInt("PartidosEmpatados"));
                        arrayListaEstadisticas.add(estadisticas);
                        System.out.println("----------------"+arrayListaEstadisticas);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                lvEst.setAdapter(new ListaPuntosAdapter(getActivity(), arrayListaEstadisticas));

            } else {
                Snackbar.make(getView(), "Error de conexion", Snackbar.LENGTH_LONG).show();

            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        lvEst.setAdapter(new ListaGolesAdapter(getActivity(), arrayListaEstadisticas));
    }
}

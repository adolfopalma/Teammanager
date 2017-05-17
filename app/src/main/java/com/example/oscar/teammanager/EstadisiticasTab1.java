package com.example.oscar.teammanager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.oscar.teammanager.Adaptadores.EstadisticasAdapter;
import com.example.oscar.teammanager.Adaptadores.GestionListAdapter;
import com.example.oscar.teammanager.Adaptadores.ListaGolesAdapter;
import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class EstadisiticasTab1 extends Fragment {

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


    public EstadisiticasTab1() {
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

        lvEst = (ListView)view.findViewById(R.id.lvEst);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.layout_estadisticas1, container, false);

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
                parametrosPosteriores.put("ins_sql","select j.Nombre, e.CodigoJug, e.Goles, e.TarjetaAmarilla, e.TarjetaRoja, e.codPeña from estadisticas e, jugadores j where e.codPeña = 1 and e.CodigoJug = j.Correo ORDER BY Goles DESC");

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
                        estadisticas.setGoles(jsonObject.getInt("Goles"));
                        estadisticas.setTarjetaAmarilla(jsonObject.getInt("TarjetaAmarilla"));
                        estadisticas.setTarjetaRoja(jsonObject.getInt("TarjetaRoja"));
                        arrayListaEstadisticas.add(estadisticas);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                lvEst.setAdapter(new ListaGolesAdapter(getActivity(), arrayListaEstadisticas));

            } else {

            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }
}

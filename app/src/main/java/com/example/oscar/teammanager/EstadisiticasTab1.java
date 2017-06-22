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
import com.example.oscar.teammanager.Adaptadores.ListaGolesAdapter;
import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


public class EstadisiticasTab1 extends Fragment {

    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private Estadisticas estadisticas;
    private ArrayList<Estadisticas> arrayListaEstadisticas;
    ListView lvEst;



    public EstadisiticasTab1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvEst = (ListView)view.findViewById(R.id.lvEst);
        devuelveJSON = new ClaseConexion();
        arrayListaEstadisticas = new ArrayList<>();
        new EstadisiticasTab1.ConsultaTaskGoles().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.layout_estadisticas1, container, false);

    }

    //Creamos asynctask para obtener los datos
    class ConsultaTaskGoles extends AsyncTask<String, String, JSONArray> {
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
                parametrosPosteriores.put("ins_sql","select j.Nombre, e.CodigoJug, e.Goles, e.TarjetaAmarilla, e.TarjetaRoja, e.codPeña from estadisticas e, jugadores j where e.codPeña = "+GlobalParams.codPeña+" and e.CodigoJug = j.Correo ORDER BY Goles DESC");
                System.out.println("--------------"+GlobalParams.url_consulta+parametrosPosteriores);

                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);

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

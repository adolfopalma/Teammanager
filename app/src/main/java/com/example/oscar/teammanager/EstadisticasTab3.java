package com.example.oscar.teammanager;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oscar.teammanager.Adaptadores.ListaMultasAdapter;
import com.example.oscar.teammanager.Objects.Multas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by oscar on 30/03/2017.
 */

public class EstadisticasTab3 extends Fragment {

    private JSONArray jSONArray3;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject3;
    private Multas multa;
    private ArrayList<Multas> arrayListaMultas;
    protected ListView lvEstMult;
    protected TextView tvInfo;


    public EstadisticasTab3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvEstMult = (ListView)view.findViewById(R.id.lvEst);
        devuelveJSON = new ClaseConexion();
        arrayListaMultas = new ArrayList<>();
        tvInfo = (TextView)view.findViewById(R.id.tvInfoMult);
        new EstadisticasTab3.ConsultaTaskMultas().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_estadisticas3, container, false);

    }

    //Creamos asynctask para obtener los datos
    class ConsultaTaskMultas extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select m.*, j.Nombre from multas m, jugadores j where m.codPeña = "+GlobalParams.codPeña+" and m.CodigoJug = j.Correo");

                jSONArray3 = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);

                if (jSONArray3.length() > 0) {
                    return jSONArray3;
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
                            jsonObject3 = json.getJSONObject(i);
                            multa = new Multas();
                            multa.setCodMulta(jsonObject3.getInt("CodMulta"));
                            multa.setCodigoJug(jsonObject3.getString("Nombre"));
                            multa.setCantidad(jsonObject3.getInt("Cantidad"));
                            multa.setEstado(jsonObject3.getString("Estado"));
                            multa.setTipoMulta(jsonObject3.getString("TipoMulta"));
                            arrayListaMultas.add(multa);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                lvEstMult.setVisibility(View.VISIBLE);
                tvInfo.setVisibility(View.GONE);
                lvEstMult.setAdapter(new ListaMultasAdapter(getActivity(), arrayListaMultas));

            } else {
                lvEstMult.setVisibility(View.GONE);
                tvInfo.setVisibility(View.VISIBLE);

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
        lvEstMult.setAdapter(new ListaMultasAdapter(getActivity(), arrayListaMultas));
    }
}

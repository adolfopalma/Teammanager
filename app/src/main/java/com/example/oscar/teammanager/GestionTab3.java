package com.example.oscar.teammanager;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import org.json.JSONObject;



public class GestionTab3 extends Fragment {
    CheckBox passcode,pvd,iva,margin;
    EditText etStore;
    LinearLayout save_pref;
    protected String url_consulta1,url_codificada1,url_consulta2,url_codificada2,url_consulta3,url_codificada3;
    protected JSONObject jsonObject1,jsonObject2,jsonObject3;
    protected ClaseConexion devuelveJSON;
    String pref_pvp = "PVD";
    String pref_iva = "0";
    String pref_margin = "0";


    public GestionTab3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        passcode = (CheckBox)view.findViewById(R.id.pref_block_passcode);
        pvd = (CheckBox)view.findViewById(R.id.pref_tarifa_pvp);
        iva = (CheckBox)view.findViewById(R.id.pref_precios_iva);
        margin = (CheckBox)view.findViewById(R.id.pref_margen_propio);
        etStore = (EditText) view.findViewById(R.id.etNameStore);
        save_pref = (LinearLayout)view.findViewById(R.id.btn_pref_save);
        devuelveJSON = new ClaseConexion();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_gestion3, container, false);
    }


    //Creamos asynctask para modificar preferencias
    class PreferencesTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        Object local;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());


        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                if (jsonObject1 != null) {
                    return jsonObject1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(JSONObject json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {



            } else {

            }

        }

    }
}

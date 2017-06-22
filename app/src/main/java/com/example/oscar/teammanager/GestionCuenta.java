package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GestionCuenta extends AppCompatActivity {

    protected LinearLayout contraseña,baja,admin;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject,jsonObject1,jsonObject2,jsonObject3;
    protected Dialog dialog;
    protected EditText passActual, passNueva, passRepetir;
    protected Button bCambiar, bCancelar;
    protected String passNuev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_cuenta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devuelveJSON = new ClaseConexion();
        contraseña = (LinearLayout)findViewById(R.id.lnDatos);
        baja = (LinearLayout)findViewById(R.id.lnBaja);
        admin = (LinearLayout)findViewById(R.id.lnAdmin);


        contraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        baja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalParams.administradores != null && GlobalParams.administradores.contains(GlobalParams.correoUsuario)){
                    AlertDialog.Builder builderc = new AlertDialog.Builder(GestionCuenta.this);
                    builderc.setMessage(getResources().getString(R.string.administrador_borr));
                    builderc.setPositiveButton(getResources().getString(R.string.acept),
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(GestionCuenta.this, GestionAdministrador.class);
                                    startActivity(i);
                                }
                            });
                    builderc.setNegativeButton(getResources().getString(R.string.cancel), null);
                    builderc.create();
                    builderc.show();
                }else {
                    AlertDialog.Builder builderc = new AlertDialog.Builder(GestionCuenta.this);
                    builderc.setMessage(getResources().getString(R.string.baja_usu));
                    builderc.setPositiveButton(getResources().getString(R.string.acept),
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                public void onClick(DialogInterface dialog, int which) {
                                    BajaTask task = new BajaTask();
                                    task.execute();
                                }
                            });
                    builderc.setNegativeButton(getResources().getString(R.string.cancel), null);
                    builderc.create();
                    builderc.show();
                }
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GestionCuenta.this, GestionAdministrador.class);
                startActivity(i);
            }
        });

    }

    public void dialog() {
        //Creacion dialog de filtrado
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_pass);
        dialog.setCancelable(false);

        passActual = (EditText)dialog.findViewById(R.id.passAnt);
        passNueva = (EditText)dialog.findViewById(R.id.passNueva);
        passRepetir = (EditText)dialog.findViewById(R.id.passRepit);
        bCambiar = (Button)dialog.findViewById(R.id.bGuardar);
        bCambiar = (Button)dialog.findViewById(R.id.bCancelarFiltro);

        //Accion de boton guardar filtrado
        dialog.findViewById(R.id.bGuardar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String passAct = passActual.getText().toString();
                passNuev = passNueva.getText().toString();
                String passRep = passRepetir.getText().toString();

                if (passAct.equals("") || passNuev.equals("") || passRep.equals("")){
                    Snackbar.make(findViewById(android.R.id.content), R.string.debe, Snackbar.LENGTH_LONG).show();
                }else{
                    if (GlobalParams.passUsu.equals(passAct)) {
                        if (passNuev.equals(passRep)) {
                            PassTask task = new PassTask();
                            task.execute();
                        } else {
                            final AlertDialog.Builder builderc = new AlertDialog.Builder(GestionCuenta.this);
                            builderc.setMessage(getResources().getString(R.string.pass_no_coincide));
                            builderc.setPositiveButton(getResources().getString(R.string.acept),
                                    new DialogInterface.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                        public void onClick(DialogInterface dialog, int which) {
                                            builderc.setCancelable(true);
                                        }
                                    });
                            builderc.create();
                            builderc.show();
                        }
                    } else {
                        final AlertDialog.Builder builderc = new AlertDialog.Builder(GestionCuenta.this);
                        builderc.setMessage(getResources().getString(R.string.pass_error));
                        builderc.setPositiveButton(getResources().getString(R.string.acept),
                                new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    public void onClick(DialogInterface dialog, int which) {
                                        builderc.setCancelable(true);
                                    }
                                });
                        builderc.create();
                        builderc.show();
                    }
                }

            }
        });

        dialog.findViewById(R.id.bCancelarFiltro).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class BajaTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionCuenta.this);
            pDialog.setMessage(getResources().getString(R.string.eliminar));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                    HashMap<String, String> parametrosPost = new HashMap<>();
                    parametrosPost.put("ins_sql", " delete from jugadores where Correo = " +"'"+GlobalParams.correoUsuario+"'");
                    jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

                    HashMap<String, String> parametrosPost1 = new HashMap<>();
                    parametrosPost1.put("ins_sql", " delete from componente_peña where CodigoJug = " +"'"+GlobalParams.correoUsuario+"'");
                    jsonObject1 = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost1);

                    HashMap<String, String> parametrosPost2 = new HashMap<>();
                    parametrosPost2.put("ins_sql", " delete from estadisticas where CodigoJug = " +"'"+GlobalParams.correoUsuario+"'");
                    jsonObject2 = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost2);

                    HashMap<String, String> parametrosPost3 = new HashMap<>();
                    parametrosPost3.put("ins_sql", " delete from multas where CodigoJug = " +"'"+GlobalParams.correoUsuario+"'");
                    jsonObject3 = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost3);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if(jsonObject != null && jsonObject1 != null && jsonObject2 != null) {
                try {
                    switch (jsonObject.getInt("added")){
                        case 1:
                            finish();
                            Intent i = new Intent(GestionCuenta.this, LoginActivity.class);
                            startActivity(i);
                            break;
                        default:
                            Snackbar.make(findViewById(android.R.id.content), "Error borrando cuenta", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class PassTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionCuenta.this);
            pDialog.setMessage(getResources().getString(R.string.act));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE jugadores SET Pass = "+"'"+passNuev+"' WHERE Correo = "+"'"+GlobalParams.correoUsuario+"'");
                jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonObject != null) {
                try {
                    switch (jsonObject.getInt("added")){
                        case 1:
                            AlertDialog.Builder builders = new AlertDialog.Builder(GestionCuenta.this);                            builders.setMessage("Datos actualizados");
                            builders.setPositiveButton(getResources().getString(R.string.acept),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            builders.setCancelable(false);
                            builders.create();
                            builders.show();
                            break;
                        default:
                            Snackbar.make(findViewById(android.R.id.content), "Error actualizando datos", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

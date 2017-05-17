package com.example.oscar.teammanager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.example.oscar.teammanager.Adaptadores.EstadisticasAdapter;
import com.example.oscar.teammanager.Adaptadores.GestionListAdapter;
import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EstadisticasActivity extends AppCompatActivity {

    protected Toolbar tbt;
    protected ViewPager vp;
    protected TabLayout tabs;
    protected String url_codificada;
    protected int tab_activa,credito_actual,impago,credito_limite;
    protected Bundle extras,bundle;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        //añadimos toolbar
        tbt = (Toolbar) findViewById(R.id.toolbartabs);

        if(tbt != null){
            tbt.setTitle("Estadisticas");
            //tbt.setSubtitleTextColor(getResources().getColor(R.color.Gainsboro));
        }

        setSupportActionBar(tbt);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        vp = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(vp);

        //añadimos tabs
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(vp);
        tabs.setBackgroundColor(getResources().getColor(R.color.DimGray));
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.DeepSkyBlue));
        tabs.setSelectedTabIndicatorHeight(15);



    }

    private void setupViewPager(ViewPager viewPager) {
        EstadisticasAdapter adapter = new EstadisticasAdapter(getSupportFragmentManager());


        //Enviamos datos
        //Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList("ListaObjetos", (ArrayList<? extends Parcelable>) arrayListaEstadisticas);



        //Creamos los fragment
        EstadisiticasTab1 pt1 = new EstadisiticasTab1();
        pt1.setArguments(bundle);
        EstadisticasTab2 pt2 = new EstadisticasTab2();
        pt2.setArguments(bundle);
        EstadisticasTab3 pt3 = new EstadisticasTab3();
        pt3.setArguments(bundle);


        //Cargamos los fragment
        adapter.addFragment(pt1, "Goleadores");
        adapter.addFragment(pt2, "Puntuaciones");
        adapter.addFragment(pt3, "Multas");
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_activa);
    }


}

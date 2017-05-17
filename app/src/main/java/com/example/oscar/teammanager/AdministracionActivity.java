package com.example.oscar.teammanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.oscar.teammanager.Adaptadores.AdministrarAdapter;

public class AdministracionActivity extends AppCompatActivity {

    protected ViewPager vp;
    protected TabLayout tabs;
    Bundle extras,bundle;
    protected int tab_activa,credito_actual,impago,credito_limite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administracion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        AdministrarAdapter adapter = new AdministrarAdapter(getSupportFragmentManager());


        //Enviamos datos
        bundle=new Bundle();
        bundle.putInt("credito_actual",credito_actual);
        bundle.putInt("credito_limite",credito_limite);
        bundle.putInt("impago",impago);


        //Creamos los fragment
        GestionTab1 pt1 = new GestionTab1();
        pt1.setArguments(bundle);
        GestionTab2 pt2 = new GestionTab2();
        pt2.setArguments(bundle);
        GestionTab3 pt3 = new GestionTab3();
        pt3.setArguments(bundle);


        //Cargamos los fragment
        adapter.addFragment(pt1, "A");
        adapter.addFragment(pt2, "B");
        adapter.addFragment(pt3, "C");
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_activa);
    }

}

package com.example.oscar.teammanager;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.example.oscar.teammanager.Adaptadores.EstadisticasAdapter;

public class EstadisticasActivity extends AppCompatActivity {

    protected Toolbar tbt;
    protected ViewPager vp;
    protected TabLayout tabs;
    protected int tab_activa;
    protected Bundle bundle;




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


        //Creamos los fragment
        EstadisiticasTab1 pt1 = new EstadisiticasTab1();

        EstadisticasTab2 pt2 = new EstadisticasTab2();

        EstadisticasTab3 pt3 = new EstadisticasTab3();



        //Cargamos los fragment
        adapter.addFragment(pt1, "Goleadores");
        adapter.addFragment(pt2, "Puntuaciones");
        adapter.addFragment(pt3, "Multas");
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_activa);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}

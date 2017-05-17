package com.example.oscar.teammanager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ptmarketing04 on 30/03/2017.
 */

public class EstadisticasTab2 extends Fragment {

    protected TextView tv,tv2,tv3,tv4;
    protected String mensaje;
    protected int impago;
    protected double actual,limite,disponible;

    public EstadisticasTab2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);






    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_estadisticas2, container, false);
    }
}

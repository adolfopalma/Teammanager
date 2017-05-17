package com.example.oscar.teammanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by ptmarketing04 on 30/03/2017.
 */

public class GestionTab1 extends Fragment {

    protected TextView tv,tv2,tv3,tv4,tv5,tv6;
    protected LinearLayout send_mail;

    public GestionTab1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv = (TextView)view.findViewById(R.id.perfil_nombre);
        tv2 = (TextView)view.findViewById(R.id.perfil_email);
        tv3 = (TextView)view.findViewById(R.id.perfil_ccl);
        tv4 = (TextView)view.findViewById(R.id.perfil_comercial_nombre);
        tv5 = (TextView)view.findViewById(R.id.perfil_comercial_email);
        tv6 = (TextView)view.findViewById(R.id.perfil_comercial_ext);
        send_mail = (LinearLayout)view.findViewById(R.id.btn_send_mail);






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_gestion1, container, false);
    }
}

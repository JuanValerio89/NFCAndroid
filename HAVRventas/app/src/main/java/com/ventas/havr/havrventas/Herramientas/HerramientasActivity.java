package com.ventas.havr.havrventas.Herramientas;

import android.graphics.Typeface;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ventas.havr.havrventas.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HerramientasActivity extends AppCompatActivity {

    private final static String TAG = "Herramientas actividad";
    ArrayList<String> leyDeOhmGroups = new ArrayList<>();
    ArrayList<String> Hypertemrinal = new ArrayList<>();
    ArrayList<String> capacitoresGroups = new ArrayList<>();
    ArrayList<String> resistenciasGroup = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herramientas);

        ExpandableListView expandableListView = findViewById(R.id.exp_ListView);
        HashMap<String, List<String>> item = new HashMap<>();

        leyDeOhmGroups.add("Corriente Continua");
        item.put("LEY DE OHM", leyDeOhmGroups);

        capacitoresGroups.add("Ceramicos");
        capacitoresGroups.add("Electroliticos");
        capacitoresGroups.add("Poliester");
        capacitoresGroups.add("SMD");
        capacitoresGroups.add("valores Comerciales");
        item.put("CAPACITORES", capacitoresGroups);

        Hypertemrinal.add("Ir a la aplicación");
        item.put("Hyperterminal", Hypertemrinal);

        resistenciasGroup.add("Codigo de colores");
        resistenciasGroup.add("Resistencias en paralelo");
        resistenciasGroup.add("Resistencias en serie");
        resistenciasGroup.add("Divisor de tensión");
        item.put("Resistencias", resistenciasGroup);

        ExpandibleList expandibleList = new ExpandibleList(item, getApplicationContext());
        expandableListView.setAdapter(expandibleList);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(
                    ExpandableListView parent, View v,
                    int groupPosition, int childPosition,
                    long id) {
                return false;
            }
        });

    }
}

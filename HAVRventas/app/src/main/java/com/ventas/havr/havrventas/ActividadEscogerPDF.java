package com.ventas.havr.havrventas;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ventas.havr.havrventas.Adaptadores.TutorialesVo;

import java.util.ArrayList;

public class ActividadEscogerPDF extends AppCompatActivity {
    ArrayList<TutorialesVo> listaTutoriales;
    RecyclerView recyclerViewTutoriales;

    private Button BtEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_escoger_pdf);

        listaTutoriales = new ArrayList<>();
        recyclerViewTutoriales = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerViewTutoriales.setLayoutManager(new LinearLayoutManager(this));

        llenarTutoriales();

        RecyclerAdapterPDF adapterPDF = new RecyclerAdapterPDF(listaTutoriales);
        adapterPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentoa = new Intent(ActividadEscogerPDF.this,ActividadPDF.class);
                intentoa.putExtra("Tutorial",recyclerViewTutoriales.getChildAdapterPosition(view));
                startActivity(intentoa);
                Toast.makeText(getApplicationContext(),"Seleccion:"+
                        listaTutoriales.get(recyclerViewTutoriales.getChildAdapterPosition(view)).getTitulo(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewTutoriales.setAdapter(adapterPDF);

        BtEnviar = (Button) findViewById(R.id.bt_enviar);
        BtEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.h-avr.mx/tutoriales");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void llenarTutoriales() {
        listaTutoriales.add(new TutorialesVo("Montaje caja arduino mega",getResources().getString(R.string.tutorial_a),R.drawable.ar0008));
        listaTutoriales.add(new TutorialesVo("Cargador de batería LIPO ",getResources().getString(R.string.tutorial_b),R.drawable.mo0025));
        listaTutoriales.add(new TutorialesVo("Celda peltier 12706 con sensor de temperatura MLX90614",getResources().getString(R.string.tutorial_c),R.drawable.mo0041));
        listaTutoriales.add(new TutorialesVo("Bluetooth con amplificador PAM8403",getResources().getString(R.string.tutorial_d),R.drawable.mo0146));
        listaTutoriales.add(new TutorialesVo("Termostato W1209",getResources().getString(R.string.tutorial_e),R.drawable.mo0111));
        listaTutoriales.add(new TutorialesVo("Pantalla OLED 0.96",getResources().getString(R.string.tutorial_f),R.drawable.ds0021));
    }
}

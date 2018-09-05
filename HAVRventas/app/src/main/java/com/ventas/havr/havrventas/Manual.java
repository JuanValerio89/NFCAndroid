package com.ventas.havr.havrventas;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;


import com.ventas.havr.havrventas.Adaptadores.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class Manual extends AppCompatActivity {

    private static final String TAG = "Manual";
    private String TipoManual;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapatador;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_componentes); // RecyclerView

        Resources res = getResources();

        String[] StringPerfiles = res.getStringArray(R.array.conjunto_componentes);

        List<String> list = new ArrayList<String>();
        for(int i=0; i < StringPerfiles.length; i++){
            list.add(StringPerfiles[i]);
        }

        Log.d(TAG,"Tamaño lista:"+list.size());

        mRecyclerView =  (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapatador = new MyAdapter(list, R.layout.recycler_componentes, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, int position) {
                //Toast.makeText(Manual.this, name + "=" + position, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), " - Valor: "+itemval, Toast.LENGTH_LONG).show();
                Intent moverse = new Intent(Manual.this,MaterialSKU.class);
                moverse.putExtra("Manual",TipoManual);
                moverse.putExtra("Posicion",position);
                moverse.putExtra("SPerfil",name);
                startActivity(moverse);
            }
        }, 0);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(mAdapatador);
/*
        MyAdapterList myAdapter = new MyAdapterList(this, R.layout.lista_uno, list);
        ListaMateriales.setAdapter(myAdapter);

        ListaMateriales.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int item = position;
                String itemval = (String)ListaMateriales.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), " - Valor: "+itemval, Toast.LENGTH_LONG).show();

                Intent moverse = new Intent(Manual.this,Perfiles.class);
                moverse.putExtra("Manual",TipoManual);
                moverse.putExtra("Perfil",item);
                moverse.putExtra("SPerfil",itemval);
                startActivity(moverse);
            }

        });
        */

    }

}

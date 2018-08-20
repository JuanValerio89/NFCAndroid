package com.documentos.havr.nfc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClaseFolio extends AppCompatActivity {

    private TextView TxFolios;
    private Button BtSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clase_folio);

        TxFolios = (TextView) findViewById(R.id.tx_folios);
        BtSalir = (Button) findViewById(R.id.bt_salir_folio);


        Intent intentoFolio = getIntent();
        String folio = intentoFolio.getStringExtra("DATAFOLIOS");
        String[] datafolio = folio.split(" ");
        Log.d("ClaseFolio","Texto:"+datafolio[1]);
        TxFolios.setText(datafolio[1]);

        BtSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

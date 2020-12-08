package com.ventas.havr.havrventas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.ventas.havr.havrventas.Adaptadores.AdaptadorSKU;
import com.ventas.havr.havrventas.Adaptadores.TutorialesVo;
import com.ventas.havr.havrventas.Modelos.BaseSKU;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActividadEscogerPDF extends AppCompatActivity {

    ArrayList<TutorialesVo> listaTutoriales;
    RecyclerView recyclerViewTutoriales;

    private Button BtEnviar;

    private static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp80FOe7lrKWGMBcdnn2dMEacQ7sMAszu2r4kxAy1wWpRGMgs7b4W11fWR5q+PMwqwXfmg+iUEwUOCe+HYU0IXY0MpRRDdI085gHG86LHa+Hc48UQ2CPmj38rhQE5O0+hLH8f7MQCylWQulJcmb6SChHWiQcumQKoPze5sJl3C/nq02MbK6wUSJ4yvlFHq6rI8m/mC3iqlAGdyToIbGDyBgvKyyh+roaRvzbh1DEAV6TAfy/9BYsOAsaUC09u0wFGTAYX8kEX5kfrH5jUPV74Hs6EHFWh/1VpRbOUgqX9fQg3mNW1qtGvRewLUxh9o4/IBq3yw2GUTUU1N5j4DX2QzwIDAQAB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_escoger_pdf);

        recyclerViewTutoriales = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerViewTutoriales.setLayoutManager(new LinearLayoutManager(this));

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



}

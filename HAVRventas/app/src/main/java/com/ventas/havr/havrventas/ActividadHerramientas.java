package com.ventas.havr.havrventas;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class ActividadHerramientas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_herramientas);

        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("https://www.h-avr.com/facturacion.html");
    }
}

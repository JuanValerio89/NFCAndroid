package com.ventas.havr.havrventas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Actividad principal";
    private Button BtComponentes;
    private Button BtTutoriales;
    private Button BtCursos;
    private Button BtHerramientas;
    private Button BtCotizaciones;

    private Button BtCorreo;
    private Button BtYoutube;
    private Button BtFacebook;
    private Button BtWhatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtComponentes = (Button) findViewById(R.id.bt_componentes);
        BtTutoriales = (Button) findViewById(R.id.bt_tutoriales);
        BtCursos = (Button) findViewById(R.id.bt_cursos);
        BtHerramientas = (Button) findViewById(R.id.bt_herramientas);
        BtCotizaciones = (Button) findViewById(R.id.bt_cotizacion);

        BtCorreo = (Button) findViewById(R.id.bt_correo);
        BtYoutube = (Button) findViewById(R.id.bt_youtube);
        BtFacebook = (Button) findViewById(R.id.bt_facebook);
        BtWhatsapp = (Button) findViewById(R.id.bt_whatsapp);

        BtComponentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentoa = new Intent(MainActivity.this, Manual.class);
                startActivity(intentoa);
            }
        });

        BtTutoriales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentob = new Intent(MainActivity.this,ActividadEscogerPDF.class);
                startActivity(intentob);
            }
        });

        BtHerramientas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intentoc = new Intent(MainActivity.this,ActividadHerramientas.class);
                //startActivity(intentoc);
                Toast.makeText(MainActivity.this,"Próximamente",Toast.LENGTH_SHORT).show();
            }
        });

        BtCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intentoc = new Intent(MainActivity.this,ActividadHerramientas.class);
                //startActivity(intentoc);
                Toast.makeText(MainActivity.this,"Próximamente",Toast.LENGTH_SHORT).show();
            }
        });

        BtCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intentoc = new Intent(MainActivity.this,ActividadHerramientas.class);
                //startActivity(intentoc);
                Toast.makeText(MainActivity.this,"Próximamente",Toast.LENGTH_SHORT).show();
            }
        });

        BtCotizaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Proximamente",Toast.LENGTH_SHORT).show();
            }
        });

        BtCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] TO = {"ventas@h-avr.com"}; //aquí pon tu correo
                String[] CC = {"Contacto HAVR."};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
// Esto podrás modificarlo si quieres, el asunto y el cuerpo del mensaje
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                    finish();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this,
                            "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BtFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.facebook.com/0HAVR/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        BtYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.youtube.com/channel/UC3LffcvivoPw_K1AYWarOOA");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        BtWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                PackageManager pm=getPackageManager();

                String toNumber = "5513410798";
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + toNumber));
                sendIntent.putExtra("sms_body", "Quiero cotizar el siguiente material:");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
                */

                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                String toNumber = "+525513410798";
                try {
                    String url = "https://api.whatsapp.com/send?phone="+ toNumber +"&text=" + URLEncoder.encode("Hola, me gustaria cotizar el siguiente material:", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }

}

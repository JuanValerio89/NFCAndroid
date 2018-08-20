package com.documentos.havr.nfc;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //Button BtNFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {

        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i=new Intent(MainActivity.this,NFCdetection.class);
                startActivity(i);
            }
        }, 3000);

    }

    private void tareaLarga() {
        try {
            setContentView(R.layout.activity_main);
            Thread.sleep(2000);
        } catch (InterruptedException var2) {
            ;
        }

    }
}

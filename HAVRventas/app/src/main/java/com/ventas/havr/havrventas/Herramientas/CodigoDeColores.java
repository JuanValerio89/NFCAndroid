package com.ventas.havr.havrventas.Herramientas;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ventas.havr.havrventas.R;

public class CodigoDeColores extends AppCompatActivity {

    final static String TAG = "Colores";

    private Spinner spinnerB1;
    private Spinner spinnerB2;
    private Spinner spinnerB3;
    private Spinner spinnerB4;

    private TextView Resultado;
    private TextView Tolerancia;

    private Boolean B1 = false;
    private Boolean B2 = false;
    private Boolean B3 = false;
    private Boolean B4 = false;

    private int Banda1 = 0;
    private int Banda2 = 0;
    private int Banda3 = 0;
    private int Banda4 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_de_colores);

        spinnerB1 = (Spinner) findViewById(R.id.spin_banda1);
        spinnerB2 = (Spinner) findViewById(R.id.spin_banda2);
        spinnerB3 = (Spinner) findViewById(R.id.spin_banda3);
        spinnerB4 = (Spinner) findViewById(R.id.spin_banda4);
        Resultado = findViewById(R.id.resultado);
        Tolerancia = findViewById(R.id.tx_tolerancia);

        String[] opcV1 = {"Escoger el color", "Negro", "Cafe", "Rojo", "Naranja", "Amarillo", "Verde", "Azul", "Violeta", "Gris", "Plateado"};
        String[] opcV2 = {"Escoger el color", "Plateado", "Dorado", "Rojo", "Cafe", "Verde", "Azul", "Violeta", "Gris"};
        ArrayAdapter<String> adapv1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcV1);
        ArrayAdapter<String> adapv2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcV2);

        spinnerB1.setAdapter(adapv1);
        spinnerB1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Banda1 = position - 1;
                    if (position > 0){
                        B1 = true;
                        spinnerB1.setBackgroundColor(PonerColor(position));
                    }
                    else{
                        B1 = false;
                        spinnerB1.setBackground(getDrawable(R.drawable.spinner_a));
                    }

                }
                checarResultado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        spinnerB2.setAdapter(adapv1);
        spinnerB2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Banda2 = position - 1;
                    if (position > 0) {
                        B2 = true;
                        spinnerB2.setBackgroundColor(PonerColor(position));
                    }
                    else {
                        B2 = false;
                        spinnerB2.setBackground(getDrawable(R.drawable.spinner_a));
                    }
                }

                checarResultado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        spinnerB3.setAdapter(adapv1);
        spinnerB3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Banda3 = position;
                    if (position > 0) {
                        B3 = true;
                        spinnerB3.setBackgroundColor(PonerColor(position));
                    }
                    else {
                        B3 = false;
                        spinnerB3.setBackground(getDrawable(R.drawable.spinner_a));
                    }
                }

                checarResultado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        spinnerB4.setAdapter(adapv2);
        spinnerB4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    if (position > 0) {
                        B4 = true;
                        spinnerB4.setBackgroundColor(PonerColorA(position));
                    }
                    else {
                        B4 = false;
                        spinnerB4.setBackground(getDrawable(R.drawable.spinner_a));
                    }
                }
                Banda4 = position;
                checarResultado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });
    }

    private int PonerColor(int position) {
        switch (position) {
            case 1:
                return Color.BLACK;
            case 2:
                return Color.rgb(128, 64, 0); // Cafe
            case 3:
                return Color.rgb(128, 0, 0); // Rojo
            case 4:
                return Color.rgb(255, 108, 0); // Naranja
            case 5:
                return Color.rgb(255, 255, 0); // Amarillo
            case 6:
                return Color.rgb(0, 128, 0); // Verde
            case 7:
                return Color.rgb(0, 0, 128); // Azul
            case 8:
                return Color.rgb(255, 0, 255); // Violeta
            case 9:
                return Color.rgb(128, 128, 128); // Gris
        }
        return 0;
    }

    private int PonerColorA(int position) {
        switch (position) {
            case 1:
                return Color.rgb(158, 158, 158); // Plateado
            case 2:
                return Color.rgb(245, 174, 98); // Dorado
            case 3:
                return Color.rgb(128, 0, 0); // Rojo
            case 4:
                return Color.rgb(128, 64, 0); // Cafe
            case 5:
                return Color.rgb(0, 128, 0); // Verde
            case 6:
                return Color.rgb(0, 0, 128); // Azul
            case 7:
                return Color.rgb(255, 0, 255); // Violeta
            case 8:
                return Color.rgb(128, 128, 128); // Gris
        }
        return 0;
    }

    private void checarResultado() {
        if (B1 && B2 && B3 && B4) {
            int Banda1A = Banda1 * 10;
            int Banda3A = (int) Math.pow(10, (Banda3 - 1));
            Log.d(TAG, "Banda 1: " + Banda1);
            Log.d(TAG, "Banda 2: " + Banda2);
            Log.d(TAG, "Banda 3: " + Banda3);
            double Res = (Banda1A + Banda2) * (Banda3A);
            double Resul = (Res / 1000) ;
            Log.d(TAG,"Resultado de 1000:"+Resul);
            if ((Res / 1000) >= 1) {
                Log.d(TAG, "Mayor a 1000");
                double ResA = Res / 1000;
                Resultado.setText("Valor resistencia: " + ResA + "K ohms");
                if ((Res / 1000000) >= 1) {
                    Log.d(TAG, "Mayor a 1,000,0000");
                    double ResB = Res / 1000000;
                    Resultado.setText("Valor resistencia: " + ResB + "M ohms");
                } else {

                }
            } else {
                Resultado.setText("Valor resistencia: " + Res + " ohms");
            }
            switch (Banda4) {
                case 1:
                    Tolerancia.setText("Tolerancia: 10%");
                    break;
                case 2:
                    Tolerancia.setText("Tolerancia: 5%");
                    break;
                case 3:
                    Tolerancia.setText("Tolerancia: 2%");
                    break;
                case 4:
                    Tolerancia.setText("Tolerancia: 1%");
                    break;
                case 5:
                    Tolerancia.setText("Tolerancia: 0.5%");
                    break;
                case 6:
                    Tolerancia.setText("Tolerancia: 0.25%");
                    break;
                case 7:
                    Tolerancia.setText("Tolerancia: 0.1%");
                    break;
                case 8:
                    Tolerancia.setText("Tolerancia: 0.05%");
                    break;
            }
        } else {
            Resultado.setText("Faltan datos.");
        }
    }
}

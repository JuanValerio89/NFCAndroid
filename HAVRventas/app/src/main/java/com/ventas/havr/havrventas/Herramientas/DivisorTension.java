package com.ventas.havr.havrventas.Herramientas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventas.havr.havrventas.R;

public class DivisorTension extends AppCompatActivity {

    private Spinner SpinRes1;
    private Spinner SpinRes2;

    private EditText EditRes1;
    private EditText EditRes2;
    private EditText EditRes3;

    private TextView TxSalida;

    private int multiplicadorR1;
    private int multiplicadorR2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divisor_tension);

        TxSalida = findViewById(R.id.tx_salida);

        SpinRes1 = findViewById(R.id.spin_r1);
        SpinRes2 = findViewById(R.id.spin_r2);

        EditRes1 = findViewById(R.id.edit_r1);
        EditRes2 = findViewById(R.id.edit_r2);
        EditRes3 = findViewById(R.id.edit_r3);

        String[] opcR1 = {"Ω", "k Ω", "M Ω"};
        ArrayAdapter<String> adapv1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcR1);
        SpinRes1.setAdapter(adapv1);
        SpinRes2.setAdapter(adapv1);

        SpinRes1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR1 = Multiplicar(position);
                CambiarValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        SpinRes2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR2 = Multiplicar(position);
                CambiarValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        EditRes1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        EditRes2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        EditRes3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private int Multiplicar(int position) {
        switch (position) {
            case 0:
                return 1;
            case 1:
                return 1000;
            case 2:
                return 1000000;
        }
        return 1;
    }

    private void CambiarValor() {
        double Res = 0;
        if (!EditRes1.getText().toString().isEmpty() && !EditRes2.getText().toString().isEmpty()
                && !EditRes3.getText().toString().isEmpty()) {
            double Res1 = Double.parseDouble(EditRes1.getText().toString());
            double Res2 = Double.parseDouble(EditRes2.getText().toString());
            double Vol1 = Double.parseDouble(EditRes3.getText().toString());
            double R1 = Res1 * multiplicadorR1;
            double R2 = Res2 * multiplicadorR2;
            Res = ( (R2)/(R1 + R2) ) * Vol1;
            TxSalida.setText(String.format("Vout = %.2f V", Res));
        } else {
            TxSalida.setText("Vout = " + Res + " V");
        }
    }
}

package com.ventas.havr.havrventas.Herramientas;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ventas.havr.havrventas.R;

public class LeyDeOhmDC extends AppCompatActivity {

    private static final String TAG = "Ley Ohm";

    private EditText editTextRes_volt;
    private EditText editTextRes_corr;
    private EditText editTextVolt_corr;
    private EditText editTextVolt_res;
    private EditText editTextCorr_volt;
    private EditText editTextCorr_res;
    private TextView resultado_res;
    private TextView resultado_volt;
    private TextView resultado_corr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ley_de_ohm_dc);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Ley De Ohm DC");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editTextRes_volt = (EditText) findViewById(R.id.editRes_volt);
        editTextRes_corr = (EditText) findViewById(R.id.editRes_corr);

        editTextVolt_corr = (EditText) findViewById(R.id.editVolt_corr);
        editTextVolt_res = (EditText) findViewById(R.id.editVolt_res);

        editTextCorr_volt = (EditText) findViewById(R.id.editCorr_volt);
        editTextCorr_res = (EditText) findViewById(R.id.editCorr_res);

        editTextRes_volt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editTextRes_corr.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editTextVolt_corr.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editTextVolt_res.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editTextCorr_volt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editTextCorr_res.setRawInputType(InputType.TYPE_CLASS_NUMBER);


        resultado_res = findViewById(R.id.editResultado_res);
        resultado_volt = findViewById(R.id.editResultado_vol);
        resultado_corr = findViewById(R.id.editResultado_corr);

        editTextRes_volt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextRes_corr.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextVolt_corr.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValorV();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextVolt_res.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValorV();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextCorr_volt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValorC();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editTextCorr_res.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValorC();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    private void CambiarValor() {
        if (!editTextRes_volt.getText().toString().isEmpty() && !editTextRes_corr.getText().toString().isEmpty()) {
            try {
                String Tx1 = editTextRes_volt.getText().toString();
                String Tx2 = editTextRes_corr.getText().toString();
                double x1 = Double.parseDouble(Tx1);
                double x2 = Double.parseDouble(Tx2);
                if (x2 == 0) {
                    Toast.makeText(LeyDeOhmDC.this, "Valores incorrectos", Toast.LENGTH_LONG).show();
                } else {
                    double res = x1 / x2;

                    resultado_res.setText(ConvertirRes(res));
                }

            } catch (Exception e) {
                Toast.makeText(LeyDeOhmDC.this, "Valores incorrectos", Toast.LENGTH_LONG).show();
            }
        } else {
            resultado_res.setText(String.format("0.0 (Ω)"));
        }
    }

    private void CambiarValorV() {
        if (!editTextVolt_res.getText().toString().isEmpty() && !editTextVolt_corr.getText().toString().isEmpty()) {

            String Tx1 = editTextVolt_corr.getText().toString();
            String Tx2 = editTextVolt_res.getText().toString();

            try {
                double x1 = Double.parseDouble(Tx1);
                double x2 = Double.parseDouble(Tx2);
                double res = x1 * x2;
                resultado_volt.setText(ConvertirVol(res));
            } catch (Exception e) {

            }

        } else {
            resultado_res.setText(String.format("0.0 (Ω)"));
        }
    }

    private void CambiarValorC() {
        if (!editTextCorr_res.getText().toString().isEmpty() && !editTextCorr_volt.getText().toString().isEmpty()) {

            String Tx1 = editTextCorr_volt.getText().toString();
            String Tx2 = editTextCorr_res.getText().toString();
            try {
                double x1 = Double.parseDouble(Tx1);
                double x2 = Double.parseDouble(Tx2);
                if (x2 == 0) {
                    Toast.makeText(LeyDeOhmDC.this, "Valores incorrectos", Toast.LENGTH_LONG).show();
                } else {
                    double res = x1 / x2;
                    resultado_corr.setText(ConvertirAmp(res));
                }

            } catch (Exception e) {
            }

        } else {
            resultado_res.setText(String.format("0.0 (Ω)"));
        }
    }

    private String ConvertirRes(double resultado) {
        if ((resultado / 1000) >= 1) {
            Log.d(TAG, "Mayor a 1000");
            double ResA = resultado / 1000;
            //ValRes.setText("Valor resistencia: " + ResA + " K Ω");
            if ((resultado / 1000000) >= 1) {
                Log.d(TAG, "Mayor a 1,000,0000");
                double ResB = resultado / 1000000;
                //ValRes.setText("Valor resistencia: " + ResB + " M Ω");

                return String.format("%5.2f  M (Ω)", ResB);
            } else {
                return String.format("%5.2f  k (Ω)", ResA);
            }
        } else {
            //ValRes.setText("Valor resistencia: " + resultado + " ohms");
            return String.format("%5.2f  (Ω)", resultado);
        }
    }

    private String ConvertirVol(double resultado) {
        if ((resultado / 1000) >= 1) {
            Log.d(TAG, "Mayor a 1000");
            double ResA = resultado / 1000;
            //ValRes.setText("Valor resistencia: " + ResA + " K Ω");
            if ((resultado / 1000000) >= 1) {
                Log.d(TAG, "Mayor a 1,000,0000");
                double ResB = resultado / 1000000;
                //ValRes.setText("Valor resistencia: " + ResB + " M Ω");

                return String.format("%5.2f  M (V)", ResB);
            } else {
                return String.format("%5.2f  k (V)", ResA);
            }
        } else {
            //ValRes.setText("Valor resistencia: " + resultado + " ohms");
            return String.format("%5.2f  (V)", resultado);
        }
    }

    private String ConvertirAmp(double resultado) {
        if ((resultado / 1000) >= 1) {
            Log.d(TAG, "Mayor a 1000");
            double ResA = resultado / 1000;
            //ValRes.setText("Valor resistencia: " + ResA + " K Ω");
            if ((resultado / 1000000) >= 1) {
                Log.d(TAG, "Mayor a 1,000,0000");
                double ResB = resultado / 1000000;
                //ValRes.setText("Valor resistencia: " + ResB + " M Ω");

                return String.format("%5.2f  M (A)", ResB);
            } else {
                return String.format("%5.2f  k (A)", ResA);
            }
        } else {
            //ValRes.setText("Valor resistencia: " + resultado + " ohms");
            return String.format("%5.2f  (A)", resultado);
        }
    }
}

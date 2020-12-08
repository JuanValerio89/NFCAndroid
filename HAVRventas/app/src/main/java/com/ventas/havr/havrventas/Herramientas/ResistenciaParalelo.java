package com.ventas.havr.havrventas.Herramientas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventas.havr.havrventas.R;

public class ResistenciaParalelo extends AppCompatActivity {

    private static final String TAG = "Res Serie";

    private Button BtAgregar;
    private Button BtEliminar;

    private Spinner SpinRes1;
    private Spinner SpinRes2;
    private Spinner SpinRes3;
    private Spinner SpinRes4;
    private Spinner SpinRes5;
    private Spinner SpinRes6;
    private Spinner SpinRes7;
    private Spinner SpinRes8;
    private Spinner SpinRes9;

    private TextView TxRes3;
    private TextView TxRes4;
    private TextView TxRes5;
    private TextView TxRes6;
    private TextView TxRes7;
    private TextView TxRes8;

    private EditText EditRes1;
    private EditText EditRes2;
    private EditText EditRes3;
    private EditText EditRes4;
    private EditText EditRes5;
    private EditText EditRes6;
    private EditText EditRes7;
    private EditText EditRes8;

    private int multiplicadorR1;
    private int multiplicadorR2;
    private int multiplicadorR3;
    private int multiplicadorR4;
    private int multiplicadorR5;
    private int multiplicadorR6;
    private int multiplicadorR7;
    private int multiplicadorR8;

    private TextView ValRes;

    double Resultado;
    int ResistenciasAgregadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resistencia_paralelo);

        TxRes3 = findViewById(R.id.tx_res3);
        TxRes4 = findViewById(R.id.tx_res4);
        TxRes5 = findViewById(R.id.tx_res5);
        TxRes6 = findViewById(R.id.tx_res6);
        TxRes7 = findViewById(R.id.tx_res7);
        TxRes8 = findViewById(R.id.tx_res8);

        SpinRes1 = findViewById(R.id.spin_r1);
        SpinRes2 = findViewById(R.id.spin_r2);
        SpinRes3 = findViewById(R.id.spin_r3);
        SpinRes4 = findViewById(R.id.spin_r4);
        SpinRes5 = findViewById(R.id.spin_r5);
        SpinRes6 = findViewById(R.id.spin_r6);
        SpinRes7 = findViewById(R.id.spin_r7);
        SpinRes8 = findViewById(R.id.spin_r8);

        EditRes1 = findViewById(R.id.edit_r1);
        EditRes2 = findViewById(R.id.edit_r2);
        EditRes3 = findViewById(R.id.edit_r3);
        EditRes4 = findViewById(R.id.edit_r4);
        EditRes5 = findViewById(R.id.edit_r5);
        EditRes6 = findViewById(R.id.edit_r6);
        EditRes7 = findViewById(R.id.edit_r7);
        EditRes8 = findViewById(R.id.edit_r8);

        ValRes = findViewById(R.id.valor_res);

        BtAgregar = findViewById(R.id.bt_agregar_res);
        BtEliminar = findViewById(R.id.bt_borrar_res);

        TxRes3.setVisibility(View.GONE);
        EditRes3.setVisibility(View.GONE);
        SpinRes3.setVisibility(View.GONE);
        TxRes4.setVisibility(View.GONE);
        EditRes4.setVisibility(View.GONE);
        SpinRes4.setVisibility(View.GONE);
        TxRes5.setVisibility(View.GONE);
        EditRes5.setVisibility(View.GONE);
        SpinRes5.setVisibility(View.GONE);
        TxRes6.setVisibility(View.GONE);
        EditRes6.setVisibility(View.GONE);
        SpinRes6.setVisibility(View.GONE);
        TxRes7.setVisibility(View.GONE);
        EditRes7.setVisibility(View.GONE);
        SpinRes7.setVisibility(View.GONE);
        TxRes8.setVisibility(View.GONE);
        EditRes8.setVisibility(View.GONE);
        SpinRes8.setVisibility(View.GONE);

        BtAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResistenciasAgregadas = ResistenciasAgregadas + 1;
                if (ResistenciasAgregadas >= 6)
                    ResistenciasAgregadas = 6;
                switch (ResistenciasAgregadas) {
                    case 1:
                        TxRes3.setVisibility(View.VISIBLE);
                        EditRes3.setVisibility(View.VISIBLE);
                        SpinRes3.setVisibility(View.VISIBLE);
                        CambiarValor();
                        break;
                    case 2:
                        TxRes4.setVisibility(View.VISIBLE);
                        EditRes4.setVisibility(View.VISIBLE);
                        SpinRes4.setVisibility(View.VISIBLE);
                        CambiarValor();
                        break;
                    case 3:
                        TxRes5.setVisibility(View.VISIBLE);
                        EditRes5.setVisibility(View.VISIBLE);
                        SpinRes5.setVisibility(View.VISIBLE);
                        CambiarValor();
                        break;
                    case 4:
                        TxRes6.setVisibility(View.VISIBLE);
                        EditRes6.setVisibility(View.VISIBLE);
                        SpinRes6.setVisibility(View.VISIBLE);
                        CambiarValor();
                        break;
                    case 5:
                        TxRes7.setVisibility(View.VISIBLE);
                        EditRes7.setVisibility(View.VISIBLE);
                        SpinRes7.setVisibility(View.VISIBLE);
                        CambiarValor();
                        break;
                    case 6:
                        TxRes8.setVisibility(View.VISIBLE);
                        EditRes8.setVisibility(View.VISIBLE);
                        SpinRes8.setVisibility(View.VISIBLE);
                        CambiarValor();
                        break;
                }
            }
        });

        BtEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResistenciasAgregadas = ResistenciasAgregadas - 1;
                if (ResistenciasAgregadas <= 0)
                    ResistenciasAgregadas = 0;
                switch (ResistenciasAgregadas) {
                    case 0:
                        TxRes3.setVisibility(View.GONE);
                        EditRes3.setVisibility(View.GONE);
                        SpinRes3.setVisibility(View.GONE);
                        CambiarValor();
                        break;
                    case 1:
                        TxRes4.setVisibility(View.GONE);
                        EditRes4.setVisibility(View.GONE);
                        SpinRes4.setVisibility(View.GONE);
                        CambiarValor();
                        break;
                    case 2:
                        TxRes5.setVisibility(View.GONE);
                        EditRes5.setVisibility(View.GONE);
                        SpinRes5.setVisibility(View.GONE);
                        CambiarValor();
                        break;
                    case 3:
                        TxRes6.setVisibility(View.GONE);
                        EditRes6.setVisibility(View.GONE);
                        SpinRes6.setVisibility(View.GONE);
                        CambiarValor();
                        break;
                    case 4:
                        TxRes7.setVisibility(View.GONE);
                        EditRes7.setVisibility(View.GONE);
                        SpinRes7.setVisibility(View.GONE);
                        CambiarValor();
                        break;
                    case 5:
                        TxRes8.setVisibility(View.GONE);
                        EditRes8.setVisibility(View.GONE);
                        SpinRes8.setVisibility(View.GONE);
                        CambiarValor();
                        break;
                }
            }
        });

        String[] opcR1 = {"Ω", "K Ω", "M Ω"};
        ArrayAdapter<String> adapv1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcR1);
        SpinRes1.setAdapter(adapv1);
        SpinRes2.setAdapter(adapv1);
        SpinRes3.setAdapter(adapv1);
        SpinRes4.setAdapter(adapv1);
        SpinRes5.setAdapter(adapv1);
        SpinRes6.setAdapter(adapv1);
        SpinRes7.setAdapter(adapv1);
        SpinRes8.setAdapter(adapv1);

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

        SpinRes3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR3 = Multiplicar(position);
                CambiarValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        SpinRes4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR4 = Multiplicar(position);
                CambiarValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        SpinRes5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR5 = Multiplicar(position);
                CambiarValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });

        SpinRes6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR6 = Multiplicar(position);
                CambiarValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });
        SpinRes7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR7 = Multiplicar(position);
                CambiarValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });
        SpinRes8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                multiplicadorR8 = Multiplicar(position);
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

        EditRes4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        EditRes5.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        EditRes6.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        EditRes7.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        EditRes8.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                CambiarValor();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void CambiarValor() {
        if (!EditRes1.getText().toString().isEmpty() && !EditRes2.getText().toString().isEmpty()) {
            switch (ResistenciasAgregadas) {
                case 0:
                    ResultadoRes();
                    break;
                case 1:
                    if (!EditRes3.getText().toString().isEmpty()) {
                        ResultadoRes();
                    } else {
                        ValRes.setText("Valor total resistencia: 0.0 Ω");
                    }
                    break;
                case 2:
                    if (!EditRes3.getText().toString().isEmpty() && !EditRes4.getText().toString().isEmpty()) {
                        ResultadoRes();
                    } else {
                        ValRes.setText("Valor total resistencia: 0.0 Ω");
                    }
                    break;
                case 3:
                    if (!EditRes3.getText().toString().isEmpty() && !EditRes4.getText().toString().isEmpty() && !EditRes5.getText().toString().isEmpty()) {
                        ResultadoRes();
                    } else {
                        ValRes.setText("Valor total resistencia: 0.0 Ω");
                    }
                    break;

                case 4:
                    if (!EditRes3.getText().toString().isEmpty() && !EditRes4.getText().toString().isEmpty()
                            && !EditRes5.getText().toString().isEmpty() && !EditRes6.getText().toString().isEmpty()) {
                        ResultadoRes();
                    } else {
                        ValRes.setText("Valor total resistencia: 0.0 Ω");
                    }
                    break;
                case 5:
                    if (!EditRes3.getText().toString().isEmpty() && !EditRes4.getText().toString().isEmpty()
                            && !EditRes5.getText().toString().isEmpty() && !EditRes6.getText().toString().isEmpty()
                            && !EditRes7.getText().toString().isEmpty()) {
                        ResultadoRes();
                    } else {
                        ValRes.setText("Valor total resistencia: 0.0 Ω");
                    }
                    break;
                case 6:
                    if (!EditRes3.getText().toString().isEmpty() && !EditRes4.getText().toString().isEmpty()
                            && !EditRes5.getText().toString().isEmpty() && !EditRes6.getText().toString().isEmpty()
                            && !EditRes7.getText().toString().isEmpty() && !EditRes8.getText().toString().isEmpty()) {
                        ResultadoRes();
                    } else {
                        ValRes.setText("Valor total resistencia: 0.0 Ω");
                    }
                    break;
            }


        } else {
            ValRes.setText("Valor total resistencia: 0.0 Ω");
        }
    }

    private void ResultadoRes() {
        double Res1 = Double.parseDouble(EditRes1.getText().toString());
        double Res2 = Double.parseDouble(EditRes2.getText().toString());
        double R1 = Res1 * multiplicadorR1;
        double R2 = Res2 * multiplicadorR2;
        switch (ResistenciasAgregadas) {
            case 0:
                Resultado = 1/R1 + 1/R2;
                break;
            case 1:
                double Res3 = Double.parseDouble(EditRes3.getText().toString());
                double R3 = Res3 * multiplicadorR3;
                Resultado = 1/R1 + 1/R2 + 1/R3;
                break;
            case 2:
                double Res3a = Double.parseDouble(EditRes3.getText().toString());
                double R3a = Res3a * multiplicadorR3;
                double Res4 = Double.parseDouble(EditRes4.getText().toString());
                double R4 = Res4 * multiplicadorR4;
                Resultado = 1/R1 + 1/R2 + 1/R3a + 1/R4;
                break;
            case 3:
                double Res3aa = Double.parseDouble(EditRes3.getText().toString());
                double R3aa = Res3aa * multiplicadorR3;
                double Res4a = Double.parseDouble(EditRes4.getText().toString());
                double R4a = Res4a * multiplicadorR4;
                double Res5 = Double.parseDouble(EditRes5.getText().toString());
                double R5 = Res5 * multiplicadorR5;
                Resultado = 1/R1 + 1/R2 + 1/R3aa + 1/R4a + 1/R5;
                break;
            case 4:
                double Res3aaa = Double.parseDouble(EditRes3.getText().toString());
                double R3aaa = Res3aaa * multiplicadorR3;
                double Res4aa = Double.parseDouble(EditRes4.getText().toString());
                double R4aa = Res4aa * multiplicadorR4;
                double Res5a = Double.parseDouble(EditRes5.getText().toString());
                double R5a = Res5a * multiplicadorR5;
                double Res6 = Double.parseDouble(EditRes6.getText().toString());
                double R6 = Res6 * multiplicadorR6;
                Resultado = 1/R1 + 1/R2 + 1/R3aaa + 1/R4aa + 1/R5a + 1/R6;
                break;
            case 5:
                double Res3aaaa = Double.parseDouble(EditRes3.getText().toString());
                double R3aaaa = Res3aaaa * multiplicadorR3;
                double Res4aaa = Double.parseDouble(EditRes4.getText().toString());
                double R4aaa = Res4aaa * multiplicadorR4;
                double Res5aa = Double.parseDouble(EditRes5.getText().toString());
                double R5aa = Res5aa * multiplicadorR5;
                double Res6a = Double.parseDouble(EditRes6.getText().toString());
                double R6a = Res6a * multiplicadorR6;
                double Res7 = Double.parseDouble(EditRes7.getText().toString());
                double R7 = Res7 * multiplicadorR7;
                Resultado = 1/R1 + 1/R2 + 1/R3aaaa + 1/R4aaa + 1/R5aa + 1/R6a + 1/R7;
                break;
            case 6:
                double Res3aaaaa = Double.parseDouble(EditRes3.getText().toString());
                double R3aaaaa = Res3aaaaa * multiplicadorR3;
                double Res4aaaa = Double.parseDouble(EditRes4.getText().toString());
                double R4aaaa = Res4aaaa * multiplicadorR4;
                double Res5aaa = Double.parseDouble(EditRes5.getText().toString());
                double R5aaa = Res5aaa * multiplicadorR5;
                double Res6aa = Double.parseDouble(EditRes6.getText().toString());
                double R6aa = Res6aa * multiplicadorR6;
                double Res7a = Double.parseDouble(EditRes7.getText().toString());
                double R7a = Res7a * multiplicadorR7;
                double Res8 = Double.parseDouble(EditRes8.getText().toString());
                double R8 = Res8 * multiplicadorR8;
                Resultado = 1/R1 + 1/R2 + 1/R3aaaaa + 1/R4aaaa + 1/R5aaa + 1/R6aa + 1/R7a + 1/R8;
                break;
        }
        ValRes.setText("Valor resistencia: " + 1/Resultado + "Ω");
        String valor = ConvertirRes(1/Resultado);
    }

    private String ConvertirRes(double resultado) {
        if ((resultado / 1000) >= 1) {
            Log.d(TAG, "Mayor a 1000");
            double ResA = resultado / 1000;
            ValRes.setText(String.format("Valor total resistencia:  %.3f k Ω", ResA));
            if ((resultado / 1000000) >= 1) {
                Log.d(TAG, "Mayor a 1,000,0000");
                double ResB = resultado / 1000000;
                ValRes.setText(String.format("Valor total resistencia:  %.3f M Ω", ResB));
                return "" + ResB;
            } else {
                return "" + ResA;
            }
        } else {
            ValRes.setText(String.format("Valor total resistencia:  %.3f Ω", resultado));
        }
        return "0.0 ";
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
}

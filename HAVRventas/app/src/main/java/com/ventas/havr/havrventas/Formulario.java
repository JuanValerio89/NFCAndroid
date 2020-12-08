package com.ventas.havr.havrventas;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.ventas.havr.havrventas.Herramientas.CodigoDeColores;

public class Formulario extends AppCompatActivity {

    private final String TAG = "Formulario";
    private Button BtEnviar;
    private Button BtCancelar;

    private TextInputEditText ETNombre;
    private TextInputEditText ETRFC;
    private TextInputEditText ETCorreo;
    private TextInputEditText ETTelefono;

    String Nombre;
    String Telefono;
    String Correo;
    String RFC;

    private DatabaseReference dbFormulario;
    private DatabaseReference dbUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        BtEnviar = findViewById(R.id.bt_enviar);
        BtCancelar = findViewById(R.id.bt_cancelar);

        ETNombre = findViewById(R.id.edit_nombre);
        ETTelefono = findViewById(R.id.edit_telefono);
        ETCorreo = findViewById(R.id.edit_correo);
        ETRFC = findViewById(R.id.edit_rfc);

        dbFormulario = FirebaseDatabase.getInstance().getReference().child("Formulario");

        BtEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nombre = ETNombre.getText().toString();
                RFC = ETRFC.getText().toString();
                Telefono = ETTelefono.getText().toString();
                Correo = ETCorreo.getText().toString();

                if(!Nombre.isEmpty() && !RFC.isEmpty() && !Telefono.isEmpty() && !Correo.isEmpty()){
                    Log.d(TAG,"Todos los datos estan:"+Nombre);
                    EscribirUsuario(Nombre,RFC, Correo, Telefono);
                    Toast.makeText(getApplicationContext(), "Información enviada", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Falta información por agregar", Toast.LENGTH_SHORT).show();
                }

            }
        });

        BtCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @IgnoreExtraProperties
    public static class Usuario {
        public String usuario;
        public String rfc;
        public String correo;
        public String telefono;

        public Usuario() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Usuario(String usuario, String rfc, String correo, String telefono) {
            this.usuario = usuario;
            this.rfc = rfc;
            this.correo = correo;
            this.telefono = telefono;
        }
    }

    private void EscribirUsuario(String usuario, String rfc, String correo, String telefono) {
        Usuario user = new Usuario(usuario, rfc, correo, telefono);
        dbUsuario = dbFormulario;
        dbUsuario.push().setValue(user);
    }
}

package com.ventas.havr.havrventas;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BaseSKU;
import com.ventas.havr.havrventas.Modelos.BaseUsuarios;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActividadLogin extends AppCompatActivity {
    public static final String TAG = "Actividad login";


    private TextView txTitle;
    private TextInputEditText EditUsuario;
    private TextInputEditText EditPassword;
    private Button BtIngresar;
    private Button BtCancelar;

    private BaseUsuarios baseUsuarios;
    private RealmResults<BaseUsuarios> ResulstBaseUsuarios;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    // FIREBASE
    private DatabaseReference dbPrecio;
    private ValueEventListener eventListener;

    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_login);


        txTitle = (TextView) findViewById(R.id.tx_title);
        EditUsuario = findViewById(R.id.edit_user);
        EditPassword = findViewById(R.id.edit_password);

        BtCancelar = findViewById(R.id.bt_cancelar);
        BtIngresar = findViewById(R.id.bt_ingreso);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        realm = Realm.getDefaultInstance();


        BtIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Usuario = EditUsuario.getText().toString().trim();
                String Password = EditPassword.getText().toString().trim();

                if (Usuario.length() > 0 && Password.length() > 0) {
                    // Revisar si el usuario existe.
                    try {
                        baseUsuarios = realm.where(BaseUsuarios.class)
                                .equalTo("Usuario", Usuario)
                                .findFirst();
                        if(baseUsuarios.getUsuario().isEmpty()){
                            Toast.makeText(getApplicationContext(), "El usuario no es valido.", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            String datoComparar = baseUsuarios.getPassword();
                            if (datoComparar.compareTo(Password) == 0) {
                                editor = prefs.edit();
                                editor.putString(ActividadPrincipal.USUARIO, ActividadPrincipal.DISTRIBUIDOR);
                                editor.putString(ActividadPrincipal.BANDERA_TIENDA, ActividadPrincipal.BANDERA_TIENDA_OK);
                                editor.putString(ActividadPrincipal.TIENDA, Usuario);
                                editor.commit();
                                Toast.makeText(getApplicationContext(),
                                        "Bienvenido:" + baseUsuarios.getUsuario(),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Los datos no son validos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "No se ha podido revisar la base datos.");
                        }
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "El usuario no es valido.", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Los datos no son validos",Toast.LENGTH_SHORT).show();
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

    private void SegmentarUsuarios(String datos) {
        String[] informacion = datos.split("&");
        BorrarTablasUsuarios();
        Log.d(TAG,"Datos:"+datos);
        for (int x = 0; x < informacion.length - 1; x++) {
            String[] tienda = informacion[x].split("=");
            tienda[0] = tienda[0].replace("{", "");
            tienda[0] = tienda[0].replace(" ", "");
            tienda[0] = tienda[0].replace(",", "");
            String[] segundoSegmento = tienda[1].split("/");
            String correo = segundoSegmento[0];
            String password = segundoSegmento[1];
            String telefono = segundoSegmento[2];
            Log.d(TAG,"Tienda almacenada:"+tienda[0]);
            GuardarUsuarios(tienda[0], correo, password, telefono);
        }
    }

    private void GuardarUsuarios(String user, String correo,
                                 String password, String telefono) {
        realm.beginTransaction();
        BaseUsuarios Usuarios = new BaseUsuarios(user, password, telefono, correo);
        realm.copyToRealm(Usuarios);
        realm.commitTransaction();
    }

    private void BorrarTablasUsuarios() {
        realm.beginTransaction();
        ResulstBaseUsuarios.deleteAllFromRealm();
        realm.commitTransaction();
    }


}

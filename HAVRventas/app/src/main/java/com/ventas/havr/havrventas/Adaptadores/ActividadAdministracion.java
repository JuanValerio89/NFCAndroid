package com.ventas.havr.havrventas.Adaptadores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.ventas.havr.havrventas.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadAdministracion extends AppCompatActivity {

    private final String TAG = "Administración";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ColeccionUsuarios = db.collection("USUARIOS");

    private String Correo;
    private String Password;
    private String Telefono;

    List<CrearUsuarios> crearUsuarios;

    private CrearUsuarios User;
    private CrearUsuarios RecuperarUsuarios;

    private Button BtNuevoUsuario;

    private ArrayList<String> listaCorreos;
    private ArrayList<String> listaPassword;
    private ArrayList<String> listaTelefono;

    private RecyclerView reciclador;
    private AdaptadorAdministracion adaptador;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_administracion);

        BtNuevoUsuario = findViewById(R.id.bt_nuevo_usuario);
        reciclador = findViewById(R.id.recycler);
        progressBar = findViewById(R.id.progress_administracion);

        crearUsuarios = new ArrayList<>();

        /*
        ColeccionUsuarios.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d("LogIn", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            RecuperarUsuarios = documentSnapshot.toObject(CrearUsuarios.class);
                            crearUsuarios.add(RecuperarUsuarios);
                            Log.d(TAG,"User:"+RecuperarUsuarios.getEmail());
                        }
                        //adaptador.notifyDataSetChanged();
                        adaptador = new AdaptadorAdministracion(crearUsuarios);
                        reciclador.setAdapter(adaptador);
                        progressBar.setVisibility(View.GONE);
                        reciclador.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    }
                });
        */
        BtNuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearUnUsuario();
            }
        });

        ColeccionUsuarios
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        crearUsuarios.removeAll(crearUsuarios);
                        for (QueryDocumentSnapshot documentSnapshot : snapshots) {
                            Log.d("LogIn", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            RecuperarUsuarios = documentSnapshot.toObject(CrearUsuarios.class);
                            crearUsuarios.add(RecuperarUsuarios);
                            Log.d(TAG,"User:"+RecuperarUsuarios.getEmail());
                            BtNuevoUsuario.setEnabled(true);
                        }
                        adaptador = new AdaptadorAdministracion(crearUsuarios);
                        reciclador.setAdapter(adaptador);
                        adaptador.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        reciclador.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });

    }

    public void CrearUnUsuario() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_crear_usuario, null);
        dialogBuilder.setView(dialogView);

        final EditText edit_correo = (EditText) dialogView.findViewById(R.id.edit_dis_correo);
        final EditText edit_password = (EditText) dialogView.findViewById(R.id.edit_dis_password);
        final EditText edit_telefono = (EditText) dialogView.findViewById(R.id.edit_dis_telefono);

        dialogBuilder.setTitle("Crear un nuevo usuario");
        dialogBuilder.setMessage("Ingrese la información correcta.");
        //dialogBuilder.setIcon(R.drawable.candando);

        dialogBuilder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Correo = edit_correo.getText().toString();
                Password = edit_password.getText().toString();
                Telefono = edit_telefono.getText().toString();

                if (!(Correo.matches("")) && !(Password.matches("")) && !(Telefono.matches(""))) {
                    Log.d(TAG, "Se guardan los datos!");

                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    DocumentReference ref;
                    ref = rootRef.collection("USUARIOS").document(Correo);
                    User = new CrearUsuarios(Correo,Password, Telefono,new Timestamp(new Date()));
                    ref.set(User, SetOptions.merge());
                    Toast.makeText(getApplicationContext(),"Se han guardado los datos",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al ingresar la información.", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
        b.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorRojo));
        b.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorVerde));
    }

    @Override
    protected void onStart() {
        super.onStart();



    }
}


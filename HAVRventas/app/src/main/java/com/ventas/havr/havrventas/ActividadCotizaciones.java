package com.ventas.havr.havrventas;

import android.animation.TimeAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventas.havr.havrventas.Adaptadores.AdaptadorActividadCot;
import com.ventas.havr.havrventas.Adaptadores.CotAdapter;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseUsuarios;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActividadCotizaciones extends AppCompatActivity implements RealmChangeListener<BaseCotizaciones>, AdapterView.OnItemClickListener{

    private static final String TAG = "Actividad Cotizaciones";

    private FloatingActionButton fab;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private int NumProyectos = 0;
    private int NumeroCotizacion = 0;
    private int tamañoDbCot;
    private int Posicion = 0;

    private String RegimenUsuario = "";
    private String PDFNombre = "";
    private String PDFcorreo = "";

    private DatabaseReference dbNumCot;
    private DatabaseReference dbCorreo;
    private BaseUsuarios baseUsuarios;

    public Realm realm;
    private RealmResults<BaseCotizaciones> basecot;

    private AdaptadorActividadCot adaptador;
    private ListView listView;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_cotizaciones);

        dbNumCot = FirebaseDatabase.getInstance().getReference()
                .child("NumCot");
        dbCorreo = FirebaseDatabase.getInstance().getReference().child("Cotizaciones");

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        fab = (FloatingActionButton) findViewById(R.id.fab_agregar_cot);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumProyectos = prefs.getInt("NumeroProyectos", 0);
                Log.d(TAG,"Numero:"+NumProyectos+",Regimen:"+RegimenUsuario);
                showAlertForCreatingCot("Nuevo cotización.", "Agregue una nueva cotización.");
            }
        });

        realm.init(this);
        realm = Realm.getDefaultInstance();
        tamañoDbCot = realm.where(BaseCotizaciones.class).findAll().size();
        basecot = realm.where(BaseCotizaciones.class).findAll();
        ConsultarUsuario();

        adaptador = new AdaptadorActividadCot(this, basecot,R.layout.list_cot_cot);
        listView = findViewById(R.id.lista_cotizaciones);
        listView.setAdapter(adaptador);
        listView.setOnItemClickListener(this);

        basecot.addChangeListener(new RealmChangeListener<RealmResults<BaseCotizaciones>>() {
            @Override
            public void onChange(RealmResults<BaseCotizaciones> baseCotizaciones) {
                try {
                    adaptador.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        });

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Log.d(TAG, "Se registro un cambio en la base datos");
                    Log.d("database:", dataSnapshot.getValue().toString());
                    NumeroCotizacion = Integer.parseInt(dataSnapshot.getValue().toString());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No se ha encontrado la tarjeta en el sistema.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaser", "Error!", databaseError.toException());
            }
        };
        dbNumCot.addValueEventListener(eventListener);
        registerForContextMenu(listView);
    }

    private void showAlertForCreatingCot(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_create_cot, null);
        builder.setView(viewInflate);
        EditText input = (EditText) viewInflate.findViewById(R.id.editText_dialog_componentes);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String CotName = input.getText().toString().trim();
                if (CotName.length() > 0) {
                    dbNumCot.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Log.d(TAG, "Se registro un cambio en la base datos");
                            Log.d("database:", snapshot.getValue().toString());
                            NumeroCotizacion = Integer.parseInt(snapshot.getValue().toString());
                            NumeroCotizacion = NumeroCotizacion + 1;
                            dbNumCot.setValue(NumeroCotizacion);
                            createNewCot(CotName,NumeroCotizacion);
                            NumProyectos = prefs.getInt("NumeroProyectos", 0);
                            NumProyectos = NumProyectos + 1;
                            dbCorreo.push().setValue("Cot:"+NumeroCotizacion+", " + PDFNombre + " , " + PDFcorreo);
                            editor = prefs.edit();
                            editor.putInt("NumeroProyectos",NumProyectos);
                            editor.commit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "No se ha escrito un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNewCot(String cotName, int NumeroCotizacion) {
        cotName = NumeroCotizacion + "-" + cotName;
        realm.beginTransaction();
        BaseCotizaciones Cot = new BaseCotizaciones(cotName,"0");
        realm.copyToRealm(Cot);
        realm.commitTransaction();
        Log.d("Cotizaciones", "Cotizacion almacenada:" + Cot.getId());
        editor.putInt("Cotizacion", Cot.getId());
        editor.commit();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Posicion = position;

        DialogError("Puede realizar las siguientes acciones para la cotización","¿Que desea hacer?",position);

    }

    public void DialogError(String message, String title, int posicion) {
        Log.d(TAG,"Posicion"+posicion);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("Ir a la cotizacion", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intento = new Intent(ActividadCotizaciones.this, ActividadPedidos.class);
                        intento.putExtra("id", basecot.get(posicion).getId());
                        editor.putInt("Cotizacion", basecot.get(posicion).getId());
                        editor.commit();
                        startActivity(intento);
                        Log.d(TAG, "Posicion:" + posicion + ", ID:" + basecot.get(posicion).getId());
                    }
                })
                .setNeutralButton("Agregar material", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentoA = new Intent(ActividadCotizaciones.this,Manual.class);
                        intentoA.putExtra("id", basecot.get(posicion).getId());
                        editor.putInt("Cotizacion", basecot.get(posicion).getId());
                        editor.commit();
                        startActivity(intentoA);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setTitle(title)
                .setIcon(R.drawable.pregunta)
                .setInverseBackgroundForced(true);

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.negroAzul));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.verde));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.rojo));

    }


    @Override
    public void onChange(BaseCotizaciones baseCotizaciones) {
        adaptador.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cotizacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                DialogError("Esta operación no se puede revertir.", "¿Eliminar los artículos?");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void DialogError(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        realm.beginTransaction();
                        basecot.deleteAllFromRealm();
                        realm.commitTransaction();
                        NumProyectos = prefs.getInt("NumeroProyectos", 0);
                        NumProyectos = 0;
                        editor = prefs.edit();
                        editor.putInt("NumeroProyectos",NumProyectos);
                        editor.commit();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setTitle(title)
                .setIcon(R.drawable.bt_delete)
                .setInverseBackgroundForced(true);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean ConsultarUsuario() {
        try {
            String Usuario = prefs.getString(ActividadPrincipal.TIENDA, ActividadPrincipal.NOMBRE_TIENDA);
            baseUsuarios = realm.where(BaseUsuarios.class)
                    .equalTo("Usuario", Usuario)
                    .findFirst();
            String NombreTienda = baseUsuarios.getUsuario();
            baseUsuarios = realm.where(BaseUsuarios.class)
                    .equalTo("Usuario", Usuario)
                    .findFirst();
            PDFNombre = baseUsuarios.getUsuario();
            PDFcorreo = baseUsuarios.getCorreo();
            if (NombreTienda.compareTo("HAVR") == 0)
                return false;
            else
                return true;
        }catch (Exception e){
            Toast.makeText(this, "No es posible crear una cotización, revise su conexión a internet",Toast.LENGTH_LONG);
        }
        return false;
    }

}

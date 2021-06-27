package com.ventas.havr.havrventas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventas.havr.havrventas.Adaptadores.AdaptadorSKU;
import com.ventas.havr.havrventas.Adaptadores.MyAdapter;
import com.ventas.havr.havrventas.Modelos.BaseActualizar;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BaseMasVendidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import io.realm.RealmResults;

public class Manual extends AppCompatActivity {

    private static final String TAG = "Manual";
    private String TipoManual;

    private TextView ListaActualizada;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapatador;
    private RecyclerView.LayoutManager mLayoutManager;
    private ConstraintLayout mConstraint;

    // BASE DE DATOS
    public Realm realm;
    private RealmResults<BaseSKU> ResulstBaseSKU;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private RealmResults<BaseMasVendidos> ResulstBaseMasVendidos;

    // FIREBASE
    private DatabaseReference dbPrecio;
    private ValueEventListener eventListener;
    private DatabaseReference ActualizaPrecio;
    private DatabaseReference dbImagenes;
    private DatabaseReference dbMasVendidos;

    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private String TiempoActualizar = "";
    private String TiempoActualizarPreference = "";
    private String SKUEnviar;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_componentes); // RecyclerView

        mConstraint = findViewById(R.id.constraint_componentes);

        res = getResources();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            TiempoActualizarPreference = prefs.getString("TiempoPrecios", "00-00-00");
        } catch (Exception e) {
            Log.e(TAG, "No existe la preferencia indicada.");
            GuardarPrefenciaTiempo("00-00-00");
            Log.e(TAG, "Almacenada..00-00-00");
            TiempoActualizarPreference = "00-00-00";
        }

        ListaActualizada = findViewById(R.id.text_lista);
        dbPrecio = FirebaseDatabase.getInstance().getReference().child("Precios").child("Todos").child("0");
        ActualizaPrecio = FirebaseDatabase.getInstance().getReference().child("TiempoActualizar");
        dbImagenes = FirebaseDatabase.getInstance().getReference().child("Imagenes").child("0");
        dbMasVendidos = FirebaseDatabase.getInstance().getReference().child("MasVendido");

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Se registro un cambio. Se necesita actualizar.");
                TiempoActualizar = dataSnapshot.getValue().toString();
                String dato[] = TiempoActualizar.split(("/"));
                Log.e(TAG, "Comparar string de tiempo:" + TiempoActualizar.compareTo(TiempoActualizarPreference));
                ListaActualizada.setText("Lista actualizada: " + TiempoActualizarPreference);
                if (TiempoActualizar.compareTo(TiempoActualizarPreference) == 0) {
                    Log.d(TAG, "Precios actualizados, no se requiere ninguna acción");

                    ListaActualizada.setText("Lista actualizada: " + dato[0]);
                } else {
                    try {
                        Log.d(TAG, "Se requiere actualizar precios.");
                        LimpiarListaActualizados(TiempoActualizar);
                    } catch (Exception e) {
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaser", "Error!", databaseError.toException());
            }
        };
        try {
            ActualizaPrecio.addValueEventListener(eventListener);
        } catch (Exception e) {
        }

        realm = Realm.getDefaultInstance();

        ActualizarLista();
    }

    private void LimpiarListaActualizados(String TiempoActualizar) {
        BaseActualizar baseActualizar = realm.where(BaseActualizar.class).findAll().last();
        for(int x = 0; x <= baseActualizar.getId(); x++){
            realm.beginTransaction();
            try {
                BaseActualizar baseA = realm.where(BaseActualizar.class).equalTo("id",x).findFirst();
                baseA.setEstado(false);
                realm.insertOrUpdate(baseA);
            } catch (Exception e) {
                Log.d(TAG,"No se ha podido modificar la base");
            }
            realm.commitTransaction();
        }
        GuardarPrefenciaTiempo(TiempoActualizar);
        Log.d(TAG,"Se ha limpiado la base de datos");
    }

    private void ActualizarLista() {
        String[] StringPerfiles = res.getStringArray(R.array.conjunto_componentes);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < StringPerfiles.length; i++) {
            list.add(StringPerfiles[i]);
        }
        List<String> listArticulos = new ArrayList<String>();
        for (int i = 0; i < StringPerfiles.length; i++) {
            listArticulos.add(BuscarArticulos(i));
        }
        Log.d(TAG, "Tamaño lista:" + list.size());
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapatador = new MyAdapter(getBaseContext(), list, listArticulos, R.layout.recycler_componentes, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, int position) {
                Intent moverse = new Intent(Manual.this, MaterialSKU.class);
                moverse.putExtra("Posicion", position);
                moverse.putExtra("SPerfil", name);
                startActivity(moverse);
                overridePendingTransition(R.anim.left_in,R.anim.left_out);
            }
        }, 0);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mRecyclerView.setAdapter(mAdapatador);
    }

    private String EnviarDatosLista(String buscar) {
        SKUEnviar = buscar;
        ResulstBaseSKU = realm.where(BaseSKU.class)
                .beginsWith("SKU", buscar).findAll().sort("SKU");
        ResulstBaseImagenes = realm.where(BaseImagenes.class)
                .beginsWith("SKU", buscar).findAll().sort("SKU");
        int infoNum = ResulstBaseSKU.size();
        if(infoNum == 0)
            return "Requiere actualizar";
        else
            return infoNum + "";
    }

    private String BuscarArticulos(int dato) {
        switch (dato) {
            case 0: // Alambre magneto
                return EnviarDatosLista("AM");
            case 1: // Arduinos
                return EnviarDatosLista("AR");
            case 2: // Baterias
                return EnviarDatosLista("BA");
            case 3: // Brocas
                return EnviarDatosLista("BR");
            case 4: // Bases
                return EnviarDatosLista("BS");
            case 5: // Bluetooth
                return EnviarDatosLista("BT");
            case 6: // Cables
                return EnviarDatosLista("CA");
            case 7: // Capacitores
                return EnviarDatosLista("CC");
            case 8: // CN
                return EnviarDatosLista("CN");
            case 9: // Circuitos
                return EnviarDatosLista("CR");
            case 10: // Cables
                return EnviarDatosLista("CS");
            case 11: // Cautin
                return EnviarDatosLista("CT");
            case 12: // Disipadores
                return EnviarDatosLista("DI");
            case 13: // Dip switch
                return EnviarDatosLista("DP");
            case 14: // display
                return EnviarDatosLista("DS");
            case 15: // Laminación
                return EnviarDatosLista("EI");
            case 16: // Fuentes
                return EnviarDatosLista("FC");
            case 17: // Header
                return EnviarDatosLista("HR");
            case 18: // HAVR
                return EnviarDatosLista("HV");
            case 19: // Led
                return EnviarDatosLista("LD");
            case 20: // Medidor
                return EnviarDatosLista("ME");
            case 21: // Modulo
                return EnviarDatosLista("MO");
            case 22: // Motores
                return EnviarDatosLista("MT");
            case 23: // Multimetros
                return EnviarDatosLista("MU");
            case 24: // Placas
                return EnviarDatosLista("PL");
            case 25: // Potenciometros
                return EnviarDatosLista("PM");
            case 26: // Programadores
                return EnviarDatosLista("PR");
            case 27: // Pinzas
                return EnviarDatosLista("PS");
            case 28: // Protoboard
                return EnviarDatosLista("PT");
            case 29: // Radiofrecuencia
                return EnviarDatosLista("RF");
            case 30: // Resistencias SMD
                return EnviarDatosLista("RM");
            case 31: // Resistencias
                return EnviarDatosLista("RS");
            case 32: // Relevadores
                return EnviarDatosLista("RY");
            case 33: // Sensores
                return EnviarDatosLista("SR");
            case 34: // Servomotores
                return EnviarDatosLista("SV");
            case 35: // Switch
                return EnviarDatosLista("SW");
            case 36: // Miscelaneo
                return EnviarDatosLista("TB");
            case 37: // Teclados
                return EnviarDatosLista("TE");
            case 38: // Transformadores
                return EnviarDatosLista("TM");
            case 39: // Potenciometro lineales
                return EnviarDatosLista("TR");
            case 40: // Trimpots
                return EnviarDatosLista("TV");


        }
        return "Requiere actualizar";
    }

    private void GuardarPrefenciaTiempo(String time) {
        Log.d(TAG, "Actualizar tiempo:" + time);
        editor = prefs.edit();
        editor.putString("TiempoPrecios", time);
        editor.commit();
        try {
            TiempoActualizarPreference = prefs.getString("TiempoPrecios", "00-00-00");
        } catch (Exception e) {
            Log.e(TAG, "No existe la preferencia indicada.");
            GuardarPrefenciaTiempo("00-00-00");
            Log.e(TAG, "Almacenada..00-00-00");
            TiempoActualizarPreference = "00-00-00";
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buscador, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.buscador:
                Intent intento = new Intent(Manual.this, ActividadBusqueda.class);
                startActivity(intento);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActualizarLista();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActualizarLista();
    }
}

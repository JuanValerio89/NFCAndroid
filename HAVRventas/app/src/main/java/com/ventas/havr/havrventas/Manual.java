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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    // BASE DE DATOS
    public Realm realm;
    private RealmResults<BaseSKU> ResulstBaseSKU;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private RealmResults<BaseMasVendidos> ResulstBaseMasVendidos;
    private List<BaseSKU> ListBaseSKU;
    private BaseSKU baseSKU;

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

    // Dialog
    private ProgressDialog vDialog;
    private MiTareaAsincronaDialog tarea2;
    private int TipoDialog = 0;
    private int Progreso = 0;

    private int enviarProgreso = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_componentes); // RecyclerView
        Resources res = getResources();
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
                        CrearDialog();
                        tarea2 = new MiTareaAsincronaDialog();
                        tarea2.execute();
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
        String[] StringPerfiles = res.getStringArray(R.array.conjunto_componentes);
        realm = Realm.getDefaultInstance();
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
        return infoNum + "";
    }

    private String BuscarArticulos(int dato) {
        switch (dato) {
            case 0: // Arduinos
                return EnviarDatosLista("AR");
            case 1: // Baterias
                return EnviarDatosLista("BA");
            case 2: // Bases
                return EnviarDatosLista("BS");
            case 3: // Brocas
                return EnviarDatosLista("BR");
            case 4: // Bluetooth
                return EnviarDatosLista("BT");
            case 5: // Cables
                return EnviarDatosLista("CA");
            case 6: // Ceramicos
                return EnviarDatosLista("CC");
            case 7: // Cautines
                return EnviarDatosLista("CT");
            case 8: // Circuitos
                return EnviarDatosLista("CR");
            case 9: // Dupont
                return EnviarDatosLista("CS");
            case 10: // Disipadores
                return EnviarDatosLista("DI");
            case 11: // DIP switch
                return EnviarDatosLista("DP");
            case 12: // Display
                return EnviarDatosLista("DS");
            case 13: // Header
                return EnviarDatosLista("HR");
            case 14: // H_AVR
                return EnviarDatosLista("HV");
            case 15: // Laser
                return EnviarDatosLista("LS");
            case 16: // Leds
                return EnviarDatosLista("LD");
            case 17: // Medidores
                return EnviarDatosLista("ME");
            case 18: // Modulos
                return EnviarDatosLista("MO");
            case 19: // Motores
                return EnviarDatosLista("MT");
            case 20: // Placas
                return EnviarDatosLista("PL");
            case 21: // Potenciometros
                return EnviarDatosLista("PM");
            case 22: // Programadores
                return EnviarDatosLista("PR");
            case 23: // Pinzas
                return EnviarDatosLista("PS");
            case 24: // Radiofrecuencia
                return EnviarDatosLista("RF");
            case 25: // Radiofrecuencia
                return EnviarDatosLista("RM");
            case 26: // Resistencias
                return EnviarDatosLista("RS");
            case 27: // Relevadores
                return EnviarDatosLista("RY");
            case 28: // CNC Router
                return EnviarDatosLista("CN");
            case 29: // Celda Solar
                return EnviarDatosLista("SL");
            case 30: // Sistema minimo
                return EnviarDatosLista("SM");
            case 31: // Sensores
                return EnviarDatosLista("SR");
            case 32: // Servomotor
                return EnviarDatosLista("SV");
            case 33: // Switch
                return EnviarDatosLista("SW");
            case 34: // Terminal Block
                return EnviarDatosLista("TB");
            case 35: // Transformadores
                return EnviarDatosLista("TM");
            case 36: // Potenciometro lineal
                return EnviarDatosLista("TR");
            case 37: // Trimpos
                return EnviarDatosLista("TV");
            case 38: // Wire
                return EnviarDatosLista("WI");
        }
        return "0";
    }


    private int ActualizarPrecioHandler(){
        vDialog.setProgress(5);
        dbPrecio.addListenerForSingleValueEvent(new ValueEventListener() {
            int data_x = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String total = dataSnapshot.getValue().toString();
                OrdenarPrecios(total, 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //tarea2.onProgressUpdate(45);
        runOnUiThread(new Runnable(){
            public void run() {
                tarea2.onProgressUpdate(45);
                Toast.makeText(getApplicationContext(), "Actualizando precios...",Toast.LENGTH_LONG).show();
            }
        });
        Log.d(TAG, "Progreso enviado:" + Progreso);
        return Progreso;
    }

    private void ActualizarImagenesHandler( ) {
        final String[] data = {""};

        dbImagenes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String total = dataSnapshot.getValue().toString();
                data[0] = total;
                Log.d(TAG, "Imagenes:" + total);
                OrdenarImagenes(total, 1);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        runOnUiThread(new Runnable(){
            public void run() {
                Toast.makeText(getApplicationContext(), "Imagenes actualizados...",Toast.LENGTH_LONG).show();
                tarea2.onProgressUpdate(80);
            }
        });

    }

    private void ActualizarMasVendidosHandler( ) {
        tarea2.onProgressUpdate(5);
        dbMasVendidos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String total = dataSnapshot.getValue().toString();
                Log.d(TAG, "Mas vendidos:" + total);
                OrdenarMasVendido(total);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void OrdenarMasVendido(String masVendido) {
        Log.d("Base mas vendido:", masVendido);
        ResulstBaseMasVendidos = realm.where(BaseMasVendidos.class).findAll();
        BorrarTablasMasVendido();
        String[] listaA = masVendido.split(",");
        Log.d(TAG, "Almacenando los precios, tamaño lista por concepto:" + listaA.length);
        for (int x = 0; x < listaA.length; x++){
            String[] separaNombres = listaA[x].split("=");
            try {
                separaNombres[0] = separaNombres[0].replace(" ", "");
                separaNombres[0] = separaNombres[0].replace("{", "");
                String SKU = separaNombres[0].replace("}", "");
                separaNombres[1] = separaNombres[1].replace(" ", "");
                separaNombres[1] = separaNombres[1].replace("{", "");
                String Otro = separaNombres[1].replace("}", "");
                createMasVendido(SKU, Otro);

            } catch (Exception e) {
            }
        }
    }

    private void OrdenarImagenes(String imagenes, int tipo) {
        Log.d("Base imagenes Total:", imagenes);
        ResulstBaseImagenes = realm.where(BaseImagenes.class).findAll();
        BorrarTablasImagenes();
        String[] listaA = imagenes.split(",");
        Log.d(TAG, "Almacenando las imagenes:" + listaA.length);
        int datoBar = listaA.length / 100;
        int y = 0;
        for (int x = 0; x < listaA.length; x++) {
            String[] separaNombres = listaA[x].split("=");
            try {
                separaNombres[0] = separaNombres[0].replace(" ", "");
                separaNombres[0] = separaNombres[0].replace("{", "");
                String SKU = separaNombres[0].replace("}", "");
                String[] separaLink = separaNombres[1].split("@");
                String Link = separaLink[0];
                String LinkML = separaLink[1];
                String LinkKK = separaLink[2];
                String LinkPDF = separaLink[3].replace(">", "");
                createImagenList(SKU, Link, LinkML, LinkKK, LinkPDF);

                if( y == datoBar && tipo == 1) {
                    Log.d(TAG,"PROGRESO A: " + datoBar + " %");
                    y = 0;
                    enviarProgreso = enviarProgreso + 1;
                }
            } catch (Exception e) {
            }
        }
    }

    private int OrdenarPrecios(String listaPreciosTotal, int tipo) {
        Log.d("Base precios Total:", listaPreciosTotal);
        ResulstBaseSKU = realm.where(BaseSKU.class).findAll();
        BorrarTablas();
        String[] listaA = listaPreciosTotal.split("&");
        Log.d(TAG, "Almacenando los precios, Ordenar precios:" + listaA.length);
        int datoBar = listaA.length / 100;

        int y = 0;
        for (int x = 0; x < listaA.length - 1; x++) {
            y = y + 1;
            //Log.d(TAG, "Articulo:" + listaA);
            String[] separaNombres = listaA[x].split("=");
            separaNombres[0] = separaNombres[0].replace(",", "");
            separaNombres[0] = separaNombres[0].replace(" ", "");
            separaNombres[0] = separaNombres[0].replace("{", "");
            separaNombres[0] = separaNombres[0].replace("}", "");
            String[] separarPrecios = separaNombres[1].split("@");

            separarPrecios[0] = separarPrecios[0].replace("{", "");
            separarPrecios[0] = separarPrecios[0].replace("}", "");
            separarPrecios[1] = separarPrecios[1].replace(" ", "");
            separarPrecios[1] = separarPrecios[1].replace("{", "");
            separarPrecios[1] = separarPrecios[1].replace("}", "");
            createPriceList(separaNombres[0], separarPrecios[0],
                    separarPrecios[1], separarPrecios[2], separarPrecios[3]);
            if( y == datoBar && tipo == 1) {
                y = 0;
                enviarProgreso = enviarProgreso + 1;
            }

        }
        Progreso = 33;
        GuardarPrefenciaTiempo(TiempoActualizar);
        return 33;
    }

    private void createPriceList(String sku, String descripcion,
                                 String cantidad, String precioPublico,
                                 String precio) {
        realm.beginTransaction();
        BaseSKU Cot = new BaseSKU(sku, descripcion, cantidad, precio, precioPublico);
        realm.copyToRealm(Cot);
        realm.commitTransaction();
    }

    private void createMasVendido(String sku, String otro) {
        realm.beginTransaction();
        BaseMasVendidos Cot = new BaseMasVendidos(sku, otro);
        realm.copyToRealm(Cot);
        realm.commitTransaction();
    }

    private void createImagenList(String sku, String link, String linkML, String linkkk, String linkPdf) {
        realm.beginTransaction();
        BaseImagenes Cot = new BaseImagenes(sku, link, linkML, linkkk, linkPdf);
        realm.copyToRealm(Cot);
        realm.commitTransaction();
    }

    private void BorrarTablas() {
        realm.beginTransaction();
        ResulstBaseSKU.deleteAllFromRealm();
        realm.commitTransaction();
    }


    private void BorrarTablasImagenes() {
        realm.beginTransaction();
        ResulstBaseImagenes.deleteAllFromRealm();
        realm.commitTransaction();
    }

    private void BorrarTablasMasVendido() {
        realm.beginTransaction();
        ResulstBaseMasVendidos.deleteAllFromRealm();
        realm.commitTransaction();
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
            case R.id.actualizar:
                Log.d(TAG, "Actualización por cliente.");
                createSimpleDialog();
                return true;
            case R.id.buscador:
                Intent intento = new Intent(Manual.this, ActividadBusqueda.class);
                startActivity(intento);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MiTareaAsincronaDialog extends
            AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if (TipoDialog == 1) {
                Log.e(TAG, "Guardando la información. Puede demorar unos minutos... no cierre la aplicación.");
                ActualizarImagenesHandler();
                ActualizarPrecioHandler();
                ActualizarMasVendidosHandler();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
            }
            return true;
        }
        @SuppressLint("WrongThread")
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            Log.d(TAG, "PROGRESO Vdialog:" + progreso);
            vDialog.setProgress(progreso);
        }
        @Override
        protected void onPreExecute() {
            vDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    MiTareaAsincronaDialog.this.cancel(false);
                }
            });
            vDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            vDialog.dismiss();
            runOnUiThread(new Runnable(){
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Material actualizado",
                            Toast.LENGTH_SHORT).show();
                }
            });
            ListaActualizada.setText("Lista actualizada: " + TiempoActualizar);
            Log.d(TAG, "Proceso AsyncTask concluido");
            if (result) {
                Log.d(TAG, "Proceso terminado:" + TipoDialog);
                switch (TipoDialog) {
                    case 1:
                        vDialog.dismiss();
                        ListaActualizada.setText("Lista actualizada: " + TiempoActualizar);
                        Toast.makeText(getApplicationContext(),
                                "Material actualizado",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            Log.d(TAG, "NO ha dejado de mostrarse el Dialog:" + vDialog.isShowing());
        }
    }


    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_actualizar_a);
        builder.setTitle("¿Desea actualizar la base de datos?")
                .setMessage("Esperando confirmación...")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CrearDialog();
                                tarea2 = new MiTareaAsincronaDialog();
                                tarea2.execute();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

        return builder.create();
    }

    private void CrearDialog() {

        vDialog = new ProgressDialog(Manual.this);
        vDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        vDialog.setMessage("Descargando base de datos. Puede demorar unos minutos... no cierre la aplicación.");
        vDialog.setCancelable(false);
        vDialog.setMax(100);
        vDialog.show();
        TipoDialog = 1;
    }

}

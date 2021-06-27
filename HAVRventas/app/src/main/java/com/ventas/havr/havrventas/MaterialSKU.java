package com.ventas.havr.havrventas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.DocumentException;
import com.ventas.havr.havrventas.Adaptadores.AdaptadorSKU;
import com.ventas.havr.havrventas.Modelos.BaseActualizar;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;
import com.ventas.havr.havrventas.Modelos.ObtenerImagenes;
import com.ventas.havr.havrventas.Modelos.ObtenerPrecios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MaterialSKU extends AppCompatActivity implements RealmChangeListener<BaseSKU>, AdapterView.OnItemClickListener {

    // Final String
    final private static String TAG = "MaterialSKU";
    final public static int KICHINK = 0;
    final public static int MERCADO = 1;
    final public static int PDF = 2;

    private String Buscador;
    private String imagen;
    private String Sku;
    private String TiempoActualizar;
    private String SKUEnviar;
    private String TiempoActualizarPreference;

    private int progresoAfuera = 0;
    private int CerrarDialog = 0;
    // FIREBASE
    private DatabaseReference dbPrecioFire;
    private DatabaseReference dbImagenesFire;
    private ValueEventListener eventListener;
    private ValueEventListener eventListenerImagenes;
    private boolean insertar;

    PDFCatalogo templateCatalogo;
    // XML
    private ListView ListaSKU;

    // BASE DE DATOS
    public Realm realm;
    private RealmResults<BaseSKU> ResulstBaseSKU;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private BaseImagenes baseImages;
    private BaseCotizaciones baseCotizaciones;
    private RealmList<BasePedidos> basePedidos;
    private BasePedidos basePed;
    private BaseCotizaciones baseCot;

    private int NumCotizacion;
    public int NumPiezasAgregar = 0;
    private String TipoUsuario = "";
    private int usuario = 0;
    private int valorProducto = 0;
    private int tipoTarea = 0;

    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    // Adaptador
    private AdaptadorSKU adaptador;
    private boolean Actualizar = false;
    private boolean ActualizadoPrecios = false;
    private boolean ActualizadoImagenes = false;

    // Dialog
    private ProgressDialog vDialog;
    private MiTareaAsincronaDialog tarea2;
    private int TipoDialog = 0;

    List<String> productos = Arrays.asList("AM0000", "AR0000", "BA0000", "BR0000", "BS0000", "BT0000", "CA0000",
            "CC0000", "CN0000", "CR0000", "CS0000", "CT0000", "DI0000", "DP0000", "DS0000", "EI0000", "FC0000",
            "HR0000", "HV0000", "LD0000", "ME0000", "MO0000", "MT0000", "MU0000", "PL0000", "PM0000", "PR0000",
            "PS0000", "PT0000", "RF0000", "RM0000", "RS0000", "RY0000", "SR0000", "SV0000", "SW0000", "TB0000",
            "TE0000", "TM0000", "TR0000", "TV0000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actualizar = true;
        setContentView(R.layout.activity_material_sku);

        Intent intent = getIntent();
        int ValorItem = intent.getIntExtra("Posicion", 0);
        String Material = intent.getStringExtra("SPerfil");
        ListaSKU = findViewById(R.id.list_sku);
        this.setTitle(Material);
        // REALM
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NumCotizacion = prefs.getInt("Cotizacion", 0);
        Log.d(TAG, "Numero cot:" + NumCotizacion);

        // Firebase
        dbPrecioFire = FirebaseDatabase.getInstance().getReference().child("Precio2021");
        dbImagenesFire = FirebaseDatabase.getInstance().getReference().child("Imagenes2021");

        realm = Realm.getDefaultInstance();
        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id", NumCotizacion).findFirst();
        basePedidos = baseCotizaciones.getBasePedidos();
        ResulstBaseSKU = realm.where(BaseSKU.class).findAll();
        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            usuario = 1;   // Distribuidor
        } else {
            usuario = 0;   // Tienda
        }
        // Prefenciass
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            TiempoActualizarPreference = prefs.getString("TiempoPrecios", "00-00-00");
        } catch (Exception e) {
            Log.e(TAG, "No existe la preferencia indicada.");
            GuardarPrefenciaTiempo("00-00-00");
            Log.e(TAG, "Almacenada..00-00-00");
            TiempoActualizarPreference = "00-00-00";
        }
        Log.d(TAG, "Ingresando:" + ValorItem);
        dbPrecioFire.child(productos.get(ValorItem));
        valorProducto = ValorItem;
        switch (ValorItem) {
            case 0: // Alambre Magneto
                EnviarDatosLista("AM");
                break;
            case 1: // Arduinos
                EnviarDatosLista("AR");
                break;
            case 2: // Baterias
                EnviarDatosLista("BA");
                break;
            case 3: // Brocas
                EnviarDatosLista("BR");
                break;
            case 4: // Bases
                EnviarDatosLista("BS");
                break;
            case 5: // Bluetooth
                EnviarDatosLista("BT");
                break;
            case 6: // Cables
                EnviarDatosLista("CA");
                break;
            case 7: // Capacitores
                EnviarDatosLista("CC");
                break;
            case 8: // CN
                EnviarDatosLista("CN");
                break;
            case 9: // Circuitos
                EnviarDatosLista("CR");
                break;
            case 10: // Cables
                EnviarDatosLista("CS");
                break;
            case 11: // Cautin
                EnviarDatosLista("CT");
                break;
            case 12: // Disipadores
                EnviarDatosLista("DI");
                break;
            case 13: // Dip switch
                EnviarDatosLista("DP");
                break;
            case 14: // Display
                EnviarDatosLista("DS");
                break;
            case 15: // Laminacion
                EnviarDatosLista("EI");
                break;
            case 16: // Fuentes
                EnviarDatosLista("FC");
                break;
            case 17: // Header
                EnviarDatosLista("HR");
                break;
            case 18: // HAVR
                EnviarDatosLista("HV");
                break;
            case 19: // Led
                EnviarDatosLista("LD");
                break;
            case 20: // Medidor
                EnviarDatosLista("ME");
                break;
            case 21: // Modulo
                EnviarDatosLista("MO");
                break;
            case 22: // Motores
                EnviarDatosLista("MT");
                break;
            case 23: // Multimetros
                EnviarDatosLista("MU");
                break;
            case 24: // Placas
                EnviarDatosLista("PL");
                break;
            case 25: // Potenciometros
                EnviarDatosLista("PM");
                break;
            case 26: // Programadores
                EnviarDatosLista("PR");
                break;
            case 27: // Pinzas
                EnviarDatosLista("PS");
                break;
            case 28: // Protoboard
                EnviarDatosLista("PT");
                break;
            case 29: // Radiofrecuencia
                EnviarDatosLista("RF");
                break;
            case 30: // Resistencias SMD
                EnviarDatosLista("RM");
                break;
            case 31: // Resistencias
                EnviarDatosLista("RS");
                break;
            case 32: // Relevadores
                EnviarDatosLista("RY");
                break;
            case 33: // Sensores
                EnviarDatosLista("SR");
                break;
            case 34: // Servomotores
                EnviarDatosLista("SV");
                break;
            case 35: // Switch
                EnviarDatosLista("SW");
                break;
            case 36: // Miscelaneo
                EnviarDatosLista("TB");
                break;
            case 37: // terminales 4x4
                EnviarDatosLista("TE");
                break;
            case 38: // Transformadores
                EnviarDatosLista("TM");
                break;
            case 39: // Potenciometros lineales
                EnviarDatosLista("TR");
                break;
            case 40: // Trimpot
                EnviarDatosLista("TV");
                break;
        }
    }

    private void EnviarDatosLista(String buscar) {
        Buscador = buscar;
        try {
            BaseActualizar baseActualizar = realm.where(BaseActualizar.class).equalTo("SKU", productos.get(valorProducto)).findFirst();
            if (!baseActualizar.getEstado()) { // true base actualizada
                CrearDialog();
            }
        } catch (Exception e) {
            CrearDialog();
            Log.d(TAG, "No existe base Actualizar");
        }
        mostrarListado(buscar);
    }


    private void mostrarListado(String buscar) {
        SKUEnviar = buscar;
        ResulstBaseSKU = realm.where(BaseSKU.class)
                .beginsWith("SKU", buscar).findAll().sort("SKU");
        ResulstBaseImagenes = realm.where(BaseImagenes.class)
                .beginsWith("SKU", buscar).findAll().sort("SKU");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        int tipo = 0; // Publico
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            tipo = 1;  // Distribuidor
        }
        adaptador = new AdaptadorSKU(this, ResulstBaseSKU, ResulstBaseImagenes, R.layout.list_cot_item, tipo);
        ListaSKU.setAdapter(adaptador);
        ListaSKU.setOnItemClickListener(this);
        registerForContextMenu(ListaSKU);
    }

    private void recuperarImagenes(String buscar) {
        eventListenerImagenes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Se registro un cambio en la base datos para imagenes:" + dataSnapshot.getValue().toString());
                SaveDataImagenes(dataSnapshot, buscar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaser", "Error!", databaseError.toException());
            }
        };
        try {
            dbImagenesFire.addValueEventListener(eventListenerImagenes);
        } catch (Exception e) {

        }

    }

    private void recuperarInformación(String buscar) {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Se registro un cambio en la base datos:" + dataSnapshot.getValue().toString());
                SaveDataPrecios(dataSnapshot, buscar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaser", "Error!", databaseError.toException());
            }
        };
        try {
            dbPrecioFire.addValueEventListener(eventListener);
        } catch (Exception e) {
        }
    }

    private void SaveDataImagenes(DataSnapshot dataSnapshot, String buscar) {
        Log.d(TAG, "SaveData Imagenes");
        dataSnapshot = dataSnapshot.child(productos.get(valorProducto));
        int y = 0;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            y = y + 1;
            progresoAfuera = y;
            try {
                Log.d(TAG, "Child:" + progresoAfuera + ds.toString());
                ObtenerImagenes obtenerImagenes = ds.getValue(ObtenerImagenes.class);
                crearListaImagenes(ds.getKey(), obtenerImagenes.getLinkImagen(), obtenerImagenes.getLinkMercado(),
                        obtenerImagenes.getLinkMercado(), obtenerImagenes.getLinkPDF());
            } catch (Exception e) {
                Log.d(TAG, "Error al leer la información");
            }
        }
        ActualizadoImagenes = true;
        Log.d(TAG, "Datos actualizados en realm imagenes:" + ActualizadoImagenes + "," + ActualizadoPrecios);
        if (ActualizadoImagenes && ActualizadoPrecios) {
            CerrarDialog = 1;
            mostrarListado(buscar);
            ListaActualizada(productos.get(valorProducto), true);
            vDialog.dismiss();
            tarea2.onPostExecute(true);
            Toast.makeText(getApplicationContext(),
                    "Material actualizado",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void SaveDataPrecios(DataSnapshot dataSnapshot, String buscar) {
        Log.d(TAG, "SaveData");
        dataSnapshot = dataSnapshot.child(productos.get(valorProducto));
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            try {
                Log.d(TAG, "Child:" + ds.toString());
                ObtenerPrecios obtenerPrecios = ds.getValue(ObtenerPrecios.class);
                Log.d(TAG, "Precios cantidad:" + obtenerPrecios.getCantidad());
                createPriceList(ds.getKey(), obtenerPrecios.getDescripcion(), obtenerPrecios.getCantidad() + "",
                        obtenerPrecios.getPrecioPublico(), obtenerPrecios.getPrecioTienda());
            } catch (Exception e) {
                Log.d(TAG, "Error al leer la información");
            }
        }
        ActualizadoPrecios = true;
        Log.d(TAG, "Datos actualizados en realm");
        if (ActualizadoImagenes && ActualizadoPrecios) {
            CerrarDialog = 1;
            mostrarListado(buscar);
            ListaActualizada(productos.get(valorProducto), true);
            vDialog.dismiss();
            tarea2.onPostExecute(true);
            Toast.makeText(getApplicationContext(),
                    "Material actualizado",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void ListaActualizada(String sku, boolean estado) {
        Log.d(TAG, "Base actualizar.");
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            BaseActualizar baseA = realm.where(BaseActualizar.class).equalTo("SKU", sku).findFirst();
            baseA.setEstado(estado);
            realm.insertOrUpdate(baseA);
            Log.d(TAG, "Se completo el almacenamiento");
        } catch (Exception e) {
            BaseActualizar Cot = new BaseActualizar(sku, estado);
            realm.insertOrUpdate(Cot);
            Log.d(TAG, "Se inserta el documento");
        }
        realm.commitTransaction();
        realm.close();
    }

    private void crearListaImagenes(String sku, String link, String linkML, String linkkk, String linkPdf) {
        // El try se aplica por si el SKU ya esta disponible en la base datos, si existe se actualiza,
        // si no existe se agrega un articulo nuevo.
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            BaseImagenes baseA = realm.where(BaseImagenes.class).equalTo("SKU", sku).findFirst();
            baseA.setLink(link);
            baseA.setLinkML(linkML);
            baseA.setLinkKK(linkML);
            baseA.setLinkPDF(linkPdf);
            realm.insertOrUpdate(baseA);
        } catch (Exception e) {
            BaseImagenes Cot = new BaseImagenes(sku, link, linkML, linkkk, linkPdf);
            realm.insertOrUpdate(Cot);
        }
        realm.commitTransaction();
        realm.close();

    }

    private void createPriceList(String sku, String descripcion, String cantidad,
                                 String precioPublico, String precio) {
        // El try se aplica por si el SKU ya esta disponible en la base datos, si existe se actualiza,
        // si no existe se agrega un articulo nuevo.
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            BaseSKU baseA = realm.where(BaseSKU.class).equalTo("SKU", sku).findFirst();
            baseA.setCantidad(cantidad);
            baseA.setDescripcion(descripcion);
            baseA.setPrecioPublico(precioPublico);
            baseA.setPrecio(precio);
            realm.insertOrUpdate(baseA);
        } catch (Exception e) {
            BaseSKU Cot = new BaseSKU(sku, descripcion, cantidad, precio, precioPublico);
            realm.insertOrUpdate(Cot);
        }
        realm.commitTransaction();
        realm.close();
    }

    private void GuardarPrefenciaTiempo(String time) {
        editor = prefs.edit();
        editor.putString("TiempoPrecios", time);
        editor.commit();
    }


    @Override
    public void onChange(BaseSKU baseSKU) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        realm = Realm.getDefaultInstance();
        String SKUbuscar = ResulstBaseSKU.get(position).getSKU();
        String Precio = ResulstBaseSKU.get(position).getPrecio();
        String cantidad = "0";
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            Precio = ResulstBaseSKU.get(position).getPrecio();
        } else
            Precio = ResulstBaseSKU.get(position).getPrecioPublico();
        cantidad = ResulstBaseSKU.get(position).getCantidad();
        baseImages = realm.where(BaseImagenes.class).equalTo("SKU", SKUbuscar).findFirst();
        Intent intento = new Intent(MaterialSKU.this, DialogImage.class);
        try {
            intento.putExtra("LINK", baseImages.getLink());
        } catch (Exception e) {

        }
        intento.putExtra("CANTIDAD", cantidad);
        intento.putExtra("POSICION", position);
        intento.putExtra("PRECIO", Precio);
        intento.putExtra("SKU", SKUEnviar);
        startActivity(intento);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carrito, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.carrito:
                Log.d(TAG, "Ir al carrito");
                Intent intento = new Intent(MaterialSKU.this, ActividadPedidos.class);
                intento.putExtra("id", NumCotizacion);
                startActivity(intento);
                return true;
            case R.id.pdf_catalogo:
                Log.d(TAG, "Generar catalogo");
                vDialog = new ProgressDialog(MaterialSKU.this);
                vDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                vDialog.setMessage("Generando catalogo PDF.");
                vDialog.setCancelable(false);
                vDialog.setMax(100);
                tipoTarea = 1;
                vDialog.show();
                tarea2 = new MiTareaAsincronaDialog();
                tarea2.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void CrearPDFCatalogo() throws IOException, DocumentException {

        templateCatalogo = new PDFCatalogo(getApplicationContext());
        templateCatalogo.openDocument(Buscador + "00XX");
        templateCatalogo.addCatalogo(Buscador,usuario);
        templateCatalogo.closeDocument();
        Log.d(TAG, "Finalizo PDF");

    }

    private void CrearDialog() {
        vDialog = new ProgressDialog(MaterialSKU.this);
        vDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        vDialog.setMessage("Descargando datos.");
        vDialog.setCancelable(false);
        vDialog.setMax(100);
        vDialog.show();
        tarea2 = new MiTareaAsincronaDialog();
        tarea2.execute();
    }

    private class MiTareaAsincronaDialog extends
            AsyncTask<Void, Integer, Boolean> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... params) {
            if(tipoTarea == 1){
                try {
                    CrearPDFCatalogo();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                return true;
            }else {
                Log.e(TAG, "Descargando información");
                recuperarInformación(Buscador);
                recuperarImagenes(Buscador);
                return true;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            vDialog.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {
            vDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    MaterialSKU.MiTareaAsincronaDialog.this.cancel(true);
                }
            });
            vDialog.setProgress(0);
            vDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "Proceso AsyncTask concluido");

            if(tipoTarea == 1) {
                templateCatalogo.viewPDF(Buscador + "00XX");
                Log.d(TAG, "Proceso terminado: se cierra el Dialog");
                vDialog.dismiss();
                vDialog.cancel();
                Toast.makeText(getApplicationContext(),
                        "Material actualizado",
                        Toast.LENGTH_SHORT).show();
            }
            if (result) {
                if (CerrarDialog == 1) {
                    Log.d(TAG, "Proceso terminado: se cierra el Dialog");
                    vDialog.dismiss();
                    vDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Material actualizado",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "NO ha dejado de mostrarse el Dialog:" + vDialog.isShowing());
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
        Log.d(TAG, "Actividad onResume");
        try {
            BaseActualizar baseActualizar = realm.where(BaseActualizar.class).equalTo("SKU", productos.get(valorProducto)).findFirst();
            if (!baseActualizar.getEstado()) { // true base actualizada
                //CrearDialog();
            } else {
                Log.d(TAG, "Se desactivaron las actualizaciones");
                dbPrecioFire.removeEventListener(eventListener);
                dbImagenesFire.removeEventListener(eventListenerImagenes);
            }
        } catch (Exception e) {
            //CrearDialog();
            Log.d(TAG, "No existe base Actualizar");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        realm.close();
        try {
            dbPrecioFire.addValueEventListener(eventListener);
        } catch (Exception e) {

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        realm.close();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        Actualizar = false;
        Log.d(TAG, "Boton hacia atras");
        try {
            dbPrecioFire.removeEventListener(eventListener);
            dbImagenesFire.removeEventListener(eventListenerImagenes);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.close();
        try {
            dbPrecioFire.removeEventListener(eventListener);
            dbImagenesFire.removeEventListener(eventListenerImagenes);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        Actualizar = false;
        Log.d(TAG, "Actividad terminada");
    }

    private void tareaLarga() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
    }
}

package com.ventas.havr.havrventas;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ventas.havr.havrventas.Adaptadores.AdaptadorSKU;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;

import java.io.IOException;
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

    private String data;
    private String imagen;
    private String Sku;
    private String TiempoActualizar;
    private String SKUEnviar;
    private String TiempoActualizarPreference;

    private int posicion = 0;
    // FIREBASE
    private DatabaseReference dbPrecio;
    private ValueEventListener eventListener;
    //private DatabaseReference ActualizaPrecio;

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
    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    // Adaptador
    private AdaptadorSKU adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        switch (ValorItem) {
            case 0: // Arduinos
                EnviarDatosLista("AR");
                break;
            case 1: // Baterias
                EnviarDatosLista("BA");
                break;
            case 2: // Bases
                EnviarDatosLista("BS");
                break;
            case 3: // Brocas
                EnviarDatosLista("BR");
                break;
            case 4: // Bluetooth
                EnviarDatosLista("BT");
                break;
            case 5: // Cables
                EnviarDatosLista("CA");
                break;
            case 6: // Ceramicos
                EnviarDatosLista("CC");
                break;
            case 7: // Cautines
                EnviarDatosLista("CT");
                break;
            case 8: // Circuitos
                EnviarDatosLista("CR");
                break;
            case 9: // Dupont
                EnviarDatosLista("CS");
                break;
            case 10: // Disipadores
                EnviarDatosLista("DI");
                break;
            case 11: // DIP switch
                EnviarDatosLista("DP");
                break;
            case 12: // Display
                EnviarDatosLista("DS");
                break;
            case 13: // Header
                EnviarDatosLista("HR");
                break;
            case 14: // H_AVR
                EnviarDatosLista("HV");
                break;
            case 15: // Laser
                EnviarDatosLista("LS");
                break;
            case 16: // Leds
                EnviarDatosLista("LD");
                break;
            case 17: // Medidores
                EnviarDatosLista("ME");
                break;
            case 18: // Modulos
                EnviarDatosLista("MO");
                break;
            case 19: // Motores
                EnviarDatosLista("MT");
                break;
            case 20: // Placas
                EnviarDatosLista("PL");
                break;
            case 21: // Potenciometros
                EnviarDatosLista("PM");
                break;
            case 22: // Programadores
                EnviarDatosLista("PR");
                break;
            case 23: // Pinzas
                EnviarDatosLista("PS");
                break;
            case 24: // Radiofrecuencia
                EnviarDatosLista("RF");
                break;
            case 25: // Radiofrecuencia
                EnviarDatosLista("RM");
                break;
            case 26: // Resistencias
                EnviarDatosLista("RS");
                break;
            case 27: // Relevadores
                EnviarDatosLista("RY");
                break;
            case 28: // CNC Router
                EnviarDatosLista("CN");
                break;
            case 29: // Celda Solar
                EnviarDatosLista("SL");
                break;
            case 30: // Sistema minimo
                EnviarDatosLista("SM");
                break;
            case 31: // Sensores
                EnviarDatosLista("SR");
                break;
            case 32: // Servomotor
                EnviarDatosLista("SV");
                break;
            case 33: // Switch
                EnviarDatosLista("SW");
                break;
            case 34: // Terminal Block
                EnviarDatosLista("TB");
                break;
            case 35: // Transformadores
                EnviarDatosLista("TM");
                break;
            case 36: // Potenciometro lineal
                EnviarDatosLista("TR");
                break;
            case 37: // Trimpos
                EnviarDatosLista("TV");
                break;
        }
    }

    private void EnviarDatosLista(String buscar) {
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
        posicion = position;
        String SKUbuscar = ResulstBaseSKU.get(position).getSKU();
        String Precio = ResulstBaseSKU.get(position).getPrecio();
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            Precio = ResulstBaseSKU.get(position).getPrecio();
        } else
            Precio = ResulstBaseSKU.get(position).getPrecioPublico();

        baseImages = realm.where(BaseImagenes.class).equalTo("SKU", SKUbuscar).findFirst();
        Intent intento = new Intent(MaterialSKU.this, DialogImage.class);
        try {
            intento.putExtra("LINK", baseImages.getLink());
        } catch (Exception e) {

        }
        intento.putExtra("POSICION", position);
        intento.putExtra("PRECIO", Precio);
        intento.putExtra("SKU", SKUEnviar);
        startActivity(intento);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);
    }

    // Base de datos
    private void agregarComponente(String sku, String descripcion, String precioTienda, String precioPublico, String cantidad) {

        if (usuario == 0) {
            realm.beginTransaction();
            BasePedidos tableBase = new BasePedidos(sku, descripcion, precioPublico, cantidad);
            realm.copyToRealm(tableBase);
            baseCotizaciones.getBasePedidos().add(tableBase);
            realm.commitTransaction();
            Toast.makeText(this, "El artículo agregado.", Toast.LENGTH_SHORT).show();
        } else {
            realm.beginTransaction();
            BasePedidos tableBase = new BasePedidos(sku, descripcion, precioTienda, cantidad);
            realm.copyToRealm(tableBase);
            baseCotizaciones.getBasePedidos().add(tableBase);
            realm.commitTransaction();
            Toast.makeText(this, "El artículo agregado.", Toast.LENGTH_SHORT).show();
        }
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
                //overridePendingTransition(R.anim.left_in,R.anim.left_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }
}

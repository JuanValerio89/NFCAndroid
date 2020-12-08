package com.ventas.havr.havrventas;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventas.havr.havrventas.Adaptadores.AdaptadorSKU;
import com.ventas.havr.havrventas.Adaptadores.AdapterMasvendido;
import com.ventas.havr.havrventas.Adaptadores.MyAdapter;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BaseMasVendidos;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.ventas.havr.havrventas.MaterialSKU.KICHINK;
import static com.ventas.havr.havrventas.MaterialSKU.MERCADO;
import static com.ventas.havr.havrventas.MaterialSKU.PDF;

public class ActividadMasVendido extends AppCompatActivity implements RealmChangeListener<BaseMasVendidos>, AdapterView.OnItemClickListener{
    private static final String TAG = "Mas Vendido Act";

    // Reciclador
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapatador;
    private RecyclerView.LayoutManager mLayoutManager;
    private int posicion = 0;
    // BASE DE DATOS
    public Realm realm;
    private RealmResults<BaseSKU> ResulstBaseSKU;
    private RealmList<BaseMasVendidos> ListBaseMasVendido;
    private RealmResults<BaseMasVendidos> ResultsBaseMasVendido;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private List<BaseSKU> ListBaseSKU;
    private BaseSKU baseSKU;
    private BaseCotizaciones baseCotizaciones;
    private RealmList<BasePedidos> basePedidos;
    private BasePedidos basePed;
    private BaseCotizaciones baseCot;
    private int NumCotizacion;
    public int NumPiezasAgregar = 0;
    private BaseImagenes baseImages;

    // Usuario tipo
    private String TipoUsuario = "";
    private int usuario = 0;

    // FIREBASE
    private DatabaseReference DBmasVendido;
    private ValueEventListener eventListener;
    private DatabaseReference ActualizaPrecio;
    private DatabaseReference dbImagenes;

    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private ListView ListaBusqueda;
    private AdapterMasvendido adaptador;

    private String StMasVendidos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_mas_vendido);

        ListaBusqueda = findViewById(R.id.list_mas_vendido);

        // REALM
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NumCotizacion = prefs.getInt("Cotizacion", 0);
        Log.d(TAG, "Numero cot:" + NumCotizacion);

        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            usuario = 1;   // Distribuidor
        } else {
            usuario = 0;   // Tienda
        }
        DBmasVendido = FirebaseDatabase.getInstance().getReference().child("MasVendido");
        realm = Realm.getDefaultInstance();
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Se registro un cambio. Se necesita actualizar.");
                StMasVendidos = dataSnapshot.getValue().toString();
                Log.e(TAG, "Los mas vendidos son:"+ StMasVendidos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        try {
            DBmasVendido.addValueEventListener(eventListener);
        }catch (Exception e){

        }
        Resources res = getResources();

        setTitle("Promociones");
        // Enviar base con articulos mas vendidos.
        ResultsBaseMasVendido = realm.where(BaseMasVendidos.class).findAll();
        // Enviar base con SKU
        ResulstBaseSKU = realm.where(BaseSKU.class).findAll();
        // >Enviar base con imagenes
        ResulstBaseImagenes = realm.where(BaseImagenes.class).findAll();
        // Enviar bases al adaptador
        crearLista(ResulstBaseSKU, ResulstBaseImagenes,ResultsBaseMasVendido );
    }

    public void crearLista(RealmResults<BaseSKU> resulstBaseSKU,
                           RealmResults<BaseImagenes> resultsBaseImagenes,
                           RealmResults<BaseMasVendidos> resulstBasemasVendido){
        adaptador = new AdapterMasvendido(this, resulstBaseSKU,resultsBaseImagenes,resulstBasemasVendido, R.layout.list_cot_item,usuario);
        ListaBusqueda.setAdapter(adaptador);
        ListaBusqueda.setOnItemClickListener(this);
        registerForContextMenu(ListaBusqueda);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onChange(BaseMasVendidos baseMasVendidos) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("¿Qué desea hacer?");
        getMenuInflater().inflate(R.menu.context_menu_cotizacion, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            posicion = info.position;
        }catch (Exception e){

        }

        switch (item.getItemId()) {
            case R.id.action_agregar:
                Log.d(TAG, "Agregar carrito:" + posicion);
                agregarCotizacion(ResultsBaseMasVendido.get(posicion));
                return true;
            case R.id.action_kichink:
                Log.d(TAG, "Kichink");
                IrAlLink(ResulstBaseSKU.get(posicion) , KICHINK);
                return true;
            case R.id.action_mercadolibre:
                Log.d(TAG, "MercadoLibre");
                IrAlLink(ResulstBaseSKU.get(posicion) , MERCADO);
                return true;
            case R.id.action_pdf:
                Log.d(TAG, "PDF");
                IrAlLink(ResulstBaseSKU.get(posicion) , PDF);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void IrAlLink(BaseSKU baseSKU, int tipo) {
        String SKUbuscar = baseSKU.getSKU();
        Log.d(TAG, "SKU a buscar para link: " + SKUbuscar);
        baseImages = realm.where(BaseImagenes.class)
                .beginsWith("SKU", SKUbuscar).findFirst();
        switch (tipo){
            case KICHINK:
                if(baseImages.getLinkKK().compareTo("NO") != 0) {
                    Log.d(TAG, "LINK: " + baseImages.getLinkKK());
                    Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(baseImages.getLinkKK()));
                    startActivity(browse);
                }else{
                    Toast.makeText(this,"El link aún no se ha dado de alta.",Toast.LENGTH_LONG).show();
                }
                break;
            case MERCADO:
                if(baseImages.getLinkML().compareTo("NO") != 0) {
                    Log.d(TAG, "LINK: " + baseImages.getLinkML());
                    Intent browsea = new Intent(Intent.ACTION_VIEW, Uri.parse(baseImages.getLinkML()));
                    startActivity(browsea);
                }else{
                    Toast.makeText(this,"El link aún no se ha dado de alta.",Toast.LENGTH_LONG).show();
                }
                break;
            case PDF:
                if(baseImages.getLinkPDF().compareTo("NO") != 0) {
                    Log.d(TAG, "LINK: " + baseImages.getLinkPDF());
                    Intent browseb = new Intent(Intent.ACTION_VIEW, Uri.parse(baseImages.getLinkPDF()));
                    startActivity(browseb);
                }else{
                    Toast.makeText(this,"El link aún no se ha dado de alta.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void agregarCotizacion(BaseMasVendidos baseSKU) {
        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id", NumCotizacion).findFirst();
        basePedidos = baseCotizaciones.getBasePedidos();
        int a = basePedidos.size();
        boolean agregar = true;
        for (int x = 0; x < a; x++) {
            String b = basePedidos.get(x).getSKU();
            String c = baseSKU.getSKU();
            if(b.equals(c)){
                agregar = false;
                break;
            }else{
                agregar = true;
            }
        }
        if(agregar) {
            showAlertForCreatingCot(baseSKU, "Carrito", "¿Cuantas piezas necesita?");

        }
        else
            Toast.makeText(this, "El artículo ya esta en el carrito.", Toast.LENGTH_SHORT).show();
    }

    private void showAlertForCreatingCot(BaseMasVendidos baseSKUA, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_componente, null);
        builder.setView(viewInflate);

        EditText input = (EditText) viewInflate.findViewById(R.id.editText_dialog_componentes);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String NumPiezas = input.getText().toString().trim();
                if (NumPiezas.length() > 0) {

                    Log.d(TAG, "Listo para guardar. ");

                    BaseSKU baseSKU = realm.where(BaseSKU.class).beginsWith("SKU", baseSKUA.getSKU()).findFirst();
                    try {
                        String piezasDisponiblesST = baseSKU.getCantidad();
                        int piezasDisponible = Integer.parseInt(piezasDisponiblesST);
                        NumPiezasAgregar = Integer.parseInt(NumPiezas);
                        if (NumPiezasAgregar > piezasDisponible) {
                            NumPiezasAgregar = piezasDisponible;
                            Toast.makeText(ActividadMasVendido.this, "Se agregaron solo las piezas disponibles.", Toast.LENGTH_SHORT).show();
                        }
                        agregarComponente(baseSKU.getSKU(), baseSKU.getDescripcion(), baseSKU.getPrecio(),  baseSKU.getPrecioPublico(),NumPiezasAgregar + "");
                    } catch (Exception e) {
                        Toast.makeText(ActividadMasVendido.this, "No se pudo agregar el componente.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No se ha escrito un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Base de datos
    private void agregarComponente(String sku, String descripcion, String precioTienda,String precioPublico, String cantidad) {
        if(usuario == 0) {
            realm.beginTransaction();
            BasePedidos tableBase = new BasePedidos(sku, descripcion, precioPublico, cantidad);
            realm.copyToRealm(tableBase);
            baseCotizaciones.getBasePedidos().add(tableBase);
            realm.commitTransaction();
            Toast.makeText(this, "El artículo agregado.", Toast.LENGTH_SHORT).show();
        }else{
            realm.beginTransaction();
            BasePedidos tableBase = new BasePedidos(sku, descripcion, precioTienda, cantidad);
            realm.copyToRealm(tableBase);
            baseCotizaciones.getBasePedidos().add(tableBase);
            realm.commitTransaction();
            Toast.makeText(this, "El artículo agregado.", Toast.LENGTH_SHORT).show();
        }
    }
}

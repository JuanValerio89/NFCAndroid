package com.ventas.havr.havrventas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ventas.havr.havrventas.Adaptadores.AdaptadorSKU;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;

import org.w3c.dom.Text;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.ventas.havr.havrventas.MaterialSKU.KICHINK;
import static com.ventas.havr.havrventas.MaterialSKU.MERCADO;
import static com.ventas.havr.havrventas.MaterialSKU.PDF;

public class ActividadBusqueda extends AppCompatActivity implements RealmChangeListener<BaseSKU>, AdapterView.OnItemClickListener{

    private static final String TAG = "Busqueda";

    // BASE DE DATOS
    public Realm realm;
    private RealmResults<BaseSKU> ResulstBaseSKU;
    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private BaseCotizaciones baseCotizaciones;
    private List<BaseSKU> ListBaseSKU;
    private BaseSKU baseSKU;
    private BaseImagenes baseImages;

    private TextView TxBusqueda;

    // Adaptador
    private AdaptadorSKU adaptador;
    private ListView ListaBusqueda;
    private TextInputEditText EditBuscar;
    private int posicion = 0;

    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private int Tipo = 0;
    private int NumPiezasAgregar = 0;
    private int NumCotizacion;
    private int usuario = 0;
    private int QueBuscar  = 0;
    private Boolean DatoSwitch = false;
    private String TipoUsuario = "";
    private String SKUEnviar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_busqueda);

        Switch sw = (Switch) findViewById(R.id.switch_sku);
        TxBusqueda = findViewById(R.id.tx_piezas_encontradas);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DatoSwitch = true;
                } else {
                    DatoSwitch = false;
                }
            }
        });

        ListaBusqueda = findViewById(R.id.list_buscador);
        EditBuscar = findViewById(R.id.edit_buscar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            usuario = 1;   // Distribuidor
        } else {
            usuario = 0;   // Tienda
        }
        NumCotizacion = prefs.getInt("Cotizacion", 0);
        String TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO,ActividadPrincipal.PUBLICO);
        Tipo= 0; // Publico
        if(TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)){
            Tipo = 1;  // Distribuidor
        }

        realm = Realm.getDefaultInstance();
        ResulstBaseSKU = realm.where(BaseSKU.class)
                .contains("Descripcion","arduino", Case.INSENSITIVE)
                .findAll();
        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id",NumCotizacion).findFirst();
        //Log.d(TAG,"Revision 74ls86:"+baseSKU.getSKU()+baseSKU.getPrecio());
        for(int y = 0; y < ResulstBaseSKU.size();y ++){
            Log.d(TAG,"Revision arduino:"+ResulstBaseSKU.get(y).getSKU());
        }

        EditBuscar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String ClaveBuscar = EditBuscar.getText().toString();
                SKUEnviar = ClaveBuscar;
                if(DatoSwitch) {
                    QueBuscar = 1;
                    ResulstBaseSKU = realm.where(BaseSKU.class)
                            .contains("SKU", ClaveBuscar, Case.INSENSITIVE)
                            .findAll().sort("SKU");
                }else{
                    QueBuscar = 2;
                    ResulstBaseSKU = realm.where(BaseSKU.class)
                            .contains("Descripcion", ClaveBuscar, Case.INSENSITIVE)
                            .findAll().sort("SKU");;
                }
                ResulstBaseImagenes = realm.where(BaseImagenes.class).findAll();
                crearLista(ResulstBaseSKU, ResulstBaseImagenes);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    public void crearLista(RealmResults<BaseSKU> resulstBaseSKU,
                           RealmResults<BaseImagenes> resultsBaseImagenes){
        if(resulstBaseSKU.size() == 1) {
            TxBusqueda.setText("Se encontraron: " + resulstBaseSKU.size() + " artículo.");
        }else{
            TxBusqueda.setText("Se encontraron: " + resulstBaseSKU.size() + " artículos.");
        }
        adaptador = new AdaptadorSKU(this, resulstBaseSKU,resultsBaseImagenes, R.layout.list_cot_item,Tipo);
        ListaBusqueda.setAdapter(adaptador);
        ListaBusqueda.setOnItemClickListener(this);
        registerForContextMenu(ListaBusqueda);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        posicion = position;
        String SKUbuscar = ResulstBaseSKU.get(position).getSKU();
        Log.d(TAG,"SKU a buscar:"+SKUbuscar);
        Log.d(TAG,"Posicion a buscar:"+position);
        baseImages = realm.where(BaseImagenes.class).equalTo("SKU", SKUbuscar).findFirst();
        Intent intento = new Intent(ActividadBusqueda.this,DialogImage.class);
        ResulstBaseSKU.get(posicion);
        try {
            intento.putExtra("LINK", baseImages.getLink());
        }catch (Exception e){

        }
            intento.putExtra("POSICION", position);
            intento.putExtra("SKU", SKUEnviar);
            intento.putExtra("BUSCAR", QueBuscar);
        startActivity(intento);
    }

    @Override
    public void onChange(BaseSKU baseSKU) {

    }

    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("¿Qué desea hacer?");
        getMenuInflater().inflate(R.menu.context_menu_cotizacion, menu);
    }

*7
    /*
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Log.d(TAG, "Context item:" + info.position);
            posicion = info.position;
        }catch (Exception e){

        }
        switch (item.getItemId()) {
            case R.id.action_agregar:
                Log.d(TAG, "Agregar carrito:" + posicion);
                agregarCotizacion(ResulstBaseSKU.get(posicion));
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



    private void agregarCotizacion(BaseSKU baseSKU) {

        showAlertForCreatingCot(baseSKU, "Carrito","¿Cuantas piezas necesita?");
    }

    private void showAlertForCreatingCot(BaseSKU baseSKU, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_componente, null);
        builder.setView(viewInflate);

        EditText input = (EditText) viewInflate.findViewById(R.id.editText_dialog_componentes);
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String NumPiezas = input.getText().toString().trim();
                if (NumPiezas.length() > 0) {
                    try {
                        String piezasDisponiblesST = baseSKU.getCantidad();
                        int piezasDisponible = Integer.parseInt(piezasDisponiblesST);
                        NumPiezasAgregar = Integer.parseInt(NumPiezas);
                        if(NumPiezasAgregar > piezasDisponible){
                            NumPiezasAgregar = piezasDisponible;
                            Toast.makeText(ActividadBusqueda.this, "Se agregaron solo las piezas disponibles.", Toast.LENGTH_SHORT).show();
                        }
                        agregarComponente(baseSKU.getSKU(), baseSKU.getDescripcion(),baseSKU.getPrecio(),baseSKU.getPrecioPublico(),NumPiezasAgregar + "");
                    }catch (Exception e){
                        Toast.makeText(ActividadBusqueda.this, "No se pudo agregar el componente.", Toast.LENGTH_SHORT).show();
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
    private void agregarComponente(String sku, String descripcion, String precioTienda, String precioPublico, String cantidad){
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
    */
}

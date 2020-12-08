package com.ventas.havr.havrventas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;
import com.ventas.havr.havrventas.Modelos.BaseUsuarios;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.ventas.havr.havrventas.MaterialSKU.KICHINK;
import static com.ventas.havr.havrventas.MaterialSKU.MERCADO;
import static com.ventas.havr.havrventas.MaterialSKU.PDF;

public class DialogImage extends AppCompatActivity {

    final private static String TAG = "Dialog imagen";

    private String Link;
    private String SkuBuscar;
    private String TipoUsuario = "";
    private String Precio = "";

    private int Posicion;
    private int NumCotizacion;
    private int NumPiezasAgregar = 0;
    private int usuario = 0;
    private int Buscar = 0;
    private int MOQ = 1;
    private int banderaComprar = 0;

    private TextView TxSKu;
    private TextView TxCantidad;
    private TextView TxNombre;
    private TextView TxMinimo;
    private TextView TxPrecio;

    private ImageView ImagenGrande;
    private Button BtAgregar;
    private Button BtMercadoLibre;
    private Button BtKichink;
    private Button BtWhatsapp;

    private CardView cardComprar;
    //private Button BtPdf;

    // BASE DE DATOS
    public Realm realm;
    private RealmResults<BaseSKU> ResulstBaseSKU;
    private BaseCotizaciones baseCotizaciones;
    private RealmList<BasePedidos> basePedidos;
    private BaseImagenes baseImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_image);

        realm = Realm.getDefaultInstance();
        ImagenGrande = (ImageView) findViewById(R.id.imagen_dialog);
        BtAgregar = (Button) findViewById(R.id.bt_agregar_cotizacion);
        BtMercadoLibre = (Button) findViewById(R.id.bt_mercadolibre);
        BtKichink = (Button) findViewById(R.id.bt_comprar_kichink);
        BtWhatsapp = findViewById(R.id.bt_whatsapp);
        cardComprar = findViewById(R.id.cardView_comprar);

        TxSKu = (TextView) findViewById(R.id.text_sku_producto);
        TxCantidad = (TextView) findViewById(R.id.text_piezas_disponibles);
        TxNombre = (TextView) findViewById(R.id.text_nombre_producto);
        TxMinimo = (TextView) findViewById(R.id.text_minimo);
        TxPrecio = (TextView) findViewById(R.id.text_precio);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NumCotizacion = prefs.getInt("Cotizacion", 0);
        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            usuario = 1;   // Distribuidor
        } else {
            usuario = 0;   // Tienda
        }

        Intent intent = getIntent();
        Link = intent.getStringExtra("LINK");
        Posicion = intent.getIntExtra("POSICION", 0);
        SkuBuscar = intent.getStringExtra("SKU");
        Buscar = intent.getIntExtra("BUSCAR", 0);
        Precio = intent.getStringExtra("PRECIO");

        try{
            Log.d("Dialog Fragment", "Colocando imagen" + Link);
            Glide.with(getApplicationContext()).load(Link).into(ImagenGrande);
        }catch (Exception e){
            Log.d("Dialog Fragment", "El link es:" + Link);
            Glide.with(getApplicationContext()).load("https://imagizer.imageshack.com/v2/640x480q90/923/tKIHkA.png").into(ImagenGrande);
        }
        if (Buscar == 0) {
            ResulstBaseSKU = realm.where(BaseSKU.class)
                    .beginsWith("SKU", SkuBuscar).findAll().sort("SKU");
            baseImages = realm.where(BaseImagenes.class).equalTo("SKU", SkuBuscar).findFirst();
            TxNombre.setText(ResulstBaseSKU.get(Posicion).getDescripcion());
            TxSKu.setText("" + ResulstBaseSKU.get(Posicion).getSKU());
            if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
                TxPrecio.setText(ResulstBaseSKU.get(Posicion).getPrecio());
            } else
                TxPrecio.setText(ResulstBaseSKU.get(Posicion).getPrecioPublico());
            TxCantidad.setText("Piezas disponibles: " + ResulstBaseSKU.get(Posicion).getCantidad());

        } else if (Buscar == 1) {
            ResulstBaseSKU = realm.where(BaseSKU.class)
                    .contains("SKU", SkuBuscar, Case.INSENSITIVE)
                    .findAll().sort("SKU");
            baseImages = realm.where(BaseImagenes.class).equalTo("SKU", SkuBuscar).findFirst();
            TxNombre.setText(ResulstBaseSKU.get(Posicion).getDescripcion());
            TxSKu.setText("SKU: " + ResulstBaseSKU.get(Posicion).getSKU());
            if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
                TxPrecio.setText(ResulstBaseSKU.get(Posicion).getPrecio());
            } else
                TxPrecio.setText(ResulstBaseSKU.get(Posicion).getPrecioPublico());
            TxCantidad.setText("Piezas disponibles: " + ResulstBaseSKU.get(Posicion).getCantidad());
            Log.d("Dialog Fragment", "El link es:" + Link);
            Glide.with(getApplicationContext()).load(Link).into(ImagenGrande);
        } else {
            ResulstBaseSKU = realm.where(BaseSKU.class)
                    .contains("Descripcion", SkuBuscar, Case.INSENSITIVE)
                    .findAll().sort("SKU");
            ;
            baseImages = realm.where(BaseImagenes.class).equalTo("SKU", SkuBuscar).findFirst();
            TxNombre.setText(ResulstBaseSKU.get(Posicion).getDescripcion());
            TxSKu.setText("SKU: " + ResulstBaseSKU.get(Posicion).getSKU());
            if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
                TxPrecio.setText(ResulstBaseSKU.get(Posicion).getPrecio());
            } else
                TxPrecio.setText(ResulstBaseSKU.get(Posicion).getPrecioPublico());
            TxCantidad.setText("Piezas disponibles: " + ResulstBaseSKU.get(Posicion).getCantidad());
            Log.d("Dialog Fragment", "El link es:" + Link);
            Glide.with(getApplicationContext()).load(Link).into(ImagenGrande);
        }

        BtAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCotizacion(ResulstBaseSKU.get(Posicion));
            }
        });

        BtMercadoLibre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IrAlLink(ResulstBaseSKU.get(Posicion), MERCADO);
            }
        });

        BtKichink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IrAlLink(ResulstBaseSKU.get(Posicion), KICHINK);
            }
        });

        BtWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarProducto(ResulstBaseSKU.get(Posicion).getDescripcion());
            }
        });
        // Recuperar numero de piezas
        NumeroPiezas();
        ComprobarBotones();

    }

    private void EnviarProducto(String descripcion) {
        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        String toNumber = "+525564254686";
        try {
            String url = "https://api.whatsapp.com/send?phone=" + toNumber + "&text=" + URLEncoder.encode("Hola, me gustaria comprar: " +
                    descripcion + ", me puede dar informes por favor", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void NumeroPiezas() {

        try {
            baseImages = realm.where(BaseImagenes.class)
                    .beginsWith("SKU", ResulstBaseSKU.get(Posicion).getSKU()).findFirst();
            if (baseImages.getLinkML().compareTo("NO") != 0) {
                String linkML = baseImages.getLinkML();
                Log.d(TAG, "LINK: " + linkML);
                int busquedaPiezas = 0;
                busquedaPiezas = linkML.indexOf("piezas");
                Log.d(TAG, "INDEX PIEZAS:" + busquedaPiezas);
                if (busquedaPiezas != -1) {
                    String newPiezas = linkML.substring(busquedaPiezas - 5, busquedaPiezas + 5);
                    Log.d(TAG, "AQUI esta el numero de pieza:" + newPiezas);
                    String[] dividir = newPiezas.split("-");
                    Log.d(TAG, "Tamaño dividir:" + dividir.length);
                    Log.d(TAG, "Tamaño dividir 0:" + dividir[0]);
                    Log.d(TAG, "Tamaño dividir 1:" + dividir[1]);
                    MOQ = Integer.parseInt(dividir[1]);
                    TxMinimo.setText("Pedido mínimo:" + MOQ + " piezas");
                } else {
                    TxMinimo.setText("Pedido mínimo:" + MOQ + " pieza");
                }
            } else {

            }
        }catch (Exception e){
            Toast.makeText(this, "Se debe actualizar la base de datos, existe un error",Toast.LENGTH_LONG);
        }
    }

    private void agregarCotizacion(BaseSKU baseSKU) {
        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id", NumCotizacion).findFirst();
        basePedidos = baseCotizaciones.getBasePedidos();
        int a = basePedidos.size();
        boolean agregar = true;
        for (int x = 0; x < a; x++) {
            String b = basePedidos.get(x).getSKU();
            String c = baseSKU.getSKU();
            if (b.equals(c)) {
                agregar = false;
                break;
            } else {
                agregar = true;
            }
        }
        if (agregar) {
            showAlertForCreatingCot(baseSKU, "Carrito", "¿Cuantas piezas necesita?");

        } else
            Toast.makeText(this, "El artículo ya esta en el carrito.", Toast.LENGTH_SHORT).show();
    }

    private void showAlertForCreatingCot(BaseSKU baseSKU, String title, String message) {
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
                    try {
                        String piezasDisponiblesST = baseSKU.getCantidad();
                        int piezasDisponible = Integer.parseInt(piezasDisponiblesST);
                        NumPiezasAgregar = Integer.parseInt(NumPiezas);
                        if (NumPiezasAgregar > piezasDisponible) {
                            NumPiezasAgregar = piezasDisponible;
                            Toast.makeText(DialogImage.this, "Se agregaron solo las piezas disponibles.", Toast.LENGTH_SHORT).show();
                        }
                        agregarComponente(baseSKU.getSKU(), baseSKU.getDescripcion(), baseSKU.getPrecio(), baseSKU.getPrecioPublico(), NumPiezasAgregar + "");
                    } catch (Exception e) {
                        Toast.makeText(DialogImage.this, "No se pudo agregar el componente.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No se ha dado una cantidad", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "No se agrego material", Toast.LENGTH_SHORT).show();
                //finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    // Base de datos
    private void agregarComponente(String sku, String descripcion, String precioTienda, String precioPublico, String cantidad) {

        if (usuario == 0) {
            realm.beginTransaction();
            BasePedidos tableBase = new BasePedidos(sku, descripcion, precioPublico, cantidad);
            realm.copyToRealm(tableBase);
            baseCotizaciones.getBasePedidos().add(tableBase);
            realm.commitTransaction();
            Toast.makeText(this, "El artículo se agrego correctamente.", Toast.LENGTH_SHORT).show();
        } else {
            realm.beginTransaction();
            BasePedidos tableBase = new BasePedidos(sku, descripcion, precioTienda, cantidad);
            realm.copyToRealm(tableBase);
            baseCotizaciones.getBasePedidos().add(tableBase);
            realm.commitTransaction();
            Toast.makeText(this, "El artículo se agrego correctamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private void ComprobarBotones() {
        baseImages = realm.where(BaseImagenes.class)
                .beginsWith("SKU", ResulstBaseSKU.get(Posicion).getSKU()).findFirst();
        try {
            if (baseImages.getLinkKK().compareTo("NO") != 0) {
                Log.d(TAG, "LINK: " + baseImages.getLinkKK());
                BtKichink.setVisibility(View.VISIBLE);
                banderaComprar = 0;
            } else {
                BtKichink.setVisibility(View.GONE);
                banderaComprar = banderaComprar + 1;
            }
            if (baseImages.getLinkML().compareTo("NO") != 0) {
                Log.d(TAG, "LINK: " + baseImages.getLinkML());
                BtMercadoLibre.setVisibility(View.VISIBLE);
                banderaComprar = 0;
            } else {
                BtMercadoLibre.setVisibility(View.GONE);
                banderaComprar = banderaComprar + 1;
            }
            if (baseImages.getLinkPDF().compareTo("NO") != 0) {
                Log.d(TAG, "LINK: " + baseImages.getLinkPDF());
                //BtPdf.setVisibility(View.VISIBLE);
            } else {
                //BtPdf.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            runOnUiThread(new Runnable(){
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_base,Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void IrAlLink(BaseSKU baseSKU, int tipo) {
        String SKUbuscar = baseSKU.getSKU();
        Log.d(TAG, "SKU a buscar para link: " + SKUbuscar);
        baseImages = realm.where(BaseImagenes.class)
                .beginsWith("SKU", SKUbuscar).findFirst();
        try {
            switch (tipo) {
                case KICHINK:
                    if (baseImages.getLinkKK().compareTo("NO") != 0) {
                        Log.d(TAG, "LINK: " + baseImages.getLinkKK());
                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(baseImages.getLinkKK()));
                        startActivity(browse);
                    } else {
                        Toast.makeText(this, "El link aún no se ha dado de alta.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case MERCADO:
                    if (baseImages.getLinkML().compareTo("NO") != 0) {
                        Log.d(TAG, "LINK: " + baseImages.getLinkML());
                        Intent browsea = new Intent(Intent.ACTION_VIEW, Uri.parse(baseImages.getLinkML()));
                        startActivity(browsea);
                    } else {
                        Toast.makeText(this, "El link aún no se ha dado de alta.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case PDF:
                    if (baseImages.getLinkPDF().compareTo("NO") != 0) {
                        Log.d(TAG, "LINK: " + baseImages.getLinkPDF());
                        Intent browseb = new Intent(Intent.ACTION_VIEW, Uri.parse(baseImages.getLinkPDF()));
                        startActivity(browseb);
                    } else {
                        Toast.makeText(this, "El link aún no se ha dado de alta.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }catch (Exception e){
            Log.d(TAG,"No se detectaron los links");
            runOnUiThread(new Runnable(){
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_base,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carrito_pdf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.pdf_boton:
                if (baseImages.getLinkPDF().compareTo("NO") != 0) {
                    Log.d(TAG, "LINK: " + baseImages.getLinkPDF());
                    Intent browseb = new Intent(Intent.ACTION_VIEW, Uri.parse(baseImages.getLinkPDF()));
                    startActivity(browseb);
                } else {
                    Toast.makeText(this, "El link aún no se ha dado de alta.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.carrito:
                Log.d(TAG, "Ir al carrito");
                Intent intento = new Intent(DialogImage.this, ActividadPedidos.class);
                intento.putExtra("id", NumCotizacion);
                startActivity(intento);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }
}
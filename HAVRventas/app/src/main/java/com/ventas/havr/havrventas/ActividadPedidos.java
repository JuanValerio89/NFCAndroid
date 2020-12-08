package com.ventas.havr.havrventas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.ventas.havr.havrventas.Adaptadores.AdapterCotHolder;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseUsuarios;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ActividadPedidos extends AppCompatActivity implements RealmChangeListener<BaseCotizaciones>,
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener, View.OnTouchListener, View.OnFocusChangeListener {

    private static final String TAG = "Actividad Pedidos";
    String[] monthName = {"Enero", "Febrero",
            "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Septiembre", "Octubre", "Noviembre",
            "Diciembre"};

    private TemplatePDF templatePDF;
    private String[] header = {"#", "Imagen", "Descripción", "SKU", "Cantidad", "Precio Unitario", "Importe"};
    private String PrecioFinalA;
    private String PrecioFinal;
    private String Fecha;
    private String Precio;
    private String YearS;
    private String PDFNombre;
    private String PDFtelefono;
    private String PDFcorreo;
    private String Usuario;
    public Realm realm;
    private ListView listView;
    private AdapterCotHolder adapter;
    private FloatingActionButton fab;
    private int baseCotID;
    private int LogCompleto = 0;
    private int DayS;
    private int MonthS;
    private int Year;
    private TextView TxTotal;

    // Dialog
    ProgressDialog progressDialog;
    private MiTareaAsincronaDialog tarea2;
    private int TipoDialog = 0;
    private ProgressDialog vDialog;

    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private BaseCotizaciones baseCotizaciones;
    private RealmList<BasePedidos> basePedidos;
    private BaseUsuarios baseUsuarios;

    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private float TotalCotizacion = 0;
    private ArrayList<String[]> Lista;
    private Boolean CreacionTablaCompleta = true;

    private RealmResults<BaseCotizaciones> basecot;

    private ArrayList<String[]> clientes;
    private String nombreProyecto = "";
    private int tamañoDb;
    private boolean consultaUsuario = false;
    private FirebaseAuth mAuth;// ...

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_pedidos);

        realm = Realm.getDefaultInstance();
        basecot = realm.where(BaseCotizaciones.class).findAll();

        TxTotal = findViewById(R.id.text_total);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (getIntent().getExtras() != null) {
            baseCotID = getIntent().getExtras().getInt("id");
            Log.d(TAG, "BASE COT ID:" + baseCotID);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        firebaseAuth = FirebaseAuth.getInstance();
        consultaUsuario = ConsultarUsuario();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                    Log.d(TAG, "Log completo.");
                    String NombreTienda = prefs.getString("Nombre", "Error");
                    String correo = prefs.getString("Correo", "Error");
                    Log.d(TAG, "USUARIO:" + NombreTienda + ",CORREO:" + correo);
                } else {
                    if (!consultaUsuario)
                        Toast.makeText(getApplicationContext(), "Necesita ingresar a su cuenta", Toast.LENGTH_LONG).show();
                }
            }
        };

        baseCotizaciones = realm.where(BaseCotizaciones.class).equalTo("id", baseCotID).findFirst();
        baseCotizaciones.addChangeListener(this);
        basePedidos = baseCotizaciones.getBasePedidos();
        this.setTitle("Pedidos");
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        Fecha = day + "/" + month + "/" + year;
        YearS = year + "";
        DayS = day;
        MonthS = month - 1;
        Year = year - 2000;
        Lista = getClients();


        adapter = new AdapterCotHolder(this, basePedidos, R.layout.list_item_cot_a,
                baseCotID, "0", 0);
        listView = (ListView) findViewById(R.id.lista_pedidos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setOnTouchListener(this);

        clientes = getClients();
        getTotal();
        PrecioFinal = baseCotizaciones.getTotalPrecio();
        nombreProyecto = baseCotizaciones.getCotizacion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pedidos, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        clientes = getClients();
        switch (item.getItemId()) {
            case R.id.action_pdf:
                if (!clientes.isEmpty()) {
                    if (consultaUsuario) {
                        vDialog = new ProgressDialog(ActividadPedidos.this);
                        vDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        vDialog.setMessage("Generando archivo. Puede demorar un poco..");
                        vDialog.setCancelable(false);
                        vDialog.setMax(100);
                        vDialog.show();
                        TipoDialog = 1;
                        tarea2 = new MiTareaAsincronaDialog();
                        tarea2.execute();
                    } else {
                        if (LogCompleto == 0) {
                            Toast.makeText(this, "Necesita ingresar una cuenta", Toast.LENGTH_LONG).show();
                            Intent intentb = new Intent(ActividadPedidos.this, ActividadPublicoLogIn.class);
                            startActivity(intentb);
                        } else {
                            vDialog = new ProgressDialog(ActividadPedidos.this);
                            vDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            vDialog.setMessage("Generando archivo. Puede demorar un poco..");
                            vDialog.setCancelable(false);
                            vDialog.setMax(100);
                            vDialog.show();
                            TipoDialog = 1;
                            tarea2 = new MiTareaAsincronaDialog();
                            tarea2.execute();
                        }
                    }
                } else {
                    Toast.makeText(this, "No hay articulos para mostrar", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setUserData(FirebaseUser user) {
        String nombre = user.getDisplayName();
        String correo = user.getEmail();
        LogCompleto = 1;
        // Pasamos al información a las preferencias.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Nombre", nombre);
        editor.putString("Correo", correo);
        editor.commit();
        editor.apply();
    }

    private boolean ConsultarUsuario() {
        String Usuario = prefs.getString(ActividadPrincipal.TIENDA, ActividadPrincipal.NOMBRE_TIENDA);
        baseUsuarios = realm.where(BaseUsuarios.class)
                .equalTo("Usuario", Usuario)
                .findFirst();
        String NombreTienda = baseUsuarios.getUsuario();
        baseUsuarios = realm.where(BaseUsuarios.class)
                .equalTo("Usuario", Usuario)
                .findFirst();
        PDFNombre = baseUsuarios.getUsuario();
        PDFtelefono = baseUsuarios.getTelefono();
        PDFcorreo = baseUsuarios.getCorreo();
        if (NombreTienda.compareTo("HAVR") == 0)
            return false;
        else
            return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void CrearPDF(String Direccion, int tipoUser) {

        if (!clientes.isEmpty()) {
            String NombreTienda = "0";
            String Telefono = "1";
            String correo = "2";
            if (tipoUser == 0) {
                NombreTienda = PDFNombre;
                Telefono = PDFtelefono;
                correo = PDFcorreo;
            } else {
                NombreTienda = prefs.getString("Nombre", "Error");
                correo = prefs.getString("Correo", "Error");
            }

            Log.d("PDF direccion", "Crear PDF, con direccion:" + Direccion);
            templatePDF = new TemplatePDF(getApplicationContext());
            templatePDF.openDocument();
            try {
                templatePDF.colocarHAVR();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String Desglosado = String.format("$ %,.2f", TotalCotizacion);
            TotalCotizacion = (float) (TotalCotizacion * 1.16);
            String TotalPesos = String.format("$ %,.2f", TotalCotizacion);
            float IVA = (float) (TotalCotizacion * 0.16);
            String TotalIVA = String.format("$ %,.2f", IVA);
            float TotalConIVA = IVA + TotalCotizacion;

            Log.e(TAG, "Precios:" + TotalCotizacion + ",IVA:" + TotalConIVA);
            templatePDF.addMetaData("H-AVR S.A. de C.V.", "Ventas", "HAVR SA de CV");
            Log.e(TAG, "Test: A");
            String[] dirA = Direccion.split("@");
            Log.e(TAG, "Test: B");
            String Cotizacion = "COT:" + nombreProyecto;
            try {
                templatePDF.colocarHAVRInformacion("CDMX, " +
                                DayS + " de " +
                                monthName[MonthS] + " de " +
                                YearS,
                        "HAV150422Q19\nXocotitlán 6227 Col.Aragón\n Inguarán. C.P.07820\n" +
                                "Gustavo A. Madero, CDMX,\n Tel. 5567968483, ventas@h-avr.com\n ",
                        Cotizacion,
                        NombreTienda,
                        Telefono,
                        correo,
                        "HAVR");
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //templatePDF.addEmpresaB();
            templatePDF.DrawLine();
            //templatePDF.addParagraph("¡Muchas gracias por su preferencia!");
            // Crea la tabla con todos los perfiles, precios y unidades.
            templatePDF.createTable(header, clientes);
            templatePDF.createTableTotal(TotalPesos, TotalIVA, Desglosado);
            templatePDF.addParagraph("Esperamos que la cotización sea de su agrado, quedamos atentos a sus comentarios " +
                    "\n\n¡Gracias por su preferencia!" + "\n\n*Precios sujetos a cambios sin previo aviso." +
                    "\n*Algunas imágenes son ilustrativas al producto." +
                    "\n*El costo de envío no esta incluido.");
            templatePDF.addSpace(70);
            templatePDF.createTableContacto();
            //templatePDF.createTableCuadroInferior();
            templatePDF.closeDocument();
            Log.d(TAG, "Finalizo PDF");

            // Guardar el precio final para colocar revisiones
            editor = prefs.edit();
            editor.putString("PrecioFinal", PrecioFinal);
            editor.commit();
        } else {
            Toast.makeText(getApplicationContext(), "No hay material por mostrar", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<String[]> getClients() {
        ArrayList<String[]> rows = new ArrayList<>();
        TotalCotizacion = 0;
        tamañoDb = basePedidos.size();
        //tamañoDb = realm.where(BaseMM.class).findAll().size();
        int y = 1;

        // Crear documentacion para archivo de PDF y tambien archivo de excel con precios de compra. Archivo interno.-
        for (int x = 0; x < tamañoDb; x++) {
            basePedidos.get(x);
            try {
                CreacionTablaCompleta = true;
                String Descripcion = basePedidos.get(x).getDescripcion();
                String Cantidad = basePedidos.get(x).getCantidad();
                String SKU = basePedidos.get(x).getSKU();
                String Precio = basePedidos.get(x).getPrecio();
                String PrecioA = Precio.replace("$", "");
                PrecioA = PrecioA.replace(",", "");
                String Imagen = Descripcion;
                Log.d(TAG, "Agregando info:" + Descripcion);
                float CanFloat = Float.parseFloat(Cantidad);
                float PreFloat = Float.parseFloat(PrecioA);
                PreFloat = (float) (PreFloat / 1.16);
                float TotalArticulo = CanFloat * PreFloat;
                TotalCotizacion = TotalArticulo + TotalCotizacion;
                String PesosFormatoTotal = String.format("$ %,.2f", TotalArticulo);
                float piezasTotales = Float.parseFloat(basePedidos.get(x).getCantidad());

                float PrecioUnitario = PreFloat;
                String PesosUnitario = String.format("$ %,.2f", PrecioUnitario);

                //private String[] header = {"Partida","Descripción","Calidad","Piezas","Unidad","Precio Unitario","Total"};
                rows.add(new String[]{"" + y, // Partida #
                        Imagen,
                        Descripcion, // Descripcion
                        SKU,
                        Cantidad, // Unidad
                        PesosUnitario,  // Precio unitario
                        PesosFormatoTotal});   // Importe
                y = y + 1;
            } catch (Exception e) {
                CreacionTablaCompleta = false;
                Toast.makeText(this, "Debes revisar que todos los items esten bien antes de continuar.", Toast.LENGTH_LONG).show();
            }
        }
        return rows;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getTotal();
        Log.d(TAG,"inItemClick");
    }

    @Override
    public void onChange(BaseCotizaciones baseCotizaciones) {
        adapter.notifyDataSetChanged();
        getTotal();
        Log.d(TAG,"onChange Base");

    }

    private void getTotal() {
        clientes = getClients();
        String PesosFormatoTotal = String.format("$ %,.2f", TotalCotizacion);
        float IVA = (float) (TotalCotizacion * 0.16);
        String TotalIVA = String.format("$ %,.2f", IVA);
        float TotalConIVA = IVA + TotalCotizacion;
        String TotalconIVA = String.format("$ %,.2f", TotalConIVA);
        TxTotal.setText("Precio total: "+TotalconIVA);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    private void updateUI(FirebaseUser currentUser) {
        Usuario = currentUser.getDisplayName();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogCompleto = 0;
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //getTotal();
        Log.d(TAG,"onTouch");
        return false;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //getTotal();
    }

    private class MiTareaAsincronaDialog extends
            AsyncTask<Void, Integer, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... params) {
            if (TipoDialog == 1) {
                Log.e(TAG, "Generando archivo. Puede demorar un poco..");
                CrearPDF("HAVR", LogCompleto);
                try {
                    Thread.sleep(3500);

                } catch (InterruptedException e) {
                }
            }

            if (TipoDialog == 2) {
                Log.e(TAG, "Borrando base de datos");

                try {
                    Thread.sleep(350);

                } catch (InterruptedException e) {
                }
            }
            return true;
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
                    MiTareaAsincronaDialog.this.cancel(true);
                }
            });
            vDialog.setProgress(0);
            vDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            vDialog.dismiss();
            if (TipoDialog == 1) {
                if (CreacionTablaCompleta)
                    templatePDF.viewPDF();
                Log.d(TAG, "Proceso AsyncTask concluido");
                if (result) {
                    Log.d(TAG, "Proceso terminado:" + TipoDialog);
                    switch (TipoDialog) {
                        case 1:
                            vDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Material actualizado",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
            if (TipoDialog == 2) {
                Intent intent = new Intent(ActividadPedidos.this, ActividadCotizaciones.class);
                startActivity(intent);
            }
            Log.d(TAG, "NO ha dejado de mostrarse el Dialog:" + vDialog.isShowing());
        }
    }

    private void tareaLarga() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
}


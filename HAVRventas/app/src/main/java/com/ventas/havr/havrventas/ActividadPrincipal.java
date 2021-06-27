
package com.ventas.havr.havrventas;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentActivity;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ventas.havr.havrventas.Adaptadores.ActividadAdministracion;
import com.ventas.havr.havrventas.Herramientas.HerramientasActivity;
import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseUsuarios;

import java.net.URLEncoder;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActividadPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public static String USUARIO = "user";
    public static String BANDERA_TIENDA = "tienda";
    public static String BANDERA_TIENDA_OK = "ok";
    public static String BANDERA_TIENDA_NO = "no";
    public static String DISTRIBUIDOR = "distribuidor";
    public static String PUBLICO = "publico";
    public static String NOMBRE_TIENDA = "HAVR";
    public static String TIENDA = "nombre_tienda";
    private static final String TAG = "Actividad principal";


    private CardView BtComponentes;
    private Button BtPDFTienda;
    private CardView BtMasVendidos;
    private CardView BtHerramientas;
    private CardView BtCotizaciones;
    private Button BtTipoCliente;


    private String TipoUsuario = "";
    //private Typeface custom_font;

    // Preferencias
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    // FIREBASE
    private DatabaseReference dbCorreo;
    private ValueEventListener eventListener;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference dbPrecio;
    private DatabaseReference dbVersion;
    private RealmResults<BaseUsuarios> ResulstBaseUsuarios;
    private DatabaseReference dbNumCot;

    public Realm realm;
    private RealmResults<BaseCotizaciones> basecot;
    private int NumeroCotizacion = 0;
    private int NumProyectos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "Ok ingreso HAVR");
                }
            }
        };

        realm = Realm.getDefaultInstance();
        dbPrecio = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        dbVersion = FirebaseDatabase.getInstance().getReference().child("Version");

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String version = dataSnapshot.getValue().toString();
                String versionA = "";
                Log.d(TAG, "Version aplicación firebase:" + version);
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    versionA = pInfo.versionName;
                    Log.d(TAG, "Version aplicación app:" + version);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (version.compareTo(versionA) == 0) {
                    Log.d(TAG, "Es la misma versión.");
                } else {
                    createSimpleDialog();
                    Log.d(TAG, "Las versiones son diferentes, se necesita actualizar.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("database", "Error!", databaseError.toException());
            }
        };
        try {
            dbVersion.addValueEventListener(eventListener);
        } catch (Exception e) {
        }

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Se registro un cambio. Se necesita actualizar.");
                String users = dataSnapshot.getValue().toString();
                Log.d(TAG, "Usuario:" + users);
                SegmentarUsuarios(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaser", "Error!", databaseError.toException());
            }
        };
        try {
            dbPrecio.addValueEventListener(eventListener);
        } catch (Exception e) {
        }

        ResulstBaseUsuarios = realm.where(BaseUsuarios.class).findAll();

        BtComponentes = findViewById(R.id.bt_componentes);
        BtPDFTienda = (Button) findViewById(R.id.bt_pdf_tienda);
        BtMasVendidos = findViewById(R.id.bt_ofertas);
        BtHerramientas = findViewById(R.id.bt_herramientas);
        BtCotizaciones = findViewById(R.id.bt_cotizaciones);
        BtTipoCliente = (Button) findViewById(R.id.bt_tipo_venta);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        dbNumCot = FirebaseDatabase.getInstance().getReference()
                .child("NumCot");

        realm = Realm.getDefaultInstance();
        basecot = realm.where(BaseCotizaciones.class).findAll();
        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            CambioDistribuidor();
        } else {
            CambioPublico();
        }

        dbCorreo = FirebaseDatabase.getInstance().getReference().child("Correos");

        BtComponentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (basecot.size() == 0) {
                    showAlertForCreatingCot("Nueva cotización.", "Genere una nueva cotización.");
                } else {
                    Intent intentoa = new Intent(ActividadPrincipal.this, Manual.class);
                    startActivity(intentoa);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_ou);
                }
            }
        });

        BtPDFTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://drive.google.com/file/d/1diTdQOk1REgY2f_4M3nLXD92IfRrmK_e/view?usp=sharing");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        BtPDFTienda.setVisibility(View.INVISIBLE);
        BtCotizaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentob = new Intent(ActividadPrincipal.this, ActividadCotizaciones.class);
                startActivity(intentob);
            }
        });
        BtHerramientas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.h-avr.com/facturacion.html"));
                startActivity(browserIntent);
            }
        });

        BtMasVendidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentoc = new Intent(ActividadPrincipal.this, ActividadMasVendido.class);
                startActivity(intentoc);
            }
        });

        BtTipoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BtTipoCliente.getText().toString().contains("Distribuidor"))
                    AlertTipoCliente();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_distribuidor) {
            Intent intentob = new Intent(ActividadPrincipal.this, ActividadLogin.class);
            startActivity(intentob);
        } else if (id == R.id.nav_mercadolibre) {
            Uri uri = Uri.parse("https://listado.mercadolibre.com.mx/_CustId_169800644");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_facebook) {
            Uri uri = Uri.parse("https://www.facebook.com/havr.electronica");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_whatsapp) {
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String toNumber = "+525564254686";
            try {
                String url = "https://api.whatsapp.com/send?phone=" + toNumber + "&text=" + URLEncoder.encode("Hola, me gustaria cotizar el siguiente material:", "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_youtube) {
            Uri uri = Uri.parse("https://www.youtube.com/channel/UC3LffcvivoPw_K1AYWarOOA");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_correo) {
            String[] TO = {"juan.valerio@h-avr.com"}; //aquí pon tu correo
            String[] CC = {"Contacto HAVR."};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            // Esto podrás modificarlo si quieres, el asunto y el cuerpo del mensaje
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje");

            try {
                startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                finish();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ActividadPrincipal.this,
                        "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_salir) {
            firebaseAuth.signOut();
            //LoginManager.getInstance().logOut();
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, "Salida correcta.");
                        Toast.makeText(getApplicationContext(), "Se ha cerrado la cuenta.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No se ha ingresado a una cuenta.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAlertForCreatingCot(String title, String message) {
        Log.d(TAG, "Dialogo para crear cotización");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_create_cot, null);
        builder.setView(viewInflate);
        EditText input = (EditText) viewInflate.findViewById(R.id.editText_dialog_componentes);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String CotName = input.getText().toString().trim();
                Log.d(TAG, "COT name:" + CotName);
                if (CotName.length() > 0) {
                    try {
                        dbNumCot.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Log.d(TAG, "Se registro un cambio en la base datos");
                                Log.d("database:", snapshot.getValue().toString());
                                Log.d(TAG,"Num Cot:"+snapshot.getValue().toString());
                                NumeroCotizacion = Integer.parseInt(snapshot.getValue().toString());
                                NumeroCotizacion = NumeroCotizacion + 1;
                                dbNumCot.setValue(NumeroCotizacion);
                                createNewCot(CotName, NumeroCotizacion);
                                NumProyectos = prefs.getInt("NumeroProyectos", 0);
                                NumProyectos = NumProyectos + 1;
                                editor = prefs.edit();
                                editor.putInt("NumeroProyectos", NumProyectos);
                                editor.commit();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getMessage());
                            }


                        });
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No se ha escrito un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.verde));
        //dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void CambioDistribuidor() {
        this.setTitle("H-AVR Distribuidor");
        BtTipoCliente.setVisibility(View.INVISIBLE);
        BtTipoCliente.setText(("Distribuidor"));
        BtPDFTienda.setVisibility(View.VISIBLE);
        editor = prefs.edit();
        editor.putString(ActividadPrincipal.USUARIO, ActividadPrincipal.DISTRIBUIDOR);
        editor.commit();
    }

    private void CambioPublico() {

        this.setTitle("H-AVR Eletrónica");
        BtPDFTienda.setVisibility(View.INVISIBLE);
        BtTipoCliente.setVisibility(View.VISIBLE);
        BtTipoCliente.setText(("¡ Precios distribuidor !"));
        editor = prefs.edit();
        editor.putString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        editor.commit();
    }

    @Override
    protected void onResume() {
        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
        if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
            CambioDistribuidor();
        } else {
            CambioPublico();
        }
        super.onResume();
    }

    private void SegmentarUsuarios(String datos) {
        String[] informacion = datos.split("&");
        BorrarTablasUsuarios();
        Log.d(TAG, "Datos:" + datos);
        for (int x = 0; x < informacion.length - 1; x++) {
            String[] tienda = informacion[x].split("=");
            tienda[0] = tienda[0].replace("{", "");
            tienda[0] = tienda[0].replace(" ", "");
            tienda[0] = tienda[0].replace(",", "");
            String[] segundoSegmento = tienda[1].split("/");
            String correo = segundoSegmento[0];
            String password = segundoSegmento[1];
            String telefono = segundoSegmento[2];
            Log.d(TAG, "Tienda almacenada:" + tienda[0]);
            GuardarUsuarios(tienda[0], correo, password, telefono);
        }
    }

    private void GuardarUsuarios(String user, String correo,
                                 String password, String telefono) {
        realm.beginTransaction();
        BaseUsuarios Usuarios = new BaseUsuarios(user, password, telefono, correo);
        realm.copyToRealm(Usuarios);
        realm.commitTransaction();
        BtTipoCliente.setVisibility(View.VISIBLE);
    }

    private void BorrarTablasUsuarios() {
        realm.beginTransaction();
        ResulstBaseUsuarios.deleteAllFromRealm();
        realm.commitTransaction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actividad_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cambio:
                String bandera = prefs.getString(ActividadPrincipal.BANDERA_TIENDA, ActividadPrincipal.BANDERA_TIENDA_NO);
                if (bandera.compareTo(ActividadPrincipal.BANDERA_TIENDA_OK) == 0) {
                    if (TipoUsuario.equals(ActividadPrincipal.DISTRIBUIDOR)) {
                        CambioPublico();
                        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
                    } else {
                        CambioDistribuidor();
                        TipoUsuario = prefs.getString(ActividadPrincipal.USUARIO, ActividadPrincipal.PUBLICO);
                    }
                } else {
                    Toast.makeText(this, "No tiene los permisos.", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewCot(String cotName, int NumeroCotizacion) {
        cotName = NumeroCotizacion + "-" + cotName;
        realm.beginTransaction();
        BaseCotizaciones Cot = new BaseCotizaciones(cotName, "0");
        realm.copyToRealm(Cot);
        realm.commitTransaction();
        Log.d("Cotizaciones", "Cotizacion almacenada:" + Cot.getId());
        editor.putInt("Cotizacion", Cot.getId());
        editor.commit();
    }

    public AlertDialog AlertTipoCliente() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.iniciar_sesion);
        builder.setTitle("¡Tenemos los mejores precios!")
                .setMessage("Ahora puede iniciar sesión como distribuidor, también puede solicitar su registro.")
                .setCancelable(false)
                .setPositiveButton("Iniciar Sesion",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentob = new Intent(ActividadPrincipal.this, ActividadLogin.class);
                                startActivity(intentob);
                            }
                        })
                .setNegativeButton("Registro",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentob = new Intent(ActividadPrincipal.this, Formulario.class);
                                startActivity(intentob);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.verde));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        return builder.create();
    }

    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_play_store);
        builder.setTitle("¡Versión nueva encontrada!")
                .setMessage("Es necesario actualizar.")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.ventas.havr.havrventas");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
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
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.verde));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        return builder.create();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}

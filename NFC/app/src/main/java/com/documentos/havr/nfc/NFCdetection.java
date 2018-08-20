package com.documentos.havr.nfc;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by end user on 12/03/2018.
 */


public class NFCdetection extends AppCompatActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int SIN_FOTO = 1;
    private static final int CON_FOTO = 0;
    public static final int PERMS_REQUEST_CODE = 1;
    private static final String TAG = "NFC";

    private TextView DatosNFC;
    private NfcAdapter mNfcAdapter;
    private Button BtNFCiniciar;
    private PendingIntent pendingIntent;
    private IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;


    private TextView TxEsperando;
    private String HexNFC;
    private String NFCdat = "s";
    private String FolioEnviar;
    private ProgressBar Barra;

    private Button BtInvisible;
    private Button BtCorreo;
    private Button BtImagenes;
    private Button BtSalir;
    private Button BtConfiguracion;
    private Button BtBack;

    private File path;
    private Boolean BoFoto = false;
    LocationManager locationManager;
    public double longitudeBest, latitudeBest;
    public double longitudeGPS, latitudeGPS;
    private String[] Validos;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String locationProviderA = LocationManager.NETWORK_PROVIDER;

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProviderA);
        longitudeGPS = lastKnownLocation.getLongitude();
        latitudeGPS = lastKnownLocation.getLatitude();
        Toast.makeText(this, "lat,lon:"+longitudeGPS+","+latitudeGPS, Toast.LENGTH_SHORT).show();
        //isLocationEnabled();

        BtCorreo = (Button) findViewById(R.id.bt_correo);
        BtImagenes = (Button) findViewById(R.id.bt_imagen);
        BtInvisible = (Button) findViewById(R.id.bt_invisible);
        BtSalir = (Button) findViewById(R.id.bt_salir);
        BtConfiguracion = (Button) findViewById(R.id.bt_configuracion);
        BtNFCiniciar = (Button) findViewById(R.id.bt_inciar_nfc);
        BtBack = (Button) findViewById(R.id.bt_back);

        TxEsperando = (TextView) findViewById(R.id.tx_nfc);
        DatosNFC = (TextView) findViewById(R.id.tx_datos);
        Barra = (ProgressBar) findViewById(R.id.barra_progreso);

        Barra.setMax(10);
        Barra.setProgress(0);

        // Aceptar el uso de NFC en la aplicación
        if (ContextCompat.checkSelfPermission(NFCdetection.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NFCdetection.this, Manifest.permission.GET_ACCOUNTS)) {
                ActivityCompat.requestPermissions(NFCdetection.this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(NFCdetection.this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMS_REQUEST_CODE);
            }
        } else {
            AccountManager accountManager = AccountManager.get(getApplicationContext());
            Account[] accounts = accountManager.getAccounts();
            for (Account account : accounts) {
                Log.d(TAG, "account: " + account.name + " : " + account.type);
            }
            Log.i("EXCEPTION", "mails: " );
        }

        Resources res = getResources();

        String[] Folios = res.getStringArray(R.array.folios);
        Validos =  res.getStringArray(R.array.validos);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        path = new File("/data/data/com.documentos.havr.nfc/Imagenes");
        if (!path.exists()) {
            Log.d(TAG,"It seems like parent directory does not exist...");
            if (!path.mkdirs()) {
                Log.d(TAG,"And we cannot create it...");
            }
        }

        BtInvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BtIntent = new Intent(NFCdetection.this,ClaseFolio.class);
                BtIntent.putExtra("DATAFOLIOS",FolioEnviar);
                startActivity(BtIntent);
            }
        });
        BtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barra.setVisibility(View.INVISIBLE);
                TxEsperando.setVisibility(View.VISIBLE);
                TxEsperando.setText("Esperando lectura");
                DatosNFC.setVisibility(View.GONE);
                BtBack.setVisibility(View.GONE);
                BtCorreo.setVisibility(View.GONE);
                BtImagenes.setVisibility(View.GONE);
                BtInvisible.setVisibility(View.GONE);
                BtConfiguracion.setVisibility(View.VISIBLE);

            }
        });
        BtSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BtCorreo.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                }
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(NFCdetection.this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Acepta el envío de correo electrónico?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 1);


                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        sendMail(SIN_FOTO);
                    }
                });
                dialogo1.show();
            }
        });

        if (mNfcAdapter == null) {
            SuperActivityToast.create(this, new Style(), Style.TYPE_BUTTON)
                    .setButtonText("Dispositivo sin NFC")
                    .setButtonIconResource(R.drawable.ic_launcher_background)
                    .setOnButtonClickListener("good_tag_name", null, null)
                    .setProgressBarColor(Color.WHITE)
                    .setText("HAVR")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_STANDARD)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_CYAN))
                    .setAnimations(Style.DURATION_SHORT).show();
            finish();
            return;
        }else{
            SuperActivityToast.create(this, new Style(), Style.TYPE_BUTTON)
                    .setButtonText("Listo para usar")
                    .setButtonIconResource(R.drawable.ic_launcher_background)
                    .setOnButtonClickListener("good_tag_name", null, null)
                    .setProgressBarColor(Color.WHITE)
                    .setText("HAVR")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_STANDARD)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_CYAN))
                    .setAnimations(Style.DURATION_SHORT).show();
        }

        ;
        BtNFCiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        handleIntent(getIntent());

    }

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
        else{
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            //3
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(path+File.separator + "image.jpg");
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Toast t=Toast.makeText(this,"Enviando correo", Toast.LENGTH_SHORT);
            t.show();
            sendMail(CON_FOTO);
        }
    }

    public interface ParsedNdefRecord{

        String str();

    }

    private void write(String text, Tag tag) throws IOException, FormatException {
        //Creamos un array de elementos NdefRecord. Este Objeto representa un registro del mensaje NDEF
        //Para crear el objeto NdefRecord usamos el método createRecord(String s)
        NdefRecord[] records = {createRecord(text)};
        //NdefMessage encapsula un mensaje Ndef(NFC Data Exchange Format). Estos mensajes están
        //compuestos por varios registros encapsulados por la clase NdefRecord
        NdefMessage message = new NdefMessage(records);
        //Obtenemos una instancia de Ndef del Tag
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }
    //Método createRecord será el que nos codifique el mensaje para crear un NdefRecord
    @SuppressLint("NewApi") private NdefRecord createRecord(String text) throws UnsupportedEncodingException{
        String lang = "us";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payLoad = new byte[1 + langLength + textLength];

        payLoad[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payLoad, 1, langLength);
        System.arraycopy(textBytes, 0, payLoad, 1+langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payLoad);

        return recordNFC;

    }


    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccounts();
        List<String> possibleEmails = new LinkedList<String>();
        Log.d(TAG,"Revisando cuentas:"+accounts.length);
        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type
            // values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return null;
        } else
            return null;
    }


    public void cancelar() {
        //finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled())
                showWirelessSettings();

            setupForegroundDispatch(this, mNfcAdapter);
            //mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }

    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        //locationManager.removeUpdates((LocationListener) NFCdetection.this);
        Log.d(TAG,"Se ha parado la aplicación");
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    void processIntent(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        Log.d(TAG,new String(msg.getRecords()[0].getPayload()));
        //textView.setText();
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        Log.d(TAG,"Lectura NFC: inicio za");
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        Log.d(TAG,"Lectura NFC: inicio zb");
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
            Log.d(TAG,"Lectura NFC: inicio");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        Log.d(TAG,"stopForegroundDispatch");
        adapter.disableForegroundDispatch(activity);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        /*
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        String action = intent.getAction();
        String valor = intent.toString();
        Log.d(TAG,"Action NFC"+action);
        Log.d(TAG,"To string:"+valor);
        if (intent.hasExtra((NfcAdapter.EXTRA_TAG))) {
            Barra.setVisibility(View.VISIBLE);
            TxEsperando.setVisibility(View.INVISIBLE);
            DatosNFC.setVisibility(View.INVISIBLE);
            BtBack.setVisibility(View.VISIBLE);
            //
            // Toast.makeText(this, "NFC!!", Toast.LENGTH_LONG).show();
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            //String datoNFC1 =  intent.getParcelableExtra(NfcAdapter.FLAG_READER_NFC_BARCODE);
            Log.d(TAG, "Datos del NFC:");

        }
        else
            Log.d(TAG, "NFc vacia");
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"Lectura NFC: inicio A");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            String dat = String.valueOf(intent.getData());
            //String uri = intent.getDataString();
            NdefMessage[] msgs;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            Log.d(TAG,"Tipo MYME:"+type);
            Log.d(TAG,"dat:"+dat);
            if(dat.length() > 5)
                NFCdat = dat;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
                Log.d(TAG, "Leer NFC" + type);
            }else if(dat.length() > 5){
                Log.d(TAG,"No MYME, si dat");
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            }
            else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];
            Log.d(TAG, "TAG:" + "NdefReaderTask");
            StringBuilder sb = new StringBuilder();
            byte[] id = tag.getId();
            sb.append("ID HEX :").append(toHex(id));
            HexNFC = sb.toString();
            Log.d(TAG, "TAG:" + HexNFC);
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                Log.e(TAG, "NFC not supported");
                return null;
            }

            if(NFCdat.length() > 5 ) {
                String regresar = NFCdat;
                NFCdat = "s";
                Log.d(TAG,"Se enviaran los datos aaa ");
                return regresar;
            }else {

                NdefMessage ndefMessage = ndef.getCachedNdefMessage();

                NdefRecord[] records = ndefMessage.getRecords();
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        Log.d(TAG, "Lectura NFC:" + ndefMessage);

                        try {
                            Log.d(TAG, "DATOS:" + ndefMessage);
                            return readText(ndefRecord);
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "Unsupported Encoding", e);
                        }
                    }
                }
            }
            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
            Log.d(TAG,"Lectura NFC: inicio B");
            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                String[] resultadoFolio = result.split("#");
                FolioEnviar = resultadoFolio[1];
                Barra.setVisibility(View.INVISIBLE);
                TxEsperando.setVisibility(View.VISIBLE);
                DatosNFC.setVisibility(View.VISIBLE);
                BtConfiguracion.setVisibility(View.INVISIBLE);
                BtCorreo.setVisibility(View.VISIBLE);
                BtImagenes.setVisibility(View.VISIBLE);
                BtInvisible.setVisibility(View.VISIBLE);

                boolean DocumentoValido = false;
                for(int x = 0; x < Validos.length ; x++) {
                    Log.d(TAG, "HEXNFCVALIDO:" + Validos[x] + ",NFC read:" + HexNFC + "//");
                    if (HexNFC.contains(Validos[x])){
                        DocumentoValido = true;
                        break;
                    }else
                        DocumentoValido = false;
                }

                    if (DocumentoValido) {
                        BtCorreo.setEnabled(false);
                        BtInvisible.setEnabled(true);
                        TxEsperando.setText("¡Documento valido!");
                        SuperActivityToast.create(NFCdetection.this, new Style(), Style.TYPE_BUTTON)
                                .setButtonText("Documento valido")
                                .setButtonIconResource(R.drawable.chipc)
                                .setOnButtonClickListener("good_tag_name", null, null)
                                .setProgressBarColor(Color.WHITE)
                                .setText("HAVR")
                                .setDuration(Style.DURATION_LONG)
                                .setFrame(Style.FRAME_STANDARD)
                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                                .setAnimations(Style.DURATION_SHORT).show();

                    } else {
                        BtInvisible.setEnabled(false);
                        BtCorreo.setEnabled(true);
                        SuperActivityToast.create(NFCdetection.this, new Style(), Style.TYPE_BUTTON)
                                .setButtonText("Documento no valido")
                                .setButtonIconResource(R.drawable.chipc)
                                .setOnButtonClickListener("good_tag_name", null, null)
                                .setProgressBarColor(Color.WHITE)
                                .setText("HAVR")
                                .setDuration(Style.DURATION_LONG)
                                .setFrame(Style.FRAME_STANDARD)
                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                                .setAnimations(Style.DURATION_SHORT).show();
                        ActivityCompat.requestPermissions(NFCdetection.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                0
                        );

                        ActivityCompat.requestPermissions(NFCdetection.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                1
                        );
                        if (ActivityCompat.checkSelfPermission(NFCdetection.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(NFCdetection.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);

                        TxEsperando.setText("¡Documento no valido!");


                    }

                DatosNFC.setText(resultadoFolio[1]);
                Log.d(TAG,"Lectura:"+result);
            }
        }
    }
    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }


    public void sendMail( int tipo) {
        try
        {
            BoFoto = false;
            if(tipo == SIN_FOTO)
            {
                BoFoto = true;
            }
            LongOperation l=new LongOperation();

            l.execute();  //sends the email in background
            //Toast.makeText(this, l.get(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(android.location.Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            Log.d(TAG,"Ubicación: LAT"+latitudeGPS+",LON:"+longitudeGPS);
            String msg="New Latitude: "+longitudeGPS + "New Longitude: "+longitudeGPS;
            //Toast.makeText(NFCdetection.this,msg,Toast.LENGTH_LONG).show();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
            Location location = null;

            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            Log.d(TAG,"Ubicación: LAT"+latitudeGPS+",LON:"+longitudeGPS);
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public class LongOperation extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            try {
                int dato = 0;

                GMailSender sender = new GMailSender("carbopapel.holograma@gmail.com", "carbo12345");
                String name = "image.jpg";
                String directorio = "/data/data/com.documentos.havr.nfc/Imagenes/" + name;
                String titulo = "El documento que se ha leido no es valido. " +
                        "Favor de reportarlo al 01 800 XX XXX XXX." +

                        "Evento ocurrio en!!: http://maps.google.com/maps?q=loc:"+latitudeGPS+","+longitudeGPS;
                if(BoFoto == false) {
                    sender.addAttachment(directorio, titulo);
                    dato = 0;
                }else
                    dato = 1;
                sender.sendMail(dato,"Holograma de seguridad!",
                        titulo + "",
                        "valeriovaa@gmail.com",

                        "valeriovaa@gmail.com,juan.valerio@h-avr.com,heriberto.segura@carbopapel.com.mx,santosa.arturo@gmail.com");
                // ,heriberto.segura@carbopapel.com.mx
            } catch (Exception e) {
                Log.e("error", e.getMessage(), e);
                return "Email Not Sent";
            }
            return "Email Sent";
        }


        @Override
        protected void onPostExecute(String result) {
            Log.e("LongOperation", result + "");
        }

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    @Override
    protected void onDestroy() {
        //locationManager.removeUpdates((LocationListener) NFCdetection.this);
        super.onDestroy();
    }
}

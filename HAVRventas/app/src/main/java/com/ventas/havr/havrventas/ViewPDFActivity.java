package com.ventas.havr.havrventas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.ventas.havr.havrventas.Correo.HAVRsender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ViewPDFActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_READ_STORAGE = 3;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private PDFView pdfView;
    private File pdfFile;
    private File file;
    private String Correo0;
    private String Correo1;
    private String Correo2;
    private String Telefono;

    private ProgressDialog vDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Correo0 = prefs.getString("Correo", "<->");
        Correo1 = prefs.getString("nombre_mail", "<->");
        Correo2 = "valeriovaa@gmail.com";
        Telefono = prefs.getString("nombre_telefono","000");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if(checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }


        pdfView = (PDFView) findViewById(R.id.pdf_view);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            file = new File(bundle.getString("path",""));
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }

    private void addPreferencesFromResource(int pref_empresa) {
    }



    public class LongOperation extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            try {
                int dato = 0;
                HAVRsender sender = new HAVRsender("ventas@h-avr.com", "1103201517VALE_");
                String titulo = "¡Muchas gracias por su preferencia!";
                try {
                    sender.HAVRAddAttachment("Cotizaciones",titulo+"");
                    sender.HAVRSendMail("Cotización",
                            titulo + "",
                            "ventas@h-avr.com",
                            "ventas@h-avr.com,valeriovaa@gmail.com," +
                                    "juan.valerio@h-avr.com");
                }catch (Exception e) {
                    Log.e("error", e.getMessage(), e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage(), e);
                return "Email Not Sent";
            }
            return "Email Sent";
        }


        @Override
        protected void onPostExecute(String result) {
            Log.e("LongOperation", result + "");
            Toast.makeText(getApplicationContext(), "Correo enviado. Revise su bandeja de entrada.", Toast.LENGTH_LONG).show();
            vDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            vDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    LongOperation.this.cancel(true);
                }
            });
            vDialog.setProgress(0);
            vDialog.show();
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            vDialog.setProgress(50);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_correo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.correo:
                Log.d("HAVR MAIL:","Enviar correo");
                LongOperation l = new LongOperation();
                vDialog = new ProgressDialog(ViewPDFActivity.this);
                vDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                vDialog.setMessage("Enviando correo...");
                vDialog.setCancelable(false);
                vDialog.setMax(100);
                l.execute();
                return true;
            case R.id.share:
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File folder = new File("/sdcard/");
                // your sd card
                String sdCard = Environment.getExternalStorageDirectory().toString();
                // the file to be moved or copied
                File sourceLocation = new File ("/data/data/com.ventas.havr.havrventas/PDFA" + "/CotizacionHAVR.pdf");
                // make sure your target location folder exists!
                File targetLocation = new File (folder + "/CotizacionHAVR.pdf");
                // just to take note of the location sources
                Log.v("PDF FILE", "sourceLocation: " + sourceLocation);
                Log.v("PDF FILE", "targetLocation: " + targetLocation);

                try {
                    // 1 = move the file, 2 = copy the file
                    int actionChoice = 2;
                    // moving the file to another directory
                    if(actionChoice==1){
                        if(sourceLocation.renameTo(targetLocation)){
                            Log.v("PDF FILE", "Move file successful.");
                        }else{
                            Log.v("PDF FILE", "Move file failed.");
                        }
                    }
                    // we will copy the file
                    else{
                        // make sure the target file exists
                        if(sourceLocation.exists()){
                            InputStream in = new FileInputStream(sourceLocation);
                            OutputStream out = new FileOutputStream(targetLocation);
                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            in.close();
                            out.close();
                            Log.v("PDF FILE", "Copy file successful.");
                        }else{
                            Log.v("PDF FILE", "Copy file failed. Source file missing.");
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                pdfFile = new File(folder, "CotizacionHAVR.pdf");
                pdfFile.setReadable(true);
                pdfFile.setReadable(true,false);
                pdfFile.setWritable(true,false);
                if(pdfFile.exists()) {
                    //intentShareFile.addCategory(Intent.CATEGORY_OPENABLE);
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Cotización HAVR ventas");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "¡Muchas gracias!");
                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }else{
                    Toast.makeText(getApplicationContext(),"No existe el archivo.",Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}

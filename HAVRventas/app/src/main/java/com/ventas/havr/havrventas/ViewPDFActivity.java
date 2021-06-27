package com.ventas.havr.havrventas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ventas.havr.havrventas.Correo.HAVRsender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.net.Proxy.Type.HTTP;

public class ViewPDFActivity extends AppCompatActivity {
    private static final String TAG = "VIEW PDF Activity";
    private static final int MY_PERMISSIONS_READ_STORAGE = 3;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final int PICK_PDF_FILE = 2;
    private PDFView pdfView;
    private File pdfFile;
    private File file;
    private String nombreArchivo;
    private String Correo1;
    private String Correo2;
    private String Telefono;
    private String nombreProyecto;

    private String pathSD = "/sdcard/";
    private ProgressDialog vDialog;
    private StorageReference storageRefence;
    private int tipoArchivo = 0; // Archivo tipo cotizacion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        checkAndRequestPermissions();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Correo1 = prefs.getString("nombre_mail", "<->");
        Correo2 = "valeriovaa@gmail.com";
        Telefono = prefs.getString("nombre_telefono", "000");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Log.d(TAG, "Oncreate VIEWPDF");


        pdfView = (PDFView) findViewById(R.id.pdf_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            file = new File(bundle.getString("path", ""));
            nombreProyecto = bundle.getString("Nombre");
            tipoArchivo = bundle.getInt("tipoArchivo");
            nombreArchivo = bundle.getString("nomCot");
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
        storageRefence = FirebaseStorage.getInstance().getReference();
        // Copiar el archivo local a la SD para enviar a Storage
        MoverArchivo();
        copiarSDSoporte(10);
    }

    private void EnviarArchivoNube(String nombre) {
        String TipoCot;
        File folder = new File(pathSD);
        TipoCot = nombreProyecto + ".pdf";
        pdfFile = new File(folder, "/" + nombreProyecto + ".pdf");

        pdfFile.setReadable(true);
        pdfFile.setReadable(true, false);
        pdfFile.setWritable(true, false);
        if (pdfFile.exists()) {
            Uri file = Uri.fromFile(new File(folder + "/" + TipoCot));
            StorageReference reference;
            if(tipoArchivo == 0)
                reference = storageRefence.child(nombreArchivo + "/" + TipoCot);
            else
                reference = storageRefence.child("Catalogo" + "/" + TipoCot);
            reference.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Toast.makeText(getApplicationContext(), "El archivo se ha enviado al servidor.", Toast.LENGTH_LONG).show();
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "No se subio el archivo. Error", Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "No existe el archivo.", Toast.LENGTH_LONG).show();
        }
    }

    private File copiarSDSoporte(int numero) {
        Log.d(TAG, "Inicio:" + numero);
        File folder = new File(pathSD);
        File sourceLocation = new File("/data/data/com.ventas.havr.havrventas/PDFA" + "/" + nombreProyecto + ".pdf");
        File targetLocation = new File(folder + "/" + nombreProyecto + ".pdf");
        Log.v("PDF FILE", "sourceLocation: " + sourceLocation);
        Log.v("PDF FILE", "targetLocation: " + targetLocation);
        try {
            int actionChoice = 2;
            if (actionChoice == 1) {
                if (sourceLocation.renameTo(targetLocation)) {
                    Log.v("PDF FILE", "Move file successful.");
                } else {
                    Log.v("PDF FILE", "Move file failed.");
                }
            }
            // we will copy the file
            else {
                // make sure the target file exists
                if (sourceLocation.exists()) {
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
                    Log.v("PDF FILE", "Archivo copiado exitosamente, envío en proceso");
                    EnviarArchivoNube(nombreProyecto);
                } else {
                    Log.v("PDF FILE", "Copy file failed. Source file missing.");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_correo, menu);
        return true;
    }

    private void MoverArchivo() {
        File folder = new File(pathSD);
        String nombreArchivo = "/" + nombreProyecto + ".pdf";
        Log.d("VIEWPDF", "file:" + folder);
        folder.setReadable(true, false);
        folder.setWritable(true, false);
        folder.setExecutable(true, false);
        if (!folder.exists()) {
            folder.mkdirs();
            Log.d("CreateFILE", "Folder creado en SD");
        } else {
            Log.d("CreateFILE", "El directorio existe en SD");
        }

        File dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(dirPath, nombreProyecto + ".pdf");

        try {
            if(!dirPath.isDirectory()) {
                dirPath.mkdirs();
                Log.d(TAG,"pdfFile creado");
            }
            pdfFile.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }

        dirPath.setReadable(true, false);
        dirPath.setWritable(true, false);
        //String sdCard = Environment.getExternalStorageDirectory().toString();
        File sourceLocation = new File("/data/data/com.ventas.havr.havrventas/PDFA" + nombreArchivo);
        File targetLocation = new File(dirPath + "/" + nombreArchivo);

        // just to take note of the location sources
        Log.v("PDF FILE", "sourceLocation: " + sourceLocation);
        Log.v("PDF FILE", "targetLocation: " + targetLocation);

        try {
            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;
            // moving the file to another directory
            if (actionChoice == 1) {
                if (sourceLocation.renameTo(targetLocation)) {
                    Log.v("PDF FILE", "Move file successful.");
                } else {
                    Log.v("PDF FILE", "Move file failed.");
                }
            }
            // we will copy the file
            else {
                // make sure the target file exists
                if (sourceLocation.exists()) {
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
                } else {
                    Log.v("PDF FILE", "Copy file failed. Source file missing.");
                }
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "ERROR A");
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "ERROR B");
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                // You can also attach multiple items by passing an ArrayList of UrisX
                File folder = new File(pathSD);
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                pdfFile = new File(folder, "/" + nombreProyecto + ".pdf");
                pdfFile.setReadable(true);
                pdfFile.setReadable(true, false);
                pdfFile.setWritable(true, false);
                if (pdfFile.exists()) {
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Cotización HAVR ventas");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "¡Muchas gracias!");
                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intentShareFile.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if(android.os.Build.VERSION.SDK_INT >= 30){

                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                    }else {
                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No existe el archivo.", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestPermissions();
        copiarSDSoporte(12);
    }
}

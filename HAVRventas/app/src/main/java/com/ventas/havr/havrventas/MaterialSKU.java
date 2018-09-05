package com.ventas.havr.havrventas;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MaterialSKU extends AppCompatActivity {

    // Final String
    final private static String TAG = "MaterialSKU";
    // Variables
    public String[] row;
    public String[] SpinString = new String[ 400 ];
    public String[] SpinStringA = new String[ 400 ];
    public String[] ConjuntoBase;
    private List scoreList;
    String data;
    String imagen;
    String Sku;
    String TipoComponente;

    // XML
    private Button BtAgregar;
    private ImageView ImagenPerfiles;
    private TextView TxComponente;
    private TextView TxResultado;
    private ProgressBar BarraEspera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_sku);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_perfiles);
        ImagenPerfiles = (ImageView) findViewById(R.id.image_perfiles);
        TxComponente = (TextView) findViewById(R.id.tx_componente);
        BtAgregar = (Button) findViewById(R.id.bt_agregar);
        TxResultado = (TextView) findViewById(R.id.tx_resultado);
        BarraEspera = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        int ValorItem = intent.getIntExtra("Posicion",0);
        TipoComponente = intent.getStringExtra("SPerfil");
        TxComponente.setText(TipoComponente);

        InputStream inputStream;
        CSVFile csvFile;

        switch (ValorItem){
            case 0:
                ImagenPerfiles.setImageResource(R.drawable.im_arduino);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 1:
                ImagenPerfiles.setImageResource(R.drawable.im_bateria);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 2:
                ImagenPerfiles.setImageResource(R.drawable.im_broca);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.brocas);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 3:
                ImagenPerfiles.setImageResource(R.drawable.im_bluetooth);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.bluetooth);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 4:
                ImagenPerfiles.setImageResource(R.drawable.im_cable);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 5:
                ImagenPerfiles.setImageResource(R.drawable.im_capacitor);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 6:
                ImagenPerfiles.setImageResource(R.drawable.im_cautin);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 7:
                ImagenPerfiles.setImageResource(R.drawable.im_circuito);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 8:
                ImagenPerfiles.setImageResource(R.drawable.im_cable);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 9:
                ImagenPerfiles.setImageResource(R.drawable.im_disipador);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 10:
                ImagenPerfiles.setImageResource(R.drawable.im_dip);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.arduino);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 11:
                ImagenPerfiles.setImageResource(R.drawable.im_display);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.displays);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 12:
                ImagenPerfiles.setImageResource(R.drawable.im_header);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.displays);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 13:
                ImagenPerfiles.setImageResource(R.drawable.im_hv);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.displays);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 14:
                ImagenPerfiles.setImageResource(R.drawable.im_laser);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.displays);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 15:
                ImagenPerfiles.setImageResource(R.drawable.im_led);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.displays);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 16:
                ImagenPerfiles.setImageResource(R.drawable.im_medidor);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.displays);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 17:
                ImagenPerfiles.setImageResource(R.drawable.im_modulos);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.modulos);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;
            case 18:
                ImagenPerfiles.setImageResource(R.drawable.im_motores);
                Log.d("Componente","Info:"+ValorItem);
                inputStream = getResources().openRawResource(R.raw.displays);
                csvFile = new CSVFile(inputStream);
                scoreList = csvFile.read();
                Log.d("Perfiles","Info: B"+ scoreList.size());
                break;

        }
        // Colocar la información en el Spinner
        final String[] SpinOtro = new String[ scoreList.size() ];
        for(int ii = 0; ii < scoreList.size();ii++) {
            SpinOtro[ii] = SpinString[ii]+ " - " + SpinStringA[ii];
        };

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinOtro));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // Se selecciona por medida, la informamcion es procesado al corta por ","
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                BarraEspera.setVisibility(View.VISIBLE);
                TxResultado.setText(" ");
                Sku = adapterView.getItemAtPosition(pos).toString();
                String[] STsku = Sku.split(" ");
                STsku[0] = STsku[0].toLowerCase();
                getWebsite(STsku[0]);
                Log.d(TAG,"Texto SKU:"+STsku[0]);
                int resID = 0;
                Log.d(TAG,"Tipo componente: "+TipoComponente);
                TipoComponente = TipoComponente.toLowerCase();
                if(TipoComponente.equals("brocas")){
                    resID = getResources().getIdentifier("br0000" , "drawable", getPackageName());
                }else{
                    resID = getResources().getIdentifier(STsku[0] , "drawable", getPackageName());
                }

                ImagenPerfiles.setImageResource(resID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
    }

    public class CSVFile {
        InputStream inputStream;

        public CSVFile(InputStream inputStream){
            this.inputStream = inputStream;
        }

        public List read(){
            List resultList = new ArrayList();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String csvLine;
                int i = 0;
                while ((csvLine = reader.readLine()) != null) {
                    row = csvLine.split(",");
                    SpinString[i] = row[0]; // Guarda el SKU
                    SpinStringA[i] = row[1];  // Guarda el nombre del producto
                    i += 1;
                    resultList.add(row);
                }

            }
            catch (IOException ex) {
                throw new RuntimeException("Error in reading CSV file: "+ex);
            }
            finally {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    throw new RuntimeException("Error while closing input stream: "+e);
                }
            }
            return resultList;
        }
    }

    private void getWebsite(final String sku) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                String srcLink = "http://www.h-avr.mx/product-page/arduino-lilypad-usb";
                //Obtener el arreglo String de values
                Resources res = getResources();
                TipoComponente = TipoComponente.toLowerCase();
                String BaseConcatenar = "base_"+TipoComponente;
                int resID = getResources().getIdentifier(BaseConcatenar , "array", getPackageName());
                ConjuntoBase = res.getStringArray(resID);
                for(int xx = 0; xx < ConjuntoBase.length; xx++)
                {
                    String[] DivisionDatos = ConjuntoBase[xx].split("@");
                    String SKUchecar = DivisionDatos[0];
                    if(sku.equals(SKUchecar)){
                        srcLink = DivisionDatos[1];
                    }
                }
                // Obtener el texto de la pagina
                try {
                    Document doc = Jsoup.connect(srcLink).get();
                    Elements iframes  = doc.getElementsByTag("iframe");
                    for (Element iframe1 : iframes) {
                        String src = iframe1.attr("src");
                        String title = iframe1.attr("title");
                        Log.d("Material","Data:"+src+title);
                        if(title.equals("Product Page"))
                        {
                            try {
                            Document docA = Jsoup.connect(src).get();
                            Elements metaTags  = docA.getElementsByTag("meta");
                            for (Element metaTag : metaTags) {
                                String name = metaTag.attr("property");
                                String content = metaTag.attr("content");
                                if(name.equals("og:description")) {
                                    data = content;
                                    Log.d("Material", "Data:" + name + content);
                                }
                            }
                            } catch (IOException e) {
                                builder.append("Error : ").append(e.getMessage()).append("\n");
                            }

                        }
                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                // Obtener imagen de la pagina
                try {
                    Log.d("Material","Obtener imagen");
                    Document doc = Jsoup.connect("http://www.h-avr.mx/product-page/amplificador-de-audio-tda2050-25w").get();
                    Elements iframes  = doc.getElementsByTag("iframe");
                    for (Element iframe1 : iframes) {
                        String src = iframe1.attr("src");
                        String title = iframe1.attr("title");
                        Log.d("Material","Data:"+src+title);
                        if(title.equals("Product Page"))
                        {
                            Log.d("Material","Obtener imagen A");
                            try {
                                Document docA = Jsoup.connect(src).get();
                                Elements metaTags  = docA.getElementsByTag("meta");
                                for (Element metaTag : metaTags) {
                                    String name = metaTag.attr("property");
                                    String content = metaTag.attr("content");
                                    Log.d("Material img", "Imagen:" + name + content);
                                    if(name.equals("og:image")) {
                                        Log.d("Material", "Data:"+ content);
                                        imagen = content;

                                        break;
                                    }
                                }
                            } catch (IOException e) {
                                builder.append("Error : ").append(e.getMessage()).append("\n");
                            }
                            break;
                        }
                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data = data.replace("<p>","");
                        data = data.replace("</p>","");
                        data = data.replace("<br>","\r\n");
                        TxResultado.setText(data);
                        BarraEspera.setVisibility(View.INVISIBLE);
                        Log.d("MaterialSKU","Info:"+data);
                    }
                });


            }
        }).start();
    }
}

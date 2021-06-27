package com.ventas.havr.havrventas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BaseSKU;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.Table;

import static com.itextpdf.text.html.HtmlTags.IMG;

public class PDFCatalogo {

    private final static String TAG = "Template PDF";
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private RealmResults<BaseSKU> ResulstBaseSKU;

    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private BaseImagenes BaseImagenes;
    private BaseSKU Basesku;
    public Realm realm;
    private BaseColor colorHAVR = new BaseColor(25, 118, 210);
    private Font fTotal = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
    private Font fAgotado = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.RED);
    private Font fTitle = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE); //FF00269C
    private Font fPrecio = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, colorHAVR); //FF00269C
    private Font fSubtitle = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private Font fText = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);

    private BaseColor colorGris = new BaseColor(232, 232, 232);

    public PDFCatalogo(Context context) {
        this.context = context;
    }

    public void openDocument(String nombreDoc) {
        createFile(nombreDoc);
        realm.init(context);
        realm = Realm.getDefaultInstance();
        ResulstBaseSKU = realm.where(BaseSKU.class).findAll();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        } catch (Exception e) {
            Log.e("OpenDocument", e.toString());

        }
    }

    public void addCatalogo(String listaItems, int tipoCliente) throws DocumentException, IOException {
        paragraph = new Paragraph(" ", fText);
        // Recuperamos la información de las bases de datos
        ResulstBaseSKU = realm.where(BaseSKU.class)
                .beginsWith("SKU", listaItems).findAll().sort("SKU");
        ResulstBaseImagenes = realm.where(BaseImagenes.class)
                .beginsWith("SKU", listaItems).findAll().sort("SKU");
        int totalSku = ResulstBaseSKU.size();
        int totalImg = ResulstBaseImagenes.size();
        Log.d(TAG, "Total de " + listaItems + ":" + totalSku + "," + totalImg);
        // Creamos el catalogo
        try {
            PdfPTable pdfPTable = new PdfPTable(4);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setWidths(new float[]{25, 25, 25, 25});

            PdfPCell pdfPCell = null;
            pdfPCell = new PdfPCell(new Phrase("", fSubtitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Log.d(TAG, "Modulo :" + totalSku % 4);
            int vecesExtra = totalSku % 4;
            int IndexFila = 0;
            ArrayList<String> listaSKU = new ArrayList<String>();
            ArrayList<String> listaLink = new ArrayList<String>();
            ArrayList<String> listaPrecio = new ArrayList<String>();
            ArrayList<String> listaCantidad = new ArrayList<String>();
            ArrayList<String> listaImagenes = new ArrayList<String>();
            ArrayList<String> listaDescripcion = new ArrayList<String>();
            for (int indexRow = 0; indexRow < (totalSku + vecesExtra); indexRow++) {
                Log.d(TAG, "Aca indexRow:" + indexRow);
                try {
                    listaSKU.add(ResulstBaseSKU.get(indexRow).getSKU());
                    if(tipoCliente == 0)
                        listaPrecio.add(ResulstBaseSKU.get(indexRow).getPrecioPublico());
                    else
                        listaPrecio.add(ResulstBaseSKU.get(indexRow).getPrecio());
                    listaCantidad.add(ResulstBaseSKU.get(indexRow).getCantidad());
                    listaDescripcion.add(ResulstBaseSKU.get(indexRow).getDescripcion());
                } catch (Exception e) {
                    listaPrecio.add(" ");
                    listaCantidad.add("99999");
                    listaDescripcion.add("");
                }
                try{
                    BaseImagenes = realm.where(BaseImagenes.class).contains("SKU",ResulstBaseSKU.get(indexRow).getSKU()).findFirst();
                    listaImagenes.add(BaseImagenes.getLink());
                    listaLink.add(BaseImagenes.getLinkML());
                }catch (Exception e){
                    listaImagenes.add("");
                }
                try {
                    pdfPCell = new PdfPCell(new Phrase(ResulstBaseSKU.get(indexRow).getSKU(), fTitle));
                } catch (Exception e) {
                    pdfPCell = new PdfPCell(new Phrase("", fTitle));
                }
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfPCell.setBackgroundColor(colorHAVR);
                pdfPCell.setBorderColor(colorHAVR);
                pdfPCell.setFixedHeight(20);
                pdfPTable.addCell(pdfPCell);

                IndexFila++;
                if (IndexFila == 4) {
                    IndexFila = 0;
                    // Colocamos las imagenes
                    for (int x = 0; x <= 3; x++) {
                        Log.d(TAG, "Aca x:" + x);
                        Bitmap bmp = Glide.with(context).asBitmap().
                                load("https://imagizer.imageshack.com/v2/640x480q90/924/Drdh42.png").submit().get();
                        try {
                            bmp = Glide.with(context).asBitmap().load(listaImagenes.get(x)).submit().get();
                        } catch (Exception e) {
                            Log.d(TAG, "Error al leer la base de datos");
                            bmp = Glide.with(context).asBitmap().
                                    load("https://imagizer.imageshack.com/v2/640x480q90/924/Drdh42.png").submit().get();
                        }
                        Bitmap bmpA = Bitmap.createScaledBitmap(
                                bmp, 280, 200, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmpA.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        Image image = Image.getInstance(stream.toByteArray());
                        PdfPCell pdfPCellA = new PdfPCell(image, true);
                        pdfPCellA.setBackgroundColor(BaseColor.WHITE);
                        pdfPCellA.setPaddingTop(7);
                        pdfPCellA.setPadding(5);
                        pdfPCellA.setBorder(0);
                        pdfPCellA.setBorderWidthLeft(1);
                        pdfPCellA.setBorderWidthRight(1);
                        pdfPCellA.setBorderColorLeft(colorHAVR);
                        pdfPCellA.setBorderColorRight(colorHAVR);
                        pdfPCellA.setImage(image);
                        pdfPCellA.setFixedHeight(110);
                        try {
                            if(listaLink.get(x).compareTo("NO") != 0) {
                                pdfPCellA.setCellEvent(new TemplatePDF.LinkInCell(listaLink.get(x)));
                                Log.d(TAG, "Link:" + listaLink.get(x));
                            }else{
                                pdfPCellA.setCellEvent(new TemplatePDF.LinkInCell("http://www.h-avr.com/"));
                                Log.d(TAG,"El link no esta dado de alta");
                            }
                        }catch (Exception e){
                            pdfPCellA.setCellEvent(new TemplatePDF.LinkInCell("http://www.h-avr.com/"));
                            Log.d(TAG,"No se agrego link.");
                        }
                        pdfPTable.addCell(pdfPCellA);
                    }
                    //Colocamos las descripciones
                    for (int x = 0; x <= 3; x++) {
                        pdfPCell = new PdfPCell(new Phrase(listaDescripcion.get(x), fTotal));
                        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfPCell.setBackgroundColor(BaseColor.WHITE);
                        pdfPCell.setBorder(0);
                        pdfPCell.setBorderWidthTop(1);
                        pdfPCell.setBorderWidthBottom(1);
                        pdfPCell.setBorderWidthLeft(1);
                        pdfPCell.setBorderWidthRight(1);
                        pdfPCell.setBorderColorLeft(colorHAVR);
                        pdfPCell.setBorderColorBottom(colorHAVR);
                        pdfPCell.setBorderColorRight(colorHAVR);
                        pdfPCell.setBorderColorTop(colorHAVR);
                        pdfPCell.setFixedHeight(30);
                        pdfPCell.setCellEvent(new TemplatePDF.LinkInCell("http://www.h-avr.com/"));
                        pdfPTable.addCell(pdfPCell);
                    }
                    //Colocamos las precios
                    for (int x = 0; x <= 3; x++) {
                        pdfPCell = new PdfPCell(new Phrase(listaPrecio.get(x), fPrecio));
                        pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        pdfPCell.setBackgroundColor(BaseColor.WHITE);
                        pdfPCell.setBorder(0);
                        pdfPCell.setBorderWidthLeft(1);
                        pdfPCell.setBorderWidthRight(1);
                        pdfPCell.setBorderColorLeft(colorHAVR);
                        pdfPCell.setBorderColorRight(colorHAVR);
                        pdfPCell.setFixedHeight(15);
                        pdfPTable.addCell(pdfPCell);
                    }
                    //Colocamos Cantidades
                    for (int x = 0; x <= 3; x++) {

                        if (Integer.parseInt(listaCantidad.get(x)) == 0) {
                            pdfPCell = new PdfPCell(new Phrase("Agotado", fAgotado));
                            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfPCell.setBackgroundColor(BaseColor.WHITE);
                            pdfPCell.setBorder(0);
                            pdfPCell.setBorderWidthBottom(1);
                            pdfPCell.setBorderWidthLeft(1);
                            pdfPCell.setBorderWidthRight(1);
                            pdfPCell.setBorderColorLeft(colorHAVR);
                            pdfPCell.setBorderColorBottom(colorHAVR);
                            pdfPCell.setBorderColorRight(colorHAVR);
                            pdfPCell.setFixedHeight(15);
                            pdfPTable.addCell(pdfPCell);
                        } else if (Integer.parseInt(listaCantidad.get(x)) == 99999) {
                            pdfPCell = new PdfPCell(new Phrase(" ", fAgotado));
                            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfPCell.setBackgroundColor(BaseColor.WHITE);
                            pdfPCell.setBorder(0);
                            pdfPCell.setBorderWidthBottom(1);
                            pdfPCell.setBorderWidthLeft(1);
                            pdfPCell.setBorderWidthRight(1);
                            pdfPCell.setBorderColorLeft(colorHAVR);
                            pdfPCell.setBorderColorBottom(colorHAVR);
                            pdfPCell.setBorderColorRight(colorHAVR);
                            pdfPCell.setFixedHeight(15);
                            pdfPTable.addCell(pdfPCell);
                        } else {
                            pdfPCell = new PdfPCell(new Phrase("Existencia: " + listaCantidad.get(x), fTotal));
                            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            pdfPCell.setBackgroundColor(BaseColor.WHITE);
                            pdfPCell.setBorder(0);
                            pdfPCell.setBorderWidthBottom(1);
                            pdfPCell.setBorderWidthLeft(1);
                            pdfPCell.setBorderWidthRight(1);
                            pdfPCell.setBorderColorLeft(colorHAVR);
                            pdfPCell.setBorderColorBottom(colorHAVR);
                            pdfPCell.setBorderColorRight(colorHAVR);
                            pdfPCell.setFixedHeight(15);
                            pdfPTable.addCell(pdfPCell);
                        }

                    }
                    listaPrecio.clear();
                    listaCantidad.clear();
                    listaImagenes.clear();
                    listaDescripcion.clear();
                    listaLink.clear();
                }
            }
            //paragraph.setSpacingBefore(6);
            //paragraph.add(pdfPTable);
            document.add(pdfPTable);
        } catch (
                Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    private void createFile(String nombreDoc) {
        //File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "PDF");
        File folder = new File("/data/data/com.ventas.havr.havrventas/PDFA");
        Log.d(TAG, "file:" + folder);
        folder.setReadable(true, false);
        folder.setWritable(true, false);
        folder.setExecutable(true, false);
        if (!folder.exists()) {
            folder.mkdirs();
            Log.d("CreateFILE", "Folder creado Antonio");
        } else {
            Log.d("CreateFILE", "El directorio existe Valerio");
        }
        pdfFile = new File(folder, nombreDoc + ".pdf");
        pdfFile.setReadable(true, false);
        pdfFile.setWritable(true, false);
        pdfFile.setExecutable(true, false);
        pdfFile.canRead();
        pdfFile.canExecute();
        pdfFile.canWrite();

    }

    public void closeDocument() {
        document.close();
    }

    public void addMetaData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void viewPDF(String nombreProyecto) {
        Intent intent = new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.putExtra("Nombre", nombreProyecto);
        intent.putExtra("tipoArchivo",1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}

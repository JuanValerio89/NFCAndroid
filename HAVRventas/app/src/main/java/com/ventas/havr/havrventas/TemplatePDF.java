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

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.Table;

import static com.itextpdf.text.html.HtmlTags.IMG;

public class TemplatePDF {

    private final static String TAG = "Template PDF";
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;

    private RealmResults<BaseImagenes> ResulstBaseImagenes;
    private BaseImagenes BaseImagenes;
    public Realm realm;

    private Font fEmpresa = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private Font fEmpresab = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private Font fTotal = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
    private Font TituloAzul = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLUE);
    private Font TituloBlanco14 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE);
    private Font TituloBlanco12 = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.WHITE);
    private Font fTitle = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE); //FF00269C
    private Font fTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK); //FF00269C
    private Font fCot = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED); //FF00269C
    private Font fSubtitle = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private Font fText = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    private Font fHighText = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);
    private BaseColor colorHAVR = new BaseColor(25, 118, 210);
    private BaseColor colorGris = new BaseColor(232, 232, 232);

    public TemplatePDF(Context context) {
        this.context = context;
    }

    private float tamañoImagenes = 50;

    public void openDocument() {
        createFile();
        realm.init(context);
        realm = Realm.getDefaultInstance();
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

    public void colocarHAVR(String fecha, String cotizacion) throws DocumentException, IOException {
        try {
            // Logo de HAVR
            Bitmap bmp = Glide.with(context).asBitmap().load("https://imagizer.imageshack.com/v2/640x480q90/923/urfjRg.png").submit().get();
            Bitmap bmpA = Bitmap.createScaledBitmap(
                    bmp, 175, 175, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmpA.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());

            PdfPTable pdfPTable = new PdfPTable(3);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setWidths(new float[]{25, 40, 30});
            PdfPCell pdfPCell;

            pdfPCell = new PdfPCell(new Phrase("H-AVR Electrónica", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(75);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPCell.setImage(image);
            pdfPTable.addCell(pdfPCell);
            pdfPTable.setSpacingAfter(15);

            String InfoCompleta = "H-AVR S.A de C.V." + "\n" +
                    "HAV150422Q19" + "\n" +
                    "Xocotitlan 6227, Col Aragón" + "\n" +
                    "Inguarán, Gustavo A. Madero" + "\n" +
                    "CDMX, C.P. 07820, México" + "\n" +
                    "Tel: 5567968483" + "\n" +
                    "Correo: ventas@h-avr.com";

            pdfPCell = new PdfPCell(new Phrase(InfoCompleta, fSubtitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            // Tabla de cotización
            PdfPTable tablaAnidadaReporto1 = new PdfPTable(1);
            tablaAnidadaReporto1.setWidthPercentage(99);
            tablaAnidadaReporto1.setWidths(new float[]{100});
            PdfPCell pdfPCellAnidada;

            pdfPCellAnidada = new PdfPCell(new Phrase("Cotización", fTitulo));
            pdfPCellAnidada.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCellAnidada.setVerticalAlignment(Element.ALIGN_CENTER);
            pdfPCellAnidada.setBackgroundColor(colorGris);
            pdfPCellAnidada.setBorderColor(BaseColor.WHITE);
            tablaAnidadaReporto1.addCell(pdfPCellAnidada);

            pdfPCellAnidada = new PdfPCell(new Phrase(cotizacion, fCot));
            pdfPCellAnidada.setVerticalAlignment(Element.ALIGN_CENTER);
            pdfPCellAnidada.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCellAnidada.setBorderColor(BaseColor.WHITE);
            tablaAnidadaReporto1.addCell(pdfPCellAnidada);

            pdfPCellAnidada = new PdfPCell(new Phrase("Fecha", fTitulo));
            pdfPCellAnidada.setVerticalAlignment(Element.ALIGN_CENTER);
            pdfPCellAnidada.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCellAnidada.setBackgroundColor(colorGris);
            pdfPCellAnidada.setBorderColor(BaseColor.WHITE);
            tablaAnidadaReporto1.addCell(pdfPCellAnidada);

            pdfPCellAnidada = new PdfPCell(new Phrase(fecha, fTitulo));
            pdfPCellAnidada.setVerticalAlignment(Element.ALIGN_CENTER);
            pdfPCellAnidada.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCellAnidada.setBorderColor(BaseColor.WHITE);
            tablaAnidadaReporto1.addCell(pdfPCellAnidada);
            pdfPCell.addElement(tablaAnidadaReporto1);
            pdfPTable.addCell(pdfPCell);

            document.add(pdfPTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void colocarHAVRInformacion(String NombreEmpresa, String telefono, String correo) throws DocumentException, IOException {
        try {
            PdfPTable pdfPTable = new PdfPTable(1);
            pdfPTable.setWidthPercentage(99);
            pdfPTable.setWidths(new float[]{100});
            PdfPCell pdfPCell;

            pdfPCell = new PdfPCell(new Phrase("Nombre: " + NombreEmpresa, fTitulo));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase("Teléfono: " + telefono, fTitulo));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase("Correo: " + correo, fTitulo));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            document.add(pdfPTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void colocarHAVRTabla() throws DocumentException, IOException {
        try {

            Drawable d = context.getResources().getDrawable(R.drawable.logo_app);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            Bitmap bmpA = Bitmap.createScaledBitmap(
                    bmp, 70, 70, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmpA.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAbsolutePosition(40f, 750f);

            //document.add(image);

            //Image ImageA = new Image(ImageDataFactory.create(IMG));
            PdfPTable pdfPTable = new PdfPTable(1);
            pdfPTable.setWidthPercentage(20);
            pdfPTable.setWidths(new float[]{20});
            pdfPTable.addCell(image);
            PdfPCell pdfPCell;
            pdfPCell = new PdfPCell(new Phrase("", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPCell.setImage(image);
            pdfPTable.addCell(pdfPCell);
            document.add(pdfPTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
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
        pdfFile = new File(folder, "CotizacionHAVR.pdf");
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


    public void createTableContacto() {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(5);
            pdfPTable.setWidthPercentage(60);
            pdfPTable.setWidths(new float[]{10, 10, 10, 10, 10});
            pdfPTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell pdfPCell;

            // Imagen 1
            Drawable d = context.getResources().getDrawable(R.drawable.ic_havr_whats);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            Bitmap bmpA = Bitmap.createScaledBitmap(
                    bmp, 120, 120, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmpA.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            PdfPCell cellImageInTable = new PdfPCell(image, true);
            cellImageInTable.setCellEvent(new LinkInCell(
                    "https://wa.me/525513410798"));
            cellImageInTable.setFixedHeight(tamañoImagenes);
            cellImageInTable.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(cellImageInTable);

            // Imagen 2
            Drawable da0 = context.getResources().getDrawable(R.drawable.ic_havr_facebook);
            BitmapDrawable bitDwa0 = ((BitmapDrawable) da0);
            Bitmap bmpa0 = bitDwa0.getBitmap();
            Bitmap bmpAa0 = Bitmap.createScaledBitmap(
                    bmpa0, 120, 120, true);
            ByteArrayOutputStream streama0 = new ByteArrayOutputStream();
            bmpAa0.compress(Bitmap.CompressFormat.PNG, 100, streama0);
            Image imagea0 = Image.getInstance(streama0.toByteArray());
            PdfPCell cellImageInTableA0 = new PdfPCell(imagea0, true);
            cellImageInTableA0.setCellEvent(new LinkInCell(
                    "https://www.facebook.com/havr.electronica"));
            cellImageInTableA0.setFixedHeight(tamañoImagenes);
            cellImageInTableA0.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(cellImageInTableA0);

            // Imagen 3
            Drawable da = context.getResources().getDrawable(R.drawable.ic_havr_mail);
            BitmapDrawable bitDwa = ((BitmapDrawable) da);
            Bitmap bmpa = bitDwa.getBitmap();
            Bitmap bmpAa = Bitmap.createScaledBitmap(
                    bmpa, 120, 120, true);
            ByteArrayOutputStream streama = new ByteArrayOutputStream();
            bmpAa.compress(Bitmap.CompressFormat.PNG, 100, streama);
            Image imagea = Image.getInstance(streama.toByteArray());
            PdfPCell cellImageInTableA = new PdfPCell(imagea, true);
            cellImageInTableA.setCellEvent(new LinkInCell(
                    "https://mail.google.com/mail/?view=cm&fs=1&to=ventas@h-avr.com" +
                            "&su=SUBJECT&body=BODY"));
            cellImageInTableA.setFixedHeight(tamañoImagenes);
            cellImageInTableA.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(cellImageInTableA);

            // Imagen 4
            Drawable da1 = context.getResources().getDrawable(R.drawable.ic_havr_youtube);
            BitmapDrawable bitDwa1 = ((BitmapDrawable) da1);
            Bitmap bmpa1 = bitDwa1.getBitmap();
            Bitmap bmpAa1 = Bitmap.createScaledBitmap(
                    bmpa1, 120, 120, true);
            ByteArrayOutputStream streama1 = new ByteArrayOutputStream();
            bmpAa1.compress(Bitmap.CompressFormat.PNG, 100, streama1);
            Image imagea1 = Image.getInstance(streama1.toByteArray());
            PdfPCell cellImageInTableA1 = new PdfPCell(imagea1, true);
            cellImageInTableA1.setCellEvent(new LinkInCell(
                    "https://www.youtube.com/channel/UC3LffcvivoPw_K1AYWarOOA"));
            cellImageInTableA1.setFixedHeight(tamañoImagenes);
            cellImageInTableA1.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(cellImageInTableA1);

            // Imagen 5
            Drawable da1a = context.getResources().getDrawable(R.drawable.ic_havr_instagram);
            BitmapDrawable bitDwa1a = ((BitmapDrawable) da1a);
            Bitmap bmpa1a = bitDwa1a.getBitmap();
            Bitmap bmpAa1a = Bitmap.createScaledBitmap(
                    bmpa1a, 120, 120, true);
            ByteArrayOutputStream streama1a = new ByteArrayOutputStream();
            bmpAa1a.compress(Bitmap.CompressFormat.PNG, 100, streama1a);
            Image imagea1a = Image.getInstance(streama1a.toByteArray());
            PdfPCell cellImageInTableA1a = new PdfPCell(imagea1a, true);
            cellImageInTableA1a.setCellEvent(new LinkInCell(
                    "https://www.instagram.com/havr_electronica/"));
            cellImageInTableA1a.setFixedHeight(tamañoImagenes);
            cellImageInTableA1a.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(cellImageInTableA1a);

            paragraph.add(pdfPTable);
            paragraph.setSpacingAfter(10);
            paragraph.setSpacingBefore(10);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("Fallo tabla", e.toString());
        }
    }

    public void createTableCuadroInferior() {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(2);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setWidths(new float[]{10, 90});
            pdfPTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell pdfPCell;

            // Imagen 6
            pdfPCell = new PdfPCell(new Phrase("", fSubtitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            Drawable cuadroA = context.getResources().getDrawable(R.drawable.cuadro_inferior);
            BitmapDrawable bitCuadro = ((BitmapDrawable) cuadroA);
            Bitmap bmpCuadro = bitCuadro.getBitmap();
            Bitmap bmpCuadroA = Bitmap.createScaledBitmap(
                    bmpCuadro, 480, 240, true);
            ByteArrayOutputStream streamCuadro = new ByteArrayOutputStream();
            bmpCuadroA.compress(Bitmap.CompressFormat.PNG, 100, streamCuadro);
            Image imageCuadro = Image.getInstance(streamCuadro.toByteArray());
            PdfPCell cellImageInTableCuadro = new PdfPCell(imageCuadro, true);
            cellImageInTableCuadro.setCellEvent(new LinkInCell(
                    "https://www.h-avr.mx"));
            cellImageInTableCuadro.setFixedHeight(100);
            cellImageInTableCuadro.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(cellImageInTableCuadro);

            paragraph.add(pdfPTable);
            paragraph.setSpacingAfter(10);
            paragraph.setSpacingBefore(10);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("Fallo tabla", e.toString());
        }
    }

    static class LinkInCell implements PdfPCellEvent {
        protected String url;

        public LinkInCell(String url) {
            this.url = url;
        }

        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
            PdfWriter writer = canvases[0].getPdfWriter();
            PdfAction action = new PdfAction(url);
            PdfAnnotation link = PdfAnnotation.createLink(
                    writer, position, PdfAnnotation.HIGHLIGHT_INVERT, action);
            writer.addAnnotation(link);
        }
    }


    public void addParagraph(String text) {
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(10);
            paragraph.setSpacingBefore(10);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void addSpace(int distancia) {
        try {
            paragraph = new Paragraph();
            paragraph.setSpacingAfter(distancia);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("Agregando la aplicación", e.toString());
        }

    }

    // Crea la tabla principal - donde estan los precios, imagenes, subtotal por articulo
    public void createTable(String[] header, ArrayList<String[]> clients) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setWidths(new float[]{5, 10, 26, 14, 11, 18, 18});

            PdfPCell pdfPCell = null;
            int indexC = 0;
            while (indexC < header.length) {
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], fTitle));
                pdfPCell.setFixedHeight(30);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfPCell.setMinimumHeight(30);
                pdfPCell.setBackgroundColor(colorHAVR);
                pdfPTable.addCell(pdfPCell);
            }
            for (int indexRow = 0; indexRow < clients.size(); indexRow++) {
                String[] row = clients.get(indexRow);
                for (indexC = 0; indexC < header.length; indexC++) {
                    if (indexC != 1) {
                        pdfPCell = new PdfPCell(new Phrase(row[indexC], fText));
                        pdfPCell.setFixedHeight(50);
                    }
                    if (indexC == 1) {
                        Bitmap bmp = Glide.with(context).asBitmap().
                                load("https://imagizer.imageshack.com/v2/640x480q90/923/9Tgdbh.png").submit().get();
                        try {
                            BaseImagenes = realm.where(BaseImagenes.class).equalTo("SKU", row[3]).findFirst();
                            bmp = Glide.with(context).asBitmap().load(BaseImagenes.getLink()).submit().get();
                        } catch (Exception e) {
                            Log.d(TAG, "Error al leer la base de datos");
                            bmp = Glide.with(context).asBitmap().
                                    load("https://imagizer.imageshack.com/v2/640x480q90/923/tKIHkA.png").submit().get();
                        }

                        Bitmap bmpA = Bitmap.createScaledBitmap(
                                bmp, 280, 200, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        pdfPCell.setPaddingTop(7);
                        bmpA.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        Image image = Image.getInstance(stream.toByteArray());
                        pdfPCell.setImage(image);
                    } else
                        pdfPCell.setPaddingTop(15);
                    // Ponemos el color a la celda
                    if (indexRow % 2 == 0) {
                        pdfPCell.setBackgroundColor(colorGris);
                    }
                    pdfPCell.setBorderColor(BaseColor.WHITE);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTable.addCell(pdfPCell);
                }
            }
            paragraph.setSpacingBefore(6);
            paragraph.add(pdfPTable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void colocarLogo() throws DocumentException, IOException {
        try {
            Drawable d = context.getResources().getDrawable(R.drawable.ic_actualizar_a);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            Bitmap bmpA = Bitmap.createScaledBitmap(
                    bmp, 150, 60, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmpA.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAbsolutePosition(45f, 780f);
            document.add(image);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createTableTotal(String totalPrecio, String IVA, String Subtotal) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(3);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setWidths(new float[]{64, 18, 18});
            pdfPTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPCell pdfPCell;
            pdfPCell = new PdfPCell(new Phrase("", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase("Subtotal", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCell.setBackgroundColor(colorGris);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase(Subtotal, fTotal));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.WHITE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase("", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase("I.V.A.", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase(IVA, fTotal));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.WHITE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase("", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase("Total", fText));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pdfPCell.setBackgroundColor(colorGris);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase(totalPrecio, fTotal));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.WHITE);
            pdfPCell.setBorderColor(BaseColor.WHITE);
            pdfPTable.addCell(pdfPCell);

            paragraph.add(pdfPTable);
            paragraph.setSpacingAfter(2);
            paragraph.setSpacingBefore(2);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void viewPDF(String nombreProyecto, String nombreArchivo,int TipoArchivo) {
        Intent intent = new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.putExtra("Nombre", nombreProyecto);
        intent.putExtra("nomCot", nombreArchivo);
        intent.putExtra("tipoArchivo",TipoArchivo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}

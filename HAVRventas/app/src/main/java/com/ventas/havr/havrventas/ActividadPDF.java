package com.ventas.havr.havrventas;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class ActividadPDF extends AppCompatActivity implements  OnPageChangeListener, OnLoadCompleteListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "tutorial1.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_pdf);
        Intent intento = getIntent();
        int Tutorial = intento.getIntExtra("Tutorial",0);
        pdfView= (PDFView)findViewById(R.id.pdfView);
        Log.d(TAG,"Tutorial:"+Tutorial);
        switch (Tutorial){
            case 0:
                displayFromAsset("montaje_caja_arduino_mega_.pdf");
                break;
            case 1:
                displayFromAsset("cargador_bateria.pdf");
                break;
            case 2:
                displayFromAsset("mlx90614_peltier.pdf");
                break;
            case 3:
                displayFromAsset("pam8403_bluetooth.pdf");
                break;
            case 4:
                displayFromAsset("w1209.pdf");
                break;
            case 5:
                displayFromAsset("pantalla_oled.pdf");
                break;
            case 6:
                displayFromAsset("cargador_bateria.pdf");
                break;
        }


    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(pdfFileName)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10)
                .load();
    }


    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }



    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}

package com.ventas.havr.havrventas.Modelos;

public class ObtenerImagenes {
    private String linkImagen;
    private String linkMercado;
    private String linkPDF;
    public ObtenerImagenes() {
        //public no-arg constructor needed
    }

    public ObtenerImagenes(String linkImagen, String linkMercado, String linkPDF) {
        this.linkImagen = linkImagen;
        this.linkMercado = linkMercado;
        this.linkPDF = linkPDF;
    }

    public String getLinkImagen() {
        return linkImagen;
    }
    public String getLinkMercado() {
        return linkMercado;
    }
    public String getLinkPDF() {
        return linkPDF;
    }
}

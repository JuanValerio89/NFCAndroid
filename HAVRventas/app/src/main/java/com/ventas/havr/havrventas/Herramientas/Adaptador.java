package com.ventas.havr.havrventas.Herramientas;

import java.util.List;

public class Adaptador {

    private String nombre;
    private List list;
    private int img;

    public Adaptador(String nombre, int img, List lista) {
        this.nombre = nombre;
        this.img = img;
        this.list = lista;
    }

    public String getNombre() {
        return nombre;}

    public int getImg(){
        return img;}


}

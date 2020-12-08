package com.ventas.havr.havrventas.Modelos;

import com.ventas.havr.havrventas.app.MyAplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BaseSKU extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String SKU;
    @Required
    private String Descripcion;
    @Required
    private String Cantidad;
    @Required
    private String Precio;
    @Required
    private String PrecioPublico;

    public BaseSKU(){
    }

    public BaseSKU(String SKU, String descripcion, String Cantidad, String Precio, String PrecioPublico){
        this.id = MyAplication.BaseSKU.incrementAndGet();
        this.SKU = SKU;
        this.Descripcion = descripcion;
        this.Cantidad = Cantidad;
        this.Precio = Precio;
        this.PrecioPublico = PrecioPublico;
    }

    public int getId() {
        return id;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
    }

    public String getPrecio() {
        return Precio;
    }

    public String getPrecioPublico() {
        return PrecioPublico;
    }

    public void setPrecioPublico(String precioPublico) {
        PrecioPublico = precioPublico;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }
}

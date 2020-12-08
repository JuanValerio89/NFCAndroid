package com.ventas.havr.havrventas.Modelos;

import com.ventas.havr.havrventas.app.MyAplication;

import io.realm.Case;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BasePedidos extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String SKU;
    @Required
    private String Descripcion;
    @Required
    private String Precio;
    @Required
    private String Cantidad;


    public BasePedidos(){

    }

    public BasePedidos(String SKU, String Descripcion, String Precio, String cantidad){
        this.id = MyAplication.BasePedidos.incrementAndGet();
        this.SKU = SKU;
        this.Descripcion = Descripcion;
        this.Precio = Precio;
        this.Cantidad = cantidad;
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

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String numeroCotizacion) {
        Cantidad = numeroCotizacion;
    }
}

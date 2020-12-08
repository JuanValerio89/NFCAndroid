package com.ventas.havr.havrventas.Modelos;

import com.ventas.havr.havrventas.app.MyAplication;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BaseCotizaciones extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String Cotizacion;
    @Required
    private String TotalPrecio;
    @Required
    private Date CreatedDate;

    public BaseCotizaciones(){
    }

    private RealmList<BasePedidos> basePedidos;

    public BaseCotizaciones(String Cotizacion, String TotalPrecio){
        this.id = MyAplication.BaseCotizaciones.incrementAndGet();
        this.Cotizacion = Cotizacion;
        this.TotalPrecio = TotalPrecio;
        this.CreatedDate = new Date();
        this.basePedidos = new RealmList<BasePedidos>();
    }

    public int getId() {
        return id;
    }

    public String getCotizacion() {
        return Cotizacion;
    }

    public void setCotizacion(String cotizacion) {
        Cotizacion = cotizacion;
    }

    public String getTotalPrecio() {
        return TotalPrecio;
    }

    public void setTotalPrecio(String totalPrecio) {
        TotalPrecio = totalPrecio;
    }

    public Date getCreatedDate() {
        return CreatedDate;
    }

    public RealmList<BasePedidos> getBasePedidos() {
        return basePedidos;
    }
}

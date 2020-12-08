package com.ventas.havr.havrventas.Modelos;

import com.ventas.havr.havrventas.app.MyAplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BaseMasVendidos extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String SKU;
    @Required
    private String Otro;

    public BaseMasVendidos(){

    }

    public BaseMasVendidos(String SKU, String otro){
        this.id = MyAplication.BasemasVendidos.incrementAndGet();
        this.SKU = SKU;
        this.Otro = otro;
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

    public String getOtro() {
        return Otro;
    }

    public void setOtro(String otro) {
        Otro = otro;
    }
}

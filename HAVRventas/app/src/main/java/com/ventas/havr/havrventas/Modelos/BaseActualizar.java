package com.ventas.havr.havrventas.Modelos;
import com.ventas.havr.havrventas.app.MyAplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BaseActualizar extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String SKU;

    private boolean estado;

    public BaseActualizar(){
    }

    public BaseActualizar(String SKU, boolean estado){
        this.id = MyAplication.BaseActualizar.incrementAndGet();
        this.SKU = SKU;
        this.estado = estado;
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
    public boolean getEstado() {
        return estado;
    }
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}

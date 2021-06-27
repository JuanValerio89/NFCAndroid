package com.ventas.havr.havrventas.app;

import android.app.Application;

import com.ventas.havr.havrventas.Modelos.BaseCotizaciones;
import com.ventas.havr.havrventas.Modelos.BaseImagenes;
import com.ventas.havr.havrventas.Modelos.BaseMasVendidos;
import com.ventas.havr.havrventas.Modelos.BaseSKU;
import com.ventas.havr.havrventas.Modelos.BasePedidos;
import com.ventas.havr.havrventas.Modelos.BaseUsuarios;
import com.ventas.havr.havrventas.Modelos.BaseActualizar;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyAplication extends Application {

    public static AtomicInteger BaseSKU = new AtomicInteger();
    public static AtomicInteger BaseCotizaciones = new AtomicInteger();
    public static AtomicInteger Baseimagenes = new AtomicInteger();
    public static AtomicInteger BasePedidos = new AtomicInteger();
    public static AtomicInteger BasemasVendidos = new AtomicInteger();
    public static AtomicInteger Baseusuarios = new AtomicInteger();
    public static AtomicInteger BaseActualizar = new AtomicInteger();

    @Override
    public void onCreate() {
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        BaseSKU = getIdByTable(realm, BaseSKU.class);
        BaseCotizaciones = getIdByTable(realm, BaseCotizaciones.class);
        Baseimagenes = getIdByTable(realm, BaseImagenes.class);
        BasePedidos = getIdByTable(realm, BasePedidos.class);
        BasemasVendidos = getIdByTable(realm, BaseMasVendidos.class);
        Baseusuarios = getIdByTable(realm, BaseUsuarios.class);
        BaseActualizar = getIdByTable(realm, BaseActualizar.class);

        realm.close();
        super.onCreate();
    }

    private void setUpRealmConfig(){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();

    }
}

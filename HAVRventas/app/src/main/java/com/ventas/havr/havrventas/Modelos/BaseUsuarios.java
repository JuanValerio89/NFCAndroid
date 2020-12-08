package com.ventas.havr.havrventas.Modelos;

import com.ventas.havr.havrventas.app.MyAplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BaseUsuarios extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String Usuario;
    @Required
    private String Password;
    @Required
    private String Telefono;
    @Required
    private String Correo;

    public BaseUsuarios(){

    }

    public BaseUsuarios(String user, String pass, String tel, String mail) {
        this.id = MyAplication.Baseusuarios.incrementAndGet();
        this.Usuario = user;
        this.Password = pass;
        this.Telefono = tel;
        this.Correo = mail;
    }

    public int getId() {
        return id;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }
}

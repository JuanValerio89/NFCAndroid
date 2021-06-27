package com.ventas.havr.havrventas.Adaptadores;

import com.google.firebase.Timestamp;

public class CrearUsuarios {
    private String email;
    private String password;
    private String telefono;
    private Timestamp fechaAlta;

    public CrearUsuarios() {
        //public no-arg constructor needed
    }

    public CrearUsuarios(String email, String password, String telefono, Timestamp fechaAlta) {
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.fechaAlta = fechaAlta;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getTelefono() {
        return telefono;
    }
    public Timestamp getFechaAlta(){
        return fechaAlta;
    }

}
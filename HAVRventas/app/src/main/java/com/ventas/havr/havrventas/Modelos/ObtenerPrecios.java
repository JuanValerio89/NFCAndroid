package com.ventas.havr.havrventas.Modelos;


public class ObtenerPrecios {
    private int cantidad;
    private String descripcion;
    private String precioPublico;
    private String precioTienda;
    public ObtenerPrecios() {
        //public no-arg constructor needed
    }

    public ObtenerPrecios(int cantidad, String descripcion, String precioPublico, String precioTienda) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.precioPublico = precioPublico;
        this.precioTienda = precioTienda;
    }

    public int getCantidad() {
        return cantidad;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getPrecioPublico() {
        return precioPublico;
    }
    public String getPrecioTienda() {
        return precioTienda;
    }
}
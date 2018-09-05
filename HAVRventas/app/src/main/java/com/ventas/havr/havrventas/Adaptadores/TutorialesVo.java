package com.ventas.havr.havrventas.Adaptadores;

public class TutorialesVo {

    private String titulo;
    private String descripcion;
    private int foto;

    public TutorialesVo(){

    }

    public TutorialesVo(String titulo, String descripcion, int foto) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.foto = foto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        descripcion = descripcion;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }
}

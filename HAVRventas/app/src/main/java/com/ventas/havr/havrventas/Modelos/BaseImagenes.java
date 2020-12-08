package com.ventas.havr.havrventas.Modelos;

import com.ventas.havr.havrventas.app.MyAplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class BaseImagenes extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String SKU;
    @Required
    private String Link;
    @Required
    private String LinkML;
    @Required
    private String LinkKK;
    @Required
    private String LinkPDF;

    public BaseImagenes(){

    }

    public BaseImagenes(String SKU, String Link, String Linkml, String Linkkk, String Linkpdf){
        this.id = MyAplication.Baseimagenes.incrementAndGet();
        this.SKU = SKU;
        this.Link = Link;
        this.LinkKK = Linkkk;
        this.LinkML = Linkml;
        this.LinkPDF = Linkpdf;
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

    public String getLink() {
        return Link;
    }

    public String getLinkML() {
        return LinkML;
    }

    public void setLinkML(String linkML) {
        LinkML = linkML;
    }

    public String getLinkKK() {
        return LinkKK;
    }

    public void setLinkKK(String linkKK) {
        LinkKK = linkKK;
    }

    public String getLinkPDF() {
        return LinkPDF;
    }

    public void setLinkPDF(String linkPDF) {
        LinkPDF = linkPDF;
    }

    public void setLink(String link) {
        Link = link;
    }
}

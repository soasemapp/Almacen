package com.almacen.alamacen202.SetterandGetters;

public class Inventario {
    private String num;
    private String producto;
    private String cantidad;
    private String escan;
    private String ubi;
    private boolean sincronizado;

    public Inventario(String  num, String producto, String cantidad,String escan,String ubi,boolean sincronizado) {
        this.num = num;
        this.producto = producto;
        this.cantidad = cantidad;
        this.escan=escan;
        this.ubi=ubi;
        this.sincronizado=sincronizado;
    }//constructor

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getEscan() {
        return escan;
    }

    public void setEscan(String escan) {
        this.escan = escan;
    }

    public String getUbi() {
        return ubi;
    }

    public void setUbi(String ubi) {
        this.ubi = ubi;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}//clase

package com.almacen.alamacen202.SetterandGetters;

public class Inventario {
    private String num;
    private String producto;
    private String cantidad;
    private String escan;

    public Inventario(String  num, String producto, String cantidad,String escan) {
        this.num = num;
        this.producto = producto;
        this.cantidad = cantidad;
        this.escan=escan;
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
}//clase

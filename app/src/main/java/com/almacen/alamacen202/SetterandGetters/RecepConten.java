package com.almacen.alamacen202.SetterandGetters;

public class RecepConten {
    private String num;
    private String producto;
    private String cantidad;
    private String prioridad;
    private String folio;
    private String palet;

    public RecepConten(String num, String producto, String cantidad, String prioridad, String folio,String palet) {
        this.num = num;
        this.producto = producto;
        this.cantidad = cantidad;
        this.prioridad = prioridad;
        this.folio = folio;
        this.palet=palet;
    }

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

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getPalet() {
        return palet;
    }

    public void setPalet(String palet) {
        this.palet = palet;
    }
}//clase

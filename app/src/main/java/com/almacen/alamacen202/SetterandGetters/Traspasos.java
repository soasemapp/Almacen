package com.almacen.alamacen202.SetterandGetters;

public class Traspasos {
    private String num;
    private String producto;
    private String cantidad;
    private String ubic;
    private String cantSurt;
    private String exist;
    private boolean sincronizado;

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

    public String getUbic() {
        return ubic;
    }

    public void setUbic(String ubic) {
        this.ubic = ubic;
    }

    public String getCantSurt() {
        return cantSurt;
    }

    public void setCantSurt(String cantSurt) {
        this.cantSurt = cantSurt;
    }

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public Traspasos(String num, String producto, String cantidad, String ubic, String cantSurt,String exist, boolean sincronizado) {

        this.num = num;
        this.producto = producto;
        this.cantidad = cantidad;
        this.ubic = ubic;
        this.cantSurt = cantSurt;
        this.exist=exist;
        this.sincronizado = sincronizado;
    }
}//clase

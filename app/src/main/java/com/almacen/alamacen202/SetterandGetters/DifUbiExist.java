package com.almacen.alamacen202.SetterandGetters;

public class DifUbiExist {
    private String num;
    private String producto;
    private String cantidad;
    private String existencia;
    private String diferencia;
    private String ubicacion;
    private String conteo;
    private String estatus;

    public DifUbiExist(String num, String producto, String cantidad, String existencia,
                       String diferencia, String ubicacion, String conteo,String estatus) {
        this.num = num;
        this.producto = producto;
        this.cantidad = cantidad;
        this.existencia = existencia;
        this.diferencia = diferencia;
        this.ubicacion = ubicacion;
        this.conteo = conteo;
        this.estatus=estatus;
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

    public String getExistencia() {
        return existencia;
    }

    public void setExistencia(String existencia) {
        this.existencia = existencia;
    }

    public String getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(String diferencia) {
        this.diferencia = diferencia;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getConteo() {
        return conteo;
    }

    public void setConteo(String conteo) {
        this.conteo = conteo;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}//clase

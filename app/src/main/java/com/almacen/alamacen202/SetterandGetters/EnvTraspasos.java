package com.almacen.alamacen202.SetterandGetters;

public class EnvTraspasos {
    private String num;
    private String producto;
    private String ubic;
    private String existencia;
    private String cantidad;
    private String partida;
    private String cantSurt;
    private String almEnv;
    private String linea;
    boolean sincronizado;

    public EnvTraspasos(String num, String producto, String ubic, String existencia,
                        String cantidad, String partida, String cantSurt,String almEnv,String linea,boolean sincronizado) {
        this.num = num;
        this.producto = producto;
        this.ubic = ubic;
        this.existencia = existencia;
        this.cantidad = cantidad;
        this.partida = partida;
        this.cantSurt = cantSurt;
        this.almEnv=almEnv;
        this.linea=linea;
        this.sincronizado=sincronizado;
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

    public String getUbic() {
        return ubic;
    }

    public void setUbic(String ubic) {
        this.ubic = ubic;
    }

    public String getExistencia() {
        return existencia;
    }

    public void setExistencia(String existencia) {
        this.existencia = existencia;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getCantSurt() {
        return cantSurt;
    }

    public void setCantSurt(String cantSurt) {
        this.cantSurt = cantSurt;
    }

    public String getAlmEnv() {
        return almEnv;
    }

    public void setAlmEnv(String almEnv) {
        this.almEnv = almEnv;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}//clase

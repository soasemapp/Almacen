package com.almacen.alamacen202.SetterandGetters;

public class DetUbi {
    private String ubicacion;
    private String cant;
    private String max;
    private String min;
    private String punt;
    private String clasf;

    public DetUbi(String ubicacion, String cant, String max, String min, String punt,String clasf) {
        this.ubicacion = ubicacion;
        this.cant = cant;
        this.max = max;
        this.min = min;
        this.punt = punt;
        this.clasf=clasf;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCant() {
        return cant;
    }

    public void setCant(String cant) {
        this.cant = cant;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getPunt() {
        return punt;
    }

    public void setPunt(String punt) {
        this.punt = punt;
    }

    public String getClasf() {
        return clasf;
    }

    public void setClasf(String clasf) {
        this.clasf = clasf;
    }
}//clase

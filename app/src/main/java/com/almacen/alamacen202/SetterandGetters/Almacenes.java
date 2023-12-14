package com.almacen.alamacen202.SetterandGetters;

public class Almacenes {
    private String Almacen;
    private String NomAlm;
    private String Exist;

    public Almacenes(String almacen, String nomAlm, String exist) {
        Almacen = almacen;
        NomAlm = nomAlm;
        Exist = exist;
    }

    public String getAlmacen() {
        return Almacen;
    }

    public void setAlmacen(String almacen) {
        Almacen = almacen;
    }

    public String getNomAlm() {
        return NomAlm;
    }

    public void setNomAlm(String nomAlm) {
        NomAlm = nomAlm;
    }

    public String getExist() {
        return Exist;
    }

    public void setExist(String exist) {
        Exist = exist;
    }
}//clase

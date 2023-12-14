package com.almacen.alamacen202.SetterandGetters;

public class RecepListSucCont {
    private String sucursal;
    private String clasif;
    private String exist;
    private String compr;
    private String trans;
    private String dem;

    public RecepListSucCont(String sucursal, String clasif, String exist, String compr, String trans, String dem) {
        this.sucursal = sucursal;
        this.clasif = clasif;
        this.exist = exist;
        this.compr = compr;
        this.trans = trans;
        this.dem = dem;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getClasif() {
        return clasif;
    }

    public void setClasif(String clasif) {
        this.clasif = clasif;
    }

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }

    public String getCompr() {
        return compr;
    }

    public void setCompr(String compr) {
        this.compr = compr;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getDem() {
        return dem;
    }

    public void setDem(String dem) {
        this.dem = dem;
    }
}

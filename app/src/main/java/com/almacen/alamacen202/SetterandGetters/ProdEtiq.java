package com.almacen.alamacen202.SetterandGetters;

public class ProdEtiq {
    private String num;
    private String prod;
    private String descrip;

    public ProdEtiq(String num, String prod, String descrip) {
        this.num = num;
        this.prod = prod;
        this.descrip = descrip;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }
}//clase

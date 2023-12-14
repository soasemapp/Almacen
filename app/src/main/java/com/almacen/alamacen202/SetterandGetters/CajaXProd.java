package com.almacen.alamacen202.SetterandGetters;

public class CajaXProd {
    private String caja;
    private String cant;

    public CajaXProd(String caja, String cant) {
        this.caja = caja;
        this.cant = cant;
    }

    public String getCaja() {
        return caja;
    }

    public void setCaja(String caja) {
        this.caja = caja;
    }

    public String getCant() {
        return cant;
    }

    public void setCant(String cant) {
        this.cant = cant;
    }
}

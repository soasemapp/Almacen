package com.almacen.alamacen202.XML;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class XMLRepEtiq extends SoapSerializationEnvelope {

    private String usuario;
    private String contrasena;
    private String producto;
    private String sucursal;
    private String cont;

    public XMLRepEtiq(int version) {
        super(version);
    }

    public void XMLRep(String usuario, String contrasena,String producto,String sucursal,String cont) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.producto = producto;
        this.sucursal = sucursal;
        this.cont = cont;
    }//void

    @Override
    public void write(XmlSerializer writer) throws IOException {
        env = "http://schemas.xmlsoap.org/soap/envelope/";
        String tem = "";
        writer.startDocument("UTF-8", true);
        writer.setPrefix("soap", env);
        writer.setPrefix("", tem);
        writer.startTag(env, "Envelope");
        writer.startTag(env, "Body");
        writer.startTag(tem, "ReporteEtiqRequest");


        writer.startTag(tem, "Login");
        writer.startTag(tem, "user");
        writer.text(usuario);
        writer.endTag(tem, "user");
        writer.startTag(tem, "pass");
        writer.text(contrasena);
        writer.endTag(tem, "pass");
        writer.endTag(tem, "Login");


        writer.startTag(tem, "ReporteEtiqq");

        writer.startTag(tem, "k_prod");
        writer.text(producto);
        writer.endTag(tem, "k_prod");

        writer.startTag(tem, "k_suc");
        writer.text(sucursal);
        writer.endTag(tem, "k_suc");

        writer.startTag(tem, "k_usu");
        writer.text(usuario);
        writer.endTag(tem, "k_usu");

        writer.startTag(tem, "k_cont");
        writer.text(cont);
        writer.endTag(tem, "k_cont");

        writer.endTag(tem, "ReporteEtiqq");


        writer.endTag(tem, "ReporteEtiqRequest");
        writer.endTag(env, "Body");
        writer.endTag(env, "Envelope");
        writer.endDocument();

    }//vrite
}

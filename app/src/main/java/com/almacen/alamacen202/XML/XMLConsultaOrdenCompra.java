package com.almacen.alamacen202.XML;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class XMLConsultaOrdenCompra extends SoapSerializationEnvelope {

    private String usuario     ="";
    private String contrasena  ="";
    private String sucursal    ="";
    private String folio       ="";

    public XMLConsultaOrdenCompra(int version) {
        super(version);
    }

    public void XMLConsultaOrd(String usuario, String contrasena,
                                  String sucursal, String folio) {
        this.usuario    = usuario;
        this.contrasena = contrasena;
        this.sucursal   = sucursal;
        this.folio      = folio;
    }//public void XMLConsultaOrdenCompra

    @Override
    public void write(XmlSerializer writer) throws IOException {
        env = "http://schemas.xmlsoap.org/soap/envelope/";
        String tem = "";
        writer.startDocument("UTF-8", true);
        writer.setPrefix("soap", env);
        writer.setPrefix("", tem);
        writer.startTag(env, "Envelope");
        writer.startTag(env, "Body");
        writer.startTag(tem, "ConsulOrdCompRequest");


        writer.startTag(tem, "Login");
        writer.startTag(tem, "user");
        writer.text(usuario);
        writer.endTag(tem, "user");
        writer.startTag(tem, "pass");
        writer.text(contrasena);
        writer.endTag(tem, "pass");
        writer.endTag(tem, "Login");


        writer.startTag(tem, "OrdComp");
        writer.startTag(tem, "k_suc");
        writer.text(sucursal);
        writer.endTag(tem, "k_suc");

        writer.startTag(tem, "k_folio");
        writer.text(folio);
        writer.endTag(tem, "k_folio");

        writer.endTag(tem, "OrdComp");


        writer.endTag(tem, "ConsulOrdCompRequest");
        writer.endTag(env, "Body");
        writer.endTag(env, "Envelope");
        writer.endDocument();

    }//vrite
}

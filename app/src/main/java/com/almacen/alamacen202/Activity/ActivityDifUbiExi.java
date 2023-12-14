package com.almacen.alamacen202.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorListAlmacenes;
import com.almacen.alamacen202.Adapter.AdaptadorListaFolios;
import com.almacen.alamacen202.Adapter.AdaptadorListaFolios2;
import com.almacen.alamacen202.Adapter.AdapterDifUbiExi;
import com.almacen.alamacen202.Adapter.AdapterInventario;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Almacenes;
import com.almacen.alamacen202.SetterandGetters.DifUbiExist;
import com.almacen.alamacen202.SetterandGetters.Folios;
import com.almacen.alamacen202.SetterandGetters.Inventario;
import com.almacen.alamacen202.SetterandGetters.ProdEtiq;
import com.almacen.alamacen202.SetterandGetters.RecepConten;
import com.almacen.alamacen202.SetterandGetters.RecepListSucCont;
import com.almacen.alamacen202.SetterandGetters.UbicacionSandG;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.XML.XMDifUbiExist;
import com.almacen.alamacen202.XML.XMLActualizaDif;
import com.almacen.alamacen202.XML.XMLActualizaInv;
import com.almacen.alamacen202.XML.XMLFolios;
import com.almacen.alamacen202.XML.XMLUbicacionAlma;
import com.almacen.alamacen202.XML.XMLValidEsc;
import com.almacen.alamacen202.XML.XMLlistInv;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;

import dmax.dialog.SpotsDialog;

public class ActivityDifUbiExi extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private TextView tvEstatus;
    private SharedPreferences preference,preferenceD;
    private SharedPreferences.Editor editor;
    private int posicion=0;
    public static final int MY_DEFAULT_TIMEOUT = 15000;
    private String strusr,strpass,strServer,strbran,codeBar,ProductoAct="",folio="",fecha="",hora="",mensaje,serv="",where=" AND CONTEO>0 ";
    private ArrayList<DifUbiExist> lista2 = new ArrayList<>();
    private ArrayList<DifUbiExist> listaPSincro = new ArrayList<>();
    private ArrayList<Almacenes> listaAlm = new ArrayList<>();
    private EditText txtFolioInv,txtProductoVi,txtFechaI,txtHoraI,txtProducto,txtCant,txtContF,txtExistS,txtDif,txtUbb;
    private ArrayList<Folios>listaFol;
    private Button btnGuardar,btnSincronizar,btnCont,btnNoCont,btnAlma,btnRefr;
    private CheckBox chbMan;
    private RecyclerView rvDifUbiExi;
    private AdapterDifUbiExi adapter;
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private ConexionSQLiteHelper conn;
    private SQLiteDatabase db;
    private RecyclerView rvFolios;//para alertdialog
    private AlertDialog dialog;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dif_ubi_exi);

        MyToolbar.show(this, "Diferencia Ubic. Exist.", true);
        preferenceD = getSharedPreferences("FolioDif", Context.MODE_PRIVATE);//para guardar folio
        editor = preferenceD.edit();

        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);

        folio=preferenceD.getString("folio", "");
        fecha=preferenceD.getString("fechaI", "");
        hora=preferenceD.getString("horaI", "");
        mQueue = Volley.newRequestQueue(this);

        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strServer = preference.getString("Server", "null");
        strbran = preference.getString("codBra", "null");
        codeBar = preference.getString("codeBar", "null");
        mDialog = new SpotsDialog.Builder().setContext(ActivityDifUbiExi.this).
                setMessage("Espere un momento...").build();
        progressDialog = new ProgressDialog(ActivityDifUbiExi.this);//parala barra de
        progressDialog.setMessage("Procesando....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        switch (strServer) {
            case "sprautomotive.servehttp.com:9090":
                serv="RODATECH";
                break;
            case "sprautomotive.servehttp.com:9095":
                serv="PARTECH";
                break;
            case "sprautomotive.servehttp.com:9080":
                serv="TG";
                break;
        }

        tvEstatus       = findViewById(R.id.tvEstatus);
        txtFolioInv     = findViewById(R.id.txtFolioInv);
        txtFechaI       = findViewById(R.id.txtFechaI);
        txtHoraI        = findViewById(R.id.txtHoraI);
        txtProducto     = findViewById(R.id.txtProducto);
        txtProductoVi   = findViewById(R.id.txtProductoVi);
        txtCant         = findViewById(R.id.txtCant);
        btnGuardar      = findViewById(R.id.btnGuardar);
        btnSincronizar  = findViewById(R.id.btnSincronizar);
        chbMan          = findViewById(R.id.chbMan);
        rvDifUbiExi     = findViewById(R.id.rvDifUbiExi);
        txtContF        = findViewById(R.id.txtContF);
        txtExistS       = findViewById(R.id.txtExistS);
        txtDif          = findViewById(R.id.txtDif);
        txtUbb          = findViewById(R.id.txtUbb);
        btnCont         = findViewById(R.id.btnCont);
        btnNoCont       = findViewById(R.id.btnNoCont);
        btnAlma         = findViewById(R.id.btnAlma);
        btnRefr         = findViewById(R.id.btnRefr);

        conn = new ConexionSQLiteHelper(ActivityDifUbiExi.this, "bd_INVENTARIO", null, 1);
        db = conn.getReadableDatabase();
        rvDifUbiExi.setLayoutManager(new LinearLayoutManager(ActivityDifUbiExi.this));
        keyboard = (InputMethodManager) getSystemService(ActivityInventario.INPUT_METHOD_SERVICE);

        txtProducto.requestFocus();
        txtProducto.setInputType(InputType.TYPE_NULL);
        txtCant.setEnabled(false);
        //BOTONES CONTADOS/NOCONTADOS
        btnCont.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.AzulBack)));
        btnNoCont.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));

        chbMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                txtProducto.setText("");
                txtProductoVi.setText("");
                txtProducto.requestFocus();
                //txtProductoVi.setText("");
                contados();
                if (b){
                    //keyboard.showSoftInput(txtProducto, InputMethodManager.SHOW_IMPLICIT);
                    txtCant.setEnabled(true);
                    txtCant.setText("");
                    //keyboard.showSoftInput(Cantidad, InputMethodManager.SHOW_IMPLICIT);
                    btnGuardar.setEnabled(true);
                    btnGuardar.setBackgroundTintList(ColorStateList.
                            valueOf(getResources().getColor(R.color.AzulBack)));
                }else{
                    txtCant.setText("0");
                    txtCant.setEnabled(false);

                    keyboard.hideSoftInputFromWindow(txtCant.getWindowToken(), 0);
                    btnGuardar.setEnabled(false);
                    btnGuardar.setBackgroundTintList(ColorStateList.
                            valueOf(getResources().getColor(R.color.ColorGris)));
                }//else
            }//oncheckedchange
        });//chbMan.setoncheckedchange

        //EVENTOS txtProducto
        txtProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                ProductoAct=editable.toString();
                if (!editable.toString().equals("")) {
                    txtProductoVi.setText(ProductoAct);
                    if (codeBar.equals("Zebra")) {//codebar
                        if (!chbMan.isChecked()) {//manual no
                            buscarXprod(ProductoAct,"1",true);
                            txtProducto.setText("");
                        }else{//manual si
                            buscarXprod(ProductoAct,"-1",false);
                            txtCant.requestFocus();
                            keyboard.showSoftInput(txtCant, InputMethodManager.SHOW_IMPLICIT);
                        }//else
                    } else{
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if (ban == '\n') {
                                if (!chbMan.isChecked()) {//manual no
                                    buscarXprod(ProductoAct,"1",true);
                                    txtProducto.setText("");
                                }else{//manual si
                                    buscarXprod(ProductoAct,"-1",false);
                                    txtCant.requestFocus();
                                    keyboard.showSoftInput(txtCant, InputMethodManager.SHOW_IMPLICIT);
                                }//else
                                break;
                            }//if
                        }//for
                    }//else
                }//if !editable
            }//after
        });//txtProducto.addTextChanged


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String v1=ProductoAct;
                String v2=txtCant.getText().toString();
                if(!v1.equals("") && !v2.equals("")){
                    posicion=-1;
                    buscarXprod(v1,v2,false);
                    txtProducto.setText("");
                    keyboard.hideSoftInputFromWindow(txtCant.getWindowToken(), 0);
                    txtProducto.requestFocus();
                    //keyboard.hideSoftInputFromWindow(txtProducto.getWindowToken(), 0);
                }else{
                    Toast.makeText(ActivityDifUbiExi.this, "Campos vacios", Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });//btnGuardar setonclick

        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultaPSincro();
                int tam=listaPSincro.size();
                if(tam>0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDifUbiExi.this);
                    /*builder.setPositiveButton("TODOS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            jSon();
                        }//onclick
                    });*/
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setNeutralButton("SINCRONIZAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AsyncActualiza().execute();
                        }
                    });
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage(
                            "Existen "+tam+" datos para sincronizar ¿Desea continuar?").create().show();
                    //jSon();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDifUbiExi.this);
                    builder.setPositiveButton("ACEPTAR", null);//positive button
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("Sin datos para sincronizar").create().show();
                }//else
            }//onclcik
        });//btnSincronizar onclick

        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtProducto.setText("");
                txtProductoVi.setText("");
                contados();

            }//onclick
        });//btnCont
        btnNoCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtProducto.setText("");
                txtProductoVi.setText("");
                noContados();
            }//onclick
        });//btnNoCont

        btnAlma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtProductoVi.getText().toString().equals("")){
                    new AsyncalListAlm().execute();
                }else{
                    Toast.makeText(ActivityDifUbiExi.this, "Ningún producto seleccionado", Toast.LENGTH_SHORT).show();
                }//else
            }//onclick
        });//btnAlma onclick

        btnRefr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chbMan.setChecked(false);
                lista2.clear();
                rvDifUbiExi.setAdapter(null);
                eliminarSql("");
                txtProductoVi.setText("");
                txtContF.setText("");
                txtExistS.setText("");
                txtDif.setText("");
                txtUbb.setText("");
                txtCant.setText("");
                new AsyncDifUbiExist().execute();
            }//onclick
        });//btnRefr

        //FOLIO
        if(folio.equals("")){//si no hay folio guardado
            new AsyncFolios().execute();
        }else{
            seleccionaFol();
            contados();
        }//else
    }//onCreate

    private static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }//isNumeric
    public void limpiaCampos(){
        txtProducto.setText("");
        txtCant.setText("");
        txtContF.setText("");
        txtExistS.setText("");
        txtDif.setText("");
        txtUbb.setText("");
        tvEstatus.setText("");
    }//limpiaCampos


    public void contados(){//cuando se muestre la parte de contados
        btnCont.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.AzulBack)));
        btnNoCont.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));
        limpiaCampos();
        where=" AND ESTATUS=1 ";
        consultaSql();
    }//contados

    public void noContados(){
        btnNoCont.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.AzulBack)));
        btnCont.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));
        limpiaCampos();
        where=" AND ESTATUS=0 ";
        consultaSql();
    }//noContados



    public void buscaFolios(View v){
        if(!folio.equals("")){//si ya hay folio guardado
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDifUbiExi.this);
            builder.setPositiveButton("FOLIO ACTUAL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    consultaSql();
                }//onclick
            });//positive button
            builder.setNegativeButton("SELECCIONAR OTRO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editor.clear().commit();
                    eliminarSql("");
                    new AsyncFolios().execute();
                }
            });//negative
            builder.setCancelable(false);
            builder.setTitle("AVISO").setMessage("Estas trabajando con un folio"+
                    "¿Desea seleccionar uno nuevo?(Se perderan los cambios no guardados)").create().show();
        }else{//si no hay folio guardado
            new AsyncFolios().execute();
        }//else
    }//buscarFolios

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void seleccionEnAlertFolios2(View v){
        int pos = rvFolios.getChildAdapterPosition(rvFolios.findContainingItemView(v));
        folio=listaFol.get(pos).getFolio();
        fecha=listaFol.get(pos).getFecha();
        hora=listaFol.get(pos).getHora();
        editor.putString("folio", folio);
        editor.putString("fechaI", fecha);
        editor.putString("horaI", hora);
        editor.commit();
        rvDifUbiExi.setAdapter(null);
        dialog.dismiss();
        seleccionaFol();
        new AsyncDifUbiExist().execute();
    }//seleccionEnAlertFolios

    public void seleccionaFol(){
        txtFolioInv.setText(folio);
        txtFechaI.setText(fecha);
        txtHoraI.setText(hora);
    }

    @SuppressLint("MissingInflatedId")
    public void listaFolio(){
        txtProductoVi.setText("");
        txtContF.setText("");
        txtExistS.setText("");
        txtDif.setText("");
        txtUbb.setText("");
        txtCant.setText("");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info_folios, null);
        builder.setView(dialogView);

        rvFolios =dialogView.findViewById(R.id.rvFolios);
        GridLayoutManager gl = new GridLayoutManager(this, 1);
        rvFolios.setLayoutManager(gl);

        AdaptadorListaFolios2 adapter = new AdaptadorListaFolios2(listaFol);
        rvFolios.setAdapter(null);
        rvFolios.setAdapter(adapter);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.show();
    }//listaFolio


    private class AsyncFolios extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {mDialog.show();}

        @Override
        protected Void doInBackground(Void... params) {
            listaFol = new ArrayList<>();
            conectaFolios();
            return null;
        }//doInBackground

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if (listaFol.size()>0) {
                listaFolio();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDifUbiExi.this);
                builder.setMessage("No se encontró folios");
                builder.setCancelable(false);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });//negative botton
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else
        }//onPostExecute
    }//AsyncFolios


    private void conectaFolios() {
        String SOAP_ACTION = "Folios";
        String METHOD_NAME = "Folios";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLFolios soapEnvelope = new XMLFolios(SoapEnvelope.VER11);
            soapEnvelope.XMLFol(strusr, strpass,strbran,"1");//solo folios abiertos
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject response0 = (SoapObject) soapEnvelope.bodyIn;
                response0 = (SoapObject) response0.getProperty(i);
                listaFol.add(new Folios((response0.getPropertyAsString("k_folio").equals("anyType{}")?"" : response0.getPropertyAsString("k_folio")),
                        (response0.getPropertyAsString("k_fecha").equals("anyType{}")?"" : response0.getPropertyAsString("k_fecha")),
                        (response0.getPropertyAsString("k_hora").equals("anyType{}")? "" : response0.getPropertyAsString("k_hora"))));
            }//for
        } catch (Exception ex) {}//catch
    }//conectaFolios


    private class AsyncDifUbiExist extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {mDialog.show();}

        @Override
        protected Void doInBackground(Void... params) {
            lista2.clear();
            conectaDifUbiExist();
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            chbMan.setChecked(false);
            if (mensaje.equals("Guardados")) {
                contados();
                mDialog.dismiss();
            }else{
                mDialog.dismiss();
                Toast.makeText(ActivityDifUbiExi.this, "Ningún dato", Toast.LENGTH_SHORT).show();
            }
            txtProducto.setText("");
        }//onPostExecute
    }//AsynInsertInv


    private void conectaDifUbiExist() {
        String SOAP_ACTION = "DifUbiExist";
        String METHOD_NAME = "DifUbiExist";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMDifUbiExist soapEnvelope = new XMDifUbiExist(SoapEnvelope.VER11);
            soapEnvelope.XMLdif(strusr, strpass, folio);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            String clave,cant,exist,dif,ubi,est;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject response0 = (SoapObject) soapEnvelope.bodyIn;
                response0 = (SoapObject) response0.getProperty(i);
                clave=(response0.getPropertyAsString("CLAVE").equals("anyType{}") ? " " : response0.getPropertyAsString("CLAVE"));
                cant=(response0.getPropertyAsString("CANTIDAD").equals("anyType{}") ? " " : response0.getPropertyAsString("CANTIDAD"));
                exist=(response0.getPropertyAsString("EXISTENCIA").equals("anyType{}") ? " " : response0.getPropertyAsString("EXISTENCIA"));
                dif=(response0.getPropertyAsString("DIFERENCIA").equals("anyType{}") ? " " : response0.getPropertyAsString("DIFERENCIA"));
                ubi=(response0.getPropertyAsString("UBICACION").equals("anyType{}") ? " " : response0.getPropertyAsString("UBICACION"));
                est=(response0.getPropertyAsString("ESTATUS").equals("anyType{}") ? "" : response0.getPropertyAsString("ESTATUS"));
                insertarSql(clave,cant, exist,dif, ubi,est);
                mensaje="Guardados";
            }//for
        } catch (Exception ex) {}//catch
    }//conectaListInv

    private class AsyncActualiza extends AsyncTask<Void, Integer, Void> {
        private String pro,cc,ubic;
        private int contador=0;
        @Override
        protected void onPreExecute() {progressDialog.show();}

        @Override
        protected Void doInBackground(Void... params) {
            progressDialog.setMax(listaPSincro.size());
            try {
                for(int j=0;j<listaPSincro.size();j++){
                    mensaje="";
                    pro=listaPSincro.get(j).getProducto();
                    cc=listaPSincro.get(j).getConteo();
                    ubic=listaPSincro.get(j).getUbicacion();
                    conectaActualiza(pro,cc,ubic);
                    if(mensaje.equals("Actualizado")){
                        eliminarSql("AND PRODUCTO='"+pro+"' ");
                        contador++;
                    }//if
                    Thread.sleep(100);
                    progressDialog.setProgress(j);
                }//for
            } catch (InterruptedException e) {
                return null;
            }//catch
            return null;
        }//doinbackground

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            if (contador==listaPSincro.size()) {
                lista2.clear();
                rvDifUbiExi.setAdapter(null);
                editor.clear().commit();
                eliminarSql("");
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDifUbiExi.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AsyncFolios().execute();
                    }//onclick
                });//positivebutton
                builder.setCancelable(false);
                builder.setTitle("Resultado Sincronización").setMessage(contador+" Datos sincronizados").create().show();

            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDifUbiExi.this);
                builder.setMessage("Error al sincronizar");
                builder.setCancelable(false);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });//negative botton
                AlertDialog dialog = builder.create();
                dialog.show();
                contados();
            }//else
        }//onPostExecute
    }//AsynInsertInv


    private void conectaActualiza (String producto, String cont, String ubic) {
        String SOAP_ACTION = "ActualizaDif";
        String METHOD_NAME = "ActualizaDif";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLActualizaDif soapEnvelope = new XMLActualizaDif(SoapEnvelope.VER11);
            soapEnvelope.XMLAct(strusr, strpass, folio, strbran, producto,cont,ubic);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            mensaje=response.getPropertyAsString("k_estado");
            //mensaje=(response.getPropertyAsString("k_estatus").equals("anyType{}") ? null : response.getPropertyAsString("k_estatus"));
        } catch (SoapFault soapFault) {
            mensaje=soapFault.getMessage();
        } catch (XmlPullParserException e) {
            mensaje=e.getMessage();
        } catch (IOException e) {
            mensaje=e.getMessage();
        } catch (Exception ex) {
            mensaje=ex.getMessage();
        }//catch
    }//conectaActualiza
    public void buscarXprod(String prod,String canti,boolean sum){
        try{
            int contA=0,cont=0,exist=0,dif=0;
            String ubi="";
            String est="";
            rvDifUbiExi.setAdapter(null);
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT PRODUCTO,CANTIDAD,EXISTENCIA,DIFERENCIA,"+
                    "UBICACION,CONTEO,ESTATUS FROM DIFUBIEXIST WHERE EMPRESA='"+serv+"' AND PRODUCTO='"+prod+"' LIMIT 1", null);
            if (fila != null && fila.moveToFirst()) {
                do {
                    ProductoAct=fila.getString(0);
                    if(canti.equals("-1")){
                        canti=fila.getString(5);
                    }
                    exist=Integer.parseInt(fila.getString(2));
                    cont=Integer.parseInt(fila.getString(5));
                    ubi=fila.getString(4);
                    int op;
                    if(sum==true){
                        op=cont+1;
                    }else{
                        contA=Integer.parseInt(canti);
                        op=contA;
                    }
                    dif=exist-op;
                    cont=op;
                    actualizarSql(prod,cont+"",dif+"",ubi,exist+"");
                    txtProductoVi.setText(ProductoAct);
                    txtContF.setText(fila.getString(1));
                    txtExistS.setText(fila.getString(2));
                    txtDif.setText(dif+"");
                    txtUbb.setText(fila.getString(4));
                    txtCant.setText(cont+"");
                    if(fila.getString(6).equals("1")){
                        est="CONTADO";
                    }else{
                        est="NO CONTADO";
                    }
                    tvEstatus.setText(est);
                } while (fila.moveToNext());
            }else{
                posicion=-1;
                Toast.makeText(this, "Producto no existe en lista", Toast.LENGTH_SHORT).show();
                txtProducto.setText("");
                limpiaCampos();
                if(chbMan.isChecked()){
                    txtProducto.requestFocus();
                }
            }
            fila.close();
        }catch(Exception e){
            ProductoAct="";
            Toast.makeText(ActivityDifUbiExi.this,
                    "No existe producto", Toast.LENGTH_SHORT).show();
        }//catch
        consultaSql();
    }//consultaSql

    public void buscar1(String prod,String canti,boolean sum){
        boolean band=false;
        int contA=0,cont=0,exist=0,dif=0;
        String ubi="";
        rvDifUbiExi.setAdapter(null);
        for(int i=0;i<lista2.size();i++){
            if(prod.equals(lista2.get(i).getProducto())){
                if(canti.equals("-1")){
                    canti=lista2.get(i).getConteo();
                }
                exist=Integer.parseInt(lista2.get(i).getExistencia());
                cont=Integer.parseInt(lista2.get(i).getConteo());
                ubi=lista2.get(i).getUbicacion();
                int op;
                if(sum==true){
                    op=cont+1;
                }else{
                    contA=Integer.parseInt(canti);
                    op=contA;
                }
                dif=exist-op;
                cont=op;
                actualizarSql(prod,cont+"",dif+"",ubi,exist+"");
                lista2.get(i).setConteo(cont+"");
                lista2.get(i).setDiferencia(dif+"");
                band=true;
                break;
            }//if
        }//for
        if (band==false){//si no existe el producto
            posicion=0;
            Toast.makeText(this, "Producto no existe en lista", Toast.LENGTH_SHORT).show();
            txtProducto.setText("");
            txtProductoVi.setText("");
            if(chbMan.isChecked()){
                txtProducto.requestFocus();
            }
        }
        consultaSql();
    }//buscar

    private class AsyncalListAlm extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreejecute
        @Override
        protected Void doInBackground(Void... params) {
            mensaje="";
            listaAlm.clear();
            conectaAlma();
            return null;
        }//doInBackground
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if(listaAlm.size()>0){
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityDifUbiExi.this);
                LayoutInflater inflater = ActivityDifUbiExi.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_lista_almacenes, null);
                alert.setView(dialogView);
                alert.setCancelable(true);
                alert.setCancelable(false);
                alert.setNegativeButton("ACEPTAR",null);

                RecyclerView rvAlmacenes =  dialogView.findViewById(R.id.rvAlmacenes);
                GridLayoutManager gl = new GridLayoutManager(ActivityDifUbiExi.this, 1);
                rvAlmacenes.setLayoutManager(gl);

                adapter= new AdapterDifUbiExi(lista2);
                rvDifUbiExi.setAdapter(adapter);

                AdaptadorListAlmacenes adapAlm = new AdaptadorListAlmacenes(listaAlm);
                rvAlmacenes.setAdapter(adapAlm);
                AlertDialog mm = alert.create();
                mm.show();
            }else{
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityDifUbiExi.this);
                alerta.setMessage("Sin Almacénes").setCancelable(false)
                        .setPositiveButton("Ok", null).setCancelable(false);//alertdialog
                AlertDialog titulo = alerta.create();
                titulo.setTitle("AVISO");
                titulo.show();
            }//else
            mensaje="";
        }//onPostExecute
    }//AsyncallUbicaciones

    private void conectaAlma() {
        String SOAP_ACTION = "ValidarEscanInv";
        String METHOD_NAME = "ValidarEscanInv";
        String NAMESPACE = "http://"+strServer+"/WSk75AlmacenesApp/";
        String URL = "http://"+strServer+"/WSk75AlmacenesApp";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLValidEsc soapEnvelope = new XMLValidEsc(SoapEnvelope.VER11);
            soapEnvelope.XMLValid(strusr, strpass, txtProductoVi.getText().toString(), strbran);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject response0 = (SoapObject) soapEnvelope.bodyIn;
                response0 = (SoapObject) response0.getProperty(i);
                listaAlm.add(new Almacenes((response0.getPropertyAsString("k_Almacen").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Almacen")),
                        (response0.getPropertyAsString("k_Descrip").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Descrip")),
                        (response0.getPropertyAsString("k_Existencia").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Existencia"))));
            }//for
        }catch (SoapFault soapFault) {
            mensaje = "Error: " + soapFault.getMessage();
        }catch (XmlPullParserException e) {
            mensaje = "Error: " + e.getMessage();
        }catch (IOException e) {
            mensaje = "No se encontró servidor";
        }catch (Exception ex) {
            mensaje ="Puede que la clave del producto no exista";
        }
    }//AsynCall


    public void onClickListDif(View v){//cada vez que se seleccione un producto en la lista
        posicion = rvDifUbiExi.getChildPosition(rvDifUbiExi.findContainingItemView(v));
        detalle(posicion);
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        rvDifUbiExi.scrollToPosition(posicion);
    }//onClickLista

    public void detalle(int posi){//detalle del producto seleccionado
        String est;
        ProductoAct=lista2.get(posi).getProducto();
        txtProductoVi.setText(ProductoAct);
        txtContF.setText(lista2.get(posi).getCantidad());
        txtExistS.setText(lista2.get(posi).getExistencia());
        txtDif.setText(lista2.get(posi).getDiferencia());
        txtUbb.setText(lista2.get(posi).getUbicacion());
        txtCant.setText(lista2.get(posi).getConteo());
        if(lista2.get(0).getEstatus().equals("1")){
            est="CONTADO";
        }else{
            est="NO CONTADO";
        }
        tvEstatus.setText(est);
    }//detalle
    public void detalleProd1(String prod){//se busca el detalle en todos lo productos ya sea contados o no contados
        for(int i=0;i<lista2.size();i++){
            if(lista2.get(i).getProducto().equals(prod)){
                txtContF.setText(lista2.get(i).getCantidad());
                txtExistS.setText(lista2.get(i).getExistencia());
                txtDif.setText(lista2.get(i).getDiferencia());
                txtUbb.setText(lista2.get(i).getUbicacion());
                txtCant.setText(lista2.get(i).getConteo());
                break;
            }
        }
        rvDifUbiExi.scrollToPosition(posicion);
    }//detalleLista2

    public void consultaSql(){
        try{
            lista2.clear();
            int j=0;
            String est;
            rvDifUbiExi.setAdapter(null);
            if(ProductoAct.equals("")){
                posicion=-1;
            }
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT PRODUCTO,CANTIDAD,EXISTENCIA,DIFERENCIA,"+
                    "UBICACION,CONTEO,ESTATUS FROM DIFUBIEXIST WHERE EMPRESA='"+serv+"' "+where+"  ORDER BY UBICACION,PRODUCTO ", null);
            if (fila.moveToFirst()) {
                do {
                    j++;
                    if(ProductoAct.equals(fila.getString(0))){
                        posicion=j-1;
                        txtProductoVi.setText(ProductoAct);
                        txtContF.setText(fila.getString(1));
                        txtExistS.setText(fila.getString(2));
                        txtDif.setText(fila.getString(3));
                        txtUbb.setText(fila.getString(4));
                        txtCant.setText(fila.getString(5));
                        if(fila.getString(6).equals("1")){
                            est="CONTADO";
                        }else{
                            est="NO CONTADO";
                        }
                        tvEstatus.setText(est);
                    }
                    lista2.add(new DifUbiExist(j+"",fila.getString(0),fila.getString(1),fila.getString(2),
                            fila.getString(3),fila.getString(4),fila.getString(5),fila.getString(6)));
                } while (fila.moveToNext());
                adapter= new AdapterDifUbiExi(lista2);
                rvDifUbiExi.setAdapter(adapter);
                adapter.index(posicion);
                adapter.notifyDataSetChanged();
                rvDifUbiExi.scrollToPosition(posicion);
            }//if
            fila.close();
        }catch(Exception e){
            Toast.makeText(ActivityDifUbiExi.this,
                    "Error al consultar datos de la base de datos interna", Toast.LENGTH_SHORT).show();
        }//catch
    }//consultaSql

    public void consultaPSincro(){
        try{
            listaPSincro.clear();
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT PRODUCTO,"+
                    "UBICACION,CONTEO FROM DIFUBIEXIST WHERE EMPRESA='"+serv+"' AND CONTEO>0 ", null);
            if (fila != null && fila.moveToFirst()) {
                do {
                    listaPSincro.add(new DifUbiExist("",fila.getString(0),"","",
                            "",fila.getString(1),fila.getString(2),""));
                } while (fila.moveToNext());
            }//if
            fila.close();
        }catch(Exception e){
            Toast.makeText(ActivityDifUbiExi.this,
                    "Error al consultar datos para sincronizar", Toast.LENGTH_SHORT).show();
        }//catch
    }//consultaSql

    public void insertarSql(String prod,String cant,String exist,String dif,String ubi,String estatus){
        try{
            if(db != null){
                ContentValues valores = new ContentValues();
                valores.put("PRODUCTO", prod);
                valores.put("CANTIDAD", cant);
                valores.put("EXISTENCIA", exist);
                valores.put("DIFERENCIA", dif);
                valores.put("UBICACION", ubi);
                valores.put("CONTEO", "0");
                valores.put("EMPRESA", serv);
                valores.put("ESTATUS", estatus);
                db.insert("DIFUBIEXIST", null, valores);
            }
        }catch(Exception e){
            Toast.makeText(this, "Problema al guardar producto", Toast.LENGTH_SHORT).show();
        }
    }//insertarSql

    public void actualizarSql(String prod,String cant,String dif,String ubi,String exist){
        try{
            ContentValues valores = new ContentValues();
            valores.put("CONTEO", Integer.parseInt(cant));
            valores.put("UBICACION", ubi);
            valores.put("EXISTENCIA", exist);
            valores.put("DIFERENCIA", Integer.parseInt(dif));
            valores.put("ESTATUS", "1");
            db.update("DIFUBIEXIST", valores, "PRODUCTO='"+prod+"' AND EMPRESA='"+serv+"'", null);
        }catch(Exception e){
            Toast.makeText(this, "Problema al actualizar la cantidad del producto", Toast.LENGTH_SHORT).show();
        }
    }//actualizarSql

    public void eliminarSql(String sentProd) {//parte de sentencia que es para eliminar prod o todos los productos
        try{
            SQLiteDatabase db = conn.getWritableDatabase();
            db.delete("DIFUBIEXIST"," EMPRESA='"+serv+"' "+sentProd,null);
        }catch(Exception e){}
    }//eliminarSql

    public void mensajeDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDifUbiExi.this);
        builder.setTitle("Respuesta");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });//negative botton
        AlertDialog dialog = builder.create();
        dialog.show();
    }//mensajeDialog

    public String armarXml(){
        String x="";
        for(int i=0;i<lista2.size();i++){
           x=x+"<Item>" +
                   "<k_prod>"+lista2.get(i).getProducto()+"</k_prod>"+
                   "<k_cont>"+lista2.get(i).getConteo()+"</k_cont>"+
                   "<k_ubi>"+lista2.get(i).getUbicacion()+"</k_ubi>"
                   +"</Item>";
        }//for
        return x;
    }//armarXml


    public void jSon(){
        //mDialog.show();
        String url = "http://"+strServer+"/"+getString(R.string.resActualizaDif);
        mensaje="";
        String xml="<Item>" +
                "<k_prod>"+lista2.get(0).getProducto()+"</k_prod>"+
                "<k_cont>"+lista2.get(0).getConteo()+"</k_cont>"+
                "<k_ubi>"+lista2.get(0).getUbicacion()+"</k_ubi>"+
                "</Item>"+ "<Item>" +
                "<k_prod>"+lista2.get(1).getProducto()+"</k_prod>"+
                "<k_cont>"+lista2.get(1).getConteo()+"</k_cont>"+
                "<k_ubi>"+lista2.get(1).getUbicacion()+"</k_ubi>"+
                "</Item>";

        JsonObjectRequest jReq = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //mDialog.dismiss();
                try {
                    mensaje=response.getString("Response").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //mensajeDialog(mensaje);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mDialog.dismiss();
                mensaje=error.getMessage();
            }
        }){    //Headers
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("user", strusr);
                headers.put("pass", strpass);
                return headers;
            }
            //params
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("k_folio",folio);
                params.put("k_suc",strbran);
                try {
                    params.put("lista", String.valueOf(XML.toJSONObject(xml)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
            /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(MY_DEFAULT_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        mQueue.add(jReq);
    }//json
}//ActivityInventario
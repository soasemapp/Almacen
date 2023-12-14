package com.almacen.alamacen202.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.ActivityMenu;
import com.almacen.alamacen202.Adapter.AdaptadorListProductos;
import com.almacen.alamacen202.Adapter.AdaptadorListaFolios;
import com.almacen.alamacen202.Adapter.AdapterInventario;
import com.almacen.alamacen202.MainActivity;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Folios;
import com.almacen.alamacen202.SetterandGetters.Inventario;
import com.almacen.alamacen202.SetterandGetters.ListProAduSandG;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.XML.XMLActualizaInv;
import com.almacen.alamacen202.XML.XMLFolios;
import com.almacen.alamacen202.XML.XMLValdiSuper;
import com.almacen.alamacen202.XML.XMLValidEsc;
import com.almacen.alamacen202.XML.XMLlistInv;
import com.almacen.alamacen202.includes.MyToolbar;
import com.google.android.material.textfield.TextInputLayout;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import dmax.dialog.SpotsDialog;
import pl.droidsonroids.gif.GifImageView;

public class ActivityInventario extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preference,preferenceF;
    private SharedPreferences.Editor editor;
    private boolean comprobar=false;
    private int posicion=0,contInsert=0;
    private String strusr,strpass,strServer,strbran,codeBar,ProductoAct="",folio="",fecha="",hora="",mensaje,bandAutori,mensajeAutoriza,UserSuper;
    private ArrayList<Inventario> listaInv = new ArrayList<>();
    private ArrayList<Inventario> listaPSincro = new ArrayList<>();
    private EditText txtFolioInv,txtProductoVi,txtFechaI,txtHoraI,txtProducto,txtEscan;
    private ArrayList<Folios>listaFol;
    private Button btnGuardar,btnSincronizar;
    private CheckBox chbMan;
    private RecyclerView rvInventario;
    private AdapterInventario adapter;
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private ConexionSQLiteHelper conn;
    private SQLiteDatabase db;
    private RecyclerView rvFolios;//para alertdialog
    private AlertDialog dialog;
    private LinearLayout lyInsert,lyEscanea;
    private Button btnAutoriza;
    private int sonido_de_reproduccion1;
    private SoundPool bepp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        MyToolbar.show(this, "Inventario", true);
        preferenceF = getSharedPreferences("Folio", Context.MODE_PRIVATE);//para guardar folio
        editor = preferenceF.edit();

        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);

        folio=preferenceF.getString("folio", "");
        fecha=preferenceF.getString("fechaI", "");
        hora=preferenceF.getString("horaI", "");

        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strServer = preference.getString("Server", "null");
        strbran = preference.getString("codBra", "null");
        codeBar = preference.getString("codeBar", "null");
        mDialog = new SpotsDialog.Builder().setContext(ActivityInventario.this).
                setMessage("Espere un momento...").build();

        progressDialog = new ProgressDialog(ActivityInventario.this);//parala barra de
        progressDialog.setMessage("Procesando datos....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        txtFolioInv     = findViewById(R.id.txtFolioInv);
        txtFechaI       = findViewById(R.id.txtFechaI);
        txtHoraI        = findViewById(R.id.txtHoraI);
        txtProducto     = findViewById(R.id.txtProducto);
        txtProductoVi   = findViewById(R.id.txtProductoVi);
        txtEscan         = findViewById(R.id.txtEscan);
        btnGuardar      = findViewById(R.id.btnGuardar);
        btnSincronizar  = findViewById(R.id.btnSincronizar);
        chbMan          = findViewById(R.id.chbMan);
        rvInventario    = findViewById(R.id.rvInventario);
        bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_de_reproduccion1 = bepp.load(ActivityInventario.this, R.raw.error, 1);

        conn = new ConexionSQLiteHelper(ActivityInventario.this, "bd_INVENTARIO", null, 1);
        db = conn.getReadableDatabase();
        rvInventario.setLayoutManager(new LinearLayoutManager(ActivityInventario.this));
        keyboard = (InputMethodManager) getSystemService(ActivityInventario.INPUT_METHOD_SERVICE);

        txtProducto.requestFocus();
        txtProducto.setInputType(InputType.TYPE_NULL);
        txtEscan.setEnabled(false);
        chbMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                txtProducto.setText("");
                txtProducto.requestFocus();
                txtProductoVi.setText("");
                posicion=-1;
                adapter.notifyDataSetChanged();
                rvInventario.scrollToPosition(0);
                if (b){
                    //keyboard.showSoftInput(txtProducto, InputMethodManager.SHOW_IMPLICIT);
                    txtEscan.setEnabled(true);
                    txtEscan.setText("");
                    //keyboard.showSoftInput(Cantidad, InputMethodManager.SHOW_IMPLICIT);
                    btnGuardar.setEnabled(true);
                    btnGuardar.setBackgroundTintList(ColorStateList.
                            valueOf(getResources().getColor(R.color.AzulBack)));
                }else {
                    txtEscan.setText("");
                    txtEscan.setEnabled(false);
                    keyboard.hideSoftInputFromWindow(txtEscan.getWindowToken(), 0);
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
                            buscar(ProductoAct,compararCantidad(ProductoAct)+"");
                            txtProducto.setText("");
                        }else{//manual si
                            txtEscan.setText("");
                            txtEscan.requestFocus();
                            keyboard.showSoftInput(txtEscan, InputMethodManager.SHOW_IMPLICIT);
                        }//else
                    } else{
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if(ban == '\n'){
                                if (!chbMan.isChecked()) {//manual no
                                    buscar(ProductoAct,compararCantidad(ProductoAct)+"");
                                }else{//manual si
                                    txtEscan.requestFocus();
                                    keyboard.showSoftInput(txtEscan, InputMethodManager.SHOW_IMPLICIT);
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
                String v1=txtProductoVi.getText().toString();
                String v2=txtEscan.getText().toString();
                if(!v1.equals("") && !v2.equals("")){
                    actualizaGuarda(v1,v2);
                    keyboard.hideSoftInputFromWindow(txtEscan.getWindowToken(), 0);
                    txtProducto.setText("");
                    txtProducto.requestFocus();
                    //keyboard.hideSoftInputFromWindow(txtProducto.getWindowToken(), 0);
                    /*if (chbMan.isChecked()) {
                        txtEscan.setText("");
                    }else{
                        txtEscan.setText("1");
                    }//else
                    */
                }else{
                    Toast.makeText(ActivityInventario.this, "Campos vacios", Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });//btnGuardar setonclick
        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultaPSincro();
                int tam=listaPSincro.size();
                if(tam>0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AsyncActualizaInv().execute();
                        }//onclick
                    });//positive button
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("Existen "+tam+" datos para sincronizar ¿Desea continuar?").create().show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                    builder.setPositiveButton("ACEPTAR", null);//positive button
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("Sin datos para sincronizar").create().show();
                }//else
            }//onclcik
        });//btnSincronizar onclick


        //FOLIO
        if(folio.equals("")){//si no hay folio guardado
            new AsyncFolios().execute();
        }else{
            comprobar=true;
            new AsyncFolios().execute();
        }//else

    }//onCreate
    public void onClickInv(View v){//cada vez que se seleccione un producto en la lista
        posicion = rvInventario.getChildPosition(rvInventario.findContainingItemView(v));
        ProductoAct=listaInv.get(posicion).getProducto();
        mostrarDetalleCod(posicion);
        txtProducto.setText("");
        if (chbMan.isChecked()){//manual no
            keyboard.hideSoftInputFromWindow(txtEscan.getWindowToken(), 0);
        }//else
        txtProducto.requestFocus();

    }//onClickLista

    public void alertAutoriza(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info_autorizacion2, null);
        alert.setView(dialogView);
        alert.setCancelable(true);
        alert.setCancelable(false);
        alert.setNegativeButton("CANCELAR",null);

        EditText txtEscanUsr,txtClavProdAut;
        txtEscanUsr =  dialogView.findViewById(R.id.txtEscanUsr);
        txtClavProdAut = dialogView.findViewById(R.id.txtClavProdAut);
        lyInsert = dialogView.findViewById(R.id.lyInsert);
        lyEscanea = dialogView.findViewById(R.id.lyEscanea);
        btnAutoriza = dialogView.findViewById(R.id.btnAutoriza);

        btnAutoriza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String claveMan=txtClavProdAut.getText().toString();
                txtProducto.setText(claveMan);
                dialog.dismiss();
            }//onclick
        });//btnAutoriza

        dialog = alert.create();
        dialog.show();
        txtEscanUsr.requestFocus();
        txtEscanUsr.setInputType(InputType.TYPE_NULL);
        txtEscanUsr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    if (codeBar.equals("Zebra")) {
                        String Usuario = editable.toString();
                        verificarUsuarioSuperv(Usuario);
                        txtEscanUsr.setText(null);
                    } else {
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if (ban == '\n') {
                                String Usuario = editable.toString();
                                Usuario = Usuario.replace("\n", "");
                                verificarUsuarioSuperv(Usuario);
                                txtEscanUsr.setText(null);
                            }//ifBan
                        }//for
                    }//else
                }//if editable
            }//afterTextChange
        });//addTextChange
    }//alertAutoriza

    public void verificarUsuarioSuperv(String usuario) {
        if (!usuario.equals("")) {
            new AsyncAutoriza(usuario).execute();
        }else {
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityInventario.this);
            alerta.setMessage("Confirme que todos los campos hayan sido llenados").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog titulo = alerta.create();
            titulo.setTitle("¡ERROR!");
            titulo.show();
        }//else

    }//alertAutoriza

    public void buscaFolios(View v){
        if(!folio.equals("")){//si ya hay folio guardado
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
            builder.setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }//onclick
            });//positive button
            builder.setNegativeButton("SELECCIONAR OTRO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editor.clear().commit();
                    eliminarSql("");
                    chbMan.setChecked(false);
                    txtProducto.setText("");
                    txtProductoVi.setText("");
                    txtEscan.setText("1");
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
    public int compararCantidad(String prod){//en caso de que no sea manual, se toma la cantidad que se tenia y se suma 1
        int cant=1;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod)){
                cant=Integer.parseInt(listaInv.get(i).getEscan())+1;
                break;
            }//if
        }//for
        return cant;
    }//compararCant

    public void seleccionEnAlertFolios(View v){
        int pos = rvFolios.getChildAdapterPosition(rvFolios.findContainingItemView(v));
        folio=listaFol.get(pos).getFolio();
        fecha=listaFol.get(pos).getFecha();
        hora=listaFol.get(pos).getHora();
        txtFolioInv.setText(folio);
        txtFechaI.setText(fecha);
        txtHoraI.setText(hora);
        editor.putString("folio", folio);
        editor.putString("fechaI", fecha);
        editor.putString("horaI", hora);
        editor.commit();
        rvInventario.setAdapter(null);
        new AsyncListInv().execute();
        dialog.dismiss();
    }//seleccionEnAlertFolios

    @SuppressLint("MissingInflatedId")
    public void listaFolio(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info_folios, null);
        builder.setView(dialogView);

        rvFolios =dialogView.findViewById(R.id.rvFolios);
        GridLayoutManager gl = new GridLayoutManager(this, 1);
        rvFolios.setLayoutManager(gl);

        AdaptadorListaFolios adapter = new AdaptadorListaFolios(listaFol);
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
            if(comprobar==true){//comprobar si folio esta abierto
                comprobar=false;
                if (listaFol.size()>0) {//ahora busca en la lista de folios disponibles
                    boolean var=false;
                    for(int i=0;i<listaFol.size();i++){//buscar si el folio esta entre los folios que estan abiertos
                        if(listaFol.get(i).getFolio().equals(folio)){
                            var=true;
                            break;
                        }//if
                    }//for
                    if(var==true){//si esta abierto
                        txtFolioInv.setText(folio);
                        txtFechaI.setText(fecha);
                        txtHoraI.setText(hora);
                        consultaSql();
                    }else {//si no esta abierto
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                        builder.setMessage("Folio cerrado");
                        builder.setCancelable(false);
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new AsyncFolios().execute();
                            }
                        });//negative botton
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }//else
                    mDialog.dismiss();
                }else{//como no hay folios disponibles el folio no esta abierto
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                    builder.setMessage("Folio cerrado");
                    builder.setCancelable(false);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AsyncFolios().execute();
                        }
                    });//negative botton
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    mDialog.dismiss();
                }//else
            }else{//cuando no se quiere comprobar el folio
                mDialog.dismiss();
                if (listaFol.size()>0) {
                    listaFolio();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
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


    private class AsyncListInv extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {mDialog.show();}

        @Override
        protected Void doInBackground(Void... params) {
            listaInv.clear();
            contInsert=0;
            posicion=-1;
            txtProductoVi.setText("");
            //txtEscan.setText("");
            conectaListInv();
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            if (listaInv.size()>0) {
                if(listaInv.size()!=contInsert){
                    Toast.makeText(ActivityInventario.this,
                            "Se guardaron "+contInsert+" datos", Toast.LENGTH_SHORT).show();
                }//if
                mDialog.dismiss();
                consultaSql();
            }else{
                mDialog.dismiss();
                Toast.makeText(ActivityInventario.this, "Ningun dato", Toast.LENGTH_SHORT).show();
            }
            txtProducto.setText("");
        }//onPostExecute
    }//AsynInsertInv


    private void conectaListInv() {
        String SOAP_ACTION = "ListInv";
        String METHOD_NAME = "ListInv";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLlistInv soapEnvelope = new XMLlistInv(SoapEnvelope.VER11);
            soapEnvelope.XMLLI(strusr, strpass, folio,strbran);
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
                String prod=(response0.getPropertyAsString("k_prod").equals("anyType{}") ? " " : response0.getPropertyAsString("k_prod"));
                String cant=(response0.getPropertyAsString("k_acum").equals("anyType{}") ? " " : response0.getPropertyAsString("k_acum"));
                listaInv.add(new Inventario(
                        (i+1)+"", prod, cant,0+""));
                if(insertarSql(prod,cant,0+"")==true){
                    contInsert++;
                }
            }//for
        } catch (Exception ex) {}//catch
    }//conectaListInv

    private class AsyncActualizaInv extends AsyncTask<Void, Integer, Void> {
        private String pro,cc;
        private int contador=0;
        @Override
        protected void onPreExecute() {progressDialog.show();}

        @Override
        protected Void doInBackground(Void... params) {
            progressDialog.setMax(listaPSincro.size());
            for(int j=0;j<listaPSincro.size();j++){//for para los registros de cada servidor
                try {
                    mensaje="";
                    pro=listaPSincro.get(j).getProducto();
                    cc=listaPSincro.get(j).getEscan();
                    conectaActualiza(pro,cc);
                    if(mensaje.equals("1")){
                        eliminarSql(" PRODUCTO='"+pro+"'");
                        contador++;
                    }//if
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return null;
                }//catch
                progressDialog.setProgress(j);
            }//for
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
            if(mensaje.equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                builder.setMessage("El folio esta cerrado");
                builder.setCancelable(false);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });//negative botton
                AlertDialog dialog = builder.create();
                dialog.show();
            }else if(contador==listaPSincro.size()) {
                listaInv.clear();
                rvInventario.setAdapter(null);
                editor.clear().commit();
                eliminarSql("");
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AsyncFolios().execute();
                    }//onclick
                });//positivebutton
                builder.setCancelable(false);
                builder.setTitle("Resultado Sincronización").setMessage(contador+" Datos sincronizados").create().show();

            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                builder.setMessage("Problemas de sincronización");
                builder.setCancelable(false);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });//negative botton
                AlertDialog dialog = builder.create();
                dialog.show();
                consultaSql();
            }//else
        }//onPostExecute
    }//AsynInsertInv


    private void conectaActualiza (String producto, String cant) {
        String SOAP_ACTION = "ActualizaInv";
        String METHOD_NAME = "ActualizaInv";
        String NAMESPACE = "http://" +strServer+ "/WSk75AlmacenesApp/";
        String URL = "http://" +strServer+ "/WSk75AlmacenesApp";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLActualizaInv soapEnvelope = new XMLActualizaInv(SoapEnvelope.VER11);
            soapEnvelope.XMLActInv(strusr, strpass, folio, strbran, producto,cant);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            mensaje=response.getPropertyAsString("k_estatus");
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

    private class AsyncAutoriza extends AsyncTask<Void, Void, Void> {
        String usuario;

        public AsyncAutoriza(String usuario) {
            this.usuario = usuario;
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            conectaAutoriza(usuario);
            return null;
        }//doInbackground

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if (bandAutori.equals("1")) {
                lyEscanea.setVisibility(View.GONE);
                lyInsert.setVisibility(View.VISIBLE);
            }else {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityInventario.this);
                alerta.setMessage(mensajeAutoriza).setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        dialog.dismiss();
                    }
                });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("¡ERROR!");
                titulo.show();
            }//else
        }//onPostExecute
    }//AsyncAutoriza

    private void conectaAutoriza(String usuario) {
        String SOAP_ACTION = "ValdiSuper";
        String METHOD_NAME = "ValdiSuper";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";
        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLValdiSuper soapEnvelope = new XMLValdiSuper(SoapEnvelope.VER11);
            soapEnvelope.XMLValdiSuper(strusr, strpass, usuario);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            response = (SoapObject) response.getProperty("message");
            bandAutori = response.getPropertyAsString("k_Val").equals("anyType{}") ? "" : response.getPropertyAsString("k_Val");
            mensajeAutoriza = response.getPropertyAsString("k_menssage").equals("anyType{}") ? "" : response.getPropertyAsString("k_menssage");

        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {}
    }//conectaAutoriza

    public void buscar(String prod,String cant){
        String nuevo=prod.substring(0,3)+"";
        if(nuevo.equals("P01") || nuevo.equals("P02") || nuevo.equals("P03") || nuevo.equals("P04") || nuevo.equals("P05")
                || nuevo.equals("P06") || nuevo.equals("P07") || nuevo.equals("8") || nuevo.equals("P09") || nuevo.equals("P10")
                || nuevo.equals("P11") || nuevo.equals("P12") || prod.substring(0,4).equals("http") || prod.substring(0,4).equals("HTTP")
                || nuevo.equals("www") || nuevo.equals("WWW")){
            bepp.play(sonido_de_reproduccion1, 1, 1, 1, 0, 0);
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityInventario.this);
            alerta.setMessage("Producto no válido").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog titulo = alerta.create();
            titulo.setTitle("¡ERROR!");
            titulo.show();
        }else{
            actualizaGuarda(prod,cant);
        }//else
    }//buscar

    public void actualizaGuarda(String prod, String cant){
        boolean bandera=false;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod)){
                bandera=true;
                if(actualizarSql(prod,Integer.parseInt(cant)+"")==true){
                    txtProductoVi.setText(prod);
                    listaInv.get(i).setEscan(cant);
                    mostrarDetalleCod(i);
                }else{
                    Toast.makeText(this, "No se pudó actualizar este código", Toast.LENGTH_SHORT).show();
                }//else no se actualizo
                break;
            }//if
        }//for
        if(bandera==false){
            if(insertarSql(prod,0+"",cant)==true){
                consultaSql();
            }else{
                Toast.makeText(this, "No se pudó guardar este código", Toast.LENGTH_SHORT).show();
            }//else no se guardo
        }//if bandera false
    }//actualizaGuarda

    public void mostrarDetalleCod(int pos){
        adapter.notifyDataSetChanged();
        adapter.index(pos);
        rvInventario.scrollToPosition(pos);
        txtProductoVi.setText(listaInv.get(pos).getProducto());
        txtEscan.setText(listaInv.get(pos).getEscan());
        posicion=pos;
    }//mostrarDetalleCod


    public void buscarEnSql(String prod,String cant){
        String nuevo=prod.substring(0,3)+"";
        if(nuevo.equals("P01") || nuevo.equals("P02") || nuevo.equals("P03") || nuevo.equals("P04") || nuevo.equals("P05")
                || nuevo.equals("P06") || nuevo.equals("P07") || nuevo.equals("8") || nuevo.equals("P09") || nuevo.equals("P10")
                || nuevo.equals("P11") || nuevo.equals("P12") || prod.substring(0,4).equals("http") || prod.substring(0,4).equals("HTTP")
                || nuevo.equals("www") || nuevo.equals("WWW")){
            bepp.play(sonido_de_reproduccion1, 1, 1, 1, 0, 0);
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityInventario.this);
            alerta.setMessage("Producto no válido").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog titulo = alerta.create();
            titulo.setTitle("¡ERROR!");
            titulo.show();
        }else{
            try{
                @SuppressLint("Recycle") Cursor fila = db.rawQuery(
                        "SELECT PRODUCTO,CANTIDAD from INVENTARIOALM WHERE PRODUCTO='"+prod+"'", null);
                if (fila != null && fila.moveToFirst()) {
                    String esc=fila.getString(1);
                    if(esc.equals("1")){

                    }else{
                        actualizarSql(prod,Integer.parseInt(cant)+"");
                    }
                }else{
                    insertarSql(prod,0+"",cant);
                }
                fila.close();
            }catch(Exception e){
                Toast.makeText(ActivityInventario.this,e+"", Toast.LENGTH_SHORT).show();
            }//catch
            consultaSql();
        }//else
    }//consultaSql



    public void consultaSql(){
        try{
            listaInv.clear();
            rvInventario.setAdapter(null);
            int j=-1;
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT PRODUCTO,CANTIDAD,ESCAN FROM INVENTARIOALM ORDER BY PRODUCTO ", null);
            if (fila != null && fila.moveToFirst()) {
                do {
                    j++;
                    if(ProductoAct.equals(fila.getString(0))){
                        posicion=j;
                    }
                    listaInv.add(new Inventario((j+1)+"",fila.getString(0),fila.getString(1),fila.getString(2)));
                }while (fila.moveToNext());

                rvInventario.setAdapter(null);
                adapter= new AdapterInventario(listaInv);
                rvInventario.setAdapter(adapter);
                if(posicion>=0){
                    mostrarDetalleCod(posicion);
                }
            }//if
            fila.close();
        }catch(Exception e){
            Toast.makeText(ActivityInventario.this,
                    "Error al consultar datos de la base de datos interna", Toast.LENGTH_SHORT).show();
        }//catch
    }//consultaSql

    public void consultaPSincro(){
        try{
            listaPSincro.clear();
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT PRODUCTO,CANTIDAD,ESCAN FROM INVENTARIOALM WHERE ESCAN>0 ORDER BY PRODUCTO ", null);
            if (fila != null && fila.moveToFirst()) {
                do {
                    listaPSincro.add(new Inventario("",fila.getString(0),fila.getString(1),fila.getString(2)));
                } while (fila.moveToNext());
            }//if
            fila.close();
        }catch(Exception e){
            Toast.makeText(ActivityInventario.this,
                    "Error al consultar datos de la base de datos interna", Toast.LENGTH_SHORT).show();
        }//catch
    }//consultaPSincro

    public boolean insertarSql(String prod,String cant,String esc){
        boolean vari=false;
        try{
            if(db != null){
                ContentValues valores = new ContentValues();
                valores.put("PRODUCTO", prod);
                valores.put("CANTIDAD", cant);
                valores.put("ESCAN", esc);
                db.insert("INVENTARIOALM", null, valores);
                vari=true;
            }
        }catch(Exception e){vari=false;}return vari;
    }//insertarSql

    public boolean actualizarSql(String prod,String escan){
        boolean var=false;
        try{
            ContentValues valores = new ContentValues();
            valores.put("ESCAN", Integer.parseInt(escan));
            db.update("INVENTARIOALM", valores, "PRODUCTO='"+prod+"'", null);
            var=true;
        }catch(Exception e){var=false;}
        return  var;
    }//actualizarSql

    public void eliminarSql(String sentProd) {//parte de sentencia que es para eliminar prod o todos los productos
        try{
            SQLiteDatabase db = conn.getWritableDatabase();
            db.delete("INVENTARIOALM",sentProd,null);
        }catch(Exception e){}
    }//eliminarSql

}//ActivityInventario
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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
import com.almacen.alamacen202.SetterandGetters.EnvTraspasos;
import com.almacen.alamacen202.SetterandGetters.Folios;
import com.almacen.alamacen202.SetterandGetters.Inventario;
import com.almacen.alamacen202.SetterandGetters.ListProAduSandG;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.XML.XMLActualizaInv;
import com.almacen.alamacen202.XML.XMLFolios;
import com.almacen.alamacen202.XML.XMLValdiSuper;
import com.almacen.alamacen202.XML.XMLValidEsc;
import com.almacen.alamacen202.XML.XMLlistInv;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import dmax.dialog.SpotsDialog;
import pl.droidsonroids.gif.GifImageView;

public class ActivityInventario extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preference,preferenceF;
    private SharedPreferences.Editor editor;
    private boolean comprobar=false;
    private int posicion=0,posicionAnt=0,contInsert=0;
    private String strusr,strpass,strServer,strbran,codeBar,
            ProductoAct="",folio="",fecha="",hora="",mensaje,
            bandAutori,mensajeAutoriza,UserSuper,UbicAct="";
    private ArrayList<Inventario> listaInv = new ArrayList<>();
    private ArrayList<Inventario> listaPSincro = new ArrayList<>();
    private EditText txtFolioInv,txtProductoVi,txtFechaI,txtHoraI,txtProducto,txtEscan,txtUbicc;
    private ArrayList<Folios>listaFol;
    private Button btnGuardar,btnSincronizar,btnElim,btnMas;
    private CheckBox chbMan;
    private RecyclerView rvInventario;
    private AdapterInventario adapter=new AdapterInventario(listaInv);
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private ConexionSQLiteHelper conn;
    private SQLiteDatabase db;
    private RecyclerView rvFolios;//para alertdialog
    private AlertDialog dialog;
    private LinearLayout lyInsert,lyEscanea;
    private Button btnAutoriza;
    private int sonido_correcto,sonido_error;
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

        bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_correcto = bepp.load(ActivityInventario.this, R.raw.sonido_correct, 1);
        sonido_error = bepp.load(ActivityInventario.this, R.raw.error, 1);

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
        txtUbicc        = findViewById(R.id.txtUbicc);

        btnGuardar      = findViewById(R.id.btnGuardar);
        btnSincronizar  = findViewById(R.id.btnSincronizar);
        chbMan          = findViewById(R.id.chbMan);
        rvInventario    = findViewById(R.id.rvInventario);
        btnElim         = findViewById(R.id.btnElim);
        btnMas          = findViewById(R.id.btnMas);

        conn = new ConexionSQLiteHelper(ActivityInventario.this, "bd_INVENTARIO",
                null, Integer.parseInt(getString(R.string.versionBaseDatos)));
        db = conn.getReadableDatabase();
        rvInventario.setLayoutManager(new LinearLayoutManager(ActivityInventario.this));
        keyboard = (InputMethodManager) getSystemService(ActivityInventario.INPUT_METHOD_SERVICE);

        txtProducto.requestFocus();
        txtProducto.setInputType(InputType.TYPE_NULL);
        txtEscan.setEnabled(false);
        txtUbicc.setEnabled(true);
        txtUbicc.setInputType(InputType.TYPE_NULL);
        chbMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                txtProducto.setText("");
                txtProducto.requestFocus();
                txtProducto.setInputType(InputType.TYPE_NULL);
                txtProductoVi.setText("");
                posicion=-1;
                adapter.index(posicion);
                adapter.notifyDataSetChanged();
                ProductoAct="";UbicAct="";
                rvInventario.setAdapter(adapter);
                if(b){//manual
                    //keyboard.showSoftInput(txtProducto, InputMethodManager.SHOW_IMPLICIT);
                    txtEscan.setEnabled(true);
                    txtEscan.setText("");
                    txtUbicc.setText("");
                    txtUbicc.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    btnGuardar.setEnabled(true);
                }else{
                    txtEscan.setText("");
                    txtEscan.setEnabled(false);
                    keyboard.hideSoftInputFromWindow(txtEscan.getWindowToken(), 0);
                    txtUbicc.setText("");
                    txtUbicc.setInputType(InputType.TYPE_NULL);
                    btnGuardar.setEnabled(false);
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
                if (!editable.toString().equals("")) {
                    if (codeBar.equals("Zebra")) {//codebar
                        if(validar(editable.toString())){
                            accionEscanea();
                        }
                    }else{
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if(ban == '\n'){
                                if(validar(editable.toString())){
                                    accionEscanea();
                                }
                                break;
                            }//if
                        }//for
                    }//else

                }//if !editable
            }//after
        });//txtProducto.addTextChanged



        txtUbicc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("") && !s.toString().equals(" ") && txtUbicc.isFocused()
                        && !txtProductoVi.getText().toString().equals("") && !txtEscan.getText().toString().equals("")
                        && Integer.parseInt(txtEscan.getText().toString())>0){
                    if(!chbMan.isChecked()) {//normal
                        ProductoAct=txtProductoVi.getText().toString();
                        UbicAct=s.toString();
                        txtProducto.requestFocus();
                        cambiaUbicacion();
                    }//if
                }//if
            }//aftertextchanged
        });

        txtEscan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtEscan.setText("");
                }else{
                    if(posicion>=0 && txtEscan.getText().toString().equals("")){
                        txtEscan.setText(listaInv.get(posicion).getEscan());
                    }//if
                }//else
            }//onfocus
        });//txtEscan on focus


        txtUbicc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtUbicc.setText("");
                    //keyboard.hideSoftInputFromWindow(txtUbicc.getWindowToken(), 0);
                }else{
                    txtProducto.requestFocus();
                    if(posicion>=0 && txtUbicc.getText().toString().equals("")){
                        txtUbicc.setText(listaInv.get(posicion).getUbi());
                    }//if
                }//else
            }//onFocusChange
        });//txtUbicc.setOnFocusChange

        txtUbicc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    txtProducto.setText("");
                    keyboard.hideSoftInputFromWindow(txtUbicc.getWindowToken(), 0);
                    txtProducto.requestFocus();
                }//if
                return false;
            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboard.hideSoftInputFromWindow(txtEscan.getWindowToken(), 0);
                keyboard.hideSoftInputFromWindow(txtEscan.getWindowToken(), 0);
                String v1=txtProductoVi.getText().toString();
                String v2=txtEscan.getText().toString();
                String v3=txtUbicc.getText().toString();
                String cant="0";

                if(v1.equals("") || (v2.equals("") && Integer.parseInt(v2)<=0) || v3.equals("")){
                    Toast.makeText(ActivityInventario.this, "Campos vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(posicion>=0){
                        if(v3.equals("-") && listaInv.get(posicion).getUbi().equals(v3)){
                            v3=listaInv.get(posicion).getUbi();
                        }
                    }//if posicion

                    ProductoAct=v1;UbicAct=v3;
                    cambioCod(v1,v2+"",v3,true);
                }//else
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
                            new AsyncResActualizaInv(folio).execute();
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
            }//onclick
        });//btnSincronizar onclick

        btnElim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posicion>=0){
                    String p=listaInv.get(posicion).getProducto();
                    String ub=listaInv.get(posicion).getUbi();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            eliminarSql(" PRODUCTO='"+p+"' AND UBIC='"+ub+"'");
                            txtProducto.setText("");
                            txtProductoVi.setText("");
                            txtEscan.setText("");
                            txtUbicc.setText("");
                            txtUbicc.setEnabled(false);
                            ProductoAct="";UbicAct="";
                            consultaSql();
                        }//onclick
                    });//positive button
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO");
                    builder.setMessage("¿Desea eliminar código "+p+" con ubicación "+ub+"?").create().show();
                }else{
                    Toast.makeText(ActivityInventario.this, "Sin código seleccionado",
                            Toast.LENGTH_SHORT).show();
                }//else
            }//onclick
        });//btnElim


        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductoAct=txtProductoVi.getText().toString();
                UbicAct="-";
                txtProducto.requestFocus();
                if(insertarSql(ProductoAct,listaInv.get(posicion).getCantidad(),
                        "1",UbicAct)==true){
                    consultaSql();
                }
            }//onclick
        });//btnMas


        //FOLIO
        if(folio.equals("")){//si no hay folio guardado
            new AsyncFolios().execute();
        }else{
            comprobar=true;
            new AsyncFolios().execute();
            /*txtFolioInv.setText(folio);
            txtFechaI.setText(fecha);
            txtHoraI.setText(hora);
            consultaSql();*/
            //new AsyncResListInv(strbran,folio).execute();
        }//else

    }//onCreate

    public boolean validar(String prod){
        boolean valido=false;
        String nuevo=prod.substring(0,3)+"";
        if(nuevo.equals("P01") || nuevo.equals("P02") || nuevo.equals("P03") || nuevo.equals("P04") || nuevo.equals("P05")
                || nuevo.equals("P06") || nuevo.equals("P07") || nuevo.equals("8") || nuevo.equals("P09") || nuevo.equals("P10")
                || nuevo.equals("P11") || nuevo.equals("P12") || prod.substring(0,4).equals("http") || prod.substring(0,4).equals("HTTP")
                || nuevo.equals("www") || nuevo.equals("WWW")){
            bepp.play(sonido_error, 1, 1, 1, 0, 0);
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
            valido=true;
        }//else
        return valido;
    }//validar

    public void cambioCod(String cod,String cantEsc,String ubi,boolean sumar){//si sumar==false
        //Buscar si hay mas de un código
        boolean find=false;
        if(ubi.equals("")){ubi="-";}
        if(cantEsc.equals("0")){cantEsc="1";}
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(cod) && listaInv.get(i).getUbi().equals(ubi)){
                find=true;
                if(sumar==true){//si sumar ==true
                    if(!chbMan.isChecked()){//normal
                        cantEsc=(Integer.parseInt(cantEsc)+1)+"";
                    }
                    if(actualizarSql(cod,cantEsc+"",ubi)==true){
                        txtProductoVi.setText(cod);
                        listaInv.get(i).setEscan(cantEsc);
                        listaInv.get(i).setUbi(ubi);
                        //keyboard.showSoftInput(txtEscan, InputMethodManager.SHOW_IMPLICIT);
                        mostrarDetalleCod(i);
                    }else{
                        Toast.makeText(this, "No se pudó actualizar este código", Toast.LENGTH_SHORT).show();
                    }//else no se actualizo
                }else{//no es necesario actualizar y solo muestra
                    txtProductoVi.setText(cod);
                    mostrarDetalleCod(i);
                }//else
                break;
            }//if
        }//for
        if(find==false){
            if(chbMan.isChecked()){
                UbicAct=ubi;
                if(insertarSql(ProductoAct,"0", cantEsc,ubi)==true){
                    if(!ubi.equals("-")){
                        eliminarSql("PRODUCTO='"+ProductoAct+"' AND UBIC='-'");
                    }//if
                    consultaSql();
                }else{
                    Toast.makeText(ActivityInventario.this,
                            "Problema al actualizar código", Toast.LENGTH_SHORT).show();
                }//else
            }else{
                new AsyncResUbicacionAlma(cod).execute();
            }
        }//find
    }//cambioCod

    public void accionEscanea(){
        txtProducto.setEnabled(false);
        String escan=txtProducto.getText().toString();
        boolean sumar=false;
        String busqUbic="";
        if(!chbMan.isChecked()){//Normal
            sumar=true;
        }
        if(!escan.equals(txtProductoVi.getText().toString())){//cuando se cambia de codigo
            busqUbic=compararUbi(escan);
            int busqCant=consulCantidad(escan,busqUbic);
            if(txtUbicc.getText().toString().equals("-")){//si campo ubicacion esta vacio
                ProductoAct=txtProductoVi.getText().toString();
                UbicAct="-";
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityInventario.this);
                alerta.setMessage("Se sugiere ingresar ubicación antes de cambiar de código").setCancelable(false);
                alerta.setPositiveButton("ACEPTAR", null);
                alerta.setCancelable(false);
                AlertDialog dialogAlert = alerta.create();
                dialogAlert.setTitle("AVISO");
                dialogAlert.show();
                cambioCod(ProductoAct,busqCant+"",UbicAct,false);
                txtProducto.setEnabled(true);
            }else if(busqUbic.equals("-")){//si no tiene ubicacion aun
                ProductoAct=escan;
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityInventario.this);
                alerta.setMessage("El código "+ProductoAct+" tiene una ubicación vacia").setCancelable(false);
                alerta.setPositiveButton("ACEPTAR",null);
                alerta.setCancelable(false);
                AlertDialog dialogAlert = alerta.create();
                dialogAlert.setTitle("AVISO");
                dialogAlert.show();
                cambioCod(ProductoAct,busqCant+"",busqUbic,false);
                txtProducto.setEnabled(true);
            }else if(busqUbic.equals("")){//si no existe el código en lista
                ProductoAct=escan;
                cambioCod(ProductoAct,busqCant+"",busqUbic,false);
                txtProducto.setEnabled(true);
            } else{//
                ProductoAct=escan;
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityInventario.this);
                alerta.setMessage("El código "+ProductoAct+" esta en la ubicación "+busqUbic+"\n" +
                        "¿Desea agregar una nueva ubicacion?").setCancelable(false);
                alerta.setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UbicAct="-";
                        if(insertarSql(ProductoAct,listaInv.get(posicion).getCantidad(), "1",UbicAct)==true){
                            consultaSql();
                        }
                        txtProducto.setEnabled(true);
                        txtProducto.requestFocus();
                    }
                });
                String finalBusqUbic = busqUbic;
                alerta.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProductoAct=escan;UbicAct= finalBusqUbic;
                        cambioCod(escan,busqCant+"", finalBusqUbic,false);
                        txtProducto.setEnabled(true);
                    }
                });
                alerta.setCancelable(false);
                AlertDialog dialogAlert = alerta.create();
                dialogAlert.setTitle("AVISO");
                dialogAlert.show();
            }//else
        }else{//para seguir escaneando el mismmo código
            busqUbic=txtUbicc.getText().toString();
            int busqCant=consulCantidad(escan,busqUbic);
            cambioCod(escan,busqCant+"",busqUbic,sumar);
            if (chbMan.isChecked()) {//manual
                txtEscan.requestFocus();
            }//else
            txtProducto.setEnabled(true);
        }//else
        txtProducto.setText("");
        txtProducto.requestFocus();
    }//accionEscanea

    public void cambiaUbicacion(){
        int cantEnc=existeUbi(ProductoAct,UbicAct);
        if(cantEnc>=0){//si existe
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
            builder.setPositiveButton("UNIFICAR A UBICACIÓN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!txtEscan.getText().toString().equals("")){
                        int cantAcum=(Integer.parseInt(txtEscan.getText().toString())+cantEnc)-1;//-1 para que hagala accion de actualizar por que si es true entonces sumara en la funcion 1
                        cambioCod(ProductoAct,cantAcum+"",UbicAct,true);
                        eliminarSql("PRODUCTO='"+ProductoAct+"' AND UBIC='-'");
                        consultaSql();
                        txtUbicc.clearFocus();
                        txtUbicc.setCursorVisible(false);
                        txtProducto.requestFocus();
                    }else{
                        Toast.makeText(ActivityInventario.this, "Campo vacio", Toast.LENGTH_SHORT).show();
                    }//
                }//onclick
            });//positive button
            builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    txtUbicc.requestFocus();
                }//onclick
            });//button
            builder.setCancelable(false);
            builder.setTitle("AVISO");
            builder.setMessage("El código "+ProductoAct+" ya tiene registro de la ubicación "
                    +UbicAct+" con "+cantEnc+" piezas\n"+
                    "¿Desea unificar con la ubicación?");
            builder.create().show();
        }else{//
            if(insertarSql(ProductoAct,"0",
                    txtEscan.getText().toString(),UbicAct)==true){
                eliminarSql("PRODUCTO='"+ProductoAct+"' AND UBIC='-'");
                consultaSql();
            }else{
                Toast.makeText(ActivityInventario.this,
                        "Problema al actualizar código", Toast.LENGTH_SHORT).show();
            }//else
            txtUbicc.clearFocus();
            txtUbicc.setCursorVisible(false);
        }//else
    }//cambia ubicacion

    public boolean firtMet() {//firtMet
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {//si hay conexion a internet
            return true;
        } else {
            return false;
        }//else
    }//FirtMet saber si hay conexion a internet

    public void onClickInv(View v){//cada vez que se seleccione un producto en la lista
        keyboard.hideSoftInputFromWindow(txtUbicc.getWindowToken(), 0);
        keyboard.hideSoftInputFromWindow(txtEscan.getWindowToken(), 0);
        posicion = rvInventario.getChildPosition(rvInventario.findContainingItemView(v));
        ProductoAct=listaInv.get(posicion).getProducto();
        //ProdGuard=ProdGuard;
        mostrarDetalleCod(posicion);
        txtProducto.setText("");
        if(chbMan.isChecked()){//manual no
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
                    txtEscan.setText("");
                    txtUbicc.setText("");
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

    public int consulCantidad(String prod,String ubi){//en caso de que no sea manual, se toma la cantidad que se tenia y se suma 1
        int cant=1;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod) &&
                    listaInv.get(i).getUbi().equals(ubi)){
                cant=Integer.parseInt(listaInv.get(i).getEscan());
                break;
            }//if
        }//for
        return cant;
    }//compararCant

    public int compararCantidad(String prod,String ubi){//en caso de que no sea manual, se toma la cantidad que se tenia y se suma 1
        int cant=1;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod) &&
            listaInv.get(i).getUbi().equals(ubi)){
                cant=Integer.parseInt(listaInv.get(i).getEscan())+1;
                break;
            }//if
        }//for
        return cant;
    }//compararCant

    public String compararUbi(String prod){//buscar ubi
        String ubi="";
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod) ){
                ubi=listaInv.get(i).getUbi();
                posicion=i;
                break;
            }//if
        }//for
        return ubi;
    }//compararCant

    public int existeUbi(String prod,String ubic){//devuelve la cantidad de la ubicación encontrada
        boolean var=false;
        int cantEnc=0;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod) && listaInv.get(i).getUbi().equals(ubic)){
                cantEnc=Integer.parseInt(listaInv.get(i).getEscan());
                var=true;
                break;
            }//if
        }//for
        if(var==false){
            cantEnc=-1;
        }//if
        return cantEnc;
    }//existeUbi

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
        new AsyncResListInv(strbran,folio).execute();
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

    private class AsyncResListInv extends AsyncTask<Void, Void, Void> {
        private String suc,folio;
        private boolean conn;

        public AsyncResListInv(String suc, String folio) {
            this.suc = suc;
            this.folio = folio;
        }//constructor

        @Override
        protected void onPreExecute() {
            mDialog.show();
            listaInv.clear();
            contInsert=0;
            posicion=0;
            txtProductoVi.setText("");
            btnElim.setEnabled(false);
            //txtEscan.setText("");
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="k_suc="+suc+"&k_fol="+folio;
                String url = "http://"+strServer+"/ListInv?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                //Log.e(TAG, "Respuesta de la url: " + jsonStr);
                if (jsonStr != null) {
                    try{
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        int num=1;
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            String prod=dato.getString("k_prod");
                            String cant=dato.getString("k_acum");
                            String ubi=dato.getString("k_ubi");
                            listaInv.add(new Inventario((i+1)+"", prod, cant,0+"",ubi,true));
                            if(insertarSql(prod,cant,0+"",ubi)==true){
                                contInsert++;
                            }
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Hubó un problema al consultar datos";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="No fue posible obtener datos del servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            if (listaInv.size()>0) {
                //Toast.makeText(ActivityInventario.this, contInsert+" datos", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
                mostrarLista();
            }else{
                mDialog.dismiss();
                Toast.makeText(ActivityInventario.this, mensaje, Toast.LENGTH_SHORT).show();
            }
            txtProducto.setText("");
        }//onPost
    }//AsyncResListInv

    public boolean conectaRes(String producto,String cantidad,String ubicacion){
        mensaje="";
        boolean var=false;
        String parametros="k_folio="+folio+"&k_suc="+strbran+"&k_prod="+producto+
                "&k_cant="+cantidad+"&k_ubi="+ubicacion;
        String url = "http://"+strServer+"/ActualizaInv?"+parametros;
        String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObj.getJSONArray("Response");
                JSONObject dato = jsonArray.getJSONObject(0);
                mensaje=dato.getString("k_estado");
                var=true;
            }catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mensaje="Sin sincronizar";
                    }//run
                });
            }//catch JSON EXCEPTION
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mensaje="Problemas de datos";
                }//run
            });//runUniTthread
        }//else
        return var;
    }//conectaRes

    private class AsyncResActualizaInv extends AsyncTask<Void, Integer, Void> {

        private String folio,producto,cantidad,ubicacion;
        private boolean conn=true;
        private int contador=0;

        public AsyncResActualizaInv(String folio) {
            this.folio = folio;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            mensaje="";
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true) {
                progressDialog.setMax(listaPSincro.size());
                for (int j = 0; j < listaPSincro.size(); j++) {//for para los registros de cada servidor
                    try {
                        mensaje = "";
                        producto = listaPSincro.get(j).getProducto();
                        cantidad = listaPSincro.get(j).getEscan();
                        ubicacion=listaPSincro.get(j).getUbi();
                        if (conectaRes(producto,cantidad,ubicacion)==true) {
                            eliminarSql(" PRODUCTO='" + producto + "' AND UBIC='"+ubicacion+"'");
                            contador++;
                        }else if(mensaje.equals("0")){
                            break;
                        }//else if
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return null;
                    }//catch
                    progressDialog.setProgress(j);
                }//for
            }else{
                mensaje="Problemas de conexión";
            }//else
            return  null;
        }//doInBackground

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if(mensaje.equals("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                builder.setMessage("Folio cerrado");
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
                builder.setTitle("Problemas de sincronización");
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
        }//onPost
    }//AsyncResActualizaInv

    private class AsyncResUbicacionAlma extends AsyncTask<Void, Void, Void> {
        private String prod,ubFind="";
        private boolean conn;

        public AsyncResUbicacionAlma(String prod) {
            this.prod = prod;
        }//constructor

        @Override
        protected void onPreExecute() {
            mensaje="";
            mDialog.show();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="k_Sucursal="+strbran+"&k_Producto="+prod;
                String url = "http://"+strServer+"/UbicacionAlma?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                //Log.e(TAG, "Respuesta de la url: " + jsonStr);
                if (jsonStr != null) {
                    try{
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            String Ubicc=dato.getString("k_Ubicacion");
                            if(!Ubicc.equals("")){
                                if(Ubicc.charAt(0)=='P' || Ubicc.charAt(0)=='Q'){
                                    ubFind=Ubicc;
                                    break;
                                }else{
                                    ubFind="-";
                                }//else
                            }else{
                                ubFind="-";
                            }//else
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Sin ubicaciónes";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="No fue posible obtener datos del servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión, no fue posible consultar ubicación";
                return null;
            }//else
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            mDialog.dismiss();
            ProductoAct=prod;
            if (!ubFind.equals("")) {
                UbicAct=ubFind;
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventario.this);
                    builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setPositiveButton("ACEPTAR",null);
                AlertDialog dialog = builder.create();
                dialog.show();
                UbicAct="-";
            }//else
            if(insertarSql(prod,"0","1",ubFind)==false){
                Toast.makeText(ActivityInventario.this, "Problema al guardar dato",
                        Toast.LENGTH_SHORT).show();
            }//else
            consultaSql();
        }//onPost
    }//AsyncResUbicacionAlma


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


    /*private class AsyncListInv extends AsyncTask<Void, Void, Void> {
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
                        (i+1)+"", prod, cant,0+"","",true));
                if(insertarSql(prod,cant,0+"","")==true){
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

    */

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

    public void buscar(String prod,String cant,String ubic){
        String nuevo=prod.substring(0,3)+"";
        if(nuevo.equals("P01") || nuevo.equals("P02") || nuevo.equals("P03") || nuevo.equals("P04") || nuevo.equals("P05")
                || nuevo.equals("P06") || nuevo.equals("P07") || nuevo.equals("8") || nuevo.equals("P09") || nuevo.equals("P10")
                || nuevo.equals("P11") || nuevo.equals("P12") || prod.substring(0,4).equals("http") || prod.substring(0,4).equals("HTTP")
                || nuevo.equals("www") || nuevo.equals("WWW")){
            bepp.play(sonido_error, 1, 1, 1, 0, 0);
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
            actualizaGuarda(prod,cant,ubic,false);
        }//else
    }//buscar

    class ComparadorLista implements Comparator<Inventario> {
        public int compare(Inventario a, Inventario b) {
            return a.getProducto().compareTo(b.getProducto());
        }//compare
    }//ComparadorLista

    public int cantidad(String prod){
        int c=0;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod)){
                c=Integer.parseInt(listaInv.get(i).getEscan());
                break;
            }
        }//for
        return c;
    }//cantidad

    public void conteo(String prod,String canti,String ubic){
        boolean bandera=false;
        int cant=0;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod) &&
            listaInv.get(i).getUbi().equals(ubic)){
                bandera=true;
                posicion=i;
                if(canti.equals("--1")){
                    cant=Integer.parseInt(listaInv.get(i).getEscan())+1;
                }else{
                    cant=Integer.parseInt(canti);
                }//else
                listaInv.get(i).setEscan(cant+"");
                txtProductoVi.setText(prod);
                mostrarDetalleCod(i);
                break;
            }//if
        }//for
        if(bandera==false){
            listaInv.add(new Inventario("0",prod,"0",cant+"","",false));
            Collections.sort(listaInv, new ComparadorLista());
            for(int i=0;i<listaInv.size();i++){
                listaInv.get(i).setNum((i+1)+"");
                if(listaInv.get(i).getProducto().equals(prod) &&
                        listaInv.get(i).getUbi().equals(ubic)){
                    posicion=i;
                    if(canti.equals("--1")){
                        cant=1;
                    }else{
                        cant=Integer.parseInt(canti);
                    }//else
                    listaInv.get(i).setEscan(cant+"");
                }//para tomar posicion
            }//for
            mostrarLista();
        }//if bandera false
    }//conteo

    public void actualizaGuarda(String prod, String cant,String ubic,boolean eliminar){
        boolean bandera=false;
        for(int i=0;i<listaInv.size();i++){
            if(listaInv.get(i).getProducto().equals(prod) &&
                    listaInv.get(i).getUbi().equals(ubic)){
                bandera=true;
                if(actualizarSql(prod,Integer.parseInt(cant)+"",ubic)==true){
                    txtProductoVi.setText(prod);
                    listaInv.get(i).setEscan(cant);
                    listaInv.get(i).setUbi(ubic);
                    //keyboard.showSoftInput(txtEscan, InputMethodManager.SHOW_IMPLICIT);
                    mostrarDetalleCod(i);
                }else{
                    Toast.makeText(this, "No se pudó actualizar este código", Toast.LENGTH_SHORT).show();
                }//else no se actualizo
                break;
            }//if
        }//for
        if(bandera==false){
            if(chbMan.isChecked()){//manual
                if(insertarSql(prod,"0",cant,ubic)==true){
                    ProductoAct=prod;UbicAct=ubic;
                    if(eliminar==true){
                        eliminarSql("PRODUCTO='"+ProductoAct+"' AND UBIC='-'");
                        consultaSql();
                    }//eliminar
                }else{
                    Toast.makeText(this, "No se guardó dato", Toast.LENGTH_SHORT).show();
                }//else
            }else{
                txtEscan.setText("");
                txtUbicc.setText("");
                txtProductoVi.setText("");
                new AsyncResUbicacionAlma(txtProducto.getText().toString()).execute();
            }
        }//if bandera false
    }//actualizaGuarda

    public void mostrarLista(){
        btnElim.setEnabled(true);
        rvInventario.setAdapter(null);
        adapter= new AdapterInventario(listaInv);
        rvInventario.setAdapter(adapter);
        if(posicion>=0){
            mostrarDetalleCod(posicion);
        }
    }//mostrarLista

    public void mostrarDetalleCod(int pos){
        adapter.notifyDataSetChanged();
        adapter.index(pos);
        rvInventario.scrollToPosition(pos);
        txtProductoVi.setText(listaInv.get(pos).getProducto());
        txtEscan.setText(listaInv.get(pos).getEscan());
        txtUbicc.setText(listaInv.get(pos).getUbi());
        posicion=pos;
        ProductoAct=txtProductoVi.getText().toString();
        UbicAct=txtUbicc.getText().toString();

        txtUbicc.setEnabled(true);
        btnMas.setEnabled(false);
        if(!UbicAct.equals("-") && !UbicAct.equals("")){
            txtUbicc.setEnabled(false);
            btnMas.setEnabled(true);
        }
        txtProducto.requestFocus();

    }//mostrarDetalleCod


    public void buscarEnSql(String prod,String cant){
        String nuevo=prod.substring(0,3)+"";
        if(nuevo.equals("P01") || nuevo.equals("P02") || nuevo.equals("P03") || nuevo.equals("P04") || nuevo.equals("P05")
                || nuevo.equals("P06") || nuevo.equals("P07") || nuevo.equals("8") || nuevo.equals("P09") || nuevo.equals("P10")
                || nuevo.equals("P11") || nuevo.equals("P12") || prod.substring(0,4).equals("http") || prod.substring(0,4).equals("HTTP")
                || nuevo.equals("www") || nuevo.equals("WWW")){
            bepp.play(sonido_error, 1, 1, 1, 0, 0);
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
                    //if(esc.equals("1")){

                    //}else{
                        //actualizarSql(prod,Integer.parseInt(cant)+"");
                    //}
                }else{
                    //insertarSql(prod,0+"",cant);
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
            posicion=j;
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT PRODUCTO,CANTIDAD,ESCAN,UBIC FROM INVENTARIOALM ORDER BY PRODUCTO ", null);
            if (fila != null && fila.moveToFirst()) {
                do {
                    j++;
                    if(ProductoAct.equals(fila.getString(0)) &&
                    UbicAct.equals(fila.getString(3))){
                        posicion=j;
                    }
                    listaInv.add(new Inventario((j+1)+"",fila.getString(0),
                            fila.getString(1),fila.getString(2),
                            fila.getString(3),true));
                }while (fila.moveToNext());

                rvInventario.setAdapter(null);
                adapter= new AdapterInventario(listaInv);
                rvInventario.setAdapter(adapter);
                if(posicion>=0){
                    mostrarDetalleCod(posicion);
                }else{
                    UbicAct="";ProductoAct="";
                    adapter.index(posicion);
                }//else
                btnElim.setEnabled(true);
            }//if
            fila.close();
        }catch(Exception e){
            Toast.makeText(ActivityInventario.this,
                    "Sin datos", Toast.LENGTH_SHORT).show();
        }//catch
    }//consultaSql

    public void consultaPSincro(){
        try{
            listaPSincro.clear();
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT PRODUCTO,CANTIDAD,ESCAN,UBIC FROM INVENTARIOALM WHERE ESCAN>0 ORDER BY PRODUCTO ", null);
            if (fila != null && fila.moveToFirst()) {
                do {
                    listaPSincro.add(new Inventario("",fila.getString(0),fila.getString(1),
                            fila.getString(2),fila.getString(3),true));
                } while (fila.moveToNext());
            }//if
            fila.close();
        }catch(Exception e){
            Toast.makeText(ActivityInventario.this,
                    "Error al consultar datos de la base de datos interna", Toast.LENGTH_SHORT).show();
        }//catch
    }//consultaPSincro

    public boolean insertarSql(String prod,String cant,String esc,String ubic){
        boolean vari=false;
        if(ubic.equals("") || ubic.equals(" ")){ubic="-";}
        try{
            if(db != null){
                ContentValues valores = new ContentValues();
                valores.put("PRODUCTO", prod);
                valores.put("CANTIDAD", cant);
                valores.put("ESCAN", esc);
                valores.put("UBIC",ubic);
                db.insert("INVENTARIOALM", null, valores);
                vari=true;
            }
        }catch(Exception e){vari=false;}return vari;
    }//insertarSql

    public boolean actualizarSql(String prod,String escan,String ubic){
        boolean var=false;
        try{
            ContentValues valores = new ContentValues();
            valores.put("ESCAN", Integer.parseInt(escan));
            db.update("INVENTARIOALM", valores,
                    "PRODUCTO='"+prod+"' AND UBIC='"+ubic+"'", null);
            consultaSql();
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
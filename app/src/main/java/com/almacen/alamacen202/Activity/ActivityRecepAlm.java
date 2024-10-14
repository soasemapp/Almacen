package com.almacen.alamacen202.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorRecepAlm;
import com.almacen.alamacen202.Adapter.AdaptadorTraspasos;
import com.almacen.alamacen202.Imprecion.BluetoothPrint;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class ActivityRecepAlm extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private boolean datos=false,modificados=false;
    private int posicion=0,posicion2=0,posG=-1,TOTP=0,RECEP=0;
    private String strusr,strpass,strbran,strServer,codeBar,mensaje,Producto="",serv,Folio="",impresora;
    private ArrayList<Traspasos> listaTrasp = new ArrayList<>();
    private EditText txtProd,txtCantidad,txtCantSurt,txtUbicT;
    private AutoCompleteTextView spAlm;
    private ImageView ivProd;
    private TextView tvProd;
    private Button btnBuscar,btnAtras,btnAdelante,btnCorr,btnBusc;
    private RecyclerView rvTraspasos;
    private AdaptadorRecepAlm adapter;
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private String urlImagenes,extImg;
    private int sonido_correcto,sonido_error;
    private SoundPool bepp;
    private String almE;
    Context context = this;
    AlertDialog dialog6 = null;
    AlertDialog.Builder builder6;
    private AlertDialog alertDialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recep_alm);

        MyToolbar.show(this, "Recepción Almacén Morelos", true);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = preference.edit();
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");
        impresora = preference.getString("Impresora", "null");
        urlImagenes=preference.getString("urlImagenes", "null");
        extImg=preference.getString("ext", "null");

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

        mDialog = new SpotsDialog.Builder().setContext(ActivityRecepAlm.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        progressDialog = new ProgressDialog(ActivityRecepAlm.this);//parala barra de
        progressDialog.setMessage("Procesando datos....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        txtProd    = findViewById(R.id.txtProducto);
        txtCantidad = findViewById(R.id.txtCantidad);
        txtCantSurt = findViewById(R.id.txtCantSurt);
        tvProd      = findViewById(R.id.tvProd);
        btnBuscar  = findViewById(R.id.btnBuscar);
        btnAtras    = findViewById(R.id.btnAtras);
        btnAdelante =findViewById(R.id.btnAdelante);
        ivProd      = findViewById(R.id.ivProd);
        btnCorr     = findViewById(R.id.btnCorr);
        //txtTotPza = findViewById(R.id.txtTotPza);
        txtUbicT = findViewById(R.id.txtUbicT);
        //btnImpr = findViewById(R.id.btnImpr);
        spAlm = findViewById(R.id.spAlm);
        btnBusc = findViewById(R.id.btnBusc);

        bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_correcto = bepp.load(ActivityRecepAlm.this, R.raw.sonido_correct, 1);
        sonido_error = bepp.load(ActivityRecepAlm.this, R.raw.error, 1);

        rvTraspasos    = findViewById(R.id.rvTraspasos);
        rvTraspasos.setLayoutManager(new LinearLayoutManager(ActivityRecepAlm.this));
        adapter = new AdaptadorRecepAlm(listaTrasp);
        keyboard = (InputMethodManager) getSystemService(ActivityRecepTraspMultSuc.INPUT_METHOD_SERVICE);

        txtProd.setInputType(InputType.TYPE_NULL);
        //txtProd.requestFocus();


        btnBusc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaTrasp.size()>0 && listaTrasp.get(posicion).isSincronizado()==false){
                    posicion2=posicion;
                    try {
                        String resp= String.valueOf(new AsyncActualizar(Folio,listaTrasp.get(posicion).getProducto(),
                                listaTrasp.get(posicion).getCantSurt()+"",
                                "change",false,Producto).execute().get());
                        resp=resp;
                        alertBusca();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    if(listaTrasp.size()>0) {
                        alertBusca();
                    }else{
                        Toast.makeText(context, "Sin datos para buscar", Toast.LENGTH_SHORT).show();
                    }//else
                }//else
            }//onclcik
        });

        txtProd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                Producto=editable.toString();
                if(!editable.toString().equals("")){
                    if (codeBar.equals("Zebra")) {
                        Producto=Producto.trim();
                        Producto=Producto.replaceAll("(\n|\r)", "");
                        posicion2=posG;
                        cambio(Producto,true);
                        txtProd.setText("");
                        txtProd.requestFocus();
                    }else{
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if (ban == '\n') {
                                posicion2=posG;
                                cambio(Producto,true);
                                txtProd.setText("");
                                break;
                            }//if
                        }//for
                    }//else
                }//if es diferente a vacio
            }//after
        });//txtProd textchange

        txtProd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId ==0) {
                    txtProd.requestFocus();
                    return true;
                }//if action done
                return false;
            }//oneditoraction
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listaTrasp.size()>0 && listaTrasp.get(posicion).isSincronizado()==false){
                    posicion2=posicion;
                    new AsyncActualizar(Folio,listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt()+"",
                            "change",false,Producto).execute();
                }else {
                    if(!almE.equals("")){
                        Folio="";
                        rvTraspasos.setAdapter(null);
                        limpiar();
                        posicion=0;
                        new AsyncReceConSinFol().execute();
                    }else{
                        Toast.makeText(context, "No hay almacén para buscar codigos", Toast.LENGTH_SHORT).show();
                    }//else
                }//else
            }//onclick
        });//btnGuardar setonclick

        btnAdelante.setOnClickListener(new View.OnClickListener() {//boton adelante
            @Override
            public void onClick(View view) {
                posicion2=posicion;
                cambio("next",false);
            }//onclick
        });//btnadelante setonclicklistener

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion2=posicion;
                cambio("back",false);
            }//onclick
        });//btnatras setonclicklistener

        btnCorr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaTrasp.get(posicion).isSincronizado()==false){
                    posicion2=posicion;
                    new AsyncActualizar(Folio,listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt()+"",
                            "change",false,Producto).execute();
                }else {
                    Toast.makeText(ActivityRecepAlm.this, "Sin cambios", Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });//btnCorr



        /*btnImpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaTrasp.size()>0){//si hay datos para imprimir
                    if(listaTrasp.get(posicion).isSincronizado()==false){
                        posicion2=posicion;
                        new AsyncActualizar(Folio,listaTrasp.get(posicion).getProducto(),
                                listaTrasp.get(posicion).getCantSurt()+"",
                                "change",false,Producto).execute();
                    }else{
                        ImprimirTicketRec(RECEP);
                    }//else
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                    builder.setPositiveButton("ACEPTAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("Sin datos para imprimir").create().show();
                }//else
            }//onclick
        });*/

        new AsyncConsulAlm().execute();
    }//onCreate


    public String folio(String folio){
        if (folio.length() < 7) {
            int fo = folio.length();
            switch (fo) {
                case 1:
                    folio = "000000" + folio;
                    break;
                case 2:
                    folio = "00000" + folio;
                    break;
                case 3:
                    folio ="0000" + folio;
                    break;
                case 4:
                    folio ="000" + folio;
                    break;
                case 5:
                    folio ="00" + folio;
                    break;
                case 6:
                    folio = "0" + folio;
                    break;
                default:
                    folio=folio;
                    break;
            }//switch
        }//if
        return folio;
    }

    public void alertBusca(){
        btnBusc.setEnabled(false);
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityRecepAlm.this);
        LayoutInflater inflater = ActivityRecepAlm.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_buscprod, null);
        alert.setView(dialogView);
        TextView tvTit = dialogView.findViewById(R.id.tvTit);
        EditText txtBuscaP = dialogView.findViewById(R.id.txtBuscaP);
        Button btnB = dialogView.findViewById(R.id.btnB);
        tvTit.setText("Buscar Producto");


        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtBuscaP.getText().toString().equals("")){
                    String comparar=txtBuscaP.getText().toString().trim();
                    boolean existe=false;
                    for(int i=0;i<listaTrasp.size();i++){
                        if(listaTrasp.get(i).getProducto().equals(comparar)){
                            btnBusc.setEnabled(true);
                            existe=true;
                            alertDialog.dismiss();
                            posicion=i;
                            mostrarDetalleProd();
                            break;
                        }//if
                    }
                    if(existe==false){
                        btnBusc.setEnabled(true);
                        bepp.play(sonido_error, 1, 1, 1, 0, 0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                        builder.setTitle("AVISO");
                        builder.setMessage("No existe "+Producto+" en la lista");
                        builder.setCancelable(false);
                        builder.setNegativeButton("OK",null);
                        AlertDialog dialogg = builder.create();
                        dialogg.show();
                    }
                }else{
                    Toast.makeText(ActivityRecepAlm.this, "Campo Vacío", Toast.LENGTH_SHORT).show();
                }//else
            }//onclick
        });//btnB

        alert.setCancelable(false);
        alert.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                keyboard.hideSoftInputFromWindow(txtBuscaP.getWindowToken(), 0);
                btnBusc.setEnabled(true);
                txtProd.requestFocus();
            }
        });//cerrar

        alertDialog = alert.create();
        alertDialog.show();
        txtBuscaP.requestFocus();
    }//alertBusca

    public boolean firtMet() {//firtMet
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {//si hay conexion a internet
            return true;
        }else {
            return false;
        }//else
    }//FirtMet saber si hay conexion a internet

    public void cambiaProd(){
        if(posicion==0 && listaTrasp.size()>1){
            btnAdelante.setEnabled(true);
            btnAdelante.setBackgroundTintList(null);
            btnAdelante.setBackgroundResource(R.drawable.btn_background3);
            btnAtras.setEnabled(false);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));

        }else if(posicion+1==listaTrasp.size() && listaTrasp.size()>1){
            btnAtras.setEnabled(true);
            btnAtras.setBackgroundTintList(null);
            btnAtras.setBackgroundResource(R.drawable.btn_background3);
            btnAdelante.setEnabled(false);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else if(listaTrasp.size()==1){
            btnAtras.setEnabled(false);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            btnAdelante.setEnabled(false);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else{
            btnAtras.setEnabled(true);
            btnAtras.setBackgroundTintList(null);
            btnAtras.setBackgroundResource(R.drawable.btn_background3);
            btnAdelante.setEnabled(true);
            btnAdelante.setBackgroundTintList(null);
            btnAdelante.setBackgroundResource(R.drawable.btn_background3);
        }//else
    }//cambiaProd

    public void onClickLista(View v){//cada vez que se seleccione un producto en la lista
        if(posicion>=0 && listaTrasp.get(posicion).isSincronizado()==false){
            posicion2=posG;
            Producto=listaTrasp.get(rvTraspasos.getChildPosition(rvTraspasos.findContainingItemView(v))).getProducto();
        }else{
            posicion2 = rvTraspasos.getChildPosition(rvTraspasos.findContainingItemView(v));
        }
        cambio("change",false);
    }//onClickLista


    public void cambio(String var,boolean sumar){
        if(!listaTrasp.get(posicion2).getProducto().equals(Producto) && posG!=-1 && listaTrasp.get(posicion2).isSincronizado()==false){//identificando que prod anterior no se sincronizó
            new AsyncActualizar(Folio,listaTrasp.get(posicion2).getProducto(),
                    listaTrasp.get(posicion2).getCantSurt(),var,sumar,Producto).execute();
        }else{//cuando se escanea o por botones de adelante, atras y onclick en lista
            if(sumar==true){//al escanear
                evaluarEscaneo(Producto);
            }else{
                tipoCambio(var);
                mostrarDetalleProd();
            }
        }//else
    }//alert

    public void tipoCambio(String var){
        switch (var){
            case "next":
                posicion++;break;
            case "back":
                posicion--;break;
            case "change":
                posicion=posicion2;
                posicion2=0;break;
            default:posicion=encontrarPosEnLista(var);break;
        }
    }
    public int totPazas(){
        int tot=0;
        for (int i=0;i<listaTrasp.size();i++){
            if(listaTrasp.get(i).isSincronizado()==true){
                tot=tot+Integer.parseInt(listaTrasp.get(i).getCantSurt());
            }
        }
        return tot;
    }

    public void mostrarDetalleProd(){//detalle por producto seleccionado
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        rvTraspasos.scrollToPosition(posicion);
        //Producto=listaTrasp.get(posicion).getProducto();
        tvProd.setText(listaTrasp.get(posicion).getProducto());
        txtCantidad.setText(listaTrasp.get(posicion).getCantidad());
        txtCantSurt.setText((Integer.parseInt(listaTrasp.get(posicion).getCantSurt())+
                Integer.parseInt(listaTrasp.get(posicion).getExist()))+"");//exist se tomara como campo que guarda lo que ya se escaneo y sesta en tabla de kepler
        txtUbicT.setText(listaTrasp.get(posicion).getUbic());

        Picasso.with(getApplicationContext()).
                load(urlImagenes +
                        tvProd.getText().toString() + extImg)
                .error(R.drawable.aboutlogo)
                .fit()
                .centerInside()
                .into(ivProd);
        if(Integer.parseInt(listaTrasp.get(posicion).getCantidad())==Integer.parseInt(listaTrasp.get(posicion).getCantSurt())){
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }else{
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
        }
        cambiaProd();

        if(!txtCantSurt.getText().toString().equals("") && Integer.parseInt(txtCantSurt.getText().toString())>0){
            btnCorr.setEnabled(true);
            btnCorr.setBackgroundTintList(null);
            btnCorr.setBackgroundResource(R.drawable.btn_background4);
        }else{
            btnCorr.setEnabled(false);
            btnCorr.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }
        posG=posicion;
    }//mostrarDetalleProd

    public void limpiar(){
        tvProd.setText("");
        txtCantidad.setText("");
        txtCantSurt.setText("");
        ivProd.setImageResource(R.drawable.logokepler);
        txtUbicT.setText("");
        btnAtras.setEnabled(false);
        btnAtras.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));
        btnAdelante.setEnabled(false);
        btnAdelante.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));
        btnCorr.setEnabled(false);
        btnCorr.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));
        posG=-1;
    }//limpiar

    public int encontrarPosEnLista(String prod){
        int p=posG;
        for(int i=0;i<listaTrasp.size();i++){
            if(listaTrasp.get(i).getProducto().equals(prod)){
                p=i;
                break;
            }//if
        }
        return p;
    }

    public void evaluarEscaneo(String prod){
        limpiar();
        boolean existe=false;
        for(int i=0;i<listaTrasp.size();i++){
            if(listaTrasp.get(i).getProducto().equals(prod)){
                posicion=i;
                existe=true;
                int cant=Integer.parseInt(listaTrasp.get(i).getCantidad());
                int recep=Integer.parseInt(listaTrasp.get(i).getExist());//cantidad de ya escaneados
                int cantS=Integer.parseInt(listaTrasp.get(i).getCantSurt());
                if(recep+(cantS+1)<=cant){
                    cantS++;
                    listaTrasp.get(i).setCantSurt(cantS+"");
                    listaTrasp.get(i).setSincronizado(false);
                    RECEP++;
                    modificados=true;
                    if((recep+cantS)==cant){
                        posicion2=i;
                        new AsyncActualizar(Folio,prod,cantS+"","change",false,Producto).execute();
                    }
                }else{
                    bepp.play(sonido_error, 1, 1, 1, 0, 0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                    builder.setPositiveButton("ACEPTAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("Excede cantidad").create().show();
                }
                break;
            }//if
        }
        if(existe==false){
            bepp.play(sonido_error, 1, 1, 1, 0, 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
            builder.setPositiveButton("ACEPTAR",null);
            builder.setCancelable(false);
            builder.setTitle("No existe "+prod+" en la lista").create().show();
        }
        mostrarDetalleProd();
    }//evaluar

    public boolean surtTodos(){
        boolean surt=false;
        int c=0;
        for(int i=0;i<listaTrasp.size();i++){
            int cant=Integer.parseInt(listaTrasp.get(i).getCantidad());
            int recep=Integer.parseInt(listaTrasp.get(i).getExist());//campo usado para guardar lo que ya fue surtido
            int cantS=Integer.parseInt(listaTrasp.get(i).getCantSurt());
            if(cant==(cantS+recep)){
                c++;
            }
        }
        if(c==listaTrasp.size()){
            surt=true;
        }
        return surt;
    }


    public void verLista(){
        txtProd.requestFocus();
        adapter = new AdaptadorRecepAlm(listaTrasp);
        rvTraspasos.setAdapter(adapter);
        txtProd.setEnabled(true);
        txtProd.requestFocus();
        btnBusc.setEnabled(true);
        if(posicion>=listaTrasp.size()){
            posicion=listaTrasp.size()-1;
            btnBusc.setEnabled(false);
        }
        mostrarDetalleProd();
    }//ver lista

    private class AsyncReceConSinFol extends AsyncTask<Void, Void, Void> {

        private boolean conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mDialog.isShowing()){
                mDialog.show();
            }
            rvTraspasos.setAdapter(null);
            limpiar();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="sucursal="+strbran+"&k_alm="+almE;
                String url = "http://"+strServer+"/RecepMultSucSinFol?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        int num=1;
                        listaTrasp.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaTrasp.add(new Traspasos(num+"",dato.getString("PRODUCTO"),dato.getString("CANTIDAD"),
                                    dato.getString("UBICACION"),"0",dato.getString("RECEPCION"),true));
                            num++;
                            mensaje="";
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="No hay códigos con existencia en este almacén";
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
            }
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            if(mensaje.equals("")) {
                mDialog.dismiss();
                verLista();
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setPositiveButton("ACEPTAR",null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage(mensaje).create().show();
            }//else
        }//onPost
    }//AsyncReceConSinFol

    private class AsyncActualizar extends AsyncTask<Void, Void, Void> {

        private String folio,producto,cantidad,var,ProductoActual,newCant,exist;
        private boolean conn=true,sumar;
        public AsyncActualizar(String folio,String producto, String cantidad,
                               String var,boolean sumar,String ProductoActual) {
            this.folio=folio;
            this.producto = producto;
            this.cantidad = cantidad;
            this.var=var;
            this.sumar=sumar;
            this.ProductoActual=ProductoActual;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                String parametros="sucursal="+strbran+"&producto="+producto+
                        "&cantidad="+cantidad+"&usuario="+strusr;
                String url = "http://"+strServer+"/recemul?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        mensaje=dato.getString("MENSAJE");
                        if(!mensaje.equals("SINCRONIZADO") ){
                            newCant=dato.getString("CANT");;
                        }
                    } catch (final JSONException e) {
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
                            mensaje="Problemas con el servidor";
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
            if(conn==false){
                mDialog.dismiss();
                Toast.makeText(ActivityRecepAlm.this, "Sin conexión a internet\n"+
                        "No se podrá seguir escaneando a menos que se actualice este producto", Toast.LENGTH_SHORT).show();
            }else if (mensaje.equals("SINCRONIZADO")) {
                Toast.makeText(ActivityRecepAlm.this, producto+" Sincronizado", Toast.LENGTH_SHORT).show();
                bepp.play(sonido_correcto, 1, 1, 1, 0, 0);

                try{
                    String resultado= String.valueOf(new AsyncReceConSinFol().execute().get());
                    resultado=resultado;
                    if(sumar==true){//al escanear
                        evaluarEscaneo(ProductoActual);
                    }else{
                        tipoCambio(var);
                        mostrarDetalleProd();
                    }
                }catch(Exception e){}//catch

            }else if(mensaje.equals("PROBLEMAS AL REGISTRAR")){
                mDialog.dismiss();
                bepp.play(sonido_error, 1, 1, 1, 0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setPositiveButton("ACEPTAR",null);
                builder.setCancelable(false);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje).create().show();
            }else{
                mDialog.dismiss();
                bepp.play(sonido_error, 1, 1, 1, 0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncReceConSinFol().execute();
                    }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listaTrasp.get(posicion).setExist(listaTrasp.get(posicion).getCantidad());
                        listaTrasp.get(posicion).setCantSurt(newCant+"");
                        mostrarDetalleProd();
                    }
                });
                builder.setCancelable(false);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje+
                        "\n\n¿DESEA ACTUALIZAR DATOS?(SI ACTUALIZA LAS PIEZAS QUE NO SE HAYAN ESCANEADO SE PERDERAN)").create().show();

            }//else
        }//onPost
    }//AsyncActualizar

    private class AsyncConsulAlm extends AsyncTask<Void, Void, String> {

        private boolean conn;
        private ArrayList <String> listaAlm= new ArrayList<>();
        private ArrayList <String> listaNomAlm= new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            almE="";
            if(mDialog.isShowing()==false){
                mDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                String parametros="k_Sucursal="+strbran;
                String url = "http://"+strServer+"/consulAlm?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if(jsonStr != null) {
                    try {

                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaAlm.add(dato.getString("k_alm"));
                            listaNomAlm.add(dato.getString("k_nomAlm"));
                            mensaje="";
                        }//for
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Sin datos";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="Problema actualizar";
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(conn==true && mensaje.equals("")) {
                mDialog.dismiss();
                ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                        ActivityRecepAlm.this,R.layout.drop_down_item,listaNomAlm);
                spAlm.setAdapter(adaptador);
                spAlm.setText(listaNomAlm.get(0),false);
                almE=listaAlm.get(0);

                spAlm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        limpiar();
                        rvTraspasos.setAdapter(null);
                        almE = listaAlm.get(position);
                    }//onItemClick
                });
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setTitle("AVISO");
                builder.setMessage("Problema al consultar almacenes");
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else<
        }//onPost
    }//AsyncConsulAlm

    public void alTerminar(int opc){
        switch (opc){
            case 1:
                if(surtTodos()==true){
                    Folio="";
                    rvTraspasos.setAdapter(null);
                    limpiar();
                    posicion=0;
                    new AsyncReceConSinFol().execute();
                }//if surtio todos
                break;
            case 2:
                startActivity(new Intent(ActivityRecepAlm.this, ActivityRecepTraspMultSuc.class));
                finish();
                break;
        }//switch
    }//alTerminar

    @Override
    public void onBackPressed() {
        if(modificados==true){
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("CANCELAR",null);
            builder.setCancelable(false);
            builder.setTitle("AVISO").setMessage("Se hicieron movimientos ¿desea salir?").create().show();
        }else{
            finish();
        }
    }//onBackPressed

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoverflow5, menu);
        MenuItem itemOtro = menu.findItem(R.id.itOtro);
        itemOtro.setTitle("Traspaso");
        return true;
    }//onCreateOptionsMenu

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itOtro:
                if(listaTrasp.size()>0 && listaTrasp.get(posicion).isSincronizado()==false){
                    posicion2=posicion;
                    new AsyncActualizar(Folio,listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt()+"",
                            "change",false,Producto).execute();
                }else {
                    startActivity(new Intent(ActivityRecepAlm.this, ActivityRecepTraspMultSuc.class));
                    finish();
                }//else
                break;
        }
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected


    public void imprimir(int Cont, BluetoothPrint imprimir){
        String empresa;
        int imagen;
        switch (strServer) {
            case "jacve.dyndns.org:9085":
                empresa="JACVE";imagen=R.drawable.jacveprint;
                break;
            case "sprautomotive.servehttp.com:9085":
                empresa="VIPLA";imagen=R.drawable.viplaprint;
                break;
            case "cecra.ath.cx:9085":
                empresa="CECRA";imagen=R.drawable.cecraprint;
                break;
            case "guvi.ath.cx:9085":
                empresa="GUVI";imagen=R.drawable.guviprint;
                break;
            case "cedistabasco.ddns.net:9085":
                empresa="PRESSA";imagen=R.drawable.pressaprint;
                break;
            case "autodis.ath.cx:9085":
                empresa="AUTODIS";imagen=R.drawable.autodisprint;
                break;
            case "sprautomotive.servehttp.com:9090":
                empresa="RODATECH";imagen=R.drawable.rodaprint;
                break;
            case "sprautomotive.servehttp.com:9095":
                empresa="PARTECH";imagen=R.drawable.partechprint;
                break;
            case "sprautomotive.servehttp.com:9080":
                empresa="SHARK";imagen=R.drawable.sharkprint;
                break;
            case "vazlocolombia.dyndns.org:9085":
                empresa="VAZLO COLOMBIA";imagen=R.drawable.bhpprint;
                break;
            default:
                empresa="PRUEBAS";imagen=R.drawable.aboutlogo;
                break;
        }//swicth
        if (imprimir.checkConnection() == true) {
            imprimir.printListRecepT(empresa, strusr, Folio,  listaTrasp, String.valueOf(Cont), R.drawable.aboutlogo,"1");
            imprimir.disconnectBT();
        } else {
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityRecepAlm.this);
            alerta.setMessage("Verifique que la impresora este encendida \n o que tenga el bluetooth hablitado").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    editor.putString("Impresora", "null");
                    editor.commit();
                    impresora = preference.getString("Impresora", "null");

                    Intent intent = new Intent(Settings.
                            ACTION_BLUETOOTH_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });//alerta
            AlertDialog titulo = alerta.create();
            titulo.setTitle("¡AVISO!");
            titulo.show();
        }//else
    }//imprimir

    public void ImprimirTicketRec(int Cont) {

        builder6 = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pantallaimprimiendo, null);
        builder6.setView(dialogView);
        builder6.setCancelable(false);
        dialog6 = builder6.create();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        dialog6.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog6.dismiss();
            }
        }, 3000);

        BluetoothPrint imprimir = new BluetoothPrint(context, getResources());
        if (!impresora.equals("null")) {
            imprimir.FindBluetoothDevice(impresora);
            imprimir.openBluetoothPrinter();
            imprimir(Cont,imprimir);
        }else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            List<String> listDevices = new ArrayList<String>();
            for (BluetoothDevice btd : pairedDevices) {
                listDevices.add(btd.getName());
            }
            String[] opciones = new String[listDevices.size()];
            for (int i = 0; i < listDevices.size(); i++) {
                opciones[i] = listDevices.get(i);
            }
            if (opciones.length > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepAlm.this);
                builder.setTitle("Seleccione una impresoras emparejada");
                BluetoothPrint finalImprimir = imprimir;
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("Impresora", opciones[which]);
                        editor.commit();
                        impresora = preference.getString("Impresora", "null");

                        finalImprimir.FindBluetoothDevice(opciones[which]);
                        finalImprimir.openBluetoothPrinter();
                        imprimir(Cont,finalImprimir);
                    }//onclick
                });//setitem
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Intent intent = new Intent(Settings.
                        ACTION_BLUETOOTH_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }//else
    }//ImprimirTicket

}//Activity
package com.almacen.alamacen202.Activity;

import static android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;

import android.annotation.SuppressLint;
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
import android.os.Build;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorCajaxProd;
import com.almacen.alamacen202.Adapter.AdaptadorEnvTraspasos;
import com.almacen.alamacen202.Adapter.AdaptadorEnvTraspasos2;
import com.almacen.alamacen202.Adapter.AdapterListaCajas;
import com.almacen.alamacen202.Imprecion.BluetoothPrint;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.CAJASSANDG;
import com.almacen.alamacen202.SetterandGetters.CajaXProd;
import com.almacen.alamacen202.SetterandGetters.EnvTraspasos;
import com.almacen.alamacen202.SetterandGetters.ListaIncidenciasSandG;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dmax.dialog.SpotsDialog;

public class ActivityEnvTraspMultSuc2 extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private int posicion=0,posicion2=0,posG=0,CAJAACT=1,posCaja=0,cantCajaOr,pC=0,TOTPZA=0;
    private String strusr,strpass,strbran,strServer,codeBar,mensaje;
    private String Producto="",ProdGuard="",Linea="";
    private ArrayList<EnvTraspasos> lista = new ArrayList<>();
    private ArrayList<ListaIncidenciasSandG> listaIncidencias = new ArrayList<>();
    private EditText txtCantidad,txtCantSurt,txtProducto,txtEnv,txtUbi,txtTotPza,txtCantSurtCont;
    private AutoCompleteTextView spLineas,spAlm;
    private ImageView ivProd;
    private TextView tvProd;
    private Button btnBuscarFol,btnAtras,btnAdelante,btnBusc;
    private RecyclerView rvEnvTrasp;
    private AdaptadorEnvTraspasos2 adapter;
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private String urlImagenes,extImg,impresora;
    private int sonido_correcto,sonido_error,posX,posY,x=0,y=0;
    private SoundPool bepp;
    private AlertDialog.Builder builder6;
    private Context context = this;
    private AlertDialog dialog6 = null;
    private AlertDialog alertDialog=null;
    private boolean escan=false,mover=false;
    private ScrollView scrollView;
    private TextInputLayout tIlSurt,tIlSurtN;
    private LinearLayout lyCaja,lyFol;
    private String almE;

    //VARIABLES PARA AALERT DE LISTA DE PROD
    private RecyclerView rvListaCajas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env_trasp_mult2);

        MyToolbar.show(this, "Env Trasp MultSuc Alm", true);
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

        mDialog = new SpotsDialog.Builder().setContext(ActivityEnvTraspMultSuc2.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        progressDialog = new ProgressDialog(ActivityEnvTraspMultSuc2.this);//parala barra de
        progressDialog.setMessage("Procesando datos....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        txtCantidad = findViewById(R.id.txtCantidad);
        txtCantSurt = findViewById(R.id.txtCantSurt);
        tvProd      = findViewById(R.id.tvProd);
        btnBuscarFol  = findViewById(R.id.btnBuscarFol);
        btnAtras    = findViewById(R.id.btnAtras);
        btnAdelante =findViewById(R.id.btnAdelante);
        ivProd      = findViewById(R.id.ivProd);
        txtProducto = findViewById(R.id.txtProducto);
        txtEnv = findViewById(R.id.txtEnv);
        spLineas = findViewById(R.id.spLineas);
        txtUbi = findViewById(R.id.txtUbi);
        txtTotPza = findViewById(R.id.txtTotPza);
        btnBusc=findViewById(R.id.btnBusc);
        scrollView = findViewById(R.id.scrollView);

        rvEnvTrasp = findViewById(R.id.rvEnvTrasp);
        tIlSurt =findViewById(R.id.tIlSurt);
        tIlSurtN = findViewById(R.id.tIlSurtN);
        txtCantSurtCont = findViewById(R.id.txtCantSurtCont);
        lyCaja = findViewById(R.id.lyCaja);
        lyFol = findViewById(R.id.lyFol);
        spAlm = findViewById(R.id.spAlm);

        adapter = new AdaptadorEnvTraspasos2(lista);
        rvEnvTrasp.setLayoutManager(new LinearLayoutManager(this));

        keyboard = (InputMethodManager) getSystemService(ActivityEnvTraspMultSuc2.INPUT_METHOD_SERVICE);

        bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_correcto = bepp.load(ActivityEnvTraspMultSuc2.this, R.raw.sonido_correct, 1);
        sonido_error = bepp.load(ActivityEnvTraspMultSuc2.this, R.raw.error, 1);

        txtProducto.setInputType(InputType.TYPE_NULL);


        txtProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                posX=scrollView.getScrollX();
                posY=scrollView.getScrollY();
                x=rvEnvTrasp.getScrollX();
                y=rvEnvTrasp.getScrollY();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                Producto=editable.toString();
                if(!editable.toString().equals("")){
                    if (codeBar.equals("Zebra")) {
                        Producto=Producto.trim();
                        String part="";
                        if(Producto.equals(tvProd.getText().toString())){//si escanean el mismo
                            part=lista.get(posicion).getPartida();
                        }
                        buscar(Producto,true);
                        txtProducto.setText("");
                        mover=true;
                    }else{
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if (ban == '\n') {
                                String part="";
                                if(Producto.equals(tvProd.getText().toString())){
                                    part=lista.get(posicion).getPartida();
                                }
                                buscar(Producto,true);
                                txtProducto.setText("");
                                mover=true;
                                break;
                            }//if
                        }//for
                    }//else
                }//if es diferente a vacio
            }//after
        });//txtProd textchange
        
        txtProducto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Producto=txtProducto.getText().toString();
                    if(!Producto.equals("")){
                        buscar(Producto,false);
                    }else{
                        Toast.makeText(ActivityEnvTraspMultSuc2.this, "Sin datos para buscar",
                                Toast.LENGTH_SHORT).show();
                    }//else
                    return true;
                }//if action done
                if (actionId ==0) {
                    txtProducto.requestFocus();
                    return true;
                }//if action done
                return false;
            }//oneditoraction
        });

        btnBuscarFol.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                spLineas.setAdapter(null);
                spLineas.setText("");
                new AsyncConsulEnvNew().execute();
            }//onclick
        });//btnGuardar setonclick


        btnAdelante.setOnClickListener(new View.OnClickListener() {//boton adelante
            @Override
            public void onClick(View view) {
                posicion2=posicion;
                cambio("next",false,0);
            }//onclick
        });//btnadelante setonclicklistener

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion2=posicion;
                cambio("back",false,0);
            }//onclick
        });//btnatras setonclicklistener



        spLineas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(primeroSinc(2)==false){
                    Linea = spLineas.getText().toString();
                    if(Linea.equals(" TODAS LAS LÍNEAS")){
                        Linea="";
                    }//if
                    new AsyncConsulEnvNew().execute();
                }//if
            }//onItemClick
        });//setonitemclick

        btnBusc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(primeroSinc(5)==false){
                    alertBusca();
                }//if
            }
        });

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if(mover==true){
                        mover=false;
                        scrollView.smoothScrollTo(posX,posY);
                    }
                }
            });
        }*/
        posX=scrollView.getScrollX();
        posY=scrollView.getScrollY();

        new AsyncConsulAlm().execute();
    }//onCreate




    public void alertBusca(){
        btnBusc.setEnabled(false);
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
        LayoutInflater inflater = ActivityEnvTraspMultSuc2.this.getLayoutInflater();
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
                    escan=false;
                    boolean existe=false;
                    for(int i=0;i<lista.size();i++){
                        if(lista.get(i).getProducto().equals(comparar)){
                            btnBusc.setEnabled(true);
                            //limpiar();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                        builder.setTitle("AVISO");
                        builder.setMessage("No existe "+Producto+" en la lista");
                        builder.setCancelable(false);
                        builder.setNegativeButton("OK",null);
                        AlertDialog dialogg = builder.create();
                        dialogg.show();
                        escan=false;
                    }
                }else{
                    Toast.makeText(ActivityEnvTraspMultSuc2.this, "Campo Vacío", Toast.LENGTH_SHORT).show();
                }//else
            }//onclick
        });//btnB

        alert.setCancelable(false);
        alert.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                escan=false;
                keyboard.hideSoftInputFromWindow(txtBuscaP.getWindowToken(), 0);
                btnBusc.setEnabled(true);
                txtProducto.requestFocus();
            }
        });//cerrar

        alertDialog = alert.create();
        alertDialog.show();
    }//alertBusca

    public boolean primeroSinc(int alTerminar){
        boolean t=false;
        for(int j=0;j<lista.size();j++){
            if(lista.get(j).isSincronizado()==false){
                t=true;
                posicion2=j;
                cambio(lista.get(j).getProducto(),false,alTerminar);
                break;
            }//if
        }//for
        return  t;
    }//primeroSinc


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

    public void verLista(){
        adapter = new AdaptadorEnvTraspasos2(lista);
        rvEnvTrasp.setAdapter(adapter);
        txtProducto.setEnabled(true);
        txtProducto.requestFocus();
        posicion=0;
        mostrarDetalleProd();
    }//verlista

    public int totPazas(){
        int tot=0;
        for (int i=0;i<lista.size();i++){
            if(lista.get(i).isSincronizado()==true){
                tot=tot+Integer.parseInt(lista.get(i).getCantSurt());
            }
        }
        return tot;
    }//totpzas


    public void mostrarDetalleProd(){//detalle por producto seleccionado
        posX=scrollView.getScrollX();
        posY=scrollView.getScrollY();
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        rvEnvTrasp.scrollToPosition(posicion);

        tvProd.setText(lista.get(posicion).getProducto());
        txtCantidad.setText(lista.get(posicion).getCantidad());
        txtCantSurt.setText(lista.get(posicion).getCantSurt());
        txtEnv.setText(lista.get(posicion).getAlmEnv());
        txtUbi.setText(lista.get(posicion).getUbic());
        txtTotPza.setText(TOTPZA+"");
        txtCantSurtCont.setText(0+"");

        if(Integer.parseInt(lista.get(posicion).getCantidad())==Integer.parseInt(lista.get(posicion).getCantSurt())){
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }else{
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
        }

        cambiaProd();
        txtTotPza.setText(totPazas()+"");

        Picasso.with(getApplicationContext()).
                load(urlImagenes +
                        tvProd.getText().toString() + extImg)
                .error(R.drawable.aboutlogo)
                .fit()
                .centerInside()
                .into(ivProd);
        posG=posicion;
        txtProducto.requestFocus();
        //scrollView.smoothScrollTo(posX,posY);
    }//mostrarDetalleProd

    public void cambiaProd(){
        if(lista.size()==1){
            btnAtras.setEnabled(false);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            btnAdelante.setEnabled(false);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else if(posicion+1==lista.size() && lista.size()>1){
            btnAtras.setEnabled(true);
            btnAtras.setBackgroundTintList(null);
            btnAtras.setBackgroundResource(R.drawable.btn_background3);
            btnAdelante.setEnabled(false);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else if(posicion==0 && lista.size()>1){
            btnAdelante.setEnabled(true);
            btnAdelante.setBackgroundTintList(null);
            btnAdelante.setBackgroundResource(R.drawable.btn_background3);
            btnAtras.setEnabled(false);
            btnAtras.setBackgroundTintList(ColorStateList.
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

    public void onClickListaE(View v){//cada vez que se seleccione un producto en la lista
        posX=scrollView.getScrollX();
        posY=scrollView.getScrollY();
        if(posicion>=0 && lista.get(posicion).isSincronizado()==false){
            posicion2=posG;
            Producto=lista.get(rvEnvTrasp.getChildPosition(rvEnvTrasp.findContainingItemView(v))).getProducto();
        }else{
            posicion2 = rvEnvTrasp.getChildPosition(rvEnvTrasp.findContainingItemView(v));
        }
        cambio("change",false,0);
    }//onClickLista

    public void cambio(String var,boolean sumar,int alTerminar){
        posX=scrollView.getScrollX();
        posY=scrollView.getScrollY();
        if(!lista.get(posicion2).getProducto().equals(Producto) && posG!=-1 && lista.get(posicion2).isSincronizado()==false){//identificando que prod anterior no se sincronizó
            new AsyncInsertAlm(lista.get(posicion2).getProducto(),
                    lista.get(posicion2).getCantSurt(), var, sumar, Producto,alTerminar).execute();
        }else{//cuando se escanea o por botones de adelante, atras y onclick en lista
            if(sumar==true){//al escanear
                buscar(Producto,true);
            }else{
                tipoCambio(var);
                mostrarDetalleProd();
            }//else
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
    }//tipoCambio

    public int encontrarPosEnLista(String prod){
        int p=posG;
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).getProducto().equals(prod)){
                p=i;
                break;
            }//if
        }
        return p;
    }//encontrarPosEnLista

    public void actualizaDat(int pos,String prod){
        posX=scrollView.getScrollX();
        posY=scrollView.getScrollY();
        posicion=pos;
        int exi=Integer.parseInt(lista.get(pos).getExistencia());
        int cant=Integer.parseInt(lista.get(pos).getCantidad());
        final int[] cantS = {Integer.parseInt(lista.get(pos).getCantSurt())};
        if((cantS[0] +1)<=cant){//SI SE PUEDE SUMAR SIN SOBREPASAR CANTIDAD
            if(exi< cantS[0]){//SI EXISTENCIA ES MENOR A CANTIDAD
                bepp.play(sonido_error, 1, 1, 1, 0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cantS[0]++;
                        lista.get(pos).setCantSurt(cantS[0] +"");
                        lista.get(pos).setSincronizado(false);
                        if(cantS[0] ==cant){
                            posicion2=pos;
                            new AsyncInsertAlm(prod, cantS[0] +"","change",
                                    false,Producto,0).execute();
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage("Existencia menor a cantidad a surtir").create().show();
            }else{
                cantS[0]++;
                lista.get(pos).setCantSurt(cantS[0] +"");
                lista.get(pos).setSincronizado(false);
                escan=true;
                adapter.setSingleSelection(pos);
                if(cantS[0] ==cant){
                    posicion2=pos;
                    new AsyncInsertAlm(prod,
                            cantS[0] +"","change",false,Producto,0).execute();
                }else{
                    txtCantSurt.setText(lista.get(posicion).getCantSurt());
                    if(Integer.parseInt(lista.get(posicion).getCantidad())==Integer.parseInt(lista.get(posicion).getCantSurt())){
                        txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    }else{
                        txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                    }
                    txtTotPza.setText(totPazas()+"");
                    rvEnvTrasp.smoothScrollBy(x,y);
                    scrollView.smoothScrollTo(posX,posY);
                }//else
            }//else
            txtProducto.requestFocus();
        }else{
            bepp.play(sonido_error, 1, 1, 1, 0, 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
            builder.setTitle("AVISO");
            builder.setMessage("Excede cantidad");
            builder.setCancelable(false);
            builder.setNegativeButton("OK",null);
            AlertDialog dialog = builder.create();
            dialog.show();
            mostrarDetalleProd();
        }//else
    }
    public void mensjProdCha(){//mensaje para escanear el mismo producto
        bepp.play(sonido_error, 1, 1, 1, 0, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
        builder.setTitle("AVISO");
        builder.setMessage("Escaneo de un producto diferente al actual");
        builder.setCancelable(false);
        builder.setNegativeButton("OK",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }//mensjProdCha

    public void buscar(String prod,boolean sumar){
        prod=prod.trim();
        if(escan==false){
            boolean existe=false;
            for(int i=0;i<lista.size();i++){
                if(lista.get(i).getProducto().equals(prod)){
                    //limpiar();
                    existe=true;
                    posicion=i;
                    if(sumar==true){
                        mostrarDetalleProd();
                        actualizaDat(i,prod);
                    }else{
                        keyboard.hideSoftInputFromWindow(txtProducto.getWindowToken(), 0);
                        mostrarDetalleProd();
                    }//else
                    break;
                }//if
            }
            if(existe==false){
                bepp.play(sonido_error, 1, 1, 1, 0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                builder.setTitle("AVISO");
                builder.setMessage("No existe "+prod+" en la lista");
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
                escan=false;
            }
        }else{//seguir escaneando el  mismo
            if(prod.equals(tvProd.getText().toString())){//para que siga escaneando
                actualizaDat(posicion,prod);
            }else{
                mensjProdCha();
            }//else
        }//else
    }//evaluar

    public void limpiar(){
        tvProd.setText("");
        txtCantidad.setText("");
        txtCantSurt.setText("");
        txtEnv.setText("");
        txtUbi.setText("");
        txtTotPza.setText("0");
        ivProd.setImageResource(R.drawable.logokepler);
        btnAtras.setEnabled(false);
        btnAtras.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));
        btnAdelante.setEnabled(false);
        btnAdelante.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.ColorGris)));
        posG=-1;
    }//limpiar


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

    @SuppressLint("ResourceAsColor")
    public void inFinBt(boolean var){
        escan=true;
        txtProducto.setInputType(InputType.TYPE_NULL);
        tIlSurtN.setVisibility(View.GONE);
        //keyboard.hideSoftInputFromWindow(txtProducto.getWindowToken(), 0);
        txtCantSurtCont.setEnabled(false);
        //chTipoS.setVisibility(View.GONE);

        if(var==true){
            btnBusc.setEnabled(false);
            btnBusc.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));

        }else{
            btnBusc.setBackgroundTintList(null);
            btnBusc.setBackgroundResource(R.drawable.btn_background1);
        }//else
    }//inFinBt

    public void alFinalizar(int alTerminar){
        if(mDialog.isShowing()){mDialog.dismiss();}
        switch (alTerminar){
            case 2:
                new AsyncConsulEnvNew().execute();
                break;
            case 5:
                alertBusca();
                break;
            default:break;
        }//swich
    }//al finalizar

    private class AsyncConsulEnvNew extends AsyncTask<Void, Void, Void> {
        private boolean conn;
        private ArrayList <String> listaLineas= new ArrayList<>();
        @Override
        protected void onPreExecute() {
            mDialog.show();
            limpiar();
            TOTPZA=0;
            mensaje="";posicion=-1;lista.clear();
            rvEnvTrasp.setAdapter(null);inFinBt(true);
            txtProducto.setText("");
            txtProducto.setEnabled(false);
            escan=false;

            //cajaGuard=false;
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="k_suc="+strbran+"&k_lin="+Linea+"&k_alm="+almE;
                String url = "http://"+strServer+"/ConsEnvTraspNew?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                //Log.e(TAG, "Respuesta de la url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        int num=1;
                        listaLineas.add(" TODAS LAS LÍNEAS");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            lista.add(new EnvTraspasos(num+"",dato.getString("k_prod"),
                                    dato.getString("k_ubi"), dato.getString("k_cant"),
                                    dato.getString("k_cant"), "",
                                    dato.getString("k_cants"),dato.getString("k_env"),
                                    dato.getString("k_linea"), true));
                            TOTPZA=TOTPZA+dato.getInt("k_cants");
                            listaLineas.add(dato.getString("k_linea"));
                            num++;mensaje="";
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="No hay datos";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable(){
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
            mDialog.dismiss();
            if (lista.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                inFinBt(false);
                txtProducto.requestFocus();
                verLista();
                //SPINNER LINEAS
                if(Linea.equals("")){
                    Set<String> hashSet = new HashSet<>(listaLineas);
                    listaLineas.clear();
                    listaLineas.addAll(hashSet);
                    Collections.sort(listaLineas);
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            ActivityEnvTraspMultSuc2.this,R.layout.drop_down_item,listaLineas);
                    spLineas.setAdapter(adaptador);
                    spLineas.setText(listaLineas.get(0),false);
                }//SPINNER


            }//else
            //chbConten.setVisibility(View.GONE);
        }//onPost
    }//AsyncConsulEnvTraspMor

    private class AsyncInsertAlm extends AsyncTask<Void, Void, String> {

        private String producto,cant,var,ProductoActual,newCant,newExist;
        private boolean conn,sumar;
        int alTerminar;

        public AsyncInsertAlm(String producto, String cant,String var,
                                 boolean sumar,String ProductoActual,int alTerminar) {
            this.producto = producto;
            this.cant = cant;
            this.var=var;
            this.sumar=sumar;
            this.ProductoActual=ProductoActual;
            this.alTerminar=alTerminar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mDialog.isShowing()==false){
                mDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            conn=firtMet();
            newCant=cant;
            if(conn==true){
                String parametros="k_suc="+strbran+"&k_alm="+almE+
                        "&k_prod="+producto+"&k_cant="+cant;
                String url = "http://"+strServer+"/InsertAlm?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if(jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        mensaje=jsonArray.getString(0);
                        if(mensaje.equals("Sincronizado") ||
                                mensaje.equals("Ya se sincronizaron suficientes piezas")){
                            newCant=jsonArray.getString(1);
                            newExist=jsonArray.getString(2);
                        }
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
            if(conn==true && mensaje.equals("Sincronizado") ||
                    mensaje.equals("Ya se sincronizaron suficientes piezas")) {
                Toast.makeText(ActivityEnvTraspMultSuc2.this, mensaje, Toast.LENGTH_SHORT).show();

                bepp.play(sonido_correcto, 1, 1, 1, 0, 0);
                TOTPZA=TOTPZA+Integer.parseInt(cant);
                lista.get(posicion2).setSincronizado(true);
                lista.get(posicion2).setCantSurt(newCant);
                lista.get(posicion2).setExistencia(newExist);
                escan=false;
                if(sumar==true){
                    buscar(ProductoActual,true);
                }else{
                    if((posicion+1)<lista.size() && Integer.parseInt(lista.get(posicion2).getCantSurt())==Integer.parseInt(lista.get(posicion2).getCantidad())){
                        var="next";
                    }
                    tipoCambio(var);
                    mostrarDetalleProd();
                }
                alFinalizar(alTerminar);
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else<
        }//onPost
    }//AsyncInsertCajasE

    //Reporte de Incidencias
    private class AsyncIncid extends AsyncTask<Void, Void, Void> {
        boolean conn;
        @Override
        protected void onPreExecute() {
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            conn=firtMet();
            if(conn==true){
                listaIncidencias.clear();
                HttpHandler sh = new HttpHandler();
                String url = "http://"+strServer+"/MensaInci";
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaIncidencias.add(new ListaIncidenciasSandG(dato.getString("k_Clave"),
                                    dato.getString("k_Mensaje")));
                        }//for
                    }catch (final JSONException e) {
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
                            mensaje="No fue posible obtener datos del servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInbackground

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            if(listaIncidencias.size() > 0) {
                /*String[] opciones = new String[listaIncidencias.size()];
                for (int i = 0; i < listaIncidencias.size(); i++) {
                    opciones[i] = listaIncidencias.get(i).getClave() + ".-" + listaIncidencias.get(i).getMensaje();
                }//for
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setIcon(R.drawable.icons8_error_52).setTitle("Seleccione la Incidencia");
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Producto =lista.get(posicion).getProducto();
                        String RazonSuper = opciones[which];
                        new AsyncReporteInici(Producto,RazonSuper,Folio,lista.get(posicion).getCantidad()).execute();
                    }//onclclick
                });
                AlertDialog dialog = builder.create();
                dialog.show();*/
                alertIncid();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else
        }//onPostExecute
    }//AsyncIncid

    //AsyncReporteInici
    private class AsyncReporteInici extends AsyncTask<Void, Void, Void> {
        boolean conn;
        private String prod,razon,fol,cant;
        private AlertDialog alertD;

        public AsyncReporteInici(String prod, String razon, String fol,String cant,AlertDialog alertD) {
            this.prod = prod;
            this.razon = razon;
            this.fol = fol;
            this.cant=cant;
            this.alertD=alertD;
        }//
        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreejecutive
        @Override
        protected Void doInBackground(Void... params) {
            conn=firtMet();
            if(conn==true){
                String parametros="k_Producto="+prod+"&k_Usuario="+strusr+
                        "&k_Razon="+razon+"&k_Sucursal="+strbran+
                        "&k_Folio=0000000&k_com=TRASPASO-0000000-"+cant+
                        "&k_tipo=SUCENV&k_cant="+cant+"&k_cants="+cant;
                String url = "http://"+strServer+"/ReportInci?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        mensaje=dato.getString("respuesta");
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="no se obtuvo respuesta";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="Problema al traer datos";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInbackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            if(mensaje.equals("Reporte Realizado")){
                alertD.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
            builder.setTitle("AVISO");
            builder.setMessage(mensaje);
            builder.setCancelable(false);
            builder.setNegativeButton("OK",null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }//onPost
    }//AsyncReporteInici

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
                        ActivityEnvTraspMultSuc2.this,R.layout.drop_down_item,listaNomAlm);
                spAlm.setAdapter(adaptador);
                spAlm.setText(listaNomAlm.get(0),false);
                almE=listaAlm.get(0);

                spAlm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        almE = listaAlm.get(position);
                    }//onItemClick
                });
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                builder.setTitle("AVISO");
                builder.setMessage("Problema al consultar almacenes");
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else<
        }//onPost
    }//AsyncConsulAlm


    public void alertIncid(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
        LayoutInflater inflater = ActivityEnvTraspMultSuc2.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_incid, null);
        alert.setView(dialogView);
        alert.setCancelable(false);
        alert.setNegativeButton("CANCELAR",null);

        Button btnEnviar =  dialogView.findViewById(R.id.btnEnviar);
        EditText txtIncidProd = dialogView.findViewById(R.id.txtIncidProd);
        EditText txtCantidad =  dialogView.findViewById(R.id.txtCantidad);
        AutoCompleteTextView spListIncid = dialogView.findViewById(R.id.spListIncid);

        txtIncidProd.setText(lista.get(posicion).getProducto());
        txtCantidad.setText((Integer.parseInt(lista.get(posicion).getCantidad())-
                Integer.parseInt(lista.get(posicion).getCantSurt()))+"");
        AlertDialog alertD = alert.create();

        ArrayList<String> listaInc=new ArrayList<>();
        for(int k=0;k<listaIncidencias.size();k++){
            listaInc.add(listaIncidencias.get(k).getClave() + ".-" + listaIncidencias.get(k).getMensaje());
        }//for

        if(listaInc.size()>0){
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                    ActivityEnvTraspMultSuc2.this,R.layout.drop_down_item,listaInc);
            spListIncid.setAdapter(adaptador);
            spListIncid.setText(listaInc.get(0),false);
        }//if

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Producto= txtIncidProd.getText().toString();
                String Razon=spListIncid.getText().toString();
                String Cant=txtCantidad.getText().toString();
                if(Producto.equals("") || Razon.equals("") || Cant.equals("")){
                    Toast.makeText(ActivityEnvTraspMultSuc2.this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                }else if((Integer.parseInt(Cant)+Integer.parseInt(lista.get(posicion).getCantSurt()))>
                        Integer.parseInt(lista.get(posicion).getCantidad())){
                    Toast.makeText(ActivityEnvTraspMultSuc2.this, "Excede cantidad", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc2.this);
                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AsyncReporteInici(Producto,Razon,"0000000",Cant,alertD).execute();
                        }//onclick
                    });
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").
                            setMessage("¿Desea enviar incidencia de "+Razon+" de "+Producto+"?").create().show();
                }//else
            }//onclick
        });//btnEnviar
        alertD.show();
    }//alertIncid

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoverflow4, menu);
        return true;
    }//onCreateOptionsMenu

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itInci:
                if(lista.size()>0){
                    new AsyncIncid().execute();
                }else{
                    Toast.makeText(this, "Sin productos seleccionados", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

}//Activity
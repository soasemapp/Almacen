package com.almacen.alamacen202.Activity;

import static android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorCajas;
import com.almacen.alamacen202.Adapter.AdaptadorCajaxProd;
import com.almacen.alamacen202.Adapter.AdaptadorEnvTraspasos;
import com.almacen.alamacen202.Adapter.AdaptadorListAlmacenes;
import com.almacen.alamacen202.Adapter.AdaptadorTraspasos;
import com.almacen.alamacen202.Adapter.AdapterDifUbiExi;
import com.almacen.alamacen202.Adapter.AdapterListaCajas;
import com.almacen.alamacen202.Imprecion.BluetoothPrint;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.CAJASSANDG;
import com.almacen.alamacen202.SetterandGetters.CajaXProd;
import com.almacen.alamacen202.SetterandGetters.EnvTraspasos;
import com.almacen.alamacen202.SetterandGetters.ListaIncidenciasSandG;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.XML.XMLMensajeIncidencias;
import com.almacen.alamacen202.XML.XMLRecepConsul;
import com.almacen.alamacen202.XML.XMLRecepMultSuc;
import com.almacen.alamacen202.XML.XMLReportInici;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dmax.dialog.SpotsDialog;

public class ActivityEnvTraspMultSuc extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private int posicion=0,posicion2=0,posG=0,CAJAACT=1,posCaja=0,cantCajaOr,pC=0,TOTCAJAS=0,TOTPZA=0;
    private String strusr,strpass,strbran,strServer,codeBar,mensaje;
    private String Producto="",ProdGuard="",Folio="",prodSelectCaj="",cajaActAl="",Linea="";
    private ArrayList<EnvTraspasos> lista = new ArrayList<>();
    private ArrayList<CAJASSANDG> listaCajas = new ArrayList<>();
    private ArrayList<CAJASSANDG> listaCajasXProd = new ArrayList<>();
    private ArrayList<ListaIncidenciasSandG> listaIncidencias = new ArrayList<>();
    private ArrayList<String> nomCajas= new ArrayList<>();
    private EditText txtFolBusq,txtCantidad,txtCantSurt,txtProducto,txtEnv,txtUbi,txtTotPza,txtCantSurtCont;
    private AutoCompleteTextView spCaja,spLineas;
    private ImageView ivProd;
    private TextView tvProd;
    private Button btnBuscarFol,btnAtras,btnAdelante,btnAggCaja,btnListaCajas,btnVerCajas,btnBusc,btnGuarMan;
    private RecyclerView rvEnvTrasp;
    private AdaptadorEnvTraspasos adapter;
    private AdapterListaCajas adapListCaj = new AdapterListaCajas(listaCajas);
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private String urlImagenes,extImg,impresora;
    private int sonido_correcto,sonido_error,posX,posY,x=0,y=0;
    private SoundPool bepp;
    private AlertDialog.Builder builder6;
    private Context context = this;
    private AlertDialog dialog6 = null;
    AlertDialog alertDialog=null;
    private boolean escan=false,mover=false;
    private ScrollView scrollView;
    private CheckBox chbConten,chTipoS;
    private TextInputLayout tIlSurt,tIlSurtN;

    //VARIABLES PARA AALERT DE LISTA DE PROD
    private RecyclerView rvListaCajas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env_trasp_mult);

        MyToolbar.show(this, "Envío Traspasos Multisucursal", true);
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

        mDialog = new SpotsDialog.Builder().setContext(ActivityEnvTraspMultSuc.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        progressDialog = new ProgressDialog(ActivityEnvTraspMultSuc.this);//parala barra de
        progressDialog.setMessage("Procesando datos....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        txtFolBusq    = findViewById(R.id.txtFolBusq);
        txtCantidad = findViewById(R.id.txtCantidad);
        txtCantSurt = findViewById(R.id.txtCantSurt);
        tvProd      = findViewById(R.id.tvProd);
        btnBuscarFol  = findViewById(R.id.btnBuscarFol);
        btnAtras    = findViewById(R.id.btnAtras);
        btnAdelante =findViewById(R.id.btnAdelante);
        ivProd      = findViewById(R.id.ivProd);
        txtProducto = findViewById(R.id.txtProducto);
        spCaja = findViewById(R.id.spCaja);
        btnAggCaja = findViewById(R.id.btnAggCaja);
        btnListaCajas = findViewById(R.id.btnListaCajas);
        btnVerCajas = findViewById(R.id.btnVerCajas);
        txtEnv = findViewById(R.id.txtEnv);
        spLineas = findViewById(R.id.spLineas);
        txtUbi = findViewById(R.id.txtUbi);
        txtTotPza = findViewById(R.id.txtTotPza);
        btnBusc=findViewById(R.id.btnBusc);
        scrollView = findViewById(R.id.scrollView);

        rvEnvTrasp = findViewById(R.id.rvEnvTrasp);
        chbConten = findViewById(R.id.chbConten);
        btnGuarMan= findViewById(R.id.btnGuarMan);
        chTipoS = findViewById(R.id.chTipoS);
        tIlSurt =findViewById(R.id.tIlSurt);
        tIlSurtN = findViewById(R.id.tIlSurtN);
        txtCantSurtCont = findViewById(R.id.txtCantSurtCont);

        adapter = new AdaptadorEnvTraspasos(lista);
        rvEnvTrasp.setLayoutManager(new LinearLayoutManager(this));

        keyboard = (InputMethodManager) getSystemService(ActivityEnvTraspMultSuc.INPUT_METHOD_SERVICE);

        bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_correcto = bepp.load(ActivityEnvTraspMultSuc.this, R.raw.sonido_correct, 1);
        sonido_error = bepp.load(ActivityEnvTraspMultSuc.this, R.raw.error, 1);

        txtFolBusq.requestFocus();
        txtProducto.setInputType(InputType.TYPE_NULL);

        CAJAACT=1;

        chbConten.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                limpiar();
                TOTPZA=0;
                mensaje="";posicion=-1;lista.clear();
                rvEnvTrasp.setAdapter(null);inFinBt(true);
                spCaja.setAdapter(null);
                spCaja.setText("");
                nomCajas.clear();
                spLineas.setAdapter(null);
                spLineas.setText("");
                txtProducto.setText("");
                txtProducto.setEnabled(false);
                escan=false;
            }//onchecked
        });//onchecked

        chTipoS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    tIlSurt.setHint("Por Caja");
                }else{
                    tIlSurt.setHint("Surtido");
                }
            }//onchecked
        });//chTipoS

        btnGuarMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion2=posG;
                keyboard.hideSoftInputFromWindow(txtCantSurtCont.getWindowToken(), 0);
                int cant=0,surtAcum=0,surt=0;
                surtAcum=Integer.parseInt(lista.get(posicion2).getCantSurt());
                cant=Integer.parseInt(txtCantidad.getText().toString());

                if((Integer.parseInt(txtCantSurtCont.getText().toString())+surtAcum)>cant){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                    builder.setTitle("AVISO");
                    builder.setMessage("Excede cantidad\nLleva "+surtAcum+" piezas de surtido de este producto");
                    builder.setCancelable(false);
                    builder.setNegativeButton("ACEPTAR", null);//
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(!txtCantSurtCont.getText().toString().equals("") && Integer.parseInt(txtCantSurtCont.getText().toString())>0 ){
                    surt=Integer.parseInt(txtCantSurtCont.getText().toString());

                    if(chTipoS.isChecked()){//surt es lo que se puso para que sea el numero de cajas en que se quiere repartir
                        if((surtAcum+(cant/surt))<0){//para no exceder del numero de cajas
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                            builder.setTitle("AVISO");
                            builder.setMessage("El número de cajas excede cantidad");
                            builder.setCancelable(false);
                            builder.setNegativeButton("ACEPTAR", null);//
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else{//si se ajusta de cajas
                            new AsyncInsertCajasEContenedores(strbran, Folio, tvProd.getText().toString(),
                                    cant, surt, lista.get(posicion2).getPartida(), strusr, "change", false, Producto,6).execute();
                        }//else se ajusta a cajas
                    }else{
                        surtAcum=surtAcum+surt;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                        builder.setTitle("CONFIRMACION");
                        builder.setMessage("¿Desea guardar "+surt+" piezas de "+tvProd.getText().toString()+"?"+
                                "\nSerán "+surtAcum+" en total");
                        builder.setCancelable(false);
                        int finalSurtAcum = surtAcum;
                        builder.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new AsyncInsertCajasE(strbran, Folio, tvProd.getText().toString(),
                                        finalSurtAcum +"", spCaja.getText().toString()+"",
                                        lista.get(posicion2).getPartida(), strusr, "change", false, Producto,6).execute();
                            }//onclick
                        });//positive
                        builder.setNegativeButton("CANCELAR", null);//
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }//else
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                    builder.setTitle("AVISO");
                    builder.setMessage("Campos en 0 o vacios");
                    builder.setCancelable(false);
                    builder.setNegativeButton("ACEPTAR", null);//
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }//else surt ==0
            }//onclick
        });//btnGuarMan

        spCaja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CAJAACT=Integer.parseInt(spCaja.getText().toString());
            }
        });

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
                if(chbConten.isChecked()==false){
                    Producto=editable.toString();
                    if(!editable.toString().equals("")){
                        if (codeBar.equals("Zebra")) {
                            Producto=Producto.trim();
                            buscar(Producto,true);
                            txtProducto.setText("");
                            mover=true;
                        }else{
                            for (int i = 0; i < editable.length(); i++) {
                                char ban;
                                ban = editable.charAt(i);
                                if (ban == '\n') {
                                    buscar(Producto,true);
                                    txtProducto.setText("");
                                    mover=true;
                                    break;
                                }//if
                            }//for
                        }//else
                    }//if es diferente a vacio
                }//if chconten is false
            }//after
        });//txtProd textchange
        
        txtProducto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Producto=txtProducto.getText().toString();
                    if(!Producto.equals("")){
                        buscar(Producto,false);
                    }else{
                        Toast.makeText(ActivityEnvTraspMultSuc.this, "Sin datos para buscar", Toast.LENGTH_SHORT).show();
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
                if(!txtFolBusq.getText().toString().equals("")){
                    Folio=folio(txtFolBusq.getText().toString());
                    CAJAACT=1;
                    new AsyncConsulEnvTrasp(strbran,Folio,"").execute();
                }else{
                    Toast.makeText(ActivityEnvTraspMultSuc.this, "Folio vacío", Toast.LENGTH_SHORT).show();
                }
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

        btnAggCaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(primeroSinc(1)==false){
                    mensajeAggCaja();
                }
            }//onclick
        });

        btnListaCajas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(primeroSinc(4)==false){
                    //new AsyncConsultCA(strbran,Folio).execute();
                    mostrarEnAlertListaCajas();
                }//if
            }
        });//btnListaCajas

        btnVerCajas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(primeroSinc(3)==false){
                    new AsyncConsultCP(strbran,Folio,tvProd.getText().toString()).execute();
                }//if
            }
        });//btnVerCajas

        spLineas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Linea = spLineas.getText().toString();
                if(primeroSinc(2)==false){
                    if(Linea.equals(" TODAS LAS LÍNEAS")){
                        new AsyncConsulEnvTrasp(strbran,Folio,"").execute();
                    }else{
                        new AsyncConsulEnvTrasp(strbran,Folio,Linea).execute();
                    }//else
                }
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
    }//onCreate

    public boolean reparticion(int cant,int surtCaj){
        boolean var=false;
        for(int i=0;i<cant;i++){

        }
        return var;
    }//reparticion


    public void alertBusca(){
        btnBusc.setEnabled(false);
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
        LayoutInflater inflater = ActivityEnvTraspMultSuc.this.getLayoutInflater();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                        builder.setTitle("AVISO");
                        builder.setMessage("No existe "+Producto+" en la lista");
                        builder.setCancelable(false);
                        builder.setNegativeButton("OK",null);
                        AlertDialog dialogg = builder.create();
                        dialogg.show();
                        escan=false;
                    }
                }else{
                    Toast.makeText(ActivityEnvTraspMultSuc.this, "Campo Vacío", Toast.LENGTH_SHORT).show();
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

    public void mensajeAggCaja(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncConsultSigCaja(Folio).execute();
            }
        });
        builder.setNegativeButton("CANCELAR",null);
        builder.setCancelable(false);
        builder.setTitle("AVISO").setMessage("¿Desea agregar caja?").create().show();
    }//mensajeAggCaja

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
        adapter = new AdaptadorEnvTraspasos(lista);
        rvEnvTrasp.setAdapter(adapter);
        txtProducto.setEnabled(true);
        txtProducto.requestFocus();
        posicion=0;
        mostrarDetalleProd();
    }

    public int totPazas(){
        int tot=0;
        for (int i=0;i<lista.size();i++){
            if(lista.get(i).isSincronizado()==true){
                tot=tot+Integer.parseInt(lista.get(i).getCantSurt());
            }
        }
        return tot;
    }


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
            new AsyncInsertCajasE(strbran, Folio, lista.get(posicion2).getProducto(),
                    lista.get(posicion2).getCantSurt(), spCaja.getText().toString() + "",
                    lista.get(posicion2).getPartida(), strusr, var, sumar, Producto,alTerminar).execute();
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
    }

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cantS[0]++;
                        lista.get(pos).setCantSurt(cantS[0] +"");
                        lista.get(pos).setSincronizado(false);
                        if(cantS[0] ==cant){
                            posicion2=pos;
                            new AsyncInsertCajasE(strbran,Folio,prod,
                                    cantS[0] +"",spCaja.getText().toString(),
                                    lista.get(posicion2).getPartida(),strusr,"change",false,Producto,0).execute();
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
                    new AsyncInsertCajasE(strbran,Folio,prod,
                            cantS[0] +"",spCaja.getText().toString(),
                            lista.get(posicion2).getPartida(),strusr,"change",false,Producto,0).execute();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
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
                        if(chbConten.isChecked()==true){
                            keyboard.showSoftInput(txtCantSurt,InputMethodManager.SHOW_IMPLICIT);
                        }//if
                    }//else
                    break;
                }//if
            }
            if(existe==false){
                bepp.play(sonido_error, 1, 1, 1, 0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
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

        if(chbConten.isChecked()==true){
            escan=false;
            txtProducto.setInputType(InputType.TYPE_CLASS_TEXT| TYPE_TEXT_FLAG_CAP_CHARACTERS);
            tIlSurtN.setVisibility(View.VISIBLE);
            txtCantSurtCont.setEnabled(true);
            //keyboard.showSoftInput(txtProducto,InputMethodManager.SHOW_IMPLICIT);
            btnGuarMan.setVisibility(View.VISIBLE);

            //chTipoS.setVisibility(View.VISIBLE);

        }else{
            escan=true;
            txtProducto.setInputType(InputType.TYPE_NULL);
            tIlSurtN.setVisibility(View.GONE);
            //keyboard.hideSoftInputFromWindow(txtProducto.getWindowToken(), 0);
            txtCantSurtCont.setEnabled(false);
            btnGuarMan.setVisibility(View.GONE);
            //chTipoS.setVisibility(View.GONE);
        }//else

        if(var==true){
            btnAggCaja.setEnabled(false);
            btnListaCajas.setEnabled(false);
            btnVerCajas.setEnabled(false);
            btnBusc.setEnabled(false);
            btnAggCaja.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            btnListaCajas.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            btnVerCajas.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            btnBusc.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            btnGuarMan.setEnabled(false);
            btnGuarMan.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));

        }else{
            btnAggCaja.setEnabled(true);
            btnListaCajas.setEnabled(true);
            btnVerCajas.setEnabled(true);
            btnBusc.setEnabled(true);
            btnAggCaja.setBackgroundTintList(null);
            btnAggCaja.setBackgroundResource(R.drawable.btn_background4);

            btnListaCajas.setBackgroundTintList(null);
            btnListaCajas.setBackgroundResource(R.drawable.btn_background2);

            btnVerCajas.setBackgroundTintList(null);
            btnVerCajas.setBackgroundResource(R.drawable.btn_background1);

            btnBusc.setBackgroundTintList(null);
            btnBusc.setBackgroundResource(R.drawable.btn_background1);

            btnGuarMan.setEnabled(true);
            btnGuarMan.setBackgroundTintList(null);
            btnGuarMan.setBackgroundResource(R.drawable.btn_background2);
        }//else


    }//inFinBt

    public void alFinalizar(int alTerminar){
        switch (alTerminar){
            case 1:
                mDialog.dismiss();
                mensajeAggCaja();
                break;
            case 2:
                if(Linea.equals(" TODAS LAS LÍNEAS")){
                    new AsyncConsulEnvTrasp(strbran,Folio,"").execute();
                }else{
                    new AsyncConsulEnvTrasp(strbran,Folio,Linea).execute();
                }//else
                break;
            case 3:
                new AsyncConsultCP(strbran,Folio,tvProd.getText().toString()).execute();
                break;
            case 4:
                //new AsyncConsultCA(strbran,Folio,"1").execute();
                mostrarEnAlertListaCajas();
                break;
            case 5:
                alertBusca();
                break;
            case 6:
                new AsyncConsultSigCaja(Folio).execute();
                break;
            default:break;
        }//swich
    }

    public void mostrarEnAlertListaCajas(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
        LayoutInflater inflater = ActivityEnvTraspMultSuc.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_lista_cajas, null);
        alert.setView(dialogView);
        alert.setCancelable(false);
        alert.setNegativeButton("CERRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<String> nomCajas=new ArrayList<>();
                for(int k=1;k<=TOTCAJAS;k++){
                    nomCajas.add(k+"");
                }//for
                if(nomCajas.size()>0){
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            ActivityEnvTraspMultSuc.this,R.layout.drop_down_item,nomCajas);
                    spCaja.setAdapter(adaptador);
                    spCaja.setText(CAJAACT+"",false);
                }//
            }
        });//cerrar

        AutoCompleteTextView spCajaNom = dialogView.findViewById(R.id.spCajaNom);
        rvListaCajas =  dialogView.findViewById(R.id.rvListaCajas);
        Button btnBack = dialogView.findViewById(R.id.btnBack);
        Button btnNext = dialogView.findViewById(R.id.btnNext);
        Button btnCambiar = dialogView.findViewById(R.id.btnCambiar);
        Button btnImprimirTicket = dialogView.findViewById(R.id.btnImprimirTicket);
        GridLayoutManager gl = new GridLayoutManager(ActivityEnvTraspMultSuc.this, 1);
        rvListaCajas.setLayoutManager(gl);
        AlertDialog mm1 = alert.create();

        //INICIA
        posCaja =0;
        prodSelectCaj="";
        cantCajaOr=0;

        cajaActAl="1";
        pC=1;
        new AsyncConsultCA(strbran,Folio,cajaActAl,btnBack,btnNext).execute();
        nomCajas.clear();
        //SPINER DE CAJA
        ArrayList<String> nomCajas2=new ArrayList<>();
        for(int k=1;k<=TOTCAJAS;k++){
            nomCajas2.add(k+"");
        }//for
        if(nomCajas2.size()>0){
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                    ActivityEnvTraspMultSuc.this,R.layout.drop_down_item,nomCajas2);
            spCajaNom.setAdapter(adaptador);
            spCajaNom.setText(nomCajas2.get(0),false);
        }
        spCajaNom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listaCajasXProd.clear();
                rvListaCajas.setAdapter(null);
                cajaActAl=spCajaNom.getText().toString();
                pC=Integer.parseInt(cajaActAl);
                new AsyncConsultCA(strbran,Folio,cajaActAl,btnBack,btnNext).execute();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaCajasXProd.clear();
                rvListaCajas.setAdapter(null);
                pC--;
                cajaActAl=pC+"";
                spCajaNom.setText(cajaActAl,false);
                new AsyncConsultCA(strbran,Folio,cajaActAl,btnBack,btnNext).execute();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaCajasXProd.clear();
                rvListaCajas.setAdapter(null);
                pC++;
                cajaActAl=pC+"";
                spCajaNom.setText(cajaActAl,false);
                new AsyncConsultCA(strbran,Folio,cajaActAl,btnBack,btnNext).execute();
            }
        });
        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaCajas.size()>0){
                    cambiarCajasAlert(prodSelectCaj,cantCajaOr, cajaActAl,nomCajas,mm1);
                }else{
                    Toast.makeText(ActivityEnvTraspMultSuc.this, "Caja sin datos", Toast.LENGTH_SHORT).show();
                }
            }
        });//btnCambiar

        btnImprimirTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImprimirTicketCajas(Integer.parseInt(spCajaNom.getText().toString()));
            }
        });//btnCambiar

        /*cajaActAl =nomCajas.get(pC);
        spCajaNom.setText(cajaActAl,false);
        tablaXcaja(btnBack,btnNext,posCaja);*/
        mm1.show();
    }//mostrarListaCajas

    public boolean datosEnCaja(String caja){
        boolean var=false;
        for(int i=0;i<nomCajas.size();i++){
            if(nomCajas.get(i).equals(caja)){
                var=true;
                break;
            }
        }//for
        return var;
    }

    public ArrayList<CAJASSANDG> listaXCaja(String cajaSelect){
        ArrayList<CAJASSANDG> lista= new ArrayList<>();
        for(int j=0;j<listaCajas.size();j++){
            if(listaCajas.get(j).getNumCajas().equals(cajaSelect)){
                lista.add(new CAJASSANDG("","","",
                        listaCajas.get(j).getClavedelProdcuto(),
                        listaCajas.get(j).getCantidadUnidades(),listaCajas.get(j).getNumCajas()));
            }//if
        }//for
        return lista;
    }//lista



    private class AsyncConsulEnvTrasp extends AsyncTask<Void, Void, Void> {
        private String suc,folio,linea;
        private boolean conn;
        private ArrayList <String> listaLineas= new ArrayList<>();
        public AsyncConsulEnvTrasp(String suc, String folio,String linea) {
            this.suc = suc;
            this.folio = folio;
            this.linea=linea;
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
            limpiar();
            TOTPZA=0;
            mensaje="";posicion=-1;lista.clear();
            rvEnvTrasp.setAdapter(null);inFinBt(true);
            spCaja.setAdapter(null);
            spCaja.setText("");
            nomCajas.clear();
            txtProducto.setText("");
            txtProducto.setEnabled(false);
            escan=false;
            chTipoS.setChecked(false);
            //cajaGuard=false;
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="k_suc="+suc+"&k_fol="+folio+"&k_lin="+linea;
                String url = "http://"+strServer+"/"+getString(R.string.resConsEnvTrasp)+"?"+parametros;
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
                            lista.add(new EnvTraspasos(num+"",dato.getString("k_prod"),dato.getString("k_ubi"),
                                    dato.getString("k_exist"),dato.getString("k_cant"),dato.getString("k_part"),
                                    dato.getString("k_cants"),dato.getString("k_env"),dato.getString("k_linea"),true));
                            TOTPZA=TOTPZA+dato.getInt("k_cants");
                            listaLineas.add(dato.getString("k_linea"));
                            TOTCAJAS=dato.getInt("k_ulcaj");
                            num++;mensaje="";
                        }//for
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Puede que el folio no exista o no haya datos";
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
            mDialog.dismiss();
            if (lista.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                inFinBt(false);
                txtFolBusq.setText(Folio);
                keyboard.hideSoftInputFromWindow(txtFolBusq.getWindowToken(), 0);
                txtProducto.requestFocus();
                verLista();
                //SPINNER LINEAS
                if(linea.equals("")){
                    Set<String> hashSet = new HashSet<>(listaLineas);
                    listaLineas.clear();
                    listaLineas.addAll(hashSet);
                    Collections.sort(listaLineas);
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            ActivityEnvTraspMultSuc.this,R.layout.drop_down_item,listaLineas);
                    spLineas.setAdapter(adaptador);
                    spLineas.setText(listaLineas.get(0),false);
                }//SPINNER

                //SPINNER DE CAJAS
                ArrayList<String> nomCajas=new ArrayList<>();
                for(int k=1;k<=TOTCAJAS;k++){
                    nomCajas.add(k+"");
                }//for
                if(nomCajas.size()>0){
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            ActivityEnvTraspMultSuc.this,R.layout.drop_down_item,nomCajas);
                    spCaja.setAdapter(adaptador);
                    CAJAACT=Integer.parseInt(nomCajas.get(nomCajas.size()-1));
                    spCaja.setText(CAJAACT+"",false);
                }//
            }//else
            //chbConten.setVisibility(View.GONE);
        }//onPost
    }//AsyncConsulEnvTrasp

    private class AsyncConsultCP extends AsyncTask<Void, Void, Void> {
        private String suc,folio,producto;
        private ArrayList<CajaXProd> listaCajaXProd= new ArrayList<>();
        private boolean conn;
        public AsyncConsultCP(String suc, String folio,String producto) {
            this.suc = suc;
            this.folio = folio;
            this.producto=producto;
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
                HttpHandler sh = new HttpHandler();
                String parametros="k_Folio="+folio+"&k_Sucursal="+suc+"&k_Producto="+producto+"";
                String url = "http://"+strServer+"/ConsultCP?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        // Obtener array de datos
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaCajaXProd.add(new CajaXProd(dato.getString("k_NumeroCajas"),dato.getString("k_Cantidad")));
                            mensaje="";
                        }//for
                    } catch (final JSONException e) {
                        //Log.e(TAG, "Error al convertir Json: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="No se ha guardado este producto en cajas";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="Problema al consultar cajas";
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
            mDialog.dismiss();
            if (listaCajaXProd.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                LayoutInflater inflater = ActivityEnvTraspMultSuc.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_cajaxprod, null);
                alert.setView(dialogView);
                alert.setCancelable(false);
                alert.setNegativeButton("ACEPTAR",null);

                RecyclerView rvCaja =  dialogView.findViewById(R.id.rvCaja);
                GridLayoutManager gl = new GridLayoutManager(ActivityEnvTraspMultSuc.this, 1);
                rvCaja.setLayoutManager(gl);

                AdaptadorCajaxProd adap  = new AdaptadorCajaxProd(listaCajaXProd);
                rvCaja.setAdapter(adap);
                AlertDialog mm = alert.create();
                mm.show();
            }//else
        }//onPost
    }//AsyncConsultCP

    private class AsyncConsultCA extends AsyncTask<Void, Void, Void> {
        private String suc,folio,caja;
        private boolean conn;
        Button btnBack,btnNext;
        public AsyncConsultCA(String suc, String folio,String caja,Button btnBack,Button btnNext) {
            this.suc = suc;
            this.folio = folio;
            this.caja=caja;
            this.btnBack=btnBack;
            this.btnNext=btnNext;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            listaCajas.clear();
            listaCajasXProd.clear();
            mensaje="";
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="k_Folio="+folio+"&k_Sucursal="+suc+"&k_Producto="+caja;//k_Producto es para caja
                String url = "http://"+strServer+"/ConsultCA?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaCajas.add(new CAJASSANDG("","","",
                                    dato.getString("k_Producto"),dato.getString("k_Cantidad"),
                                    dato.getString("k_NumeroCajas")));
                        }//for
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="No hay datos en esta caja";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="Problema al consultar lista de cajas";
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
            if (listaCajas.size()==0) {
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                cajaActAl =caja;
                posCaja=Integer.parseInt(cajaActAl);
                tablaXcaja(btnBack,btnNext,posCaja);
                rvListaCajas.setAdapter(null);
                if(listaCajas.size()>0){
                    adapListCaj= new AdapterListaCajas(listaCajas);
                    rvListaCajas.setAdapter(adapListCaj);
                    posCaja=0;
                    prodSelectCaj=listaCajas.get(0).getClavedelProdcuto();
                    cantCajaOr=Integer.parseInt(listaCajas.get(0).getCantidadUnidades());
                }
                mDialog.dismiss();
            }//else
        }//onPost
    }//AsyncConsultCA

    private class AsyncCambiarCajas extends AsyncTask<Void, Void, Void> {

        private String suc, folio,producto,cantidad,caja1,caja2;
        private AlertDialog alert1,alert2;
        private boolean conn;
        public AsyncCambiarCajas(String suc, String folio,String producto,
                                 String cantidad,String caja1,String caja2,
                                 AlertDialog alert1,AlertDialog alert2) {
            this.suc = suc;
            this.folio = folio;
            this.producto=producto;
            this.cantidad=cantidad;
            this.caja1=caja1;
            this.caja2=caja2;
            this.alert1 = alert1;
            this.alert2 = alert2;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros = "sucursal=" + suc + "&folio=" + folio + "&producto=" +
                        producto +  "&cantidad=" + cantidad + "&numCaja1=" + caja1 +"&numCaja2=" + caja2+"";
                String url = "http://" + strServer + "/CambiarPC?" + parametros;
                String jsonStr = sh.makeServiceCall(url, strusr, strpass);
                if (jsonStr != null){
                    try{
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        mensaje=jsonArray.getString(0);

                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Problemas al cambiar caja";
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
            mDialog.dismiss();
            if(mensaje.equals("Registro Exitoso") || mensaje.equals("Actualizacion Exitosa")){
                alert1.dismiss();
                alert2.dismiss();
                if(Integer.parseInt(caja2)>TOTCAJAS){
                    TOTCAJAS=Integer.parseInt(caja2);
                }
                //new AsyncConsultCA(strbran,Folio).execute();
                mostrarEnAlertListaCajas();
            }else{
                Toast.makeText(ActivityEnvTraspMultSuc.this, mensaje, Toast.LENGTH_SHORT).show();
            }//else
        }//onPost
    }//CambiarCajas

    private class AsyncInsertCajasE extends AsyncTask<Void, Void, String> {

        private String suc,folio,producto,cant,numCajas,part,usu,var,ProductoActual,newCant;
        private boolean conn,sumar;
        int alTerminar;

        public AsyncInsertCajasE(String suc, String folio, String producto, String cant,
                                 String numCajas, String part, String usu,String var,
                                 boolean sumar,String ProductoActual,int alTerminar) {
            this.suc = suc;
            this.folio = folio;
            this.producto = producto;
            this.cant = cant;
            this.numCajas = numCajas;
            this.part = part;
            this.usu = usu;
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
                String parametros="k_Sucursal="+suc+"&k_Folio="+folio+
                        "&k_Producto="+producto+"&k_Cantidad="+cant+
                        "&k_NumCajas="+numCajas+"&k_partida="+part+""+"&k_UUsuario="+usu;
                String url = "http://"+strServer+"/InsertCajasE?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if(jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        mensaje=jsonArray.getString(0);
                        if(mensaje.equals("Insertado Exitosa") || mensaje.equals("Actualizacion Exitosa")){
                            newCant=jsonArray.getString(1);
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
            if(conn==true && mensaje.equals("Insertado Exitosa") || mensaje.equals("Actualizacion Exitosa")) {
                //cajaGuard=true;
                mDialog.dismiss();
                Toast.makeText(ActivityEnvTraspMultSuc.this, "Sincronizado", Toast.LENGTH_SHORT).show();
                bepp.play(sonido_correcto, 1, 1, 1, 0, 0);
                TOTPZA=TOTPZA+Integer.parseInt(cant);
                lista.get(posicion2).setSincronizado(true);
                lista.get(posicion2).setCantSurt(newCant);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else<
        }//onPost
    }//AsyncInsertCajasE

    public int wsActualiza(String suc,String folio,String producto, int rep, int caja, String part, String usu){
        final int[] nomCaja = {caja};
        String parametros="k_Sucursal="+suc+"&k_Folio="+folio+
                "&k_Producto="+producto+"&k_Cantidad="+rep+
                "&k_NumCajas="+ nomCaja[0] +"&k_partida="+part+""+"&k_UUsuario="+usu;
        String url = "http://"+strServer+"/InsertCajasE?"+parametros;
        String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
        if(jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObj.getJSONArray("Response");
                mensaje=jsonArray.getString(0);
                if(mensaje.equals("Insertado Exitosa") || mensaje.equals("Actualizacion Exitosa")){
                    nomCaja[0]=wsSigCaja(folio);
                    TOTCAJAS=nomCaja[0];
                }else{
                    mensaje="Problema al actualizar el producto "+producto+" en la caja "+ caja;
                    nomCaja[0] =0;
                }//else
            }catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mensaje="Sin respuesta";
                        nomCaja[0] =0;
                    }//run
                });
            }//catch JSON EXCEPTION
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mensaje="Problema actualizar"; nomCaja[0] =0;
                }//run
            });//runUniTthread
        }//else
        return nomCaja[0];
    }//wsActualiza

    public int wsSigCaja(String folio){
        final int[] sigCaja = {0};
        HttpHandler sh = new HttpHandler();
        String parametros="folio="+folio+"&suc="+strbran;
        String url = "http://"+strServer+"/ConsultSigCaja?"+parametros;
        String jsonStr = sh.makeServiceCall(url,strusr,strpass);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObj.getJSONArray("Response");
                JSONObject dato = jsonArray.getJSONObject(0);
                sigCaja[0] =Integer.parseInt(dato.getString("k_sig"));
                TOTCAJAS=sigCaja[0];
            }catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mensaje="Sin datos disponibles";
                        sigCaja[0] =0;
                    }//run
                });
            }//catch JSON EXCEPTION
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mensaje="No fue posible obtener datos del servidor";sigCaja[0]=0;
                }//run
            });//runUniTthread
        }//else
        return sigCaja[0];
    }//wsSigCaja

    private class AsyncInsertCajasEContenedores extends AsyncTask<Void, Void, String> {

        private String suc,folio,producto,part,usu,var,ProductoActual,newCant;
        private boolean conn,sumar;
        int canti,numCajas,alTerminar;

        public AsyncInsertCajasEContenedores(String suc, String folio, String producto,int canti,
                                 int numCajas, String part, String usu,String var,
                                 boolean sumar,String ProductoActual,int alTerminar) {
            this.suc = suc;
            this.folio = folio;
            this.producto = producto;
            this.canti=canti;
            this.numCajas = numCajas;
            this.part = part;
            this.usu = usu;
            this.var=var;
            this.sumar=sumar;
            this.ProductoActual=ProductoActual;
            this.alTerminar=alTerminar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
        }

        @Override
        protected String doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                int rep=0;
                int caja = Integer.parseInt(spCaja.getText().toString());
                if((canti%numCajas)==0){//si se puede repartir en cajas iguales
                    rep=canti/numCajas;
                    int sum=0;
                    for(int i=0;i<numCajas;i++){
                        caja=wsActualiza(suc,folio,producto,rep,caja,part,usu);
                        sum=sum+rep;
                        newCant=sum+"";
                        if(caja==0){
                            break;
                        }//if
                    }//for
                }else{//si no se puede repartir en cajas iguales
                    rep=Math.round(canti/numCajas);
                    int sum=0;
                    for(int i=0;i<numCajas;i++){
                        if(i==(numCajas-1)){//al llegar a la ultima iteracion
                            rep=canti-sum;
                        }//if
                        caja=wsActualiza(suc,folio,producto,rep,caja,part,usu);
                        sum=sum+rep;
                        newCant=sum+"";
                        if(caja==0){
                            break;
                        }//if
                    }//for
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
            if(conn==true && mensaje.equals("Insertado Exitosa") || mensaje.equals("Actualizacion Exitosa")) {
                //cajaGuard=true;
                mDialog.dismiss();
                Toast.makeText(ActivityEnvTraspMultSuc.this, "Sincronizado", Toast.LENGTH_SHORT).show();
                bepp.play(sonido_correcto, 1, 1, 1, 0, 0);
                TOTPZA=TOTPZA+canti;
                lista.get(posicion2).setSincronizado(true);
                lista.get(posicion2).setCantSurt(newCant);
                escan=false;
                if((posicion+1)<lista.size()){
                    var="next";
                }
                tipoCambio(var);
                mostrarDetalleProd();
                CAJAACT=TOTCAJAS;
                ArrayList<String> nomCajas2=new ArrayList<>();
                for(int k=1;k<=TOTCAJAS;k++){
                    nomCajas2.add(k+"");
                }//for
                if(nomCajas2.size()>0){
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            ActivityEnvTraspMultSuc.this,R.layout.drop_down_item,nomCajas2);
                    spCaja.setAdapter(adaptador);
                    spCaja.setText(nomCajas2.get(nomCajas2.size()-1),false);
                }
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else<
        }//onPost
    }//AsyncInsertCajasEContenedores

    private class AsyncConsultSigCaja extends AsyncTask<Void, Void, Void> {

        private String folio;
        private boolean conn;

        public AsyncConsultSigCaja(String folio) {
            this.folio = folio;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mDialog.isShowing()){mDialog.show();}
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="folio="+folio+"&suc="+strbran;
                String url = "http://"+strServer+"/ConsultSigCaja?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        TOTCAJAS=Integer.parseInt(dato.getString("k_sig"));
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Sin datos disponibles";
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
            mDialog.dismiss();
            if(TOTCAJAS>=1) {
                CAJAACT=TOTCAJAS;
                ArrayList<String> nomCajas2=new ArrayList<>();
                for(int k=1;k<=TOTCAJAS;k++){
                    nomCajas2.add(k+"");
                }//for
                if(nomCajas2.size()>0){
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            ActivityEnvTraspMultSuc.this,R.layout.drop_down_item,nomCajas2);
                    spCaja.setAdapter(adaptador);
                    spCaja.setText(nomCajas2.get(nomCajas2.size()-1),false);
                }
            }else{
                TOTCAJAS=1;
                CAJAACT=TOTCAJAS;
                spCaja.setText(TOTCAJAS+"");
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
                builder.setPositiveButton("ACEPTAR",null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage(mensaje).create().show();
            }//else
        }//onPost
    }//AsyncConsulRecep

    //Reporte de Incidencias
    private class AsyncIncid extends AsyncTask<Void, Void, Void> {
        boolean conn;
        ArrayList<ListaIncidenciasSandG>listaIncidencias = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            conn=firtMet();
            if(conn==true){
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
                String[] opciones = new String[listaIncidencias.size()];
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
                        new AsyncReporteInici(Producto,RazonSuper,Folio,lista.get(posicion).getCantSurt()).execute();
                    }//onclclick
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
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

        public AsyncReporteInici(String prod, String razon, String fol,String cant) {
            this.prod = prod;
            this.razon = razon;
            this.fol = fol;
            this.cant=cant;
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
                        "&k_Folio="+fol+"&k_com=TRASPASO-"+fol+"-"+cant;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
            builder.setTitle("AVISO");
            builder.setMessage(mensaje);
            builder.setCancelable(false);
            builder.setNegativeButton("OK",null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }//onPost
    }//AsyncReporteInici



    public void tablaXcaja (Button back, Button next,int posActCaja){
        if(posActCaja==1 && TOTCAJAS>1){
            next.setEnabled(true);
            next.setBackgroundTintList(null);
            next.setBackgroundResource(R.drawable.btn_background1);
            back.setEnabled(false);
            back.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else if(TOTCAJAS==1){
            back.setEnabled(false);
            back.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            next.setEnabled(false);
            next.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        } else if(posActCaja==TOTCAJAS){
            back.setEnabled(true);
            back.setBackgroundTintList(null);
            back.setBackgroundResource(R.drawable.btn_background1);
            next.setEnabled(false);
            next.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else{
            next.setEnabled(true);
            next.setBackgroundTintList(null);
            next.setBackgroundResource(R.drawable.btn_background1);
            back.setEnabled(true);
            back.setBackgroundTintList(null);
            back.setBackgroundResource(R.drawable.btn_background1);
        }//else

    }//cambiaCaja

    public void onClickEnListaCaja(View v){
        posCaja = rvListaCajas.getChildPosition(rvListaCajas.findContainingItemView(v));
        prodSelectCaj=listaCajas.get(posCaja).getClavedelProdcuto();
        cantCajaOr=Integer.parseInt(listaCajas.get(posCaja).getCantidadUnidades());
        adapListCaj.index(posCaja);
        adapListCaj.notifyDataSetChanged();
    }//onclickEnListaCaja

    public void cambiarCajasAlert(String prod,int cantEnCaja,String origen,ArrayList<String> nomCajas,
                                  AlertDialog alert1){
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
        LayoutInflater inflater = ActivityEnvTraspMultSuc.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info_cajascambio, null);
        alert.setView(dialogView);
        alert.setCancelable(false);
        alert.setNegativeButton("CANCELAR",null);

        Button btncambiar =  dialogView.findViewById(R.id.btnCambiar);
        EditText txtCajaProd = dialogView.findViewById(R.id.txtCajaProd);
        EditText txtCajaOrigen =dialogView.findViewById(R.id.txtCajaOrigen);
        EditText txtCajaCant = dialogView.findViewById(R.id.txtCajaCant);
        //EditText txtCajaDestino =  dialogView.findViewById(R.id.txtCajaDestino);
        EditText txtCantidad =  dialogView.findViewById(R.id.txtCantidad);
        AutoCompleteTextView spCajaDest = dialogView.findViewById(R.id.spCajaDest);
        LinearLayout contP = dialogView.findViewById(R.id.contP);
        TextInputLayout cont = dialogView.findViewById(R.id.cont);
        TextInputLayout cont2 = dialogView.findViewById(R.id.cont2);

        contP.setVisibility(View.VISIBLE);
        cont2.setVisibility(View.GONE);
        cont.setVisibility(View.VISIBLE);
        txtCajaOrigen.setText(origen);
        txtCajaOrigen.setEnabled(false);
        txtCajaProd.setText(prod);
        txtCajaCant.setText(cantEnCaja+"");
        AlertDialog alert2 = alert.create();

        ArrayList<String> nomCajas2=new ArrayList<>();
        for(int k=1;k<=(TOTCAJAS+1);k++){
            if(k!=Integer.parseInt(origen)){
                nomCajas2.add(k+"");
            }
        }//for

        if(nomCajas2.size()>0){
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                    ActivityEnvTraspMultSuc.this,R.layout.drop_down_item,nomCajas2);
            spCajaDest.setAdapter(adaptador);
            spCajaDest.setText(nomCajas2.get(0),false);
        }

        btncambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Caja1ori= txtCajaOrigen.getText().toString();
                String Caja2des=spCajaDest.getText().toString();
                String cantidapro=txtCantidad.getText().toString();
                if(Caja1ori.equals(Caja2des)){
                    Toast.makeText(ActivityEnvTraspMultSuc.this, "Caja de origen igual a caja destino", Toast.LENGTH_SHORT).show();
                }else if (cantidapro.equals("") || Integer.parseInt(cantidapro)==0 || Caja2des.equals("")){
                    Toast.makeText(ActivityEnvTraspMultSuc.this,
                            "Campos vacios o en 0", Toast.LENGTH_SHORT).show();
                }else if(Integer.parseInt(cantidapro)>cantEnCaja){
                    Toast.makeText(ActivityEnvTraspMultSuc.this, "Excede cantidad de caja origen", Toast.LENGTH_SHORT).show();
                }else{
                    new AsyncCambiarCajas(strbran,Folio,prod,cantidapro,Caja1ori,Caja2des,alert1,alert2).execute();
                }//else
            }//onclick
        });
        alert2.show();
    }//cambiarCajas

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

    public void imprimir(int Cont,BluetoothPrint imprimir){
        String Cliente=lista.get(0).getAlmEnv();
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
            imprimir.printCajasE(empresa, Cliente, Folio,  listaCajasXProd, String.valueOf(Cont), imagen);
            imprimir.disconnectBT();
        } else {
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
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

    public void ImprimirTicketCajas(int Cont) {

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEnvTraspMultSuc.this);
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
    }//ImprimirTicketCaja

}//Activity
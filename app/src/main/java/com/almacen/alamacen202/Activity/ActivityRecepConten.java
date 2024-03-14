package com.almacen.alamacen202.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorCajaxProd;
import com.almacen.alamacen202.Adapter.AdaptadorEnvTraspasos;
import com.almacen.alamacen202.Adapter.AdaptadorRecepConten;
import com.almacen.alamacen202.Adapter.AdaptadorTraspasos;
import com.almacen.alamacen202.Adapter.AdapterDifUbiExi;
import com.almacen.alamacen202.Adapter.AdapterInventario;
import com.almacen.alamacen202.MainActivity;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.Inventario;
import com.almacen.alamacen202.SetterandGetters.RecepConten;
import com.almacen.alamacen202.SetterandGetters.RecepListSucCont;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
import com.almacen.alamacen202.SetterandGetters.UbicacionSandG;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.XML.XMLRecepConsul;
import com.almacen.alamacen202.XML.XMLRecepMultSuc;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import dmax.dialog.SpotsDialog;

public class ActivityRecepConten extends AppCompatActivity {
    private SharedPreferences preference;
    private float existMtz=0,existCdmx=0,existCul=0,existMty=0,existRep=0,repMtz=0,repCdmx=0,repCul=0,repMty=0,repAct=0;
    private float faltMtz=0,faltCdmx=0,faltCul=0,faltMty=0;
    private float demBMtz=0,demBCdmx=0,demBCul=0,demBMty=0;
    private int posicion=0,posicion2=0,posF=0,contReg=0;

    private String strusr,strpass,strbran,strServer,codeBar,mensaje,producto="",clasf="";
    private String matriz="01";
    private String folioAct="",folio1,folio2,folio3,sqlW="",porpalet="";
    private ArrayList<String> folios = new ArrayList<>();
    private ArrayList<RecepConten> listaRecep = new ArrayList<>();
    private ArrayList<RecepListSucCont> listaSucRecep = new ArrayList<>();
    private ArrayList<UbicacionSandG>listaUbic = new ArrayList<>();
    private EditText txtFolCon,txtCod,txtUni,txtCant,txtRecib,txtFalt,txtSobr,txtTot,txtConsol,txtProdVi;
    private ImageView ivProdR;
    private TextView tvRepMatr,tvRepCdmx,tvRepCul,tvRepMty,tvRepFres;
    private Button btnBuscar,btnListP,btnAtras,btnEnv,btnAdelante;
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private String urlImagenes,extImg;
    private RecyclerView rvListP;
    private AlertDialog alertP;
    private LinearLayout lyTvRep;
    private RadioButton rbFres,rbGua;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recep_conten);

        MyToolbar.show(this, "Recep. Contenedor", true);

        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");

        urlImagenes=preference.getString("urlImagenes", "null");
        extImg=preference.getString("ext", "null");

        mDialog = new SpotsDialog.Builder().setContext(ActivityRecepConten.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        txtFolCon   = findViewById(R.id.txtFolCon);
        txtCod      = findViewById(R.id.txtCod);
        txtProdVi   = findViewById(R.id.txtProdVi);
        txtUni      = findViewById(R.id.txtUni);
        txtCant     = findViewById(R.id.txtCant);
        txtRecib    = findViewById(R.id.txtRecib);
        txtFalt     = findViewById(R.id.txtFalt);
        txtSobr     = findViewById(R.id.txtSobr);
        txtTot      = findViewById(R.id.txtTot);
        txtConsol   = findViewById(R.id.txtConsol);

        btnBuscar   = findViewById(R.id.btnBuscar);
        btnAtras    = findViewById(R.id.btnAtras);
        btnEnv      = findViewById(R.id.btnTrasp);
        btnAdelante = findViewById(R.id.btnAdelante);

        btnListP    = findViewById(R.id.btnListP);
        tvRepMatr   = findViewById(R.id.tvRepMatr);
        tvRepCdmx   = findViewById(R.id.tvRepCdmx);
        tvRepCul    = findViewById(R.id.tvRepCul);
        tvRepMty    = findViewById(R.id.tvRepMty);
        tvRepFres   = findViewById(R.id.tvRepFres);
        lyTvRep     = findViewById(R.id.lyTvRep);
        ivProdR     = findViewById(R.id.ivProdR);

        rbFres      = findViewById(R.id.rbFres);
        rbGua       = findViewById(R.id.rbGua);

        keyboard = (InputMethodManager) getSystemService(ActivityRecepConten.INPUT_METHOD_SERVICE);

        txtCod.setInputType(InputType.TYPE_NULL);
        txtCod.setEnabled(false);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtFolCon.getText().toString().equals("")){
                    folioAct=folio(txtFolCon.getText().toString());
                    txtFolCon.setText(folioAct);
                    new AsyncRecepCon(folioAct).execute();
                }else{
                    Toast.makeText(ActivityRecepConten.this, "Folio vacío", Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });//btnBuscar

        txtCod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                producto=editable.toString();
                if(!editable.toString().equals("")){
                    txtProdVi.setText(producto);
                    buscarProd(producto);
                    /*lvFolios.performItemClick(lvFolios.getAdapter().getView(posF, null, null),
                            posF,
                            lvFolios.getAdapter().getItemId(posF));*/
                    txtCod.setText("");
                }//if es diferente a vacio
            }//after
        });//txtProd textchange

        btnListP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaRecep.size()>0){
                    AlertDialog.Builder alert = new AlertDialog.Builder(ActivityRecepConten.this);
                    LayoutInflater inflater = ActivityRecepConten.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_lista_prod2, null);
                    alert.setView(dialogView);
                    alert.setCancelable(false);
                    alert.setNegativeButton("CANCELAR",null);

                    rvListP =  dialogView.findViewById(R.id.rvListP);
                    GridLayoutManager gl = new GridLayoutManager(ActivityRecepConten.this, 1);
                    rvListP.setLayoutManager(gl);

                    AdaptadorRecepConten adapter = new AdaptadorRecepConten(listaRecep);
                    rvListP.setAdapter(adapter);
                    adapter.index(posicion);
                    rvListP.scrollToPosition(posicion);
                    alertP = alert.create();
                    alertP.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepConten.this);
                    builder.setTitle("AVISO");
                    builder.setMessage("No hay códigos");
                    builder.setCancelable(false);
                    builder.setNegativeButton("OK",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }//else
            }//onclick
        });//btnListP setonclick

        txtRecib.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int cant=Integer.parseInt(txtCant.getText().toString());
                int rec=0;
                if(!editable.toString().equals("")){
                    rec=Integer.parseInt(editable.toString());
                }
                int sob=0,falt=0;
                if(rec>cant){
                    sob=rec-cant;
                }else{
                    falt=cant-rec;
                }//else
                txtFalt.setText(falt+"");
                txtSobr.setText(sob+"");
                int tot=Integer.parseInt(txtTot.getText().toString());
                txtConsol.setText((rec-tot)+"");
            }//aftertextchange
        });

        txtRecib.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    if(txtRecib.getText().toString().equals("")){
                        txtRecib.setText("0");
                    }//iftxtrec==""
                    keyboard.hideSoftInputFromWindow(txtRecib.getWindowToken(), 0);
                    txtCod.requestFocus();
                }//if ACTION DONE
                return true;
            }//onEditoraction
        });//txtRecib.setOnEditorActionListener

        btnAdelante.setOnClickListener(new View.OnClickListener() {//boton adelante
            @Override
            public void onClick(View view) {
                posicion2=posicion;
                posicion=posicion+1;
                new AsyncRecepXProd(listaRecep.get(posicion).getProducto(),
                        listaRecep.get(posicion).getCantidad()).execute();
            }//onclick
        });//btnadelante setonclicklistener

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion2=posicion;
                posicion=posicion-1;
                new AsyncRecepXProd(listaRecep.get(posicion).getProducto(),
                        listaRecep.get(posicion).getCantidad()).execute();
            }//onclick
        });//btnatras setonclicklistener

        btnEnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncallUbicaciones().execute();
            }//onclick
        });//btnEnviar

        rbFres.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    matriz="01";
                    lyTvRep.setVisibility(View.GONE);
                }
            }//rbFres
        });//rbFres

        rbGua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    matriz="09";
                    lyTvRep.setVisibility(View.VISIBLE);
                }
            }//rbGuad
        });

    }//onCreate

    public String folio(String folio){
        if (folio.length() < 7) {
            int fo = folio.length();
            switch (fo) {
                case 1:
                    folio ="000000" + folio;
                    break;
                case 2:
                    folio ="00000" + folio;
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
                    folio ="0" + folio;
                    break;
                default:
                    folio=folio;
                    break;
            }//switch
        }//if
        return folio;
    }//folio

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

    public void limpiarCampos(){
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        existMtz=0;existCdmx=0;existCul=0;existMty=0;existRep=0;repMtz=0;repCdmx=0;repCul=0;repMty=0;
        faltMtz=0;faltCdmx=0;faltCul=0;faltMty=0;
        demBMtz=0;demBCdmx=0;demBCul=0;demBMty=0;
        txtProdVi.setText("");
        txtUni.setText("");
        txtCant.setText("0");
        txtRecib.setText("0");
        txtFalt.setText("0");
        txtSobr.setText("0");
        txtTot.setText("0");
        txtConsol.setText("0");
        tvRepMatr.setText("0");
        tvRepCdmx.setText("0");
        tvRepCul.setText("0");
        tvRepMty.setText("0");
        ivProdR.setImageResource(R.drawable.logokepler);

    }//limpiarCampos


    public void onClickListaR(View v){//cada vez que se seleccione un producto en la lista
        posicion = rvListP.getChildPosition(rvListP.findContainingItemView(v));
        seleccionaProd();
    }//onClickLista

    public void seleccionaProd(){
        if(!txtProdVi.getText().equals("")){
            producto=listaRecep.get(posicion).getProducto();
            new AsyncRecepXProd(producto,listaRecep.get(posicion).getCantidad()).execute();
        }
    }//seleccionaProd

    public void mostrarDatosProd(String pr,String cant){
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        txtProdVi.setText(pr);
        txtCant.setText(cant);
        txtRecib.setEnabled(true);
        txtFalt.setText(cant);

        Picasso.with(getApplicationContext()).
                load(urlImagenes+producto+extImg)
                .error(R.drawable.aboutlogo).fit()
                .centerInside().into(ivProdR);
        asignarReparticiones();
        cambiaProd();

        txtCod.setEnabled(true);
        txtCod.requestFocus();
        txtCod.setInputType(InputType.TYPE_NULL);
    }//mostrarDatosProd

    public void cambiaProd(){
        if(listaRecep.size()==1){
            btnAtras.setEnabled(false);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
            btnAdelante.setEnabled(false);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else if(posicion+1==listaRecep.size() && listaRecep.size()>1){
            btnAtras.setEnabled(true);
            btnAtras.setBackgroundTintList(null);
            btnAtras.setBackgroundResource(R.drawable.btn_background2);
            btnAdelante.setEnabled(false);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else if(posicion==0 && listaRecep.size()>1){
            btnAdelante.setEnabled(true);
            btnAdelante.setBackgroundTintList(null);
            btnAdelante.setBackgroundResource(R.drawable.btn_background2);
            btnAtras.setEnabled(false);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else{
            btnAtras.setEnabled(true);
            btnAtras.setBackgroundTintList(null);
            btnAtras.setBackgroundResource(R.drawable.btn_background2);
            btnAdelante.setEnabled(true);
            btnAdelante.setBackgroundTintList(null);
            btnAdelante.setBackgroundResource(R.drawable.btn_background2);
        }//else
    }//cambiaProd

    public float calculoDem(float demanda,String clasf){//
        float dem=0;
        if(clasf.equals("A")){
            dem=Float.parseFloat((demanda*2.5)+"");
        }else if(clasf.equals("B")){
            dem=Float.parseFloat((demanda*1.5)+"");
        }else{
            dem=Float.parseFloat(demanda+"");
        }
        return dem;
    }//calculoDem
    public float calculoFalt(float demanda,float exist){//
        float falt=demanda-exist;
        if(falt<0){falt=0;}
        return falt;
    }//calculoFalt

    public float calculoExistBal(String suc,float exist,float falt){
        float existBal=0;
        if(!suc.equals("01")){
            if(falt>0){
                existBal=exist;
            }
        }else{
            existBal=exist;
        }//else
        return existBal;
    }//calculoExistBal

    public float calculoDemBal(String suc,float demanda,float falt){
        float demBal=demanda;
        if(!suc.equals("01")){
            demBal=0;
            if(falt>0){
                demBal=demanda;
            }
        }else{
            demBal=demanda;
        }//else
        return demBal;
    }//calculoDemBal

    public float redondeo(float op){
        float redo=0,result=0;
        if(op>1){//REDONDEO MENOS
            redo=Math.round(op);
            if(redo>op){//es para redondear  menos, si el redondeo es mayor al original entonces se le resta uno para que sea redondeo menos
                result=redo-1;
            }else{
                result=redo;
            }
        }else{//REDONDEO MAS
            redo=Math.round(op);
            result=redo;
        }//else
        return result;
    }//redondeo

    public float calculoRepartir(float ind,float demBal,float exist,float falt){
        float op1=0,op2=0;
        if(ind<1){
            op1=(demBal*ind);
            op1=op1-exist;
        }else{
            op1=falt;
        }//else

        if(op1<0){
            op2=0;
        }else{
            op2=op1;
        }//else
        return redondeo(op2);
    }//Calculorepartir

    @SuppressLint("ResourceAsColor")
    public void calculosFinal(float sumDemBal){
        float indice=0;
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        if(existMtz!=0){
            indice=Float.parseFloat((existRep/sumDemBal)+"");
            repCdmx=calculoRepartir(indice,demBCdmx,existCdmx,faltCdmx);
            repCul=calculoRepartir(indice,demBCul,existCul,faltCul);
            repMty=calculoRepartir(indice,demBMty,existMty,faltMty);
            repMtz=existMtz-repCdmx-repCul-repMty;
        }//else

        tvRepMatr.setText(Math.round(repMtz)+"");
        tvRepCdmx.setText(Math.round(repCdmx)+"");
        tvRepCul.setText(Math.round(repCul)+"");
        tvRepMty.setText(Math.round(repMty)+"");
        int tot=Math.round(repMtz)+Math.round(repCdmx)+Math.round(repCul)+Math.round(repMty);
        txtTot.setText(tot+"");
        txtConsol.setText((Integer.parseInt(txtRecib.getText().toString())-tot)+"");

        if(tot>0){
            btnEnv.setEnabled(true);
            btnEnv.setBackgroundTintList(null);
            btnEnv.setBackgroundResource(R.drawable.btn_background3);
        }
    }//calculosFinal

    public void calculoUrgentes(String suc,float exist,float dem,String clasf,TextView tvRep){
        double op,opo;
        if(matriz.equals(suc)){
            op=exist/dem;
            if(op>0){
                op=exist/dem;
            }else{
                op=1;
            }
        }else{
            if(clasf.equals("A")){
                opo=2.5;
            }else if(clasf.equals("B")){
                opo=1.45;
            }else{
                opo=1;
            }
            op=exist/(dem/opo);
        }//else

        if(op<1 && !matriz.equals(suc)){
            tvRep.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.colorRedlight)));
        }//if urgente
    }//calculoUrgentes


    @SuppressLint("ResourceAsColor")
    public void asignarReparticiones(){
        float compr=0,trans=0,existB=0,demXClasf=0,exist,env,faltante,demB,sumDemBal=0,demanda=0;
        existMtz=0;existCdmx=0;existCul=0;existMty=0;
        faltMtz=0;faltCdmx=0;faltCul=0;faltMty=0;demBMtz=0;demBCdmx=0;demBCul=0;demBMty=0;existRep=0;
        String suc;
        tvRepMatr.setBackgroundTintList(null);
        tvRepMatr.setBackgroundResource(R.drawable.drawable_border);
        tvRepCdmx.setBackgroundTintList(null);
        tvRepCdmx.setBackgroundResource(R.drawable.drawable_border);
        tvRepCul.setBackgroundTintList(null);
        tvRepCul.setBackgroundResource(R.drawable.drawable_border);
        tvRepMty.setBackgroundTintList(null);
        tvRepMty.setBackgroundResource(R.drawable.drawable_border);
        for(int i=0;i<listaSucRecep.size();i++){
            compr=0;trans=0;exist=0;env=0;demanda=0;faltante=0;existB=0;demB=0;
            suc=listaSucRecep.get(i).getSucursal();
            clasf=listaSucRecep.get(i).getClasif();
            exist=Integer.parseInt(listaSucRecep.get(i).getExist());
            env=Integer.parseInt(listaSucRecep.get(i).getEnv());
            compr=Integer.parseInt(listaSucRecep.get(i).getCompr());
            trans=Integer.parseInt(listaSucRecep.get(i).getTrans());
            exist=exist-compr+trans+env;
            demXClasf=Float.parseFloat(listaSucRecep.get(i).getDem());
            demanda=calculoDem(demXClasf,clasf);
            faltante=calculoFalt(demanda,exist);
            existB=calculoExistBal(suc,exist,faltante);
            demB=calculoDemBal(suc,demanda,faltante);
            sumDemBal=sumDemBal+demB;
            existRep=existRep+existB;

            switch (suc){
                case "01":
                    existMtz=exist;
                    faltMtz=faltante;
                    demBMtz=demB;
                    calculoUrgentes(suc,exist,demanda,clasf,tvRepMatr);
                    break;
                case "06":
                    existCdmx=exist;
                    faltCdmx=faltante;
                    demBCdmx=demB;
                    calculoUrgentes(suc,exist,demanda,clasf,tvRepCdmx);
                    break;
                case "07":
                    existCul=exist;
                    faltCul=faltante;
                    demBCul=demB;
                    calculoUrgentes(suc,exist,demanda,clasf,tvRepCul);
                    break;
                case "08":
                    existMty=exist;
                    faltMty=faltante;
                    demBMty=demB;
                    calculoUrgentes(suc,exist,demanda,clasf,tvRepMty);
                    break;
                default:limpiarCampos();break;
            }//switch
        }//for
        calculosFinal(sumDemBal);
    }//asignarReparticiones

    public void buscarProd(String prod){
        String cant="";
        boolean band=false;
        for(int i=0;i<listaRecep.size();i++){
            if(listaRecep.get(i).getProducto().equals(prod)){
                cant=listaRecep.get(i).getCantidad();
                band=true;
                posicion=i;
                break;
            }
        }//for
        if(band==true){
            new AsyncRecepXProd(prod,cant).execute();
        }else{
            limpiarCampos();
            producto="";
            Toast.makeText(this, "Puede que el producto no se encuentre en este folio", Toast.LENGTH_SHORT).show();
        }
    }//buscarProd

    public void alertEnv(String cantRec){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepConten.this);
        LayoutInflater inflater = ActivityRecepConten.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_traspaso, null);
        builder.setView(dialogView);

        final TextView tvClvProdDial = dialogView.findViewById(R.id.tvClvProdDial);
        AutoCompleteTextView spOr = dialogView.findViewById(R.id.spOr);
        EditText txtRecep = dialogView.findViewById(R.id.txtRecep);
        EditText txtDest=dialogView.findViewById(R.id.txtDest);
        EditText txtSug = dialogView.findViewById(R.id.txtSug);
        EditText txtCantEnv= dialogView.findViewById(R.id.txtCantEnv);
        EditText txtEditable = dialogView.findViewById(R.id.txtEditable);

        tvClvProdDial.setText(listaRecep.get(posicion).getProducto());
        txtDest.setText("ENVIOS");//ubicacion almacen de recepcion
        txtRecep.setText(cantRec);

        ArrayList<String> sucu=new ArrayList<>();
        //sucu.add("MATRIZ");
        sucu.add("CDMX");
        sucu.add("CUL");
        sucu.add("MTY");

        if(sucu.size()>0){
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                    ActivityRecepConten.this,R.layout.drop_down_item,sucu);
            spOr.setAdapter(adaptador);
            spOr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i){
                        case 0:
                            txtDest.setText("ENVIOS CDMX");
                            txtSug.setText(tvRepCdmx.getText().toString());
                            break;
                        case 1:
                            txtDest.setText("ENVIOS CULIACAN");
                            txtSug.setText(tvRepCul.getText().toString());
                            break;
                        case 2:
                            txtDest.setText("ENVIOS MONTERREY");
                            txtSug.setText(tvRepMty.getText().toString());
                            break;
                    }//switch
                    txtEditable.setText(txtSug.getText().toString());
                }
            });//spCaja.setText(CAJAACT+"",false);
        }//if suclist>0
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", null);
        builder.setNegativeButton("Cancelar",null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String edita=txtEditable.getText().toString();
                        String sug=txtSug.getText().toString();
                        String recep=txtRecep.getText().toString();
                        if(edita.equals("") || Integer.parseInt(edita)<=0){
                            Toast.makeText(ActivityRecepConten.this, "Campo Vacío", Toast.LENGTH_SHORT).show();
                        }else if(Integer.parseInt(edita)> Integer.parseInt(sug)){
                            Toast.makeText(ActivityRecepConten.this, "Mayor a sugerido", Toast.LENGTH_SHORT).show();
                        }else if(Integer.parseInt(edita)>Integer.parseInt(recep)){
                            Toast.makeText(ActivityRecepConten.this, "No hay suficientes en recepción", Toast.LENGTH_SHORT).show();
                        }else{
                            new AsyncEnviar(folioAct,tvClvProdDial.getText().toString(),edita,"").execute();
                        }//else
                    }//ONCLICK
                });//SET ON CLICK
            }//onShow
        });//setonshowlistener
        dialog.show();
    }//alertEnv


    private class AsyncRecepCon extends AsyncTask<Void, Void, Void> {
        private String folio;
        private boolean conn;
        public AsyncRecepCon(String folio) {
            this.folio = folio;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
            posicion=0;
            listaRecep.clear();
            limpiarCampos();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="k_folio="+folio+"&k_suc="+strbran;
                String url = "http://"+strServer+"/resRecepConten?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        int num=1;
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaRecep.add(new RecepConten(num+"",dato.getString("k_prod"),
                                    dato.getString("k_cant") ));
                            mensaje="";num++;
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Problema al traer datos";
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
            if (listaRecep.size()>0) {
                keyboard.hideSoftInputFromWindow(txtFolCon.getWindowToken(), 0);
                new AsyncRecepXProd(listaRecep.get(posicion).getProducto(),
                        listaRecep.get(posicion).getCantidad()).execute();
            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepConten.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else
        }//onPost
    }//AsyncRecepCon

    private class AsyncRecepXProd extends AsyncTask<Void, Boolean, Boolean> {
        private String producto,cantidad;
        private boolean conn;
        public AsyncRecepXProd(String producto,String cantidad) {
            this.producto=producto;
            this.cantidad=cantidad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mDialog.isShowing()){
                mDialog.show();
            }//if showing
            mensaje="";
            listaSucRecep.clear();
            limpiarCampos();
        }//onPreExecute

        @Override
        protected Boolean doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();//separar párametros con &
                String parametros="k_producto="+producto;
                String url = "http://"+strServer+"/resRecepXprod?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaSucRecep.add(new RecepListSucCont(dato.getString("k_suc"),
                                    dato.getString("k_clasf"), dato.getString("k_exist"),
                                    dato.getString("k_env"),dato.getString("k_compr"),
                                    dato.getString("k_trans"), dato.getString("k_dem")));
                            mensaje="";
                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Problema al traer datos";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                } else {
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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mDialog.dismiss();
            if(listaSucRecep.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepConten.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
                txtProdVi.setText("");
                limpiarCampos();
            }else{
                if(alertP!=null){//si esta mostrando el alert de lista de productos para cerrarlo
                    alertP.dismiss();
                }
                mostrarDatosProd(producto,cantidad);
            }//else
        }//onPost
    }//AsyncRecepCon

    private class AsyncEnviar extends AsyncTask<Void, Void, String> {

        private String folio,producto,cant,ubEnv;
        private boolean conn;

        public AsyncEnviar(String folio, String producto, String cant,
                                 String ubEnv) {
            this.folio = folio;
            this.producto = producto;
            this.cant = cant;
            this.ubEnv = ubEnv;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                String parametros="k_Sucursal="+strbran+"&k_Folio="+folio+
                        "&k_Producto="+producto+"&k_Cantidad="+cant;
                String url = "http://"+strServer+"/InsertEnv?"+parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url,strusr,strpass);
                if(jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        mensaje=jsonArray.getString(0);
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
            if(conn==true && mensaje.equals("Guardado")) {

            }else{
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepConten.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else<
        }//onPost
    }//AsyncEnviar

    private class AsyncallUbicaciones extends AsyncTask<Void, Void, Void> {

        private boolean conn;
        private String cantRecep="0";

        @Override
        protected void onPreExecute() {
            mDialog.show();
            mensaje="";
            listaUbic.clear();
            if(mDialog.isShowing()==false){
                mDialog.show();
            }
        }//onPreejecute

        @Override
        protected Void doInBackground(Void... params) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="&k_suc="+strbran;
                String url = "http://"+strServer+"/listUbic?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            String alm=dato.getString("k_alm");
                            if(alm.equals("20")){
                                cantRecep=dato.getString("k_cant");
                                mensaje="";
                                break;
                            }

                        }//for
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="Problema al traer datos";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="No se recibió la cantidad de piezas del almacen de recepción";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            }else{
                mensaje="Problemas de conexión";
                return null;
            }//else
        }//doInBackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if(mensaje.equals("")){
                alertEnv(cantRecep);
            }else {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityRecepConten.this);
                alerta.setMessage(mensaje).setCancelable(false).
                        setNegativeButton("Ok", null);//alertDialogBuilder
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Aviso");
                titulo.show();
            }//else
        }//OnpostEjecute
    }//class AsynCall

}//ActivityRecepConten

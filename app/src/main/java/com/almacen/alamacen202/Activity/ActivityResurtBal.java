package com.almacen.alamacen202.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorDetUbi;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.DetUbi;
import com.almacen.alamacen202.SetterandGetters.RecepListSucCont;
import com.almacen.alamacen202.XML.XMLCLArticulo;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class ActivityResurtBal extends AppCompatActivity {
    private ArrayList<RecepListSucCont> listaSucRecep = new ArrayList<>();
    private ArrayList<DetUbi> listaUb = new ArrayList<>();
    private AdaptadorDetUbi adapUb;
    private SharedPreferences preference;
    private String strusr,strpass,strbran,strServer,codeBar,mensaje="",clasf="";
    private AlertDialog mDialog;
    private EditText txtEscanP,txtEscanVi,txtRecolect,txtSurt,txtRellenar,txtRest,txtCantUb;
    private AutoCompleteTextView spUbic;//Spinner
    private Button btnSurtir;
    //private RecyclerView rvUbic;
    private ImageView ivProdMont;
    private TextView tvRepMtz,tvRepCdmx,tvRepCul,tvRepMty,tvUbic,tvMaxi,tvMini,tvCantU;
    private String urlImagenes,extImg,producto="",celda="",prodGuard="";
    private float existMtz=0,existCdmx=0,existCul=0,existMty=0,existRep=0,repMtz=0,repCdmx=0,repCul=0,repMty=0,repAct=0;
    private float faltMtz=0,faltCdmx=0,faltCul=0,faltMty=0;
    private float demBMtz=0,demBCdmx=0,demBCul=0,demBMty=0;
    private int posicion1=0,totRec=0,surtido=0,maxU=0;
    private RequestQueue mQueue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resurt);

        MyToolbar.show(this, "Resurtido Balanceo", true);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");
        urlImagenes=preference.getString("urlImagenes", "null");
        extImg=preference.getString("ext", "null");

        mDialog = new SpotsDialog.Builder().setContext(ActivityResurtBal.this).
                setMessage("Espere un momento...").build();
        mQueue = Volley.newRequestQueue(this);

        txtEscanP   = findViewById(R.id.txtEscanP);
        txtEscanVi  = findViewById(R.id.txtEscanVi);
        spUbic      = findViewById(R.id.spUbic);

        tvRepMtz    = findViewById(R.id.tvRepMtz);
        tvRepCdmx   = findViewById(R.id.tvRepCdmx);
        tvRepCul    = findViewById(R.id.tvRepCul);
        tvRepMty    = findViewById(R.id.tvRepMty);
        tvUbic      = findViewById(R.id.tvUbic);
        tvMaxi      = findViewById(R.id.tvMaxi);
        tvMini      = findViewById(R.id.tvMini);
        tvCantU     = findViewById(R.id.tvCantU);
        txtCantUb   = findViewById(R.id.txtCantUb);
        tvMaxi      = findViewById(R.id.tvMaxi);

        txtRecolect = findViewById(R.id.txtRecolect);
        txtSurt     = findViewById(R.id.txtSurt);
        txtRellenar = findViewById(R.id.txtRellenar);
        txtRest    = findViewById(R.id.txtRest);
        ivProdMont  = findViewById(R.id.ivProdMont);

        btnSurtir = findViewById(R.id.btnSurtir);


        txtEscanP.setInputType(InputType.TYPE_NULL);
        txtEscanP.requestFocus();


        //rvUbic.setLayoutManager(new LinearLayoutManager(ActivityResurtBal.this));
        adapUb = new AdaptadorDetUbi(listaUb);

        txtEscanP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                producto=editable.toString();
                if (!editable.toString().equals("")) {
                    evaluarNovacios(editable);
                }//if
            }//afterTextChange
        });

        btnSurtir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int s=Integer.parseInt(txtSurt.getText().toString());
                if(s>0){
                    String origen=spUbic.getText().toString(),destino=tvUbic.getText().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResurtBal.this);
                    builder.setTitle("CONFIRMAR");
                    builder.setMessage("¿Desea surtir de "+origen+" a "+destino+" "+s+" piezas?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("SURTIR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AsyncModificarUbicDestino(origen,destino,txtEscanVi.getText().toString(),s+"",dialogInterface).execute();
                        }
                    });
                    builder.setNegativeButton("CANCELAR",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResurtBal.this);
                    builder.setTitle("AVISO");
                    builder.setMessage("Sin cantidad para surtir");
                    builder.setCancelable(false);
                    builder.setNegativeButton("OK",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }//else

            }//onclick
        });//btnSurtir onclick
    }//onCreate

    public void evaluarNovacios(Editable editable){
        if(editable.toString().equals(prodGuard)){//si es el mismo producto ya solo hace conteo y no va a buscar de nuevo
            if((surtido+1)>totRec){//para que no exceda totRec
                Toast.makeText(ActivityResurtBal.this, "Excede cantidad a recolectar", Toast.LENGTH_SHORT).show();
            }else{
                if(!tvUbic.getText().equals("") || Integer.parseInt(txtRecolect.getText().toString())>0 || Integer.parseInt(txtRellenar.getText().toString())>0){
                    surtido=surtido+1;
                    escanear();
                }else{
                    Toast.makeText(this, "No se puede surtir", Toast.LENGTH_SHORT).show();
                }
            }
            txtEscanP.setText("");
        }else{//busca el producto y sus datos
            txtEscanVi.setText(producto);
            prodGuard=producto;
            surtido=0;
            if (codeBar.equals("Zebra")) {
                new AsyncRecepXProd(producto).execute();
            } else {
                for (int i = 0; i < editable.length(); i++) {
                    char ban;
                    ban = editable.charAt(i);
                    if (ban == '\n') {
                        new AsyncRecepXProd(producto).execute();
                    }//if
                }//for
            }//else
            txtEscanP.setText("");
        }//else
    }

    public void escanear(){
        txtSurt.setText(surtido+"");
        txtRest.setText((totRec-surtido)+"");
    }//escanear

    public void calculoRellCelda(){
        int cantCelda=Integer.parseInt(tvCantU.getText().toString());
        int max=Integer.parseInt(tvMaxi.getText().toString()),mitadM=0,rell=0;
        int ex=cantCelda;
        if(clasf.equals("A")){
            mitadM=Math.round(max/2);
            if(ex<mitadM){
                rell=max-ex;
            }
        }
        txtRellenar.setText(rell+"");
    }//calculoRellCelda



    public void limpiarCampos(){
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        existMtz=0;existCdmx=0;existCul=0;existMty=0;existRep=0;repMtz=0;repCdmx=0;repCul=0;repMty=0;
        faltMtz=0;faltCdmx=0;faltCul=0;faltMty=0;
        demBMtz=0;demBCdmx=0;demBCul=0;demBMty=0;
        celda="";maxU=0;
        spUbic.setAdapter(null);
        spUbic.setText("");
        txtCantUb.setText("0");
        totRec=0;surtido=0;
        txtRellenar.setText("0");
        txtEscanVi.setText("");
        txtRecolect.setText("0");
        tvUbic.setText("");
        tvMaxi.setText("0");
        tvMini.setText("0");
        tvCantU.setText("0");
        txtRest.setText("0");
        txtSurt.setText("0");
        tvRepMtz.setText("0");
        tvRepCdmx.setText("0");
        tvRepCul.setText("0");
        tvRepMty.setText("0");
        ivProdMont.setImageResource(R.drawable.logokepler);
    }//limpiarCampos

    public void mostrarDatosProd(String pr){
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        txtEscanVi.setText(pr);

        Picasso.with(getApplicationContext()).
                load(urlImagenes+producto+extImg)
                .error(R.drawable.aboutlogo)
                .fit()
                .centerInside()
                .into(ivProdMont);
        asignarReparticiones();
    }//mostrarDatosProd

    public void calculos(String clasf,String max,String cantCelda){
        int maximo=0,mitadM=0,stock=0;
        int ex=Integer.parseInt(cantCelda);
        if(clasf.equals("A")){
            maximo=Integer.parseInt(max);
            mitadM=Math.round(maximo/2);
            if(ex<mitadM){
                stock=maximo-ex;
            }else{
                stock=mitadM-ex;
            }
            txtRellenar.setText(stock+"");
        }
    }

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
    }

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
    }

    @SuppressLint("ResourceAsColor")
    public void calculosFinal(float sumDemBal){
        int r1=0,r2=0,r3=0,r4=0;
        float indice=0;
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        if(existMtz!=0){
            indice=Float.parseFloat((existRep/sumDemBal)+"");
            repCdmx=calculoRepartir(indice,demBCdmx,existCdmx,faltCdmx);
            repCul=calculoRepartir(indice,demBCul,existCul,faltCul);
            repMty=calculoRepartir(indice,demBMty,existMty,faltMty);
            repMtz=existMtz-repCdmx-repCul-repMty;
        }//else

        r1=Math.round(repMtz);
        r2=Math.round(repCdmx);
        r3=Math.round(repCul);
        r4=Math.round(repMty);
        tvRepMtz.setText(r1+"");
        tvRepCdmx.setText(r2+"");
        tvRepCul.setText(r3+"");
        tvRepMty.setText(r4+"");

        totRec=r2+r3+r4;
        txtRecolect.setText(totRec+"");

    }//calculosFinal


    public void asignarReparticiones(){
        float compr=0,trans=0,existB=0,demXClasf=0,exist,faltante,demB,sumDemBal=0,demanda=0;
        existMtz=0;existCdmx=0;existCul=0;existMty=0;
        faltMtz=0;faltCdmx=0;faltCul=0;faltMty=0;demBMtz=0;demBCdmx=0;demBCul=0;demBMty=0;existRep=0;
        String suc;
        for(int i=0;i<listaSucRecep.size();i++){
            compr=0;trans=0;exist=0;demanda=0;faltante=0;existB=0;demB=0;
            suc=listaSucRecep.get(i).getSucursal();
            clasf=listaSucRecep.get(i).getClasif();
            exist=Integer.parseInt(listaSucRecep.get(i).getExist());
            compr=Integer.parseInt(listaSucRecep.get(i).getCompr());
            trans=Integer.parseInt(listaSucRecep.get(i).getTrans());
            exist=(exist-compr)+trans;
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
                    break;
                case "06":
                    existCdmx=exist;
                    faltCdmx=faltante;
                    demBCdmx=demB;
                    break;
                case "07":
                    existCul=exist;
                    faltCul=faltante;
                    demBCul=demB;
                    break;
                case "08":
                    existMty=exist;
                    faltMty=faltante;
                    demBMty=demB;
                    break;
                default:break;
            }//switch
        }//for

        calculosFinal(sumDemBal);
    }//asignarReparticiones



    private class AsyncDetUbi extends AsyncTask<Void, Void, Void> {

        String prod;
        ArrayList<String> lista = new ArrayList<>();

        public AsyncDetUbi(String prod) {
            this.prod = prod;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!mDialog.isShowing()){
                mDialog.show();
            }
            //rvUbic.setAdapter(null);
            listaUb.clear();
            mensaje="";
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();//separar párametros con &
            String parametros="k_suc="+strbran+"&k_prod="+prod;
            String url = "http://"+strServer+"/"+getString(R.string.resDetUbi)+"?"+parametros;
            String jsonStr = sh.makeServiceCall(url,strusr,strpass);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Obtener array de datos
                    JSONArray jsonArray = jsonObj.getJSONArray("Response");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                        listaUb.add(new DetUbi(dato.getString("k_ubi"),dato.getString("k_cant"),dato.getString("k_maxi"),
                                dato.getString("k_mini"),dato.getString("k_punt"),dato.getString("k_clasf")));
                        lista.add(dato.getString("k_ubi"));
                        mensaje="";
                    }//for
                }catch (final JSONException e) {
                    //Log.e(TAG, "Error al convertir Json: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="No se pudieron traer datos de celdas";
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
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            mDialog.dismiss();
            txtEscanP.setText("");
            if(listaUb.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResurtBal.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                //spUbic.setEnabled(true);
                String ub="",in="";
                boolean find=false;

                //UBICAR CELDA
                for(int i=0;i<listaUb.size();i++){
                    ub=listaUb.get(i).getUbicacion();
                    in=ub.substring(0,1)+"";
                    if(in.equals("P") && find==false){
                        lista.remove(i);
                        tvUbic.setText(ub);
                        tvMaxi.setText(listaUb.get(i).getMax());
                        tvMini.setText(listaUb.get(i).getMin());
                        tvCantU.setText(listaUb.get(i).getCant());
                        find=true;
                    }
                }//for
                if(find==false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResurtBal.this);
                    builder.setTitle("AVISO");
                    builder.setMessage("No se pudó identificar celda");
                    builder.setCancelable(false);
                    builder.setNegativeButton("OK",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityResurtBal.this,R.layout.drop_down_item,lista);
                spUbic.setAdapter(adapter);

                spUbic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        txtCantUb.setText(listaUb.get(i).getCant());
                        maxU=Integer.parseInt(listaUb.get(i).getMax());
                        calculoRellCelda();
                        txtEscanP.requestFocus();
                    }//onItemClick
                });//setonitemclick

                if(lista.size()>0){//para poner en automatico la posicion 0
                    spUbic.setText(lista.get(0),false);
                    txtCantUb.setText(listaUb.get(0).getCant());
                    maxU=Integer.parseInt(listaUb.get(0).getMax());
                    calculoRellCelda();
                    txtRest.setText(totRec+"");
                    txtEscanP.requestFocus();
                }

            }//else
        }//onPost
    }//AsyncRecepCon

    private class AsyncRecepXProd extends AsyncTask<Void, Boolean, Boolean> {
        private String producto;

        public AsyncRecepXProd(String producto) {
            this.producto=producto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
            limpiarCampos();
            listaSucRecep.clear();
        }//onPreExecute

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();//separar párametros con &
            String parametros="k_producto="+producto;
            String url = "http://"+strServer+"/"+getString(R.string.resRecepXProd)+"?"+parametros;
            String jsonStr = sh.makeServiceCall(url,strusr,strpass);
            //Log.e(TAG, "Respuesta de la url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Obtener array de datos
                    JSONArray jsonArray = jsonObj.getJSONArray("Response");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                        listaSucRecep.add(new RecepListSucCont(dato.getString("k_suc"),dato.getString("k_clasf"),
                                dato.getString("k_exist"),dato.getString("k_compr"),dato.getString("k_trans"),
                                dato.getString("k_dem")));
                        mensaje="";
                    }//for
                }catch (final JSONException e) {
                    //Log.e(TAG, "Error al convertir Json: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="No se pudieron traer datos de este producto";
                        }//run
                    });
                }//catch JSON EXCEPTION
            } else {
                //Log.e(TAG, "Problemas al traer datos");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mensaje="No fue posible obtener datos del servidor";
                    }//run
                });//runUniTthread
            }//else
            return null;
        }//doInBackground

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(listaSucRecep.size()==0) {
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResurtBal.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                mostrarDatosProd(producto);
                new AsyncDetUbi(producto).execute();
            }//else
        }//onPost
    }//AsyncRecepCon

    private class AsyncModificarUbicDestino extends AsyncTask<Void, Void, Void> {
        String UbicacionOrigen,UbicacionDestino,Producto,Cantidad;
        DialogInterface dialogInterface;

        public AsyncModificarUbicDestino(String ubicacionOrigen, String ubicacionDestino, String producto, String cantidad,DialogInterface dialogInterface) {
            UbicacionOrigen = ubicacionOrigen;
            UbicacionDestino = ubicacionDestino;
            Producto = producto;
            Cantidad = cantidad;
            this.dialogInterface=dialogInterface;
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreexecute

        @Override
        protected Void doInBackground(Void... params) {
            mensaje="";
            consultaUbicacionMod(UbicacionOrigen,UbicacionDestino,Producto,Cantidad);
            return null;
        }//doInBackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if(mensaje.equals("LA UBICACION A SIDO INSERTADO") || mensaje.equals("LA UBICACION A SIDO ACTUALIZADA")){
                dialogInterface.dismiss();
                Toast.makeText(ActivityResurtBal.this, mensaje, Toast.LENGTH_SHORT).show();
                new AsyncRecepXProd(Producto).execute();
            }else{
                Toast.makeText(ActivityResurtBal.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }//onPosteExecute
    }

    private void consultaUbicacionMod(String UbicacionOrigen,String UbicacionDestino,String Producto,String Cantidad) {
        String SOAP_ACTION = "CLArticulo";
        String METHOD_NAME = "CLArticulo";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLCLArticulo soapEnvelope = new XMLCLArticulo(SoapEnvelope.VER11);
            soapEnvelope.XMLCLArticulo(strusr, strpass, UbicacionOrigen, UbicacionDestino, strbran, Producto, Cantidad);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            response = (SoapObject) response.getProperty("message");
            mensaje = (response.getPropertyAsString("k_menssage").equals("anyType{}") ? "" : response.getPropertyAsString("k_menssage"));


        } catch (SoapFault soapFault) {
            mensaje = "Error:" + soapFault.getMessage();
            soapFault.printStackTrace();
        } catch (XmlPullParserException e) {
            mensaje = "Error:" + e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            mensaje = "No se encontró servidor";
            e.printStackTrace();
        } catch (Exception ex) {
            mensaje = "Error:" + ex.getMessage();
        }//catch
    }//AsyncCallModificarUbic
}//clase principal

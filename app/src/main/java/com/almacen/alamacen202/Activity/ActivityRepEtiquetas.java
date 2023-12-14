package com.almacen.alamacen202.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorTraspasos;
import com.almacen.alamacen202.Adapter.AdapterListProd;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.ListaIncidenciasSandG;
import com.almacen.alamacen202.SetterandGetters.ProdEtiq;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
import com.almacen.alamacen202.XML.XMLActualizaOrdenCompra;
import com.almacen.alamacen202.XML.XMLListProd;
import com.almacen.alamacen202.XML.XMLRecepConsul;
import com.almacen.alamacen202.XML.XMLRepEtiq;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
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
import java.util.ArrayList;
import java.util.Vector;

import dmax.dialog.SpotsDialog;

public class ActivityRepEtiquetas extends AppCompatActivity {
    private AlertDialog mDialog;
    private SharedPreferences preference;
    private String strusr,strpass,strbran,strServer,codeBar,mensaje="",claveInci="";
    private AdapterListProd adapter;
    private RecyclerView rvProd;
    private EditText txtProdE,txtNomProd,txtDescProd,txtComentario;
    private AutoCompleteTextView spInci;
    private ImageView ivProd;
    private Button btnEnviar,btnBuscP;
    private int posicion=0;
    private String urlImagenes,extImg;
    private InputMethodManager keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_etiq);
        mDialog = new SpotsDialog.Builder().setContext(ActivityRepEtiquetas.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        MyToolbar.show(this, "Reportar Incidencias", true);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");
        urlImagenes=preference.getString("urlImagenes", "null");
        extImg=preference.getString("ext", "null");

        txtProdE= findViewById(R.id.txtProdE);
        txtNomProd= findViewById(R.id.txtNomProd);
        txtDescProd= findViewById(R.id.txtDescProd);
        txtComentario= findViewById(R.id.txtComentario);
        ivProd= findViewById(R.id.ivProd);
        //ivCloseSearch = findViewById(R.id.ivCloseSearch);
        spInci = findViewById(R.id.spInci);
        btnEnviar= findViewById(R.id.btnEnviar);
        btnBuscP =findViewById(R.id.btnBuscP);

        keyboard = (InputMethodManager) getSystemService(ActivityEnvTraspMultSuc.INPUT_METHOD_SERVICE);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboard.hideSoftInputFromWindow(txtComentario.getWindowToken(), 0);
                keyboard.hideSoftInputFromWindow(txtProdE.getWindowToken(), 0);
                String prod=txtNomProd.getText().toString();
                String razon=spInci.getText().toString();
                String comm=txtComentario.getText().toString();
                if(prod.equals("") || razon.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRepEtiquetas.this);
                    builder.setPositiveButton("ACEPTAR", null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("Existen campos vacios").create().show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRepEtiquetas.this);
                    builder.setNegativeButton("CANCELAR",null);
                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            new AsyncReporteInici(prod,razon,"",comm).execute();
                        }
                    });
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("¿Desea reportar incidencia de: "+razon+" del producto "+prod+"?").create().show();
                }//else
            }
        });//btnEnviaronclick

        btnBuscP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtProdE.getText().toString().equals("")){
                    new AsyncListProd(txtProdE.getText().toString()).execute();
                }else{
                    Toast.makeText(ActivityRepEtiquetas.this, " Campo Vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });//btnBuscP
    }

    public void vaciarDet(){
        txtNomProd.setText("");
        txtDescProd.setText("");
        txtComentario.setText("");
        spInci.setText("");
        claveInci="";
        ivProd.setImageResource(R.drawable.aboutlogo);
    }
    private static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }//isNumeric

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


    public void mostrarDetalle(String prod,String descip){
        txtNomProd.setText(prod);
        txtDescProd.setText(descip);

        Picasso.with(getApplicationContext()).
                load(urlImagenes+
                        txtNomProd.getText().toString() + extImg)
                .error(R.drawable.aboutlogo)
                .fit()
                .centerInside()
                .into(ivProd);
    }//mostrarDetalle

    //Consultar prod
    private class AsyncListProd extends AsyncTask<Void, Void, Void> {

        private boolean conn,var;
        private String prod,descrip,lin;

        public AsyncListProd(String prod) {
            this.prod = prod;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            vaciarDet();
            keyboard.hideSoftInputFromWindow(txtComentario.getWindowToken(), 0);
            keyboard.hideSoftInputFromWindow(txtProdE.getWindowToken(), 0);
            mDialog.show();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn=firtMet();
            if(conn==true){
                HttpHandler sh = new HttpHandler();
                String parametros="k_prod="+prod;
                String url = "http://"+strServer+"/ListProd?"+parametros;
                String jsonStr = sh.makeServiceCall(url,strusr,strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        prod=dato.getString("k_prod");
                        descrip=dato.getString("k_descrip");
                        lin=dato.getString("k_lin");
                        mensaje="";
                        if(prod.equals("")){
                            mensaje="No existe producto";
                        }
                    }catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje="No existe producto";
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
            if(mensaje.equals("")) {
                mostrarDetalle(prod,descrip);
                new AsyncIncid().execute();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRepEtiquetas.this);
                builder.setPositiveButton("ACEPTAR",null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage(mensaje).create().show();
            }//else
        }//onPost
    }//AsyncConsulRecep



    //Consultar incidencias
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
                ArrayList<String> listaIn = new ArrayList<>();
                for (int i = 0; i < listaIncidencias.size(); i++) {
                    listaIn.add(listaIncidencias.get(i).getMensaje());
                }//for

                ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                        ActivityRepEtiquetas.this,R.layout.drop_down_item,listaIn);
                spInci.setAdapter(adaptador);
                spInci.setText(listaIn.get(0),false);
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRepEtiquetas.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }//else
        }//onPostExecute
    }//AsyncIncid

    //Registrar incidencia
    private class AsyncReporteInici extends AsyncTask<Void, Void, Void> {
        boolean conn;
        private String prod,razon,fol,comm;

        public AsyncReporteInici(String prod, String razon, String fol,String comm) {
            this.prod = prod;
            this.razon = razon;
            this.fol = fol;
            this.comm=comm;
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
                        "&k_Folio="+fol+"&k_com="+comm;
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
                                mensaje="Problema al registrar";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="Problema en el servidor";
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRepEtiquetas.this);
            builder.setTitle("AVISO");
            builder.setMessage(mensaje);
            builder.setCancelable(false);
            builder.setNegativeButton("OK",null);
            AlertDialog dialog = builder.create();
            dialog.show();
            txtProdE.setText("");
            txtProdE.requestFocus();
        }//onPost
    }//AsyncReporteInici
}

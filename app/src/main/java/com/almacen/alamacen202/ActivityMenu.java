package com.almacen.alamacen202;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.almacen.alamacen202.Activity.ActivityConsultaPA;
import com.almacen.alamacen202.Activity.ActivityDifUbiExi;
import com.almacen.alamacen202.Activity.ActivityEnvTraspMultSuc;
import com.almacen.alamacen202.Activity.ActivityInventario;
import com.almacen.alamacen202.Activity.ActivityInventarioXProd;
import com.almacen.alamacen202.Activity.ActivityLiberaciones;
import com.almacen.alamacen202.Activity.ActivityRecepTraspMultSuc;
import com.almacen.alamacen202.Activity.ActivityResurtBal;
import com.almacen.alamacen202.Activity.ActivityRepEtiquetas;
import com.almacen.alamacen202.Activity.ActivityResurtidoPicking;
import com.almacen.alamacen202.Activity.ActivityRecepConten;
import com.almacen.alamacen202.Activity.ActivityTrasladoUbi;
import com.almacen.alamacen202.Activity.ActivityInventarioXfolioComp;
import com.almacen.alamacen202.SetterandGetters.RecepListSucCont;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.includes.HttpHandler;
import com.almacen.alamacen202.includes.MyToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class ActivityMenu extends AppCompatActivity {


    private ImageView imgVi;
    private String StrServer,strusr,strpass,mensaje="",versionApp;
    private LinearLayout Conten;
    private SharedPreferences preference,preferenceF,preferenceD,preferenceR;
    private SharedPreferences.Editor editor,editor2,editor3,editor4;
    private String codeBarClave,urlImagenes,extIm;;

    private ConexionSQLiteHelper conn;
    private SQLiteDatabase db;
    private LinearLayout lyAdicSPR,ly2;
    private AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        MyToolbar.show(this, "Menu", false);

        conn = new ConexionSQLiteHelper(ActivityMenu.this, "bd_INVENTARIO", null, 1);
        db = conn.getReadableDatabase();
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        preferenceF = getSharedPreferences("Folio", Context.MODE_PRIVATE);//para guardar folio
        preferenceD = getSharedPreferences("FolioDif", Context.MODE_PRIVATE);//para guardar folio
        preferenceR = getSharedPreferences("FoliosGuarda", Context.MODE_PRIVATE);
        editor = preference.edit();
        editor2 = preferenceF.edit();
        editor3 = preferenceD.edit();
        editor4 = preferenceR.edit();
        Conten = findViewById(R.id.ContImage);
        imgVi = findViewById(R.id.productoImag);
        StrServer = preference.getString("Server", "null");
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        versionApp=getString(R.string.versionNum);
        mDialog = new SpotsDialog.Builder().setContext(ActivityMenu.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        urlImagenes=getString(R.string.urlImagenesGeneral);
        extIm=getString(R.string.ext);

        lyAdicSPR = findViewById(R.id.lyAdicSPR);
        ly2= findViewById(R.id.ly2);

        switch (StrServer) {
            case "jacve.dyndns.org:9085":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.jacve)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.VISIBLE);
                break;
            case "sprautomotive.servehttp.com:9085":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.vipla)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.VISIBLE);
                break;
            case "cecra.ath.cx:9085":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.cecra)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.VISIBLE);
                break;
            case "guvi.ath.cx:9085":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.guvi)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.VISIBLE);
                break;
            case "cedistabasco.ddns.net:9085":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.pressa)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.VISIBLE);
                break;
            case "autodis.ath.cx:9085":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.autodis)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.VISIBLE);
                break;
            case "sprautomotive.servehttp.com:9090":
                Conten.setBackgroundColor(Color.rgb(4, 59, 114));
                Picasso.with(getApplicationContext()).
                        load(R.drawable.roda)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                urlImagenes=getString(R.string.urlImagenesSPR);
                extIm=getString(R.string.ext);
                break;
            case "sprautomotive.servehttp.com:9095":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.partech)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                urlImagenes=getString(R.string.urlImagenesSPR);
                extIm=getString(R.string.ext);
                break;
            case "sprautomotive.servehttp.com:9080":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.shark)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                urlImagenes=getString(R.string.urlImagenesSPR);
                extIm=getString(R.string.ext);
                break;
            case "vazlocolombia.dyndns.org:9085":
                Picasso.with(getApplicationContext()).
                        load(R.drawable.bhp)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.VISIBLE);
                break;
            default:
                Picasso.with(getApplicationContext()).
                        load(R.drawable.logokepler)
                        .error(R.drawable.logokepler)
                        .fit()
                        .centerInside()
                        .into(imgVi);
                lyAdicSPR.setVisibility(View.GONE);
                ly2.setVisibility(View.GONE);
                break;
        }//switch
        editor.putString("urlImagenes",urlImagenes);
        editor.putString("ext", extIm);
        editor.commit();
    }

    private class AsyncVersionesApp extends AsyncTask<Void, Boolean, Boolean> {
        String respuesta="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }//onPreExecute

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();//separar párametros con &
            String parametros="Clave=3";
            String url = "http://"+StrServer+"/resVersionesApp?"+parametros;
            String jsonStr = sh.makeServiceCall(url,strusr,strpass);
            //Log.e(TAG, "Respuesta de la url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Obtener array de datos
                    JSONArray jsonArray = jsonObj.getJSONArray("Response");
                    respuesta=jsonArray.getString(0);
                }catch (final JSONException e) {
                    //Log.e(TAG, "Error al convertir Json: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            respuesta=e.getMessage();
                        }//run
                    });
                }//catch JSON EXCEPTION
            } else {
                //Log.e(TAG, "Problemas al traer datos");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        respuesta="No fue posible obtener datos del servidor";
                    }//run
                });//runUniTthread
            }//else
            return null;
        }//doInBackground

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mDialog.dismiss();
            if(!respuesta.equals(versionApp)){//si la vresion no es la misma
                if(!respuesta.equals("")){
                    mensaje="Hay una nueva actualización, favor de instalarla";
                }else{
                    mensaje=respuesta;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMenu.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }//if
        }//onPost
    }//AsyncVersionesApp


    public void eliminarSqlySP() {//eliminar bd y shared preferences yani(true cuando tambien se incluya la de inventario)
        try{
            SQLiteDatabase db = conn.getWritableDatabase();
            db.delete("INVENTARIOALM",null,null);
            db.delete("INVENTARIO",null,null);
            db.delete("DIFUBIEXIST",null,null);
            db.delete("RECEPCONT",null,null);
            editor2.clear().commit();
            editor3.clear().commit();
            editor4.clear().commit();
        }catch(Exception e){}
    }//eliminarSql

    public void Perfildelusuario (View view) {
        Intent perfilusuario = new Intent(ActivityMenu.this, ActivityPerfil.class);
        startActivity(perfilusuario);
    }
    public void ConsultaProductoMenu(View view) {
        Intent CosnultaProducto = new Intent(ActivityMenu.this, ActivityConsultaPA.class);
        startActivity(CosnultaProducto);
    }
    public void LiberacionesMenu(View view) {
        Intent Liberaciones = new Intent(ActivityMenu.this, ActivityLiberaciones.class);
        startActivity(Liberaciones);
    }
    public void trasladoUbiMenu(View view) {
        Intent UbicacionTraslado = new Intent(ActivityMenu.this, ActivityTrasladoUbi.class);
        startActivity(UbicacionTraslado);
    }
    public void RepcionCompras(View view) {
        Intent UbicacionTraslado = new Intent(ActivityMenu.this, RecepCompras.class);
        startActivity(UbicacionTraslado);
    }
    public void invXFolComp(View view) {
        Intent intent = new Intent(ActivityMenu.this, ActivityInventarioXfolioComp.class);
        startActivity(intent);
    }
    public void invXProd(View v ){
        Intent invP = new Intent(ActivityMenu.this, ActivityInventarioXProd.class);
        startActivity(invP);
    }//invXProd
    public void resurtPick(View v){
        Intent intent = new Intent(ActivityMenu.this, ActivityResurtidoPicking.class);
        startActivity(intent);
    }//resurtPicking
    public void inventario(View v){
        Intent intent = new Intent(ActivityMenu.this, ActivityInventario.class);
        startActivity(intent);
    }public void traspasos(View v){
        Intent intent = new Intent(ActivityMenu.this, ActivityRecepTraspMultSuc.class);
        startActivity(intent);
    }//inventario
    public void difUbiExis(View v){
        Intent intent = new Intent(ActivityMenu.this, ActivityDifUbiExi.class);
        startActivity(intent);
    }//diferencia entre ubicaciones y existenciasinventario
    public void recolectMontCarg(View v){
        Intent intent = new Intent(ActivityMenu.this, ActivityResurtBal.class);
        startActivity(intent);
    }//recolectMontCarg
    public void recepCont(View v){
        Intent intent = new Intent(ActivityMenu.this, ActivityRecepConten.class);
        startActivity(intent);
    }//reporte de etiquetas
    public void reportInci(View v){
        Intent intent = new Intent(ActivityMenu.this, ActivityRepEtiquetas.class);
        startActivity(intent);
    }//reporte de etiquetas
    public void envRecepTrasp(View v){
        startActivity(new Intent(ActivityMenu.this, ActivityEnvTraspMultSuc.class));
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoverflow, menu);
        if(StrServer.equals("sprautomotive.servehttp.com:9090")){
           MenuItem item = menu.findItem(R.id.MenuSPR);
           MenuItem itemRod = menu.findItem(R.id.RodatechMenu);
           MenuItem itemPartech = menu.findItem(R.id.PartechMenu);
           MenuItem itemSharck = menu.findItem(R.id.SharkMenu);
           itemRod.setVisible(false);
           itemPartech.setVisible(true);
           itemSharck.setVisible(true);
           item.setVisible(true);
       }else if(StrServer.equals("sprautomotive.servehttp.com:9095")){
           MenuItem item = menu.findItem(R.id.MenuSPR);

           MenuItem itemRod = menu.findItem(R.id.RodatechMenu);
           MenuItem itemPartech = menu.findItem(R.id.PartechMenu);
           MenuItem itemSharck = menu.findItem(R.id.SharkMenu);
           itemRod.setVisible(true);
           itemPartech.setVisible(false);
           itemSharck.setVisible(true);

           item.setVisible(true);
       }else if(StrServer.equals("sprautomotive.servehttp.com:9080")){
           MenuItem item = menu.findItem(R.id.MenuSPR);


           MenuItem itemRod = menu.findItem(R.id.RodatechMenu);
           MenuItem itemPartech = menu.findItem(R.id.PartechMenu);
           MenuItem itemSharck = menu.findItem(R.id.SharkMenu);
           itemRod.setVisible(true);
           itemPartech.setVisible(true);
           itemSharck.setVisible(false);


           item.setVisible(true);
       }else{
           MenuItem item = menu.findItem(R.id.MenuSPR);
           item.setVisible(false);
       }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (id == R.id.cerrarSe) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityMenu.this);
                alerta.setMessage("¿Desea cerrar sesión?").setCancelable(false).setNegativeButton("CANCELAR", null);
                alerta.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.clear();
                        editor.commit();
                        eliminarSqlySP();
                        getApplicationContext().deleteDatabase("bd_INVENTARIO");
                        Intent cerrar = new Intent(ActivityMenu.this, MainActivity.class);
                        startActivity(cerrar);
                        System.exit(0);
                        finish();
                    }//onclick
                });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Aviso");
                titulo.show();



            }else if (id == R.id.RodatechMenu){
                StrServer = "sprautomotive.servehttp.com:9090";
                editor.putString("Server", StrServer);
                editor.putString("urlImagenes",urlImagenes);
                editor.putString("ext", extIm);
                editor.commit();
                eliminarSqlySP();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                finish();
            }else if (id == R.id.PartechMenu){
                StrServer = "sprautomotive.servehttp.com:9095";
                editor.putString("Server", StrServer);
                editor.putString("urlImagenes",urlImagenes);
                editor.putString("ext", extIm);
                editor.commit();
                eliminarSqlySP();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                finish();
            }else if (id == R.id.SharkMenu){
                StrServer = "sprautomotive.servehttp.com:9080";
                editor.putString("Server", StrServer);
                editor.putString("urlImagenes",urlImagenes);
                editor.putString("ext", extIm);
                editor.commit();
                eliminarSqlySP();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                finish();
            }
            else if (id == R.id.idZebra){
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityMenu.this);
                alerta.setMessage("USTED A SELECCIONADO EL LECTOR DE CODIGO ZEBRA").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        codeBarClave="Zebra";
                        editor.putString("codeBar",codeBarClave);
                        editor.commit();
                    }
                });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("¡AVISO!");
                titulo.show();

            }else if (id == R.id.idOtros){
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityMenu.this);
                alerta.setMessage("USTED A SELECCIONADO EL LECTOR DE CODIGO GENERICO").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        codeBarClave="Generico";
                        editor.putString("codeBar",codeBarClave);
                        editor.commit();
                    }
                });

                AlertDialog titulo = alerta.create();
                titulo.setTitle("¡AVISO!");
                titulo.show();
            }

        } else {
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityMenu.this);
            alerta.setMessage("No hay Conexion a Internet").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog titulo = alerta.create();
            titulo.setTitle("!ERROR! CONEXION");
            titulo.show();

        }//else


        return super.onOptionsItemSelected(item);
    }//OnOptionItemSelected

}//activity menu
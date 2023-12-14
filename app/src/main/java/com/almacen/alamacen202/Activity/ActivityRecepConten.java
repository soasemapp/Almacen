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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private SharedPreferences preference,preference2;
    private SharedPreferences.Editor editor;
    private boolean revisa=false,seleccion=false,tipoBusq=false;
    private float existMtz=0,existCdmx=0,existCul=0,existMty=0,existRep=0,repMtz=0,repCdmx=0,repCul=0,repMty=0,repAct=0;
    private float faltMtz=0,faltCdmx=0,faltCul=0,faltMty=0;
    private float demBMtz=0,demBCdmx=0,demBCul=0,demBMty=0;
    private int posicion=0,posF=0,contReg=0;
    private ArrayAdapter<String> adapterLv;

    private String strusr,strpass,strbran,strServer,codeBar,mensaje,producto="",clasf="";
    private String folioAct="",folio1,folio2,folio3,sqlW="",porpalet="";
    private ArrayList<String> folios = new ArrayList<>();
    private ArrayList<RecepConten> listaRecep = new ArrayList<>();
    private ArrayList<RecepListSucCont> listaSucRecep = new ArrayList<>();
    private ListView lvFolios;
    private TextView tvFolioR,tvPalet;
    private EditText txtProdR,txtCantRec,txtPalet,txtProdVi;
    private ImageView ivProdR;
    private TextView tvRepMatr,tvRepCdmx,tvRepCul,tvRepMty;
    private Button btnBuscaFolio,btnPalet;
    private RadioButton rdXProd;
    private RecyclerView rvRecep;
    private AdaptadorRecepConten adapter;
    private AlertDialog mDialog;
    private ConexionSQLiteHelper conn;
    private SQLiteDatabase db;
    private InputMethodManager keyboard;
    private String urlImagenes,extImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recep_conten);

        MyToolbar.show(this, "Recep. Conten. x prod.", true);


        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");

        preference2= getSharedPreferences("FoliosGuarda", Context.MODE_PRIVATE);//para guardar folio
        editor = preference2.edit();
        folio1=preference2.getString("folio1","");
        folio2=preference2.getString("folio2","");
        folio3=preference2.getString("folio3","");

        urlImagenes=preference.getString("urlImagenes", "null");
        extImg=preference.getString("ext", "null");

        mDialog = new SpotsDialog.Builder().setContext(ActivityRecepConten.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        lvFolios        = findViewById(R.id.lvFolios);
        txtProdR        = findViewById(R.id.txtProdR);
        txtCantRec      = findViewById(R.id.txtCantRec);
        txtPalet        = findViewById(R.id.txtPalet);
        btnBuscaFolio   = findViewById(R.id.btnBuscaFolio);
        tvRepMatr       = findViewById(R.id.tvRepMatr);
        tvRepCdmx       = findViewById(R.id.tvRepCdmx);
        tvRepCul        = findViewById(R.id.tvRepCul);
        tvRepMty        = findViewById(R.id.tvRepMty);
        ivProdR         = findViewById(R.id.ivProdR);
        txtProdVi       = findViewById(R.id.txtProdVi);
        rdXProd         = findViewById(R.id.rdXProd);
        btnPalet        = findViewById(R.id.btnPalet);
        tvFolioR        = findViewById(R.id.tvFolioR);
        tvPalet         = findViewById(R.id.tvPalet);

        conn = new ConexionSQLiteHelper(ActivityRecepConten.this, "bd_INVENTARIO", null, 1);
        db = conn.getReadableDatabase();//apertura de la base de datos interna
        rvRecep    = findViewById(R.id.rvRecep);
        rvRecep.setLayoutManager(new LinearLayoutManager(ActivityRecepConten.this));
        adapter = new AdaptadorRecepConten(listaRecep);
        keyboard = (InputMethodManager) getSystemService(ActivityRecepConten.INPUT_METHOD_SERVICE);

        txtProdR.setInputType(InputType.TYPE_NULL);
        txtProdR.requestFocus();

        rdXProd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    porpalet="";
                    tvPalet.setVisibility(View.GONE);
                    lvFolios.performItemClick(lvFolios.getAdapter().getView(posF, null, null),
                            posF,
                            lvFolios.getAdapter().getItemId(posF));
                }
            }//onckeckedchange
        });

        btnPalet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertPalet();
            }
        });//btnPalet

        lvFolios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                posF=i;
                limpiarCampos();
                if(i>0){//cuando hay folio seleccionado
                    folioAct=folios.get(i);
                    tvFolioR.setText("FOLIO: "+folioAct);
                    tvFolioR.setVisibility(View.VISIBLE);
                    if(!porpalet.equals("")){//sin palet
                        sqlW="SELECT PRODUCTO,CANTIDAD,PRIORIDAD,''," +
                                "NAMEPALET FROM RECEPCONT " +
                                "WHERE  FOLIO='"+folioAct+"' AND NAMEPALET='"+porpalet+"'";
                        tvPalet.setVisibility(View.VISIBLE);
                        tvPalet.setText("PALET: "+porpalet);
                    }else{//con palet
                        sqlW="SELECT R.PRODUCTO,SUM(R.CANTIDAD),R.PRIORIDAD,''," +
                                "IFNULL(GROUP_CONCAT(R.NAMEPALET,','),'NP') AS PALET FROM RECEPCONT R " +
                                "WHERE R.FOLIO='"+folioAct+"' GROUP BY R.PRODUCTO,R.PRIORIDAD HAVING PALET NOT LIKE '%NP%' ";
                    }
                }else{//cuando es todos los folios
                    tvFolioR.setVisibility(View.GONE);
                    folioAct="";
                    if(!porpalet.equals("")){//sin palet
                        sqlW="SELECT PRODUCTO,CANTIDAD,PRIORIDAD,FOLIO," +
                                "NAMEPALET FROM RECEPCONT " +
                                "WHERE NAMEPALET='"+porpalet+"'";
                        tvPalet.setVisibility(View.VISIBLE);
                        tvPalet.setText("PALET: "+porpalet);
                    }else{//con palet
                        /*sqlW="SELECT R.PRODUCTO,SUM(R.CANTIDAD),R.PRIORIDAD,IFNULL((SELECT GROUP_CONCAT(FOLIO,',') " +
                                "FROM (SELECT DISTINCT FOLIO FROM RECEPCONT WHERE PRODUCTO=R.PRODUCTO)),'')," +
                                "IFNULL(GROUP_CONCAT(R.NAMEPALET,','),'NP') AS PALET FROM RECEPCONT R " +
                                " GROUP BY R.PRODUCTO,R.PRIORIDAD HAVING PALET NOT LIKE '%NP%' ";*/
                        sqlW="SELECT R.PRODUCTO,SUM(R.CANTIDAD),R.PRIORIDAD," +
                                "IFNULL((SELECT GROUP_CONCAT(FOLIO,',') " +
                                "FROM (SELECT DISTINCT FOLIO FROM RECEPCONT WHERE PRODUCTO=R.PRODUCTO)),'')," +
                                "IFNULL((SELECT GROUP_CONCAT(NAMEPALET,',') "+
                                "FROM (SELECT DISTINCT NAMEPALET FROM RECEPCONT WHERE PRODUCTO=R.PRODUCTO)),'') AS PALET FROM RECEPCONT R " +
                                " GROUP BY R.PRODUCTO,R.PRIORIDAD ";
                    }
                }//else
                tipoBusq=false;
                consultaSql(sqlW);
            }
        });

        btnBuscaFolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncRecepFolios().execute();
            }//onclick
        });//btnBuscaFolio

        txtProdR.addTextChangedListener(new TextWatcher() {
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
                    lvFolios.performItemClick(lvFolios.getAdapter().getView(posF, null, null),
                            posF,
                            lvFolios.getAdapter().getItemId(posF));
                    txtProdR.setText("");
                }//if es diferente a vacio
            }//after
        });//txtProd textchange

        if(!folio1.equals("")){
            folios.clear();
            folios.add("--Todos--");
            folios.add(folio1);
            if(!folio2.equals("")){
                folios.add(folio2);
            }if(!folio3.equals("")){
                folios.add(folio3);
            }//else
            listaFolios();
            consultaSql(sqlW);
        }
        txtProdR.requestFocus();
        txtProdR.setInputType(InputType.TYPE_NULL);
    }//onCreate

    public void alertPalet(){
        String[] palets=new String[0];
        try{
            String whe="";
            int k=0;
            if(!folioAct.equals("")){
                whe=" WHERE FOLIO='"+folioAct+"'";
            }
            @SuppressLint("Recycle") Cursor fila = db.rawQuery("SELECT NAMEPALET FROM RECEPCONT  "+whe+" GROUP BY NAMEPALET ORDER BY NAMEPALET", null);
            int t=fila.getCount();
            palets= new String[t];
            if (fila != null && fila.moveToFirst()) {
                do {
                    palets[k]=fila.getString(0);
                    k++;
                } while (fila.moveToNext());
            }//if
            fila.close();
            AlertDialog.Builder alert = new AlertDialog.Builder(ActivityRecepConten.this);
            String[] finalPalets = palets;
            alert.setTitle("Lista de de Palets").setItems(finalPalets, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    porpalet= finalPalets[i];
                    lvFolios.performItemClick(lvFolios.getAdapter().getView(posF, null, null),
                            posF,
                            lvFolios.getAdapter().getItemId(posF));
                    tvPalet.setVisibility(View.VISIBLE);
                    tvPalet.setText("PALET: "+porpalet);
                    rdXProd.setChecked(false);
                }
            }).setNegativeButton("Cancelar",null);
            alert.create();
            AlertDialog dialog = alert.create();
            dialog.show();
        }catch(Exception e){
            Toast.makeText(ActivityRecepConten.this,
                    "Error al consultar palets", Toast.LENGTH_SHORT).show();
        }//catch
    }//alertPalet

    public void listaFolios(){
        adapterLv = new ArrayAdapter<String>(ActivityRecepConten.this, android.R.layout.simple_list_item_1,folios);
        lvFolios.setAdapter(adapterLv);
        lvFolios.requestFocusFromTouch();
        lvFolios.performItemClick(lvFolios, 0, 0);
    }//listaFolios

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void limpiarCampos(){
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        existMtz=0;existCdmx=0;existCul=0;existMty=0;existRep=0;repMtz=0;repCdmx=0;repCul=0;repMty=0;
        faltMtz=0;faltCdmx=0;faltCul=0;faltMty=0;
        demBMtz=0;demBCdmx=0;demBCul=0;demBMty=0;
        txtProdVi.setText("");
        txtPalet.setText("");
        txtCantRec.setText("0");
        tvRepMatr.setText("0");
        tvRepCdmx.setText("0");
        tvRepCul.setText("0");
        tvRepMty.setText("0");
        tvFolioR.setText("");
        tvFolioR.setVisibility(View.GONE);
        tvPalet.setText("");
        tvPalet.setVisibility(View.GONE);
        ivProdR.setImageResource(R.drawable.logokepler);
    }//limpiarCampos

    public void onClickListaR(View v){//cada vez que se seleccione un producto en la lista
        posicion = rvRecep.getChildPosition(rvRecep.findContainingItemView(v));
        seleccionaProd();
    }//onClickLista

    public void seleccionaProd(){
        if(!txtProdVi.getText().equals("")){
            String f=listaRecep.get(posicion).getFolio();
            producto=listaRecep.get(posicion).getProducto();
            adapter.index(posicion);
            adapter.notifyDataSetChanged();
            rvRecep.scrollToPosition(posicion);
            if(f.equals("")){
                f=folioAct;
            }
            new AsyncRecepXProd(producto,listaRecep.get(posicion).getCantidad(),listaRecep.get(posicion).getPalet()).execute();
        }
    }//seleccionaProd

    public void mostrarDatosProd(String pr,String cant,String palet){
        repMtz=0;repCdmx=0;repCul=0;repMty=0;
        txtProdVi.setText(pr);
        txtCantRec.setText(cant);
        txtPalet.setText(palet);

        Picasso.with(getApplicationContext()).
                load(urlImagenes+producto+extImg)
                .error(R.drawable.aboutlogo)
                .fit()
                .centerInside()
                .into(ivProdR);
        asignarReparticiones();
    }//mostrarDatosProd

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
                default:limpiarCampos();break;
            }//switch
        }//for

        calculosFinal(sumDemBal);
    }//asignarReparticiones

    public void mostrarLista(){
        adapter= new AdaptadorRecepConten(listaRecep);
        rvRecep.setAdapter(adapter);
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        rvRecep.scrollToPosition(posicion);
    }//mostrar lista

    public void buscarProd(String prod){
        try{
            String cant="",palet="";
            boolean band=false;
            for(int i=0;i<listaRecep.size();i++){
                if(listaRecep.get(i).getProducto().equals(prod)){
                    cant=listaRecep.get(i).getCantidad();
                    palet=listaRecep.get(i).getPalet();
                    band=true;
                    posicion=i;
                    break;
                }
            }//for
            if(band==true){
                new AsyncRecepXProd(prod,cant,palet).execute();
            }else{
                limpiarCampos();
                producto="";
                Toast.makeText(this, "Puede que el producto no exista en el folio seleccionado y/o palet", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            limpiarCampos();
            producto="";
            Toast.makeText(ActivityRecepConten.this, "No se encontró el producto", Toast.LENGTH_SHORT).show();
        }//catch
    }

    public void consultaSql(String sql){
        try{
            listaRecep.clear();
            rvRecep.setAdapter(null);
            int j=0;
            int gu=-1;
            @SuppressLint("Recycle") Cursor fila = db.rawQuery(sql, null);
            if (fila != null && fila.moveToFirst()) {
                do {
                    j++;
                    if(producto.equals(fila.getString(0))){
                        gu=j-1;
                    }
                    listaRecep.add(new RecepConten(j+"",fila.getString(0),fila.getString(1),
                            fila.getString(2),fila.getString(3),fila.getString(4)));
                } while (fila.moveToNext());
                posicion=gu;
                mostrarLista();
            }//if
            fila.close();
        }catch(Exception e){
            Toast.makeText(ActivityRecepConten.this,
                    "Error al consultar datos de la base de datos interna", Toast.LENGTH_SHORT).show();
        }//catch
    }//consultaSql


    public boolean insertarSql(String folio,String prod,String palet,String cantp,String prior){
        boolean res=false;
        try{
            ContentValues valores = new ContentValues();
            valores.put("FOLIO", folio);
            valores.put("PRODUCTO", prod);
            valores.put("NAMEPALET", palet);
            valores.put("CANTIDAD", cantp);
            valores.put("PRIORIDAD", prior);
            db.insertOrThrow("RECEPCONT", null, valores);
            res=true;
            contReg++;
        }catch(SQLException sqlException){
            sqlException=sqlException;
        } catch(Exception e){
        }
        return res;
    }//insertarSql

    public boolean eliminarSql(String where) {
        //"FOLIO='"+folio+"' AND PRODUCTO='"+prod+"'"
        boolean res=false;
        try{
            SQLiteDatabase db = conn.getWritableDatabase();
            db.delete("RECEPCONT",where,null);
            db.delete("PALET",null,null);
            res=true;
        }catch(SQLException sqlException){}catch(Exception e){}
        return res;
    }//eliminarSql


    private class AsyncRecepCon extends AsyncTask<Void, Void, Void> {

        private String folio1,folio2,folio3;

        public AsyncRecepCon(String folio1, String folio2, String folio3) {
            this.folio1 = folio1;
            this.folio2 = folio2;
            this.folio3 = folio3;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
            editor.clear().commit();
            posicion=-1;
            listaRecep.clear();
            rvRecep.setAdapter(null);
            limpiarCampos();
            eliminarSql("");
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            String parametros="k_folio1="+folio1+"&k_folio2="+folio2+"&k_folio3="+folio3+"";
            String url = "http://"+strServer+"/"+getString(R.string.resRecepConten)+"?"+parametros;
            String jsonStr = sh.makeServiceCall(url,strusr,strpass);
            //Log.e(TAG, "Respuesta de la url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Obtener array de datos
                    JSONArray jsonArray = jsonObj.getJSONArray("Response");
                    contReg=0;
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                        insertarSql(dato.getString("k_fol"),dato.getString("k_prod"),
                                dato.getString("k_paletCaja"),dato.getString("k_cant"),dato.getString("k_prio"));
                        mensaje="";
                    }//for
                } catch (final JSONException e) {
                    //Log.e(TAG, "Error al convertir Json: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="Puede que el folio no exista";
                        }//run
                    });
                }//catch JSON EXCEPTION
            }else {
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
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            mDialog.dismiss();
            if (contReg==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepConten.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                listaFolios();
                editor.putString("folio1", folio1);
                editor.putString("folio2", folio2);
                editor.putString("folio3", folio3);
                editor.commit();
                posicion=-1;
                //mostrarLista();
                consultaSql(sqlW);
                txtProdR.requestFocus();
                txtProdR.setInputType(InputType.TYPE_NULL);
            }//else
        }//onPost
    }//AsyncRecepCon

    private class AsyncRecepXProd extends AsyncTask<Void, Boolean, Boolean> {
        private String producto,cantidad,palet;

        public AsyncRecepXProd(String producto,String cantidad,String palet) {
            this.producto=producto;
            this.cantidad=cantidad;
            this.palet=palet;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
            listaSucRecep.clear();
            limpiarCampos();
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
                mostrarDatosProd(producto,cantidad,palet);
            }//else
        }//onPost
    }//AsyncRecepCon

    private class AsyncRecepFolios extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
            folios.clear();
            limpiarCampos();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();//separar párametros con &
            String url = "http://"+strServer+"/"+getString(R.string.resRecepFolios)+"";
            String jsonStr = sh.makeServiceCall(url,strusr,strpass);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Obtener array de datos
                    JSONArray jsonArray = jsonObj.getJSONArray("Response");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                        folios.add(dato.getString("k_folio")+":"+dato.getString("k_fecha"));
                        mensaje="";
                    }//for
                }catch (final JSONException e) {
                    //Log.e(TAG, "Error al convertir Json: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje="No se pudieron traer datos de folios";
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
            if(folios.size()==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepConten.this);
                builder.setTitle("AVISO");
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setNegativeButton("OK",null);
                AlertDialog dialog = builder.create();
                dialog.show();
                txtProdVi.setText("");
            }else{
                folio1="";folio2="";folio3="";
                ArrayList<String>selectedItems = new ArrayList<>();
                CharSequence[] items = folios.toArray(new CharSequence[3]);

                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityRecepConten.this);
                alert.setTitle("Lista de Folios");
                alert.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if(b){
                            String[] sp = folios.get(i).split(":");
                            if (selectedItems.size() < 3) {
                                selectedItems.add(0, sp[0]);
                            } else {
                                ((AlertDialog) dialogInterface).getListView().setItemChecked(i, false);
                                Toast.makeText(ActivityRecepConten.this, "3 folios máximo", Toast.LENGTH_SHORT).show();
                            }//else
                        }else{
                            if(selectedItems.size()==1){
                                selectedItems.clear();
                            }else{
                                selectedItems.remove(i);
                            }
                        }
                    }
                });
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(selectedItems.size()>0){
                            folios.clear();
                            folios.add("--Todos--");
                            for (int j = 1; j<(selectedItems.size()+1); j++) {
                                folios.add(selectedItems.get(j-1));
                            }//for
                            switch (folios.size()){
                                case 2:
                                    folio1=folios.get(1);
                                    break;
                                case 3:
                                    folio1=folios.get(1);
                                    folio2=folios.get(2);
                                    break;
                                case 4:
                                    folio1=folios.get(1);
                                    folio2=folios.get(2);
                                    folio3=folios.get(3);
                                    break;
                            }
                            new AsyncRecepCon(folio1, folio2, folio3).execute();
                        }else{
                            Toast.makeText(ActivityRecepConten.this, "Sin folios para consultar", Toast.LENGTH_SHORT).show();
                        }//else
                    }//onclick
                });
                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                alert.create();
                AlertDialog dialog = alert.create();
                dialog.show();
            }//else

        }//onPost
    }//AsyncRecepCon

}//ActivityRecepConten

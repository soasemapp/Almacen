package com.almacen.alamacen202.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorlistOrdComp;
import com.almacen.alamacen202.Adapter.AdapterResurtidoPicking;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.ComprometidasSandG;
import com.almacen.alamacen202.SetterandGetters.ListProdxFolOrdComp;
import com.almacen.alamacen202.SetterandGetters.ResurtidoPicking;
import com.almacen.alamacen202.SetterandGetters.UbicacionSandG;
import com.almacen.alamacen202.XML.XMLActualizaOrdenCompra;
import com.almacen.alamacen202.XML.XMLActualizaPick;
import com.almacen.alamacen202.XML.XMLCLArticulo;
import com.almacen.alamacen202.XML.XMLCompromeAlma;
import com.almacen.alamacen202.XML.XMLConsulRack;
import com.almacen.alamacen202.XML.XMLConsulResPick;
import com.almacen.alamacen202.XML.XMLConsulXPicking;
import com.almacen.alamacen202.XML.XMLConsultaOrdenCompra;
import com.almacen.alamacen202.XML.XMLConsultaXprod;
import com.almacen.alamacen202.XML.XMLUbicacionAlma;
import com.almacen.alamacen202.includes.MyToolbar;
import com.squareup.picasso.Picasso;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import dmax.dialog.SpotsDialog;

public class ActivityResurtidoPicking extends AppCompatActivity {

    private String strusr,strpass,strbran,strServer,mensaje="",fechaReg,horaReg;
    private String totUbi,totAlm,max,min,cant,cantEmpq,clavProd,descProd,ubic,cantAlm;
    private int posicion=0,posGuard=0,ord=1,necesidad=0;
    private Button btnBuscar,btnResurtir,btnAdelante,btnAtras,btnFinalizar;
    private CheckBox chbUbic,chbPFech;
    private TextView tvClvProdPick,tvDescPick,tvCantEmpq,tvOrigenR,tvCantOrig,tvNecesidad;
    private EditText txtSumUbiPick,txtSumAlmPick,txtMax,txtMin,txtUbicPick,txtCantUbicPick,txtCantAcum;
    private RecyclerView rvPicking;
    private ImageView ivProdPick;
    private SharedPreferences preference;
    private AlertDialog mDialog;
    private ArrayList<ResurtidoPicking> listPick = new ArrayList<>();
    private ArrayList<UbicacionSandG>listaUbic = new ArrayList<>();
    private AdapterResurtidoPicking adapter = new AdapterResurtidoPicking(listPick);
    private ArrayList<ComprometidasSandG> listaComprometidas = new ArrayList<>();
    private boolean pendientes=false;//saber si hay pendientes o no
    private String urlImagenes,extImg;
    private EditText txtDestEmpq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resurtido_picking);

        MyToolbar.show(this, "Resurtido de Picking", true);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        urlImagenes=preference.getString("urlImagenes", "null");
        extImg=preference.getString("ext", "null");

        mDialog = new SpotsDialog.Builder().setContext(ActivityResurtidoPicking.this).
                setMessage("Espere un momento...").build();

        btnBuscar   = findViewById(R.id.btnBusca);
        btnResurtir = findViewById(R.id.btnResurtir);
        btnAtras    = findViewById(R.id.btnAtras);
        btnAdelante = findViewById(R.id.btnAdelante);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        tvClvProdPick= findViewById(R.id.tvClvProdPick);
        tvDescPick = findViewById(R.id.tvDescPick);
        tvCantEmpq = findViewById(R.id.tvCantEmpq);
        txtSumUbiPick = findViewById(R.id.txtSumUbiPick);
        txtSumAlmPick = findViewById(R.id.txtSumAlmPick);
        txtMax = findViewById(R.id.txtMax);
        txtMin = findViewById(R.id.txtMin);
        txtUbicPick = findViewById(R.id.txtUbicPick);
        txtCantUbicPick = findViewById(R.id.txtCantUbicPick);
        txtCantAcum = findViewById(R.id.txtCantAcum);

        rvPicking  = findViewById(R.id.rvPicking);
        ivProdPick = findViewById(R.id.ivProdPick);
        chbUbic = findViewById(R.id.chbUbic);
        chbPFech = findViewById(R.id.chbPFech);

        GridLayoutManager gl = new GridLayoutManager(ActivityResurtidoPicking.this, 1);
        rvPicking.setLayoutManager(gl);


        chbUbic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chbUbic.setChecked(true);
                chbPFech.setChecked(false);
                ord=1;
            }
        });
        chbPFech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chbPFech.setChecked(true);
                chbUbic.setChecked(false);
                ord=2;
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean var=true;
                if(listPick.size()>0){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityResurtidoPicking.this);
                    alerta.setMessage("¿Desea volver a consultar datos?").setCancelable(false).
                            setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    fechaReg=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    horaReg=new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                    firtMet();
                                    dialogInterface.cancel();
                                }//onClick
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }//onClick
                            });//Alert
                    AlertDialog titulo = alerta.create();
                    titulo.setTitle("Aviso");
                    titulo.show();
                }else{
                    fechaReg=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    horaReg=new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    firtMet();
                }//else
            }//onclick
        });//btnBuscarOrd

        btnAdelante.setOnClickListener(new View.OnClickListener() {//boton adelante
            @Override
            public void onClick(View view) {
                posicion++;
                clavProd=listPick.get(posicion).getClaveProd();
                descProd=listPick.get(posicion).getDescrip();
                ubic=listPick.get(posicion).getPicking();
                new AsynCallConsulXPicking(null).execute();
            }//onclick
        });//btnadelante setonclicklistener

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion--;
                clavProd=listPick.get(posicion).getClaveProd();
                descProd=listPick.get(posicion).getDescrip();
                ubic=listPick.get(posicion).getPicking();
                new AsynCallConsulXPicking(null).execute();
            }//onclick
        });//btnatras setonclicklistener

        btnResurtir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsynCallConsulXPicking(null).execute();
                if(necesidad>0){
                    new AsyncallUbicaciones(null).execute();
                }else{
                    alertNecesidad0();
                }//else
            }//onclick
        });//btnResurtir setonclick

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityResurtidoPicking.this);
                alerta.setMessage("¿Desea que el registro de "+clavProd+" quede como finalizado?").setCancelable(false).
                        setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new AsyncActualizaPick().execute();
                                dialogInterface.cancel();
                            }//onClick
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }//onClick
                        });//Alert
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Confirmar");
                titulo.show();
            }//onclick
        });//btnFinalizar onclick

    }//onCreate

    public void alertNecesidad0(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityResurtidoPicking.this);
        alerta.setMessage("Cantidad de necesidad es 0 o menor ¿Desea actualizar este registro como finalizado?").setCancelable(false).
                setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AsyncActualizaPick().execute();
                        dialogInterface.cancel();
                    }//onClick
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }//onClick
                });//Alert
        AlertDialog titulo = alerta.create();
        titulo.setTitle("Aviso");
        titulo.show();
    }//alertNecesidad0

    public void firtMet() {//firtMet
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {//si hay conexion a internet
            listPick.clear();
            tvClvProdPick.setText("");
            tvDescPick.setText("");
            txtUbicPick.setText("");
            txtSumUbiPick.setText("");
            txtSumAlmPick.setText("");
            txtMax.setText("");
            txtMin.setText("");
            txtCantUbicPick.setText("");
            tvCantEmpq.setText("");
            rvPicking.setAdapter(null);
            ivProdPick.setImageResource(R.drawable.aboutlogo);
            Picasso.with(getApplicationContext()).
                    load(urlImagenes +
                            tvClvProdPick.getText().toString() + extImg)
                    .error(R.drawable.aboutlogo)
                    .fit()
                    .centerInside()
                    .into(ivProdPick);
            new AsynCallConsulResPick().execute();
        } else {
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityResurtidoPicking.this);
            alerta.setMessage("NO HAY CONEXIÓN A INTERNET").setCancelable(false).
                    setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }//onClick
                    });//Alert

            AlertDialog titulo = alerta.create();
            titulo.setTitle("¡ERROR DE CONEXIÓN!");
            titulo.show();
        }//else
    }//FirtMet
    private static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }//isNumeric

    public void insertarDatosResurtir(){
        tvOrigenR.setText("SIN IDENTIFICAR");
        tvCantOrig.setText("0");
        int cantO=0,op=0;
        for(int i=0;i<listaUbic.size();i++){
            char chara=listaUbic.get(i).getUbicacion().charAt(0);
            if(!listaUbic.get(i).getUbicacion().equals("EMPAQUE") && chara!='P' && chara!='Q' && Integer.parseInt(listaUbic.get(i).getCantidad())>0){//evitar ubicaciones de picking
                tvOrigenR.setText(listaUbic.get(i).getUbicacion());
                cantO=Integer.parseInt(listaUbic.get(i).getCantidad());
                tvCantOrig.setText(cantO+"");
                break;
            }
        }//acomodar en lista string las ubicaciones

        tvCantEmpq.setText(cantEmpq);
        tvNecesidad.setText(necesidad+"");
        if(cantO>=necesidad){
            op=necesidad;
        }else{
            if(cantO>0){
                op= cantO;
            }
        }
        txtDestEmpq.setText(op+"");
    }

    public void resurtir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_resurtido, null);
        builder.setView(dialogView);

        final TextView tvClvProdDial = dialogView.findViewById(R.id.tvClvProdDial);
        final TextView tvDescProdDial = dialogView.findViewById(R.id.tvDescProdDial);
        tvCantOrig = dialogView.findViewById(R.id.tvCantOrig);
        tvCantEmpq = dialogView.findViewById(R.id.tvCantEmpq);
        txtDestEmpq=dialogView.findViewById(R.id.txtDestEmpq);
        tvNecesidad = dialogView.findViewById(R.id.tvNecesidad);
        tvOrigenR = dialogView.findViewById(R.id.tvOrigenR);

        tvClvProdDial.setText(tvClvProdPick.getText().toString());
        tvDescProdDial.setText(tvDescPick.getText().toString());

        insertarDatosResurtir();


        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar",null);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsynCallConsulXPicking(null).execute();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isNumeric(txtDestEmpq.getText().toString())==false ||Integer.parseInt(txtDestEmpq.getText().toString())==0 || tvOrigenR.getText().equals("")){
                            Toast.makeText(ActivityResurtidoPicking.this, "Campos vacios o en cero", Toast.LENGTH_SHORT).show();
                        }else {
                            if(Integer.parseInt(tvCantOrig.getText().toString())<Integer.parseInt(txtDestEmpq.getText().toString())){
                                Toast.makeText(ActivityResurtidoPicking.this, "No existe esa cantidad disponible",
                                        Toast.LENGTH_SHORT).show();
                            }else if(Integer.parseInt(tvNecesidad.getText().toString())<Integer.parseInt(txtDestEmpq.getText().toString())){
                                Toast.makeText(ActivityResurtidoPicking.this, "Cantidad excede a necesidad",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                new AsyncModificarUbicDestino(tvOrigenR.getText().toString(),"EMPAQUE",
                                        tvClvProdDial.getText().toString(),txtDestEmpq.getText().toString(),dialogInterface).execute();
                            }//else if
                        }//else
                    }//ONCLICK
                });//SET ON CLICK
            }//onShow
        });//setonshowlistener
        dialog.show();
    }//resurtir

    public void onClickLista(View v){//cada vez que se seleccione un producto en la lista
        posicion = rvPicking.getChildPosition(rvPicking.findContainingItemView(v));
        clavProd=listPick.get(posicion).getClaveProd();
        descProd=listPick.get(posicion).getDescrip();
        ubic=listPick.get(posicion).getPicking();
        new AsynCallConsulXPicking(null).execute();
    }//onClickLista

    public void mostrarProductos(){
        btnResurtir.setEnabled(true);
        btnResurtir.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.Amarillo)));
        btnFinalizar.setEnabled(true);
        btnFinalizar.setBackgroundTintList(ColorStateList.
                valueOf(getResources().getColor(R.color.AzulBack)));
        rvPicking.setAdapter(null);
        rvPicking.setAdapter(adapter);
        posicion=0;
        new AsynCallConsulXPicking(null).execute();
    }//mostrarProductos

    public void mostrarDetalleProd(){//detalle por producto seleccionado
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        rvPicking.scrollToPosition(posicion);
        posGuard=posicion;//guardar en caso de que se necesite saber posicion anterior
        tvClvProdPick.setText(clavProd);
        tvDescPick.setText(descProd);
        txtUbicPick.setText(ubic);
        txtSumUbiPick.setText(totUbi);
        txtSumAlmPick.setText(totAlm);
        txtMax.setText(max);
        txtMin.setText(min);
        txtCantUbicPick.setText(cant);
        tvCantEmpq.setText(cantEmpq);

        Picasso.with(getApplicationContext()).
                load(urlImagenes +
                        tvClvProdPick.getText().toString() + extImg)
                .error(R.drawable.aboutlogo)
                .fit()
                .centerInside()
                .into(ivProdPick);
        cambiaProd();
    }//mostrarDetalleProd

    public void cambiaProd(){
        if(posicion==0){
            btnAdelante.setEnabled(true);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.colorPrimary)));
            btnAtras.setEnabled(false);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));

        }else if(posicion+1==listPick.size()){
            btnAtras.setEnabled(true);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.colorPrimary)));
            btnAdelante.setEnabled(false);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.ColorGris)));
        }else{
            btnAtras.setEnabled(true);
            btnAtras.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.colorPrimary)));
            btnAdelante.setEnabled(true);
            btnAdelante.setBackgroundTintList(ColorStateList.
                    valueOf(getResources().getColor(R.color.colorPrimary)));
        }//else
    }//cambiaProd



    //WebService Consulta Prod
    private class AsynCallConsulResPick extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreejecute

        @Override
        protected Void doInBackground(Void... params) {
            mensaje="";
            listPick.clear();
            conectaConsulResPick();
            return null;
        }//doInBackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            if(pendientes==true){
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResurtidoPicking.this);
                builder.setPositiveButton("Mostrar pendientes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fechaReg="";horaReg="";
                        mostrarProductos();
                    }
                });//positive
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });//negative
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage("Existen registros sin terminar, no podrá consultar nuevos hasta que termine los pendientes\n¿Desea verlos?").create().show();
            }
            else if(listPick.size()>0) {
                fechaReg="";horaReg="";
                mostrarProductos();
            }else {
                mDialog.dismiss();
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityResurtidoPicking.this);
                alerta.setMessage(mensaje).setCancelable(false).
                        setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }//onclick
                        });//alertDialogBuilder

                AlertDialog titulo = alerta.create();
                titulo.setTitle("AVISO");
                titulo.show();
            }//else
            pendientes=false;
        }//OnpostEjecute
    }//class AsynCall


    private void conectaConsulResPick() {
        String SOAP_ACTION = "ConsulResPick";
        String METHOD_NAME = "ConsulResPick";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLConsulResPick soapEnvelope = new XMLConsulResPick(SoapEnvelope.VER11);
            soapEnvelope.XMLConsulResPick(strusr,strpass,strbran,fechaReg,horaReg,ord+"");
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            String claveProd,descrip,fecha,hora,pick,clasf,rack;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject response0 = (SoapObject) soapEnvelope.bodyIn;
                response0 = (SoapObject) response0.getProperty(i);
                mensaje=(response0.getPropertyAsString("k_mensaje").equals("anyType{}") ? " " : response0.getPropertyAsString("k_mensaje"));
                if(mensaje.equals("PENDIENTE")){pendientes=true;}
                claveProd=(response0.getPropertyAsString("k_clvProd").equals("anyType{}") ? " " : response0.getPropertyAsString("k_clvProd"));
                descrip=(response0.getPropertyAsString("k_descrip").equals("anyType{}") ? " " : response0.getPropertyAsString("k_descrip"));
                fecha=(response0.getPropertyAsString("k_fecha").equals("anyType{}") ? " " : response0.getPropertyAsString("k_fecha"));
                hora=(response0.getPropertyAsString("k_hora").equals("anyType{}") ? " " : response0.getPropertyAsString("k_hora"));
                pick=(response0.getPropertyAsString("k_picking").equals("anyType{}") ? " " : response0.getPropertyAsString("k_picking"));
                clasf=(response0.getPropertyAsString("k_clasif").equals("anyType{}") ? " " : response0.getPropertyAsString("k_clasif"));
                rack=(response0.getPropertyAsString("k_rack").equals("anyType{}") ? " " : response0.getPropertyAsString("k_rack"));
                listPick.add(new ResurtidoPicking(""+(i+1), claveProd, descrip, fecha, hora,pick, clasf, rack,false));
            }//for
        } catch (SoapFault soapFault) {
            mensaje = "Error :"+soapFault.getMessage();
        } catch (XmlPullParserException e) {
            mensaje = "Error: " + e.getMessage();
        } catch (IOException e) {
            mensaje = "No se encontró servidor";
        } catch (Exception ex) {
            mensaje = "Ningún dato";
        }//catch
    }//conecta

    //Webservice modifica cantidad de ubicacion destino y origen
    private class AsyncModificarUbicDestino extends AsyncTask<Void, Void, Void> {
        String UbicacionOrigen,UbicacionDestino,Producto,Cantidad;
        DialogInterface dialogInterface;

        public AsyncModificarUbicDestino(String ubicacionOrigen, String ubicacionDestino, String producto,
                                         String cantidad,DialogInterface dialogInterface) {
            UbicacionOrigen = ubicacionOrigen;
            UbicacionDestino = ubicacionDestino;
            Producto = producto;
            Cantidad = cantidad;
            this.dialogInterface=dialogInterface;
        }

        @Override
        protected void onPreExecute() {
            if(mDialog.isShowing()==false){
                mDialog.show();
            }
        }//onPreexecute

        @Override
        protected Void doInBackground(Void... params) {
            mensaje="";
            int c=Integer.parseInt(Cantidad);
            Cantidad=c+"";
            consultaUbicacionMod(UbicacionOrigen,UbicacionDestino,Producto,Cantidad);
            return null;
        }//doInBackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if(mensaje.equals("LA UBICACION A SIDO INSERTADO") || mensaje.equals("LA UBICACION A SIDO ACTUALIZADA")){
                Toast.makeText(ActivityResurtidoPicking.this, "Se surtió "+Cantidad+" piezas a la ubicación de EMPAQUE", Toast.LENGTH_SHORT).show();
                new AsynCallConsulXPicking(dialogInterface).execute();
            }else{
                dialogInterface.dismiss();
                Toast.makeText(ActivityResurtidoPicking.this, mensaje, Toast.LENGTH_SHORT).show();
                new AsynCallCompromeAlma().execute();
            }//else
        }//onPosteExecute
    }//AsynModificar

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
            /*listPick.get(posicion).setRevisado(true);
            int ca=Integer.parseInt(tvCantEmpq.getText().toString())+Integer.parseInt(Cantidad);
            listPick.get(posicion).setCantEmpq(ca+"");*/
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

    private class AsyncallUbicaciones extends AsyncTask<Void, Void, Void> {

        DialogInterface dialogI;

        public AsyncallUbicaciones(DialogInterface dialogI) {
            this.dialogI = dialogI;
        }

        @Override
        protected void onPreExecute() {
            if(mDialog.isShowing()==false){
                mDialog.show();
            }
        }//onPreejecute

        @Override
        protected Void doInBackground(Void... params) {
            mensaje="";
            listaUbic.clear();
            conectaUbicaciones();
            return null;
        }//doInBackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if (listaUbic.size()>0) {
               /* Collections.sort(listaUbic, new Comparator<UbicacionSandG>() {
                    @Override
                    public int compare(UbicacionSandG ubicacionSandG, UbicacionSandG t1) {
                        return ubicacionSandG.getFecha().compareTo(t1.getFecha());
                    }
                });
                Collections.reverse(listaUbic);*/
                resurtir();
            }else {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityResurtidoPicking.this);
                alerta.setMessage("Hubó un problema en la consulta de ubicaciones").setCancelable(false).
                        setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(dialogI!=null){
                                    dialogI.dismiss();
                                }
                            }//onclick
                        });//alertDialogBuilder
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Aviso");
                titulo.show();
            }//else
        }//OnpostEjecute
    }//class AsynCall


    private void conectaUbicaciones() {

        String SOAP_ACTION = "UbicacionAlma";
        String METHOD_NAME = "UbicacionAlma";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLUbicacionAlma soapEnvelope = new XMLUbicacionAlma(SoapEnvelope.VER11);
            soapEnvelope.XMLUbicacionAlma(strusr, strpass,tvClvProdPick.getText().toString(), strbran);
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
                listaUbic.add(new UbicacionSandG((response0.getPropertyAsString("k_Ubicacion").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Ubicacion")),
                        (response0.getPropertyAsString("k_Cantidad").equals("anyType{}") ? "0" : response0.getPropertyAsString("k_Cantidad")),
                        (response0.getPropertyAsString("k_Fecha").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Fecha")),
                        (response0.getPropertyAsString("k_Tipo").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Tipo"))));

            }//for
        }catch (SoapFault soapFault) {
            mensaje = "Error:" + soapFault.getMessage();
            soapFault.printStackTrace();
        }catch (XmlPullParserException e) {
            mensaje = "Error:" + e.getMessage();
            e.printStackTrace();
        }catch (IOException e) {
            mensaje = "No se encontró servidor";
            e.printStackTrace();
        }catch (Exception ex) {
            mensaje = "Hubó un problema";
        }//catch
    }//AsynCall

    //WebService ConsultaxProducto
    private class AsynCallConsulXPicking extends AsyncTask<Void, Void, Void> {
        DialogInterface dialogInterface;

        public AsynCallConsulXPicking(DialogInterface dialogInterface) {
            this.dialogInterface = dialogInterface;
        }

        @Override
        protected void onPreExecute() {
            if(mDialog.isShowing()==false){
                mDialog.show();
            }
        }//onPreejecute

        @Override
        protected Void doInBackground(Void... params) {
            mensaje="";totUbi="0";totAlm="0";max="0";min="0";cant="0";cantEmpq="0";cantAlm="0";necesidad=0;
            clavProd=listPick.get(posicion).getClaveProd();
            descProd=listPick.get(posicion).getDescrip();
            ubic=listPick.get(posicion).getPicking();
            conectaConsulXPicking();
            return null;
        }//doInBackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            if(dialogInterface!=null){
                if(necesidad>0){
                    dialogInterface.dismiss();
                    new AsyncallUbicaciones(dialogInterface).execute();

                }else{
                    dialogInterface.dismiss();
                    new AsynCallCompromeAlma().execute();
                    if(mensaje.equals("")){
                        alertNecesidad0();
                    }else{
                        Toast.makeText(ActivityResurtidoPicking.this, mensaje, Toast.LENGTH_SHORT).show();
                    }//else
                }//else
            }else{
                if (mensaje.equals("")) {
                    new AsynCallCompromeAlma().execute();
                }else {
                    if(listPick.size()>0){
                        Toast.makeText(ActivityResurtidoPicking.this, "No fue posible obtener detalles del producto", Toast.LENGTH_SHORT).show();
                        posicion=posGuard;
                        new AsynCallCompromeAlma().execute();
                    }else{
                        mDialog.dismiss();
                        Toast.makeText(ActivityResurtidoPicking.this, "Hubó un problema al actualizar datos del producto", Toast.LENGTH_SHORT).show();
                    }//else
                }//else
            }//else
        }//OnpostEjecute
    }//class AsynCall


    private void conectaConsulXPicking() {
        String SOAP_ACTION = "ConsulXPicking";
        String METHOD_NAME = "ConsulXPicking";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLConsulXPicking soapEnvelope = new XMLConsulXPicking(SoapEnvelope.VER11);
            soapEnvelope.XMLConsulXPicking(strusr,strpass,strbran,clavProd,ubic);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response = (SoapObject) soapEnvelope.bodyIn;
            response = (SoapObject) response.getProperty("DatP");
            totUbi=response.getPropertyAsString("k_totUbi").equals("anyType{}") ? "0" : response.getPropertyAsString("k_totUbi");
            totAlm=response.getPropertyAsString("k_totAlm").equals("anyType{}") ? "0" : response.getPropertyAsString("k_totAlm");
            max=response.getPropertyAsString("k_maxi").equals("anyType{}") ? "0" : response.getPropertyAsString("k_maxi");
            min=response.getPropertyAsString("k_mini").equals("anyType{}") ? "0" : response.getPropertyAsString("k_mini");
            cant=response.getPropertyAsString("k_cant").equals("anyType{}") ? "0" : response.getPropertyAsString("k_cant");
            cantEmpq=response.getPropertyAsString("k_cantEmp").equals("anyType{}") ? "0" : response.getPropertyAsString("k_cantEmp");
            cantAlm=(response.getPropertyAsString("k_existProc").equals("anyType{}") ? "0" : response.getPropertyAsString("k_existProc"));
            necesidad=Integer.parseInt(max)-Integer.parseInt(cant)-Integer.parseInt(cantEmpq);
        } catch (SoapFault soapFault) {
            mensaje = "Error: " + soapFault.getMessage();
            soapFault.printStackTrace();
        } catch (XmlPullParserException e) {
            mensaje = "Error: " + e.getMessage();
        } catch (IOException e) {
            mensaje = "No se encontró servidor";
        } catch (Exception ex) {
            mensaje = "Error: " + ex.getMessage();
        }//catch
    }//conecta

    private class AsyncActualizaPick extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }//onPreexecute

        @Override
        protected Void doInBackground(Void... params) {
            mensaje="";
            consultaActualizaPick();
            return null;
        }//doInBackground


        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if(mensaje.equals("Dato modificado correctamente")){
                listPick.get(posicion).setRevisado(true);
                Toast.makeText(ActivityResurtidoPicking.this, "Producto Revisado", Toast.LENGTH_SHORT).show();
                new AsynCallConsulXPicking(null).execute();
            }else{
                Toast.makeText(ActivityResurtidoPicking.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }//onPosteExecute
    }//AsynModificar

    private void consultaActualizaPick() {
        String SOAP_ACTION = "ActualizaPick";
        String METHOD_NAME = "ActualizaPick";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";


        try {
            String fechaT=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String horaT=new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLActualizaPick soapEnvelope = new XMLActualizaPick(SoapEnvelope.VER11);
            soapEnvelope.XMLActualizaPicking(strusr, strpass,strbran,clavProd, listPick.get(posicion).getFecha(),
                    listPick.get(posicion).getHora(), fechaT,horaT);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            Vector response = (Vector) soapEnvelope.getResponse();
            mensaje = response.get(0).toString();

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

    private class AsynCallCompromeAlma extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if(mDialog.isShowing()==false){
                mDialog.show();
            }
        }//onPreejecute

        @Override
        protected Void doInBackground(Void... params) {
            listaComprometidas.clear();
            conectaCompromeAlma();
            return null;
        }//doInBackground

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            int contador=0;
            for(int i=0;i<listaComprometidas.size();i++){
                contador=contador+Integer.parseInt(listaComprometidas.get(i).getCantidad());
            }//for
            int r=Integer.parseInt(cantAlm)+contador;
            txtCantAcum.setText(r+"");
            mostrarDetalleProd();
        }//onPostExecuted
    }//AsynCallCompromeAlma

    private void conectaCompromeAlma() {

        String SOAP_ACTION = "CompromeAlma";
        String METHOD_NAME = "CompromeAlma";
        String NAMESPACE = "http://" + strServer + "/WSk75AlmacenesApp/";
        String URL = "http://" + strServer + "/WSk75AlmacenesApp";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            XMLCompromeAlma soapEnvelope = new XMLCompromeAlma(SoapEnvelope.VER11);
            soapEnvelope.XMLCompromeAlma(strusr, strpass, tvClvProdPick.getText().toString(), strbran);
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
                listaComprometidas.add(new ComprometidasSandG(
                        (response0.getPropertyAsString("k_TipoDocumento").equals("anyType{}") ? " " : response0.getPropertyAsString("k_TipoDocumento")),
                        (response0.getPropertyAsString("k_Folio").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Folio")),
                        (response0.getPropertyAsString("k_Cliente").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Cliente")),
                        (response0.getPropertyAsString("k_Cantidad").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Cantidad")),
                        (response0.getPropertyAsString("k_Fecha").equals("anyType{}") ? " " : response0.getPropertyAsString("k_Fecha"))));

            }//try
        } catch (SoapFault soapFault) {
            mDialog.dismiss();
            mensaje = "Error:" + soapFault.getMessage();
            soapFault.printStackTrace();
        } catch (XmlPullParserException e) {
            mDialog.dismiss();
            mensaje = "Error:" + e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            mDialog.dismiss();
            mensaje = "No se encontro servidor";
            e.printStackTrace();
        } catch (Exception ex) {
            mDialog.dismiss();
            mensaje = "Error:" + ex.getMessage();
        }//catch
    }//AsyncallCompreAlm
}// clase

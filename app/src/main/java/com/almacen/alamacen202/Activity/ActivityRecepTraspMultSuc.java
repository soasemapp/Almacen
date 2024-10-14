package com.almacen.alamacen202.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.os.StrictMode;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almacen.alamacen202.Adapter.AdaptadorTraspasos;
import com.almacen.alamacen202.Adapter.AdapterInventario;
import com.almacen.alamacen202.Imprecion.BluetoothPrint;
import com.almacen.alamacen202.R;
import com.almacen.alamacen202.SetterandGetters.EnvTraspasos;
import com.almacen.alamacen202.SetterandGetters.Folios;
import com.almacen.alamacen202.SetterandGetters.Inventario;
import com.almacen.alamacen202.SetterandGetters.Traspasos;
import com.almacen.alamacen202.Sqlite.ConexionSQLiteHelper;
import com.almacen.alamacen202.XML.XMLActualizaInv;
import com.almacen.alamacen202.XML.XMLFolios;
import com.almacen.alamacen202.XML.XMLRecepConsul;
import com.almacen.alamacen202.XML.XMLRecepMultSuc;
import com.almacen.alamacen202.XML.XMLlistInv;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dmax.dialog.SpotsDialog;

public class ActivityRecepTraspMultSuc extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 11;
    private ProgressDialog progressDialog;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private boolean datos = false, modificados = false;
    private int posicion = 0, posicion2 = 0, posG = -1, CONTCAJA = 1, TOTCAJAS = 0, TOTP = 0, RECEP = 0;
    private String strusr, strpass, strbran, strServer, codeBar, mensaje, Producto = "", serv, Folio = "", impresora;
    private ArrayList<Traspasos> listaTrasp = new ArrayList<>();
    private EditText txtProd, txtCantidad, txtCantSurt, txtFolBusq, txtTotPza, txtUbicT;
    private AutoCompleteTextView spCaja;
    private ImageView ivProd;
    private TextView tvProd;
    private Button btnBuscar, btnAtras, btnAdelante, btnCorr, btnBackC, btnNextC, btnImpr;
    private RecyclerView rvTraspasos;
    private AdaptadorTraspasos adapter;
    private AlertDialog mDialog;
    private InputMethodManager keyboard;
    private String urlImagenes, extImg;
    private int sonido_correcto, sonido_error;
    private SoundPool bepp;
    Context context = this;
    AlertDialog dialog6 = null;
    AlertDialog.Builder builder6;
    @RequiresApi(api = Build.VERSION_CODES.M)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recep_trasp_mult_suc);


        MyToolbar.show(this, "Recepción Traspasos Multisucursal", true);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = preference.edit();
        strusr = preference.getString("user", "null");
        strpass = preference.getString("pass", "null");
        strbran = preference.getString("codBra", "null");
        strServer = preference.getString("Server", "null");
        codeBar = preference.getString("codeBar", "null");
        impresora = preference.getString("Impresora", "null");
        urlImagenes = preference.getString("urlImagenes", "null");
        extImg = preference.getString("ext", "null");

        switch (strServer) {
            case "sprautomotive.servehttp.com:9090":
                serv = "RODATECH";
                break;
            case "sprautomotive.servehttp.com:9095":
                serv = "PARTECH";
                break;
            case "sprautomotive.servehttp.com:9080":
                serv = "TG";
                break;
        }

        mDialog = new SpotsDialog.Builder().setContext(ActivityRecepTraspMultSuc.this).
                setMessage("Espere un momento...").build();
        mDialog.setCancelable(false);

        progressDialog = new ProgressDialog(ActivityRecepTraspMultSuc.this);//parala barra de
        progressDialog.setMessage("Procesando datos....");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        txtProd = findViewById(R.id.txtProducto);
        txtCantidad = findViewById(R.id.txtCantidad);
        txtCantSurt = findViewById(R.id.txtCantSurt);
        tvProd = findViewById(R.id.tvProd);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAtras = findViewById(R.id.btnAtras);
        btnAdelante = findViewById(R.id.btnAdelante);
        ivProd = findViewById(R.id.ivProd);
        btnCorr = findViewById(R.id.btnCorr);
        txtFolBusq = findViewById(R.id.txtFolBusq);
        btnBackC = findViewById(R.id.btnBackC);
        spCaja = findViewById(R.id.spCaja);
        btnNextC = findViewById(R.id.btnNextC);
        txtTotPza = findViewById(R.id.txtTotPza);
        txtUbicT = findViewById(R.id.txtUbicT);
        btnImpr = findViewById(R.id.btnImpr);

        bepp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido_correcto = bepp.load(ActivityRecepTraspMultSuc.this, R.raw.sonido_correct, 1);
        sonido_error = bepp.load(ActivityRecepTraspMultSuc.this, R.raw.error, 1);

        rvTraspasos = findViewById(R.id.rvTraspasos);
        rvTraspasos.setLayoutManager(new LinearLayoutManager(ActivityRecepTraspMultSuc.this));
        adapter = new AdaptadorTraspasos(listaTrasp);
        keyboard = (InputMethodManager) getSystemService(ActivityRecepTraspMultSuc.INPUT_METHOD_SERVICE);

        txtProd.setInputType(InputType.TYPE_NULL);
        //txtProd.requestFocus();

        txtProd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Producto = editable.toString();
                if (!editable.toString().equals("")) {
                    if (codeBar.equals("Zebra")) {
                        Producto = Producto.trim();
                        Producto = Producto.replaceAll("(\n|\r)", "");
                        posicion2 = posG;
                        cambio(Producto, true);
                        txtProd.setText("");
                        txtProd.requestFocus();
                    } else {
                        for (int i = 0; i < editable.length(); i++) {
                            char ban;
                            ban = editable.charAt(i);
                            if (ban == '\n') {
                                posicion2 = posG;
                                cambio(Producto, true);
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
                if (actionId == 0) {
                    txtProd.requestFocus();
                    return true;
                }//if action done
                return false;
            }//oneditoraction
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtFolBusq.getText().toString().equals("")) {
                    Folio = folio(txtFolBusq.getText().toString());
                    txtFolBusq.setText(Folio);
                    keyboard.hideSoftInputFromWindow(txtFolBusq.getWindowToken(), 0);
                    rvTraspasos.setAdapter(null);
                    limpiar();
                    new AsyncTotCajas(Folio).execute();
                } else {
                    Toast.makeText(ActivityRecepTraspMultSuc.this, "Folio vacío", Toast.LENGTH_SHORT).show();
                }//else
            }//onclick
        });//btnGuardar setonclick

        btnAdelante.setOnClickListener(new View.OnClickListener() {//boton adelante
            @Override
            public void onClick(View view) {
                posicion2 = posicion;
                cambio("next", false);
            }//onclick
        });//btnadelante setonclicklistener

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion2 = posicion;
                cambio("back", false);
            }//onclick
        });//btnatras setonclicklistener

        btnCorr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaTrasp.get(posicion).isSincronizado() == false) {
                    posicion2 = posicion;
                    new AsyncActualizar(Folio, listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt() + "",
                            "change", false, Producto, 0).execute();
                } else {
                    Toast.makeText(ActivityRecepTraspMultSuc.this, "Sin cambios", Toast.LENGTH_SHORT).show();
                }
            }//onclick
        });//btnCorr


        btnBackC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaTrasp.size() > 0 && listaTrasp.get(posicion).isSincronizado() == false) {
                    posicion2 = posicion;
                    new AsyncActualizar(Folio, listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt() + "",
                            "change", false, Producto, 0).execute();
                } else {
                    CONTCAJA--;
                    spCaja.setText(CONTCAJA + "", false);
                    new AsyncReceCon(strbran, Folio, CONTCAJA + "", true).execute();
                }//else
            }
        });
        btnNextC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaTrasp.size() > 0 && listaTrasp.get(posicion).isSincronizado() == false) {
                    posicion2 = posicion;
                    new AsyncActualizar(Folio, listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt() + "",
                            "change", false, Producto, 0).execute();
                } else {
                    CONTCAJA++;
                    spCaja.setText(CONTCAJA + "", false);
                    new AsyncReceCon(strbran, Folio, CONTCAJA + "", true).execute();
                }//else
            }
        });

        spCaja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CONTCAJA = Integer.parseInt(spCaja.getText().toString());
                new AsyncReceCon(strbran, Folio, CONTCAJA + "", true).execute();

            }
        });

        btnImpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listaTrasp.size() > 0) {//si hay datos para imprimir
                    if (listaTrasp.get(posicion).isSincronizado() == false) {
                        posicion2 = posicion;
                        new AsyncActualizar(Folio, listaTrasp.get(posicion).getProducto(),
                                listaTrasp.get(posicion).getCantSurt() + "",
                                "change", false, Producto, 0).execute();
                    } else {
                        ImprimirTicketRec(RECEP);
                    }//else
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                    builder.setPositiveButton("ACEPTAR", null);
                    builder.setCancelable(false);
                    builder.setTitle("AVISO").setMessage("Sin datos para imprimir").create().show();
                }//else
            }//onclick
        });

        //PERMISOS PARA BLUETOOTH SOLO SE MUESTRA EN VERSIONES POSTERIORES DE ANDROID 13
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                PackageManager.PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }//onCreate


    public void cambiaCajas() {
        if (CONTCAJA == 1 && TOTCAJAS > 1) {
            btnNextC.setEnabled(true);
            btnBackC.setEnabled(false);
        } else if (CONTCAJA == TOTCAJAS && TOTCAJAS > 1) {
            btnBackC.setEnabled(true);
            btnNextC.setEnabled(false);
        } else if (TOTCAJAS == 1) {
            btnBackC.setEnabled(false);
            btnNextC.setEnabled(false);
        } else {
            btnBackC.setEnabled(true);
            btnNextC.setEnabled(true);
        }//else
    }//cambiaProd

    public String folio(String folio) {
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
                    folio = "0000" + folio;
                    break;
                case 4:
                    folio = "000" + folio;
                    break;
                case 5:
                    folio = "00" + folio;
                    break;
                case 6:
                    folio = "0" + folio;
                    break;
                default:
                    folio = folio;
                    break;
            }//switch
        }//if
        return folio;
    }

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

    public void cambiaProd() {
        if (posicion == 0 && listaTrasp.size() > 1) {
            btnAdelante.setEnabled(true);
            btnAtras.setEnabled(false);

        } else if (posicion + 1 == listaTrasp.size() && listaTrasp.size() > 1) {
            btnAtras.setEnabled(true);
            btnAdelante.setEnabled(false);
        } else if (listaTrasp.size() == 1) {
            btnAtras.setEnabled(false);
            btnAdelante.setEnabled(false);
        } else {
            btnAtras.setEnabled(true);
            btnAdelante.setEnabled(true);
        }//else
    }//cambiaProd

    public void onClickLista(View v) {//cada vez que se seleccione un producto en la lista
        if (posicion >= 0 && listaTrasp.get(posicion).isSincronizado() == false) {
            posicion2 = posG;
            Producto = listaTrasp.get(rvTraspasos.getChildPosition(rvTraspasos.findContainingItemView(v))).getProducto();
        } else {
            posicion2 = rvTraspasos.getChildPosition(rvTraspasos.findContainingItemView(v));
        }
        cambio("change", false);
    }//onClickLista


    public void cambio(String var, boolean sumar) {
        if (!listaTrasp.get(posicion2).getProducto().equals(Producto) && posG != -1 && listaTrasp.get(posicion2).isSincronizado() == false) {//identificando que prod anterior no se sincronizó
            //new AsyncAct(listaTrasp.get(posicion2).getProducto(),listaTrasp.get(posicion2).getCantSurt(),var,sumar,Producto).execute();
            new AsyncActualizar(Folio, listaTrasp.get(posicion2).getProducto(),
                    listaTrasp.get(posicion2).getCantSurt(), var, sumar, Producto, 0).execute();
        } else {//cuando se escanea o por botones de adelante, atras y onclick en lista
            if (sumar == true) {//al escanear
                evaluarEscaneo(Producto);
            } else {
                tipoCambio(var);
                mostrarDetalleProd();
            }
        }//else
    }//alert

    public void tipoCambio(String var) {
        switch (var) {
            case "next":
                posicion++;
                break;
            case "back":
                posicion--;
                break;
            case "change":
                posicion = posicion2;
                posicion2 = 0;
                break;
            default:
                posicion = encontrarPosEnLista(var);
                break;
        }
    }

    public int totPazas() {
        int tot = 0;
        for (int i = 0; i < listaTrasp.size(); i++) {
            if (listaTrasp.get(i).isSincronizado() == true) {
                tot = tot + Integer.parseInt(listaTrasp.get(i).getCantSurt());
            }
        }
        return tot;
    }

    public void mostrarDetalleProd() {//detalle por producto seleccionado
        adapter.index(posicion);
        adapter.notifyDataSetChanged();
        rvTraspasos.scrollToPosition(posicion);
        //Producto=listaTrasp.get(posicion).getProducto();
        tvProd.setText(listaTrasp.get(posicion).getProducto());
        txtCantidad.setText(listaTrasp.get(posicion).getCantidad());
        txtCantSurt.setText(listaTrasp.get(posicion).getCantSurt());
        txtTotPza.setText(totPazas() + "");
        txtUbicT.setText(listaTrasp.get(posicion).getUbic());

        Picasso.with(getApplicationContext()).
                load(urlImagenes +
                        tvProd.getText().toString() + extImg)
                .error(R.drawable.aboutlogo)
                .fit()
                .centerInside()
                .into(ivProd);
        if (Integer.parseInt(listaTrasp.get(posicion).getCantidad()) == Integer.parseInt(listaTrasp.get(posicion).getCantSurt())) {
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        } else {
            txtCantSurt.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
        }
        cambiaProd();

        if (!txtCantSurt.getText().toString().equals("") && Integer.parseInt(txtCantSurt.getText().toString()) > 0) {
            btnCorr.setEnabled(true);
        } else {
            btnCorr.setEnabled(false);
        }
        posG = posicion;
    }//mostrarDetalleProd

    public void limpiar() {
        tvProd.setText("");
        txtCantidad.setText("");
        txtCantSurt.setText("");
        ivProd.setImageResource(R.drawable.logokepler);
        txtTotPza.setText("");
        txtUbicT.setText("");
        btnAtras.setEnabled(false);
        btnAdelante.setEnabled(false);
        btnCorr.setEnabled(false);
        posG = -1;
    }//limpiar

    public int encontrarPosEnLista(String prod) {
        int p = posG;
        for (int i = 0; i < listaTrasp.size(); i++) {
            if (listaTrasp.get(i).getProducto().equals(prod)) {
                p = i;
                break;
            }//if
        }
        return p;
    }

    public void evaluarEscaneo(String prod) {
        limpiar();
        boolean existe = false;
        for (int i = 0; i < listaTrasp.size(); i++) {
            if (listaTrasp.get(i).getProducto().equals(prod)) {
                posicion = i;
                existe = true;
                int cant = Integer.parseInt(listaTrasp.get(i).getCantidad());
                int cantS = Integer.parseInt(listaTrasp.get(i).getCantSurt());
                if ((cantS + 1) <= cant) {
                    cantS++;
                    listaTrasp.get(i).setCantSurt(cantS + "");
                    listaTrasp.get(i).setSincronizado(false);
                    RECEP++;
                    modificados = true;
                    if (cantS == cant) {
                        posicion2 = i;
                        new AsyncActualizar(Folio, prod, cantS + "", "change", false, Producto, 1).execute();
                    }
                } else {
                    bepp.play(sonido_error, 1, 1, 1, 0, 0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                    builder.setPositiveButton("ACEPTAR", null);
                    builder.setCancelable(false);
                    builder.setTitle("Excede cantidad").create().show();
                }
                break;
            }//if
        }
        if (existe == false) {
            bepp.play(sonido_error, 1, 1, 1, 0, 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
            builder.setPositiveButton("ACEPTAR", null);
            builder.setCancelable(false);
            builder.setTitle("No existe " + prod + " en la lista").create().show();
        }
        mostrarDetalleProd();
    }//evaluar

    public boolean surtTodos() {
        boolean surt = false;
        int c = 0;
        for (int i = 0; i < listaTrasp.size(); i++) {
            int cant = Integer.parseInt(listaTrasp.get(i).getCantidad());
            int cantS = Integer.parseInt(listaTrasp.get(i).getCantSurt());
            if (cant == cantS) {
                c++;
            }
        }
        if (c == listaTrasp.size()) {
            surt = true;
        }
        return surt;
    }


    public void verLista() {
        txtProd.requestFocus();
        adapter = new AdaptadorTraspasos(listaTrasp);
        rvTraspasos.setAdapter(adapter);
        txtProd.setEnabled(true);
        txtProd.requestFocus();
        posicion = 0;
        mostrarDetalleProd();
    }//ver lista

    private class AsyncReceCon extends AsyncTask<Void, Void, Void> {

        private String suc, folio, caja;
        private boolean conn, var;

        public AsyncReceCon(String suc, String folio, String caja, boolean var) {
            this.suc = suc;
            this.folio = folio;
            this.caja = caja;
            this.var = var;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
            rvTraspasos.setAdapter(null);
            limpiar();
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn = firtMet();
            if (conn == true) {
                HttpHandler sh = new HttpHandler();
                String parametros = "sucursal=" + suc + "&folio=" + folio + "&cajas=" + caja;
                String url = "http://" + strServer + "/ReceCon?" + parametros;
                String jsonStr = sh.makeServiceCall(url, strusr, strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        int num = 1;
                        listaTrasp.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject dato = jsonArray.getJSONObject(i);//Conjunto de datos
                            listaTrasp.add(new Traspasos(num + "", dato.getString("PRODUCTO"), dato.getString("CANTIDAD"),
                                    dato.getString("UBICACION"), dato.getString("RECEPCION"), dato.getString("EXISTENCIA"), true));
                            num++;
                            mensaje = "";
                        }//for
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje = "Los datos de la caja " + caja + " no han sido procesados";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje = "No fue posible obtener datos del servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            } else {
                mensaje = "Problemas de conexión";
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            if (mensaje.equals("")) {
                mDialog.dismiss();
                keyboard.hideSoftInputFromWindow(txtFolBusq.getWindowToken(), 0);
                if (var == true) {
                    verLista();
                    cambiaCajas();
                } else {
                    mDialog.dismiss();
                    verLista();
                }//else
            } else {
                cambiaCajas();
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                builder.setPositiveButton("ACEPTAR", null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage(mensaje).create().show();
            }//else
        }//onPost
    }//AsyncConsulRecep

    private class AsyncTotCajas extends AsyncTask<Void, Void, Void> {

        private String folio, consSuc;
        private boolean conn;

        public AsyncTotCajas(String folio) {
            this.folio = folio;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje = "";
            spCaja.setText("");
            spCaja.setAdapter(null);
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn = firtMet();
            if (conn == true) {
                HttpHandler sh = new HttpHandler();
                String parametros = "folio=" + folio;
                String url = "http://" + strServer + "/totcajas?" + parametros;
                String jsonStr = sh.makeServiceCall(url, strusr, strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);//Conjunto de datos
                        TOTCAJAS = Integer.parseInt(dato.getString("maximo"));
                        TOTP = Integer.parseInt(dato.getString("totalp"));
                        RECEP = Integer.parseInt(dato.getString("recepcion"));
                        consSuc = dato.getString("suc");
                        if (!consSuc.equals(strbran)) {
                            mensaje = "Este folio no pertenece a esta sucursal";
                        } else {
                            if (TOTCAJAS == 0) {
                                mensaje = "No hay productos registrados en cajas";
                            }
                        }
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje = "Sin datos disponibles";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje = "No fue posible obtener datos del servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            } else {
                mensaje = "Problemas de conexión";
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            if (mensaje.equals("")) {
                CONTCAJA = TOTCAJAS;
                ArrayList<String> nomCajas = new ArrayList<>();
                for (int k = 1; k <= TOTCAJAS; k++) {
                    nomCajas.add(k + "");
                }//for
                if (nomCajas.size() > 0) {
                    ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                            ActivityRecepTraspMultSuc.this, R.layout.drop_down_item, nomCajas);
                    spCaja.setAdapter(adaptador);
                    spCaja.setText(nomCajas.get(nomCajas.size() - 1), false);
                }
                cambiaCajas();
                new AsyncReceCon(strbran, Folio, spCaja.getText().toString(), true).execute();
            } else {
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                builder.setPositiveButton("ACEPTAR", null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage(mensaje).create().show();
            }//else
        }//onPost
    }//AsyncConsulRecep

    private class AsyncActualizar extends AsyncTask<Void, Void, Void> {

        private String folio, producto, cantidad, var, ProductoActual;
        private boolean conn = true, sumar;
        private int alTerm;

        public AsyncActualizar(String folio, String producto, String cantidad,
                               String var, boolean sumar, String ProductoActual, int alTerm) {
            this.folio = folio;
            this.producto = producto;
            this.cantidad = cantidad;
            this.var = var;
            this.sumar = sumar;
            this.ProductoActual = ProductoActual;
            this.alTerm = alTerm;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje = "";
        }//onPreExecute

        @Override
        protected Void doInBackground(Void... voids) {
            conn = firtMet();
            if (conn == true) {
                String parametros = "sucursal=" + strbran + "&producto=" + producto +
                        "&cantidad=" + cantidad + "&folio=" + folio + "&caja=" + spCaja.getText().toString();
                String url = "http://" + strServer + "/recemul?" + parametros;
                String jsonStr = new HttpHandler().makeServiceCall(url, strusr, strpass);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonObj.getJSONArray("Response");
                        JSONObject dato = jsonArray.getJSONObject(0);
                        mensaje = dato.getString("MENSAJE");
                    } catch (final JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mensaje = "Sin sincronizar";
                            }//run
                        });
                    }//catch JSON EXCEPTION
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mensaje = "Problemas con el servidor";
                        }//run
                    });//runUniTthread
                }//else
                return null;
            } else {
                mensaje = "Problemas de conexión";
                return null;
            }//else
        }//doInBackground

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            mDialog.dismiss();
            if (conn == false) {
                Toast.makeText(ActivityRecepTraspMultSuc.this, "Sin conexión a internet\n" +
                        "No se podrá seguir escaneando a menos que se actualice este producto", Toast.LENGTH_SHORT).show();
            } else if (mensaje.equals("SINCRONIZADO")) {
                Toast.makeText(ActivityRecepTraspMultSuc.this, producto + " Sincronizado", Toast.LENGTH_SHORT).show();
                if (TOTP == RECEP) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                    builder.setPositiveButton("ACEPTAR", null);
                    builder.setCancelable(false);
                    builder.setTitle("HAS TERMINADO").create().show();
                }
                bepp.play(sonido_correcto, 1, 1, 1, 0, 0);
                listaTrasp.get(posicion2).setSincronizado(true);
                if (sumar == true) {
                    evaluarEscaneo(ProductoActual);
                } else {
                    if ((posicion + 1) < listaTrasp.size()) {//para que cambie al siguiente codigo siempre y cuando no sea el ultimo
                        var = "next";
                    }//if
                    tipoCambio(var);
                    mostrarDetalleProd();
                    alTerminar(alTerm);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                builder.setPositiveButton("ACEPTAR", null);
                builder.setCancelable(false);
                builder.setTitle("AVISO").setMessage(mensaje).create().show();
            }//else
        }//onPost
    }//AsyncActualizar

    public void alTerminar(int opc) {
        switch (opc) {
            case 1:
                if (surtTodos() == true) {
                    if (CONTCAJA < TOTCAJAS) {//PASAR DE CAJA SI TODAS LOS CODIGOS YA ESTAN
                        ImprimirTicketRec(listaTrasp.size());
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                        builder.setPositiveButton("ACEPTAR", null);
                        builder.setCancelable(false);
                        builder.setTitle("SIGUIENTE CAJA").create().show();
                        CONTCAJA++;
                        spCaja.setText(CONTCAJA + "", false);
                        //cambiaCajas();
                        posicion = 0;
                        new AsyncReceCon(strbran, Folio, CONTCAJA + "", true).execute();
                    } else if (CONTCAJA == TOTCAJAS) {
                        ImprimirTicketRec(listaTrasp.size());
                    }//else if
                }//if surtio todos
                break;
            case 2:
                startActivity(new Intent(ActivityRecepTraspMultSuc.this, ActivityRecepAlm.class));
                finish();
                break;
        }//switch
    }//alTerminar

    @Override
    public void onBackPressed() {
        if (modificados == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("CANCELAR", null);
            builder.setCancelable(false);
            builder.setTitle("AVISO").setMessage("Se hicieron movimientos ¿desea salir?").create().show();
        } else {
            finish();
        }
    }//onBackPressed

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoverflow5, menu);
        MenuItem itemOtro = menu.findItem(R.id.itOtro);
        itemOtro.setTitle("Por Almacen Morelos");
        return true;
    }//onCreateOptionsMenu

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itOtro:
                if (listaTrasp.size() > 0 && listaTrasp.get(posicion).isSincronizado() == false) {
                    posicion2 = posicion;
                    new AsyncActualizar(Folio, listaTrasp.get(posicion).getProducto(),
                            listaTrasp.get(posicion).getCantSurt() + "",
                            "change", false, Producto, 2).execute();
                } else {
                    startActivity(new Intent(ActivityRecepTraspMultSuc.this, ActivityRecepAlm.class));
                    finish();
                }//else
                break;
        }
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    public void imprimir(int Cont, BluetoothPrint imprimir) {
        String empresa;
        int imagen;
        switch (strServer) {
            case "jacve.dyndns.org:9085":
                empresa = "JACVE";
                imagen = R.drawable.jacveprint;
                break;
            case "sprautomotive.servehttp.com:9085":
                empresa = "VIPLA";
                imagen = R.drawable.viplaprint;
                break;
            case "cecra.ath.cx:9085":
                empresa = "CECRA";
                imagen = R.drawable.cecraprint;
                break;
            case "guvi.ath.cx:9085":
                empresa = "GUVI";
                imagen = R.drawable.guviprint;
                break;
            case "cedistabasco.ddns.net:9085":
                empresa = "PRESSA";
                imagen = R.drawable.pressaprint;
                break;
            case "autodis.ath.cx:9085":
                empresa = "AUTODIS";
                imagen = R.drawable.autodisprint;
                break;
            case "sprautomotive.servehttp.com:9090":
                empresa = "RODATECH";
                imagen = R.drawable.rodaprint;
                break;
            case "sprautomotive.servehttp.com:9095":
                empresa = "PARTECH";
                imagen = R.drawable.partechprint;
                break;
            case "sprautomotive.servehttp.com:9080":
                empresa = "SHARK";
                imagen = R.drawable.sharkprint;
                break;
            case "vazlocolombia.dyndns.org:9085":
                empresa = "VAZLO COLOMBIA";
                imagen = R.drawable.bhpprint;
                break;
            default:
                empresa = "PRUEBAS";
                imagen = R.drawable.aboutlogo;
                break;
        }//swicth
        if (imprimir.checkConnection() == true) {
            imprimir.printListRecepT(empresa, strusr, Folio, listaTrasp, String.valueOf(Cont), R.drawable.aboutlogo, spCaja.getText().toString());
            imprimir.disconnectBT();
        } else {
            AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
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
        BluetoothPrint imprimir = new BluetoothPrint(context, getResources());
        if (!impresora.equals("null")) {
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
            imprimir.FindBluetoothDevice(impresora);
            imprimir.openBluetoothPrinter();
            imprimir(Cont, imprimir);
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(context, "Problemas de bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                Set<BluetoothDevice> pairedDevices=mBluetoothAdapter.getBondedDevices();
                List<String> listDevices = new ArrayList<String>();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice btd : pairedDevices) {
                        listDevices.add(btd.getName());
                    }
                }//if
                String[] opciones = new String[listDevices.size()];
                for (int i = 0; i < listDevices.size(); i++) {
                    opciones[i] = listDevices.get(i);
                }
                if (opciones.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecepTraspMultSuc.this);
                    builder.setTitle("SELECCIONE IMPRESORA");
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
                }//else
            }//else bluetooth adapter
        }//else
    }//ImprimirTicket

}//ActivityInventario
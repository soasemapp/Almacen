package com.almacen.alamacen202;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.almacen.alamacen202.Activity.ActivityConsultaPA;
import com.almacen.alamacen202.Activity.ActivityInventarioXfolioComp;
import com.almacen.alamacen202.SetterandGetters.Login;
import com.almacen.alamacen202.XML.xmlLog;
import com.almacen.alamacen202.XML.xmlLogin;
import com.almacen.alamacen202.includes.HttpHandler;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private String user="",name="",lName="",type="",mail="",codB="",branch="",msjToast="",versionApp;
    private String res="";
    int result1 = 0;
    private Button btn1;
    private EditText usu;
    SoapObject response;
    private EditText clave;
    private String getUsuario = "", getPass = "", mensaje = "";
    private Spinner SERVER;
    private AlertDialog mDialog;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private String StrServer = "";
    private String id;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setMessage("Espere un momento...").build();
        SERVER = (Spinner) findViewById(R.id.spinnerserver);
        usu = (EditText) findViewById(R.id.txtinUsu);
        clave = (EditText) findViewById(R.id.txtinCla);

        btn1 = (Button) findViewById(R.id.btnbuscar);
        preference = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = preference.edit();

        mQueue = Volley.newRequestQueue(this);

        versionApp=getString(R.string.versionNum);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    getUsuario = usu.getText().toString();
                    getPass = clave.getText().toString();
                    /*  token=generateRandomText();*/
                    msjToast="";
                    if (!getUsuario.isEmpty() && !getPass.isEmpty() && SERVER.getSelectedItemPosition() != 0) {
                        switch (SERVER.getSelectedItemPosition()){
                            case 1:StrServer = "jacve.dyndns.org:9085";
                                msjToast="Entrando a JACVE";break;
                            case 2: StrServer = "sprautomotive.servehttp.com:9085";
                                msjToast="Entrando a VIPLA";break;
                            case 3: StrServer = "cecra.ath.cx:9085";
                                msjToast="Entrando a CECRA";break;
                            case 4:StrServer = "guvi.ath.cx:9085";
                                msjToast="Entrando a GUVI";break;
                            case 5:StrServer = "cedistabasco.ddns.net:9085";
                                msjToast="Entrando a PRESSA";break;
                            case 6:StrServer = "autodis.ath.cx:9085";
                                msjToast="Entrando a AUTODIS";break;
                            case 7:StrServer = "sprautomotive.servehttp.com:9090";
                                msjToast="Entrando a RODATECH ";break;
                            case 8:StrServer = "sprautomotive.servehttp.com:9095";
                                msjToast="Entrando a PARTECH ";break;
                            case 9:StrServer = "sprautomotive.servehttp.com:9080";
                                msjToast="Entrando a SHARK";break;
                            case 10:StrServer = "vazlocolombia.dyndns.org:9085";
                                msjToast="Entrando a Colombia";break;
                            case 11:StrServer = "sprautomotive.servehttp.com:9075";
                                msjToast="Iniciando Pruebas";break;
                            case 12:StrServer = "192.168.1.72:9085";
                                msjToast="Iniciando Pruebas";break;
                            case 13:StrServer = "soasem.is-by.us:9085";
                                msjToast="Iniciando Pruebas";break;
                        }//swicth
                        id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        new AsyncCallWS().execute();
                    }else {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                        alerta.setMessage("Ingrese los datos Faltantes").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog titulo = alerta.create();
                        titulo.setTitle("Faltan Datos");
                        titulo.show();
                    }//else
                } else {//sin conexión a internet
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
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
            }//onclick
        });//btn1 setonclick

        String[] opciones1 = {"--Seleccione--", "JACVE", "VIPLA", "CECRA", "GUVI", "PRESSA", "AUTODIS", "RODATECH ", "PARTECH", "SHARK", "COLOMBIA", "PRUEBAS SPR", "PRUEBAS", "PRUEBAS EX"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, opciones1);
        SERVER.setAdapter(adapter1);
    }//oncreate

    private void jSon() throws InterruptedException{
        final CountDownLatch latch = new CountDownLatch(1);
        mQueue.start();
        String url="http://sprautomotive.servehttp.com:9080/Login";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                new Response.Listener<JSONObject>() {//correcto
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Response");
                            res=jsonArray.getString(0);
                            if(res.equals("0")){
                                mensaje="Contraseña y/o Usuario Incorrecto";
                            }else{
                                JSONObject dato = jsonArray.getJSONObject(0);//Conjunto de datos
                                user=dato.getString("k_usr");
                                name=dato.getString("k_name");
                                lName=dato.getString("k_lname");
                                type=dato.getString("k_type");
                                branch=dato.getString("k_dscr");
                                mail=dato.getString("k_mail1");
                                codB=dato.getString("k_codB");
                                mensaje="";
                            }//else
                            latch.countDown();//
                        } catch (JSONException e) {
                            mensaje="Problemas al recibir datos json";
                            latch.countDown();
                        }catch (Error error){
                            mensaje="Hubó un problema";
                            latch.countDown();

                        }//catch
                    }//onResponse
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {//Para error
                latch.countDown();
                mensaje="Hubó un problema al recibir datos";
            }//onError
        })//JsonObjectRequest
        {//headers
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", getUsuario);
                params.put("pass", getPass);
                return params;
            }//Map
        };//headers
        mQueue.add(request);
        //Bloqueamos el hilo hasta que el callback llame a latch.countDown
        latch.await();
    }//json

    @Override
    protected void onStart() {
        super.onStart();
        if (preference.contains("user") && preference.contains("pass")) {
            Toast.makeText(this, "Entraste", Toast.LENGTH_SHORT).show();
            Intent perfil = new Intent(this, ActivitySplash.class);
            startActivity(perfil);
            finish();
        }//if
    }//onStart

    private void guardarDatos() {
        editor.putString("user", getUsuario);
        editor.putString("pass", getPass);
        editor.putString("name", name);
        editor.putString("lname", lName);
        editor.putString("type", type);
        editor.putString("branch", branch);
        editor.putString("email", mail);
        editor.putString("codBra", codB);
        editor.putString("Server", StrServer);
        editor.putString("codeBar","Zebra");
        editor.putString("type2", null);
        //editor.putString("tokenId",token);
        editor.commit();
    }//guardaDatos
    private void trasactiv() {
        guardarDatos();
        Toast.makeText(this, msjToast, Toast.LENGTH_SHORT).show();
        Intent perfil = new Intent(this, ActivitySplash.class);
        startActivity(perfil);
        finish();
    }//trasactiv


    /*private class AsyncLogg extends AsyncTask<Void, Boolean, Boolean> {
        boolean term=false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
            mensaje="";
            user="";name="";lName="";type="";mail="";codB="";branch="";res="";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                jSon();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mDialog.dismiss();
            if (res.equals("") || res.equals("0")) {
                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }else{
                if (type.equals("ALMACEN") || type.equals("SUPERVISOR") ) {
                    //AsyncCallWS2 task2 = new AsyncCallWS2();
                    //task2.execute();
                    trasactiv();
                }else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setMessage("ESTE USUARIO NO ES PARTE DEL ALMACÉN").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog titulo = alerta.create();
                    titulo.setTitle("USUARIO ERRONEO");
                    titulo.show();
                }//else
            }//else if
        }
    }*/

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            Conectar();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (result1 == 0) {
                mDialog.dismiss();
                Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();

            } else if (result1 == 1) {
                String tipo = type;
                if (tipo.equals("ALMACEN") || tipo.equals("SUPERVISOR") ) {
                    mDialog.dismiss();
                    AsyncCallWS2 task2 = new AsyncCallWS2();
                    task2.execute();
                    trasactiv();

                } else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setMessage("ESTE USUARIO NO ES PARTE DEL ALMACEN").setCancelable(false).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            mDialog.dismiss();
                        }
                    });

                    AlertDialog titulo = alerta.create();
                    titulo.setTitle("USUARIO ERRONEO");
                    titulo.show();
                }
            }

        }

    }


    private void Conectar() {

        String SOAP_ACTION = "login";
        String METHOD_NAME = "login";
        String NAMESPACE = "http://" + StrServer + "/WSlogin/";
        String URL = "http://" + StrServer + "/WSlogin";

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            xmlLogin soapEnvelope = new xmlLogin(SoapEnvelope.VER11);
            soapEnvelope.valoresLogin(getUsuario, getPass);
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.call(SOAP_ACTION, soapEnvelope);
            Vector response0 = (Vector) soapEnvelope.getResponse();
            result1 = Integer.parseInt(response0.get(0).toString());

            if (response0 == null) {
                mensaje = "Null";

            } else {

                if (result1 == 0) {
                    mensaje = "Contraseña y/o Usuario Inconrrecto";
                } else if (result1 == 1) {
                    mensaje = "Correcto";
                    response = (SoapObject) soapEnvelope.bodyIn;
                    response = (SoapObject) response.getProperty("UserInfo");

                    user=response.getPropertyAsString("k_usr");
                    name=response.getPropertyAsString("k_name");
                    lName=response.getPropertyAsString("k_lname");
                    type=response.getPropertyAsString("k_type");
                    branch=response.getPropertyAsString("k_dscr");
                    mail=response.getPropertyAsString("k_mail1");
                    codB=response.getPropertyAsString("k_codB");
                    mensaje="";

                }

            }


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
            mensaje = e.getMessage();
            e.printStackTrace();
        } catch (Exception ex) {
            mDialog.dismiss();
            mensaje = "Error:" + ex.getMessage();
        }

    }

    private class AsyncCallWS2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected Void doInBackground(Void... params) {
            conectar2();
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected void onPostExecute(Void result) {}

    }//asyncallws2

    private void conectar2() {
        String SOAP_ACTION = "LogAppUs";
        String METHOD_NAME = "LogAppUs";
        String NAMESPACE = "http://" + StrServer + "/WSk75Branch/";
        String URL = "http://" + StrServer + "/WSk75Branch";


        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            xmlLog soapEnvelope = new xmlLog(SoapEnvelope.VER11);
            soapEnvelope.xmlLog(getUsuario, getPass, id, "LOG IN", "");
            soapEnvelope.dotNet = true;
            soapEnvelope.implicitTypes = true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE trasport = new HttpTransportSE(URL);
            trasport.debug = true;
            trasport.call(SOAP_ACTION, soapEnvelope);
            SoapObject response0 = (SoapObject) soapEnvelope.bodyIn;


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
        }
    }//conectar2

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
            String jsonStr = sh.makeServiceCall(url,getUsuario, getPass);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

}
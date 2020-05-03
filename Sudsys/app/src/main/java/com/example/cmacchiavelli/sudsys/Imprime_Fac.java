package com.example.cmacchiavelli.sudsys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Imprime_Fac extends AppCompatActivity {

  //  private Context context;
    private Toolbar toolbar;
    private static String cod = "", pol = "";
    private static int flag=1,nprecio;
    private static Integer ncuotas,nprecios;
    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;
    private static TextView tv_ase_fac,tv_nro_rec,tv_cliente,tv_nro_cuota,tv_nro_pol,tv_nro_liqui,tv_monto,tv_montobob,tv_poliza1,tv_poliza2,tv_respu,tv_idrec;
    private static String ase_fac,nro_rec,cliente,nro_cuota,nro_pol,nro_liqui,monto,montobob,aux="",nro_pol1,nro_pol2,idrec,idcompro,idcade,tnum_rec,tnum_compro,montot,verifi_cuotas="";
    private static EditText et_nombre,et_nit,et_repu;
    Button btn_fac;
    String[] codigo;
    String usuario;
    SoapObject SoapArray;

    //para las impri
    SoapObject resultString;

    private ZebraPrinter zebraPrinter;
    private Connection connection;
    ProgressDialog dialog;
    String resultado,codigo_mac_impresora ,id_empleado_usuario, todo_pagado,string_cuotas,parametro_id_empleado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_imprime__fac);

        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("FACTURA CREDINFORM");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);

      Bundle extras = getIntent().getExtras();

        if(extras != null){
            ase_fac = extras.getString("aseguradora");
            nro_rec = extras.getString("nro_rec");
            cliente = extras.getString("cliente");
            nro_cuota = extras.getString("nro_cuota");
            nro_pol = extras.getString("nro_poliza");
            nro_liqui = extras.getString("nro_liquidacion");
            monto = extras.getString("monto");
            montobob = extras.getString("monto_bob");
            aux = extras.getString("nro_poliza");
            idrec= extras.getString("idrecibo");
            tnum_rec=extras.getString("tnum_rec");
            tnum_compro=extras.getString("tv_compro");

        }

        tv_ase_fac= (TextView) findViewById(R.id.tv_aseguradora);
        tv_nro_rec= (TextView) findViewById(R.id.tv_nro_rec);
        tv_cliente= (TextView) findViewById(R.id.tv_cliente);
        tv_nro_cuota= (TextView) findViewById(R.id.tv_cuota);
        tv_nro_pol= (TextView) findViewById(R.id.tv_poliza);
        tv_nro_liqui= (TextView) findViewById(R.id.tv_liquidacion);
        tv_monto= (TextView) findViewById(R.id.tv_monto);
        tv_montobob= (TextView) findViewById(R.id.tv_monto_bob);
        tv_poliza1= (TextView) findViewById(R.id.tv_pol1);
        tv_poliza2= (TextView) findViewById(R.id.tv_pol2);
        et_nombre= (EditText) findViewById(R.id.et_nombre);
        et_nit= (EditText) findViewById(R.id.et_nit);
        tv_respu = (TextView) findViewById(R.id.tv_resp);
        et_repu = (EditText) findViewById(R.id.et_resp);
        btn_fac = (Button) findViewById(R.id.btn_fac);
        tv_idrec = (TextView)findViewById(R.id.tv_idrec);

        //insertaamos
        tv_ase_fac.setText(ase_fac);
        tv_nro_rec.setText(nro_rec);
        tv_cliente.setText(cliente);
        tv_nro_cuota.setText(nro_cuota);
        tv_nro_pol.setText(nro_pol);
        tv_nro_liqui.setText(nro_liqui);
        tv_monto.setText(monto);
        tv_montobob.setText(montobob);
        tv_idrec.setText(idrec);
        tv_respu.setText(tnum_compro);

        aux = tv_nro_pol.getText().toString();
        String car="-";

        if(aux.indexOf(car) != -1){
            codigo=aux.split("-");
            tv_poliza1.setText(codigo[0]);
            tv_poliza2.setText(codigo[1]);
            //Toast.makeText(getApplicationContext(),"es 2 un aprametro",Toast.LENGTH_LONG).show();
            cod = codigo[0];
            pol = codigo[1];
            PosibleFac();
           // verificacuota ();
        }
        else{
            tv_poliza1.setText(aux);
            tv_poliza2.setText(aux);
        }
    }

    //@Override sirve para sobrescribir un método
    @Override public  boolean onCreateOptionsMenu(Menu mimenu){
        getMenuInflater().inflate(R.menu.menu_superior, mimenu);
        return true;
    }

    //click en algun elemento del menu
    @Override public boolean onOptionsItemSelected(MenuItem opcion_menu){
        //capturamos el id del menu presionado
        int id=opcion_menu.getItemId();
        //si es el mismo id recibido del item presionado con el de configuracion almacenado en el archivo R
        if(id==R.id.configuracion){
           // IrPantallaPrincipal();
            finish();
            return true;
        }
        if(id==R.id.cerrar_sesion){
            Toast.makeText(getApplicationContext(), "Cerrar sesion desde Pantalla Principal.", Toast.LENGTH_LONG).show();
        }
        if(id==R.id.info){
            //llamamos a la funcion ejecutar info que es de tipo vista, y si no hay vista poner null
            ejecutar_info(null);
            return true;
        }
        //si no se cumple ninguna condicion, entonces debe devolver una opcion de menu por defecto
        return super.onOptionsItemSelected(opcion_menu);
    }


    //mostrar_actividad_info al hacer clieck en el item del menu
    //llamamos a la actividad info
    public void ejecutar_info(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(this, info.class);
        startActivity(i);
    }
    //llamamos a la actividad inicio
    public void pantalla_inicio(View view){
        super.finish();
    }

    public void obtfac(View View){

        if(tv_respu.getText() == ""){
            flag=1;
            String a ="",b="";
            a = verifi_cuotas;
            String [] aa =  a.split(";");
            b = tv_nro_cuota.getText().toString();
            String [] bb =  b.split(";");
            int flag2 = 0;
            for(int i = 0;i<bb.length;i++){
                String dato= bb[i];
                for(int j =0;j<aa.length;j++){
                    if(dato.equals(aa[j])){
                        flag2 ++;
                        break;
                    }

                 }

            }
            if(flag2 == bb.length){
                JSONObject cad = Cadena();
                WebServiceVolleyfac( getBaseContext(),cad.toString());
            }
            else{
                Toast.makeText(getApplicationContext(),"Por favor Contactarse con CREDINFORM y revisar las cuotas canceladas.",Toast.LENGTH_LONG).show();
            }
        }
        else{
            DatosFac();
            flag=2;
            idcompro=tnum_compro;
        }


   /*             String a ="",b="";
                a = verifi_cuotas;
                b = tv_nro_cuota.getText().toString();
                Toast.makeText(getApplicationContext(),verifi_cuotas+"-"+tv_nro_cuota.getText().toString(),Toast.LENGTH_LONG).show();
                if(a.equals(b)){
                    Toast.makeText(getApplicationContext(),"si entro posi",Toast.LENGTH_LONG).show();
                }
*/
         /*   if(tv_respu.getText() == ""){
                flag=1;
                JSONObject cad = Cadena();
                WebServiceVolleyfac( getBaseContext(),cad.toString());
            }
            else{
                DatosFac();
                flag=2;
                idcompro=tnum_compro;
            }
*/

    }
    //obtiene datos del cliente


    private static JSONObject Poliza(){
        try {
            JSONObject jgeneral = new JSONObject();
            jgeneral.put("cinit","");
            jgeneral.put("idproducto",cod);
            jgeneral.put("numpoliza",pol);
            jgeneral.put("placa","");
            jgeneral.put("idreclamo","");
            jgeneral.put("idcertificado","");
            jgeneral.put("tipo","2");


            return jgeneral;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
    //obtiene  datos de la poliza
    private void WebServiceVolley(Context context, final String cadena){
        try{
            String url = "http://190.129.70.58:8079/api/Buscador/BuscarDatos";

            final StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                      //  tv_resp.setText(jsonObject.toString());

                        //OBTENEMOS LOS RESULTADOS POST
                        String codigo=jsonObject.getString("codigo");
                        String codoperacion=jsonObject.getString("codoperacion");
                        String operacion=jsonObject.getString("operacion");
                        String certificado=jsonObject.getString("certificado");
                        String asegurado=jsonObject.getString("asegurado");

                        //SETEAMOS LAS VARIABLES
                     /*   tvcodigo.setText(codigo);
                        tvcodoperacion.setText(codoperacion);
                        tvoperacion.setText(operacion);
                        tvasegurado.setText(asegurado);

                        PosibleFac();*/


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "no entro", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace ();
                }
            }){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return cadena == null ? null : cadena.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(2000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyController.getInstance(context).addToRequestQueue(jsonObjReq);

        }
        catch (Exception ex){
            ex.toString();
        }
    }

    private void PosibleFac(){
        final ProgressDialog loading= ProgressDialog.show(this,"Cargando Informacion","Espere Porfavor...",false,false);
        String DATA_URL = "http://190.129.70.58:8079/api/ProcesoFacturacion/GetObtieneDatosFactura?idproducto="+tv_poliza1.getText()+"&poliza="+tv_poliza2.getText()+"&anexos="+tv_nro_pol.getText()+"&nombre=&nit=";
        //String DATA_URL ="http://190.129.70.58:8079/api/ProcesoFacturacion/GetObtieneDatosFactura?idproducto=CA&poliza=CBR0046648&anexos=CA-CBR0046648&nombre=&nit";



        final StringRequest stringRequest = new StringRequest(Request.Method.GET, DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String nombre=jsonObject.getString("nombre");
                    String nit=jsonObject.getString("nit");

                    //Toast.makeText(getApplicationContext(), "LA R", Toast.LENGTH_LONG).show();

                    et_nombre.setText(nombre.trim());
                    et_nit.setText(nit.trim());
                    loading.dismiss();
                    //tv_respu.setText(cad.toString());
                   // et_repu.setText(cad.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //
    private void DatosFac(){
        //final ProgressDialog loading= ProgressDialog.show(this,"Cargando Informacion","Espere Porfavor...",false,false);
        //String DATA_URL = "http://190.129.70.58:8079/api/ProcesoFacturacion/GetObtieneDatosFactura?idproducto="+tv_poliza1.getText()+"&poliza="+tv_poliza2.getText()+"&anexos="+tv_nro_pol.getText()+"&nombre=&nit=";
        String DATA_URL2 ="http://190.129.70.58:8079/api/ProcesoFacturacion/GetDatosImpresion?comprobante=F000168451&iddosificacion=0";
        String DATA_URL ="http://190.129.70.58:8079/api/ProcesoFacturacion/GetDatosImpresion?comprobante="+ tv_respu.getText()+"&iddosificacion=0";



        final StringRequest stringRequest = new StringRequest(Request.Method.GET, DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //  JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = new JSONObject(response);
                    et_repu.setText(jsonObject.toString());
                    idcade= jsonObject.toString();

                    Imprime_Fac.novenoPlanoImpresion tarea2 = new Imprime_Fac.novenoPlanoImpresion();
                    tarea2.execute();
                    }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No se encontro el nro de recibo", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // loading.dismiss();
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



        private void verificacuota(){
            final ProgressDialog loading= ProgressDialog.show(this,"Verificando","Espere Porfavor...",false,false);
            String DATA_URL = "http://190.129.70.58:8079/api/CrdWsFacturacion/ObtienePlanPagos?poliza="+tv_nro_pol.getText().toString()+"&nrocertificado=";
            //String DATA_URL ="http://190.129.70.58:8079/api/ProcesoFacturacion/GetObtieneDatosFactura?idproducto=CA&poliza=CBR0046648&anexos=CA-CBR0046648&nombre=&nit";



            final StringRequest stringRequest = new StringRequest(Request.Method.GET, DATA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    String cutoa="";
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int x = 0; x < jsonArray.length(); x++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                           // Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                            String couta = jsonObject.getString("NroCuota");
                            Integer c =  Integer.parseInt(couta)+1;
                            if(x==0){
                                cutoa= c+"";
                            }else{
                                cutoa = cutoa+";"+c+"";
                            }

                        }
                        verifi_cuotas=cutoa;
                        loading.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }


    //armamos el jsonobject para obtener el nro de comprobante
    private static JSONObject Cadena(){

        double montod = Double.parseDouble(montobob);
        //double ncuotad = Double.parseDouble(nro_cuota);
        try {
            //crear el objeto json para enviar por POST
            JSONObject jgeneral = new JSONObject();
            JSONObject listaFormaPago = new JSONObject();
            JSONArray jarraylistaFormaPago = new JSONArray();

            JSONArray jarraylistaFacturaDetalle = new JSONArray();
            JSONObject listaFacturaDetallesub = new JSONObject();
            JSONArray jarraylistappdetalle = new JSONArray();


            JSONArray jarraylistaPolizaDetalle = new JSONArray();
            JSONObject listaPolizaDetalle = new JSONObject();

            JSONObject asegurado = new JSONObject();
            JSONObject objDatosFactura = new JSONObject();
            JSONObject objUbicacion = new JSONObject();


            jgeneral.put("TotalFacturaDetalle",montod);
            jgeneral.put("TotalFormaPagoDetalle",montod);

            listaFormaPago.put("tipofp","E");
            listaFormaPago.put("moneda","D");
            listaFormaPago.put("monto",montod);
            listaFormaPago.put("banco","0");
            listaFormaPago.put("cheque","");
            listaFormaPago.put("tarjeta","");
            listaFormaPago.put("numtarjeta","");
            jarraylistaFormaPago.put(listaFormaPago);

            jgeneral.put("listaFormaPago",jarraylistaFormaPago);
            String [] vcuotas= nro_cuota.split(";");
            String [] vmonto=monto.split(";");
            int a = vcuotas.length;
            for (int i = 0;i<a;i++) {
                Double c =  Double.parseDouble(vcuotas[i])-1;
                Double m = Double.parseDouble(vmonto[i]);


                JSONObject listapdetalle = new JSONObject();
///primera parteeeeeee
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                listapdetalle.put("Identificacion", pol);
                listapdetalle.put("NroCuota", c);
                listapdetalle.put("FechaPago", date);
                listapdetalle.put("Importe", m);
                listapdetalle.put("Debito", m);
                listapdetalle.put("Saldo", 0);
                listapdetalle.put("Gestion", "NINGUNO");

                jarraylistappdetalle.put(listapdetalle);
            }
            listaFacturaDetallesub.put("poliza",nro_pol );
            listaFacturaDetallesub.put("listappdetalle",jarraylistappdetalle);

            jarraylistaFacturaDetalle.put(listaFacturaDetallesub);

            jgeneral.put("listaFacturaDetalle",jarraylistaFacturaDetalle);

//tercera parte

            listaPolizaDetalle.put("poliza",nro_pol );
            listaPolizaDetalle.put("moneda","D");
            listaPolizaDetalle.put("monto",montod);
            listaPolizaDetalle.put("montobs",0);

            jarraylistaPolizaDetalle.put(listaPolizaDetalle);

            jgeneral.put("listaPolizaDetalle",jarraylistaPolizaDetalle);

//cuarta parte
            asegurado.put("nombre",et_nombre.getText());
            asegurado.put("moneda","");
            asegurado.put("monedaliteral","");
            asegurado.put("nit",et_nit.getText());
            asegurado.put("estadopol","1");

            jgeneral.put("asegurado",asegurado);

//quinta parte
            objDatosFactura.put("iddosificacion","");
            objDatosFactura.put("nrofactura","");
            objDatosFactura.put("autorizacion","");
            objDatosFactura.put("codusuario","br_sudamericana.ws");
            objDatosFactura.put("nrocaja",30);

            jgeneral.put("objDatosFactura",objDatosFactura);


//sexta parte
            objUbicacion.put("comprobante","");
            objUbicacion.put("latitud",0);
            objUbicacion.put("longitud",0);
            objUbicacion.put("direccion","");

            jgeneral.put("objUbicacion",objUbicacion);



            return jgeneral;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
// cadena de ejemplo
    private static JSONObject Cadena1(){

        try {
            //crear el objeto json para enviar por POST
            JSONObject jgeneral = new JSONObject();
            JSONObject listaFormaPago = new JSONObject();
            JSONArray jarraylistaFormaPago = new JSONArray();

            JSONArray jarraylistaFacturaDetalle = new JSONArray();
            JSONObject listaFacturaDetallesub = new JSONObject();
            JSONArray jarraylistappdetalle = new JSONArray();
            JSONObject listapdetalle = new JSONObject();

            JSONArray jarraylistaPolizaDetalle = new JSONArray();
            JSONObject listaPolizaDetalle = new JSONObject();

            JSONObject asegurado = new JSONObject();
            JSONObject objDatosFactura = new JSONObject();
            JSONObject objUbicacion = new JSONObject();


            jgeneral.put("TotalFacturaDetalle",350);
            jgeneral.put("TotalFormaPagoDetalle",350);

            listaFormaPago.put("tipofp","E");
            listaFormaPago.put("moneda","D");
            listaFormaPago.put("monto",350);
            listaFormaPago.put("banco","0");
            listaFormaPago.put("cheque","");
            listaFormaPago.put("tarjeta","");
            listaFormaPago.put("numtarjeta","");
            jarraylistaFormaPago.put(listaFormaPago);

            jgeneral.put("listaFormaPago",jarraylistaFormaPago);

///primera parteeeeeee

            listapdetalle.put("Identificacion","CBE0038362");
            listapdetalle.put("NroCuota",5);
            listapdetalle.put("FechaPago","15012020");
            listapdetalle.put("Importe",364);
            listapdetalle.put("Debito",350);
            listapdetalle.put("Saldo",14);
            listapdetalle.put("Gestion","NINGUNO");

            jarraylistappdetalle.put(listapdetalle);

            listaFacturaDetallesub.put("poliza","CA-CBE0038362");
            listaFacturaDetallesub.put("listappdetalle",jarraylistappdetalle);

            jarraylistaFacturaDetalle.put(listaFacturaDetallesub);

            jgeneral.put("listaFacturaDetalle",jarraylistaFacturaDetalle);

//tercera parte

            listaPolizaDetalle.put("poliza","CA-CBE0038362");
            listaPolizaDetalle.put("moneda","D");
            listaPolizaDetalle.put("monto",350);
            listaPolizaDetalle.put("montobs",0);

            jarraylistaPolizaDetalle.put(listaPolizaDetalle);

            jgeneral.put("listaPolizaDetalle",jarraylistaPolizaDetalle);

//cuarta parte
            asegurado.put("nombre","CRUCENA DEL NORTE DE  G.L.P. S.R.L.");
            asegurado.put("moneda","");
            asegurado.put("monedaliteral","");
            asegurado.put("nit","120569028");
            asegurado.put("estadopol","1");

            jgeneral.put("asegurado",asegurado);

//quinta parte
            objDatosFactura.put("iddosificacion","");
            objDatosFactura.put("nrofactura","");
            objDatosFactura.put("autorizacion","");
            objDatosFactura.put("codusuario","br_sudamericana.ws");
            objDatosFactura.put("nrocaja",30);

            jgeneral.put("objDatosFactura",objDatosFactura);


//sexta parte
            objUbicacion.put("comprobante","");
            objUbicacion.put("latitud",0);
            objUbicacion.put("longitud",0);
            objUbicacion.put("direccion","");

            jgeneral.put("objUbicacion",objUbicacion);

            return jgeneral;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void WebServiceVolleyfac(Context context, final String cadena){
        try{
            String url = "http://190.129.70.58:8079/api/CrdWsFacturacion/Procesar";

            final StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        String errorMessage=jsonObject.getString("errorMessage");
                        String comprobante=jsonObject.getString("comprobante");
                        tv_respu.setText(comprobante);
                        idcompro=comprobante;

                        DatosFac();

                        } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace ();
                }
            }){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return cadena == null ? null : cadena.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyController.getInstance(context).addToRequestQueue(jsonObjReq);

        }
        catch (Exception ex){
            ex.toString();
        }
    }


    public static boolean compruebaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }


    private class primerplano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            //imgImpresora.setVisibility(View.GONE);
            //Toast.makeText(context.getApplicationContext(), "id recibo app ultimo: ", Toast.LENGTH_LONG).show();
            dialog = new ProgressDialog(Imprime_Fac.this);
            dialog.setMessage("Buscando");
            dialog.setCancelable (false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){

            // String id_recibo_app_enviar = id_recibo_app;

            String id_recibo =tnum_rec ;
            String id_empleado = "404";
            String imei_dispositivo = "357136081571342";
            String usuario_ws = "SUdMOv1l3";
            String password_ws = "AXr53.o1";

           // RevisaComprobante(tnum_rec,404,"357136081571342","SUdMOv1l3","AXr53.o1");

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //  Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();

            dialog.setMessage(resultado);
            dialog.hide();
        }
    }


    private class novenoPlanoImpresion extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            //imgImpresora.setVisibility(View.GONE);
            //Toast.makeText(context.getApplicationContext(), "id recibo app ultimo: ", Toast.LENGTH_LONG).show();
            dialog = new ProgressDialog(Imprime_Fac.this);
            dialog.setMessage("Imprimiendo Factura Credinform...");
            dialog.setCancelable (false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){

           // String id_recibo_app_enviar = id_recibo_app;

            String id_recibo =idrec ;
            String id_empleado = "404";
            String imei_dispositivo = "357136081571342";
            String usuario_ws = "SUdMOv1l3";
            String password_ws = "AXr53.o1";
            String cad= idcade;


            receipPrint2(id_recibo, id_empleado, imei_dispositivo, usuario_ws, password_ws,cad);

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
          //  Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();


           dialog.setMessage(resultado);
           dialog.hide();
        }
    }

    private void receipPrint2(String id_recibo_app, String id_empleado, String imei_dispositivo, String usuario_ws, String password_ws,String cad) {

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String  hora = new SimpleDateFormat("HH:mm").format(new Date());

        //String rec_aux = tvNombre.getText().toString();


        String numfac = "666";
        String nroauto = "42840100";
        String nitci= "6752509";
        String CodControl = "D5-9F-63-92-10";
        String feclim = "";
        String qr="";
        String anexo="";
        String comprobante=idcompro;

        String SOAP_ACTION = "http://ws.sudseguros.com/PrintCuotaAmortizada";
        String METHOD_NAME = "PrintCuotaAmortizada";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil_jose_manuel/ServiceSud.asmx";

        String codigo = "";
        String mensaje = "";
        String id_sucursal = "";
        String numero_recibo = "";
        String fecha_emision = "";
        String hora_emision = "";
        String ci_nit = "";
        String cliente = "";
        String recibido_de = "";
        String moneda = "";
        String importe = "";
        String importe_bs = "";
        String monto_literal = "";
        String concepto = "";
        String tipo_pago = "";
        String cheque_banco = "";
        String cheque_numero = "";
        String cobrador = "";
        String compania="";
        String rec_rec="";

        String sucursal = "";
        String sucursal_abreviatura = "";

        String datos_cabecera = "";

        String numero_cuota="";
        String numero_poliza="";

        String importe_final;



        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_recibo_app", id_recibo_app);
            Request.addProperty("imei_dispositivo", "357136081571342");
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            //Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            //Agarratodo el Objeto
            SoapArray = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo


            codigo = SoapArray.getProperty(0).toString();
            mensaje = SoapArray.getProperty(1).toString();
            id_sucursal = SoapArray.getProperty(18).toString();
            id_recibo_app = SoapArray.getProperty(1).toString();
            numero_recibo = SoapArray.getProperty(8).toString();
            fecha_emision = SoapArray.getProperty(11).toString();
            hora_emision = SoapArray.getProperty(7).toString();
            numero_cuota = SoapArray.getProperty(5).toString();
            numero_poliza = SoapArray.getProperty(13).toString();

            // rec_rec = SoapArray.getProperty(2).toString();
            ci_nit = "";
            cliente = SoapArray.getProperty(14).toString();
            recibido_de = SoapArray.getProperty(14).toString();
            //moneda = SoapArray.getProperty(18).toString();
            moneda = "$us";
            importe = SoapArray.getProperty(6).toString();
            importe_bs = "";
          //  double importe_verdadero_double=Double.parseDouble(importe);
            //int importe_verdadero_int = (int) importe_verdadero_double;

            concepto = "PAGO CUOTAS POLIZAS DE SEGURO ";
            tipo_pago = "";
            cheque_banco = "";
            cheque_numero = "";
            cobrador = SoapArray.getProperty(16).toString();
            compania = SoapArray.getProperty(15).toString();
            id_empleado_usuario = SoapArray.getProperty(20).toString();

            todo_pagado = SoapArray.getProperty(2).toString();
            string_cuotas = todo_pagado.toString();

            //String codqr = importe+"|"+nitci+"|"+date+"|"+id_recibo_app;

            sucursal = "";
            sucursal_abreviatura = "";

            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(cheque_numero.toString().trim().isEmpty() || cheque_numero.toString().equals("anyType{}")) {
                cheque_numero="------";
            }

            resultado = "Respuesta: " + codigo;

        } catch (Exception ex) {
            resultado = "ERROR 12: " + ex.getMessage() + " " + ex.getLocalizedMessage();
            //Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
        }

     connectPrint(); //Conexion con la Impresora

    if (codigo.equals("1") && zebraPrinter != null) {

            try {

                ZebraPrinter printer2 = ZebraPrinterFactory.getInstance(connection);

                PrinterStatus printerStatus2 = printer2.getCurrentStatus();
                if (printerStatus2.isReadyToPrint) {

                    resultado = "Listo para imprimir ";



                    if(moneda.equals("$us")){
                        importe_final = importe;
                    }else{
                        importe_final = importe_bs;
                    }
                    JSONObject obj = new JSONObject(cad);
                    String nro_fac=obj.getString("nrofactura");
                    numfac=nro_fac;
                    String nro_auto=obj.getString("autorizacion");
                    nroauto=nro_auto;
                    String nitt=obj.getString("nit");
                    nitci=nitt;
                    String nombre=obj.getString("nombre");
                    String fecha=obj.getString("fecha");
                    String [] cadfec =(fecha.substring(0,fecha.length()-9)).split("-");
                    fecha = cadfec[2] + "/"+cadfec[1]+"/"+cadfec[0];
                    date=fecha;

                    String direccion=obj.getString("direccion");
                    String codigocontrol=obj.getString("codigocontrol");
                    CodControl=codigocontrol;

                    String totalfactura=obj.getString("totalfactura");
                    String literal=obj.getString("literal");
                    String codusuario=obj.getString("codusuario");
                    anexo=codusuario;
                    String qrcode=obj.getString("qrcode");
                    qr=qrcode;
                    String fec_limite=obj.getString("fechalimite");

                    String [] cadfecl =(fec_limite.substring(0,fec_limite.length()-9)).split("-");
                    fec_limite = cadfecl[2] + "/"+cadfecl[1]+"/"+cadfecl[0];
                    feclim=fec_limite;


                    datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                            "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                            "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                            "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";


                    String Men="SUCURSAL N_F8 92\r\n"+
                            "CALLE CAPITAN RAVELO 2328\r\n"+
                            "ZONA SOPOCACHI TEL 2315566\r\n"+
                            "LA PAZ - BOLIVIA";

                    String pie="ESTA FACTURA CONTRIBUYE AL DESARROLLO DEL PAIS. EL\n"+
                            " USU ILICITO DE ESTA SERA SANCIONADO DE ACUERDO A LEY\n"+
                            " Ley N_F8 453. El proveedor debera suministrar el servicio en las\n"+
                            " modalidades y terminos ofertados o convenidos";

                    String texto = "^XA\r\n" +
                            "^CFD\r\n" +
                            "^POI\r\n" +
                            "^LH5,5\r\n" +
                            "^FO90,0^A0,30,25^FD SEGUROS Y REASEGUROS CREDINFORM^FS\r\n" +
                            "^FO170,30^A0,30,25^FD INTERNATIONAL S.A.^FS\r\n" +
                            "^FO200,60^A0,16,18^FH^FD SUCURSAL N_F8 92 ^FS\r\n"+
                            "^FO165,75^A0,16,18^FH^FD CALLE CAPITAN RAVELO 2328 ^FS\r\n"+
                            "^FO165,90^A0,16,18^FH^FD ZONA SOPOCACHI TEL 2315566 ^FS\r\n"+
                            "^FO200,105^A0,16,18^FH^FD LA PAZ - BOLIVIA ^FS\r\n";
                    if(flag==2){
                        texto += "^FO180,130^A0,30,25^FD COPIA FACTURA^FS\r\n";
                        texto += "^FO100,155^A0,30,25^FD (NO VALIDO PARA CREDITO FISCAL)^FS\r\n";
                    }
                    else {
                        texto += "^FO220,130^A0,30,25^FD FACTURA^FS\r\n";
                         }

                    texto += "^FO40,190^GB500,0,2^FS\r\n" +
                            "^FO130,200^A0,22,15^FD NIT:^FS\r\n" +
                            "^FO270,200^ADN,11,7^FD"+nitt +"^FS\r\n" +
                            "^FO130,230^A0,22,15^FH^FD N_F8 FACTURA:^FS\r\n" +
                            "^FO270,230^ADN,11,7^FD "+nro_fac +"^FS\r\n" +
                            "^FO130,260^A0,22,15^FH^FD N_F8 AUTORIZACI_E0N:^FS\r\n" +
                            "^FO270,260^ADN,11,7^FD"+nro_auto+"^FS\r\n" +
                            "^FO40,280^GB500,0,2^FS\r\n" +
                            "^FO70,290^A0,14,15^FH^FD ACTIVIDAD PRINCIPAL: PLANES DE SEGUROS GENERALES Y REASEGUROS ^FS\r\n" +
                            "^FO70,310^A0,14,15^FH^FD ACTIVIDAD SECUNDARIA: ALQUILER DE BIENES RA_D6CES PROPIOS ^FS\r\n" +
                            "^FO30,330^ADN,11,7^FD Fecha: "+fecha+"^FS\r\n" +
                            "^FO270,330^ADN,11,7^FD Hora: "+hora+"^FS\r\n" +
                            "^FO30,360^ADN,11,7^FD NIT/CI: 789456^FS\r\n" +
                            "^FO30,390^FB530,3,0,L^A0,18,20^FH^FD Se_A4or(es): "+nombre+"^FS\r\n";
                    texto += "^FO100,450^A0,18,20^FD POLIZA^FS\r\n" +
                            "^FO300,450^A0,18,20^FD ANEXO^FS\r\n" +
                            "^FO388,450^A0,18,20^FD CUOTA^FS\r\n" +
                            "^FO460,450^A0,18,20^FD IMPORTE^FS\r\n";
                    int saltador = 480;

                    JSONArray estado = obj.getJSONArray("listaFacturaDetalle");

                    for(int x = 0; x < estado.length(); x++){
                        JSONObject elemento = estado.getJSONObject(x);
                        String poliza = elemento.getString("poliza");
                        String placa = elemento.getString("placa");
                        String anexoo = elemento.getString("anexo");
                        String couta = elemento.getString("couta");
                        String imported = elemento.getString("importe");

                        texto += "^FO15," + saltador + "^A0,16,18^FD "+poliza+"^FS\r\n" +
                                "^FO280," + saltador + "^A0,16,18^FD "+anexoo +" ^FS\r\n" +
                                "^FO420," + saltador + "^A0,16,18^FD "+couta+"^FS\r\n" +
                                "^FO470," + saltador + "^A0,16,18^FD "+imported+"^FS\r\n";

                        if(x == estado.length()-1){
                            saltador += 20;
                            texto += "^FO100," + saltador + "^A0,16,18^FD "+placa+" ^FS\r\n";
                            saltador += 30;
                        }
                        else{
                            saltador += 30;
                        }

                    }


                    texto += "^FO330," + saltador + "^A0,20,20^FD TOTAL Bs  "+totalfactura+"^FS\r\n";
                    saltador += 30;
                    texto +=
                            "^FO20," + saltador + "^GB600,0,2^FS\r\n";
                    saltador += 10;
                    texto +=
                            "^FO30," + saltador + "\r\n" +
                                    "^FB500,2,0,L\r\n" +
                                    "^A0,14,15^FH^FD SON: "+literal+"^FS\r\n";
                    saltador += 30;
                    texto += "^FO190," + saltador + "^A0,30,25^FD FORMA DE PAGO^FS\r\n";


                    JSONArray listaFormaPago = obj.getJSONArray("listaFormaPago");
                    JSONObject lista = listaFormaPago.getJSONObject(0);
                    String tipofp = lista.getString("tipofp");
                    String monedafp = lista.getString("moneda");
                    String monto = lista.getString("monto");

                    importe = monto;



                    saltador += 30;
                    texto += "^FO100," + saltador + "^A0,18,20^FD TIPO^FS\r\n" +
                            "^FO330," + saltador + "^A0,18,20^FD MONEDA^FS\r\n" +
                            "^FO470," + saltador + "^A0,18,20^FD MONTO^FS\r\n";

                    saltador += 30;

                    texto += "^FO30," + saltador + "^A0,16,18^FD "+tipofp+"^FS\r\n" +
                            "^FO300," + saltador + "^A0,16,18^FD "+monedafp+"^FS\r\n" +
                            "^FO450," + saltador + "^A0,16,18^FD "+monto+"^FS\r\n";
                    saltador += 30;

                    texto +=
                            "^FO20," + saltador + "^GB600,0,2^FS\r\n";
                    saltador += 10;
                    texto +=
                            "^FO30," + saltador + "^ADN,11,7^FH^FD C_E0DIGO DE CONTROL:"+codigocontrol+"^FS\r\n";
                    saltador += 30;
                    texto +=
                            "^FO30," + saltador + "^ADN,11,7^FH^FD FECHA L_D6MITE DE EMISI_E0N: "+fec_limite+"^FS\r\n";
                    saltador += 20;
                    texto +=
                            "^FO240," + saltador + "^BQN,2,4^FD  "+qrcode +"^FS\r\n";
                    saltador += 180;
                    texto +=
                            "^FO30," + saltador + "\r\n" +
                                    "\r\n^FB530,6,0,C\r\n^A0,18,20^FH^FD"+ pie + "^FS\r\n";
                    saltador += 110;
                    texto += "^FO30," + saltador + "^A0,14,15^FD "+codusuario+"^FS\r\n" +
                            "^XZ\r\n";
                    saltador += 50;
                    String ZplCabecera = "! U1 setvar \"zpl.label_length\" \"" + saltador + "\"\r\n" + texto;


                    byte[] rcpt = ZplCabecera.getBytes();
                    try {
                        connection.write(rcpt);
                        resultado = "Documento Impreso ";
                    } catch (ConnectionException e) {
                        resultado = "Error al Imprimir "+ e.getMessage();
                    }

                } else if (printerStatus2.isPaused) {
                    resultado = "No se puede imprimir por que el dipositivo detenido.";
                } else if (printerStatus2.isHeadOpen) {
                    resultado = "No se puede imprimir por que la cabecera esta abierta.";
                } else if (printerStatus2.isPaperOut) {
                    resultado = "No se puede imprimir por que el papel esta afuera.";
                } else {
                    resultado = "No se puede imprimir.";
                }

            } catch (ConnectionException e) {
                e.printStackTrace();
            } catch (ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }


        }
        // verificamos para la reimpresion


        if(flag==1) {
            GuardaFactura(numero_recibo, id_empleado_usuario, imei_dispositivo, usuario_ws, password_ws,
                    numfac, nroauto, date, hora, nitci,
                    recibido_de, importe, CodControl, feclim,
                    qr, "wssudamericana", comprobante);
        }

    }

    private void closeConnection(){
        if(connection != null){
            try
            {
                connection.close();
            }
            catch (ConnectionException exx)
            {
                resultado = "Conexion con impresora Cerrada " + exx.getMessage();
            }
        }
    }
    public void connectPrint() {

        //OBTENEMOS MAC DE LA IMPRESORA
        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(getApplicationContext());


        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                //que columnas queremos que saque nuestra consulta
                Estructura_BBDD.NOMBRE_COLUMNA9,//1 id

        };

        //COLUMNA QUE COMPARAREMOS (WHERE)
        String selection = Estructura_BBDD.NOMBRE_COLUMNA1 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { id_empleado_usuario.toString() };

        try {

            Cursor c = db.query(
                    Estructura_BBDD.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            //se mueve el cursor al primer registro

            c.moveToFirst();

            //mostramos los valores recuperados de la base de datos de recibos
            codigo_mac_impresora = c.getString(0);
        }
        catch (Exception ex2){
            //Toast.makeText(getApplicationContext(), "No se encontro registro de MAC", Toast.LENGTH_LONG).show();
            resultado = "No es Posible conectarse con la Impresora, MAC no valida " + ex2.getMessage();
        }

        //CODIGO MAC DE LA IMPRESORA, OBLIGATORIO PARA RECONOCIMIENTO
        connection = new BluetoothConnection(codigo_mac_impresora); //c:3f:a4:a1:0e:ad

        try
        {
            connection.open();
        }

        catch (ConnectionException ex)
        {
            resultado = "No es Posible conectarse con la Impresora " + ex.getMessage();
            closeConnection();
        }

        catch(Exception ex)
        {
            resultado = "No es Posible conectarse con la Impresora " + ex.getMessage();
        }

        try
        {
            zebraPrinter = ZebraPrinterFactory.getInstance(connection);
            PrinterLanguage pl = zebraPrinter.getPrinterControlLanguage();

            resultado = "Conectado a la Impresora " + pl;
        }

        catch (ConnectionException ex)
        {
            resultado = "No pudo conectarse a la Impresora 1 " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();

        } catch(Exception ex)
        {
            resultado = "No pudo conectarse a la Impresora2  " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();
        }
    }


    private void GuardaFactura(String id_recibo_app, String id_empleado, String imei_dispositivo, String usuario_ws, String password_ws,
                               String NroFac,String autorizacion,String fecha,String hora,String nit,
                               String nombrefac,String Total,String codcontrol,String fecha_limi,String qr,String usu_sud,String comprobante) {

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String  horas = new SimpleDateFormat("HH:mm").format(new Date());
        //String NroFac= "5"+

        String SOAP_ACTION = "http://ws.sudseguros.com/SaveBillingCia";
        String METHOD_NAME = "SaveBillingCia";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil_jose_manuel/ServiceSud.asmx";

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_recibo", id_recibo_app);
            Request.addProperty("numero_factura", NroFac);
            Request.addProperty("numero_autorizacion", autorizacion);
            Request.addProperty("fecha", fecha);
            Request.addProperty("hora", hora);
            Request.addProperty("ci_nit", nit);
            Request.addProperty("facturar_a", nombrefac);
            Request.addProperty("total", Total);
            Request.addProperty("codigo_control", codcontrol);
            Request.addProperty("fecha_limite_emision", fecha_limi);
            Request.addProperty("qr", qr);
            Request.addProperty("usuario_cia", usu_sud);
            Request.addProperty("id_empleado", id_empleado);


            Request.addProperty("imei_dispositivo", "357136081571342");
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");
            Request.addProperty("comprobante", comprobante);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            //Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            //Agarratodo el Objeto
            SoapArray = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo


            // String c = String.valueOf(id_recibo_app);
            String a = SoapArray.toString();
            resultado = a;

        } catch (Exception ex) {
            resultado = "ERROR 12: " + ex.getMessage() + " " + ex.getLocalizedMessage();
            //Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
        }


    }

    private void RevisaComprobante(String tnum_rec, Integer id_empleado, String imei_dispositivo, String usuario_ws, String password_ws) {


        String SOAP_ACTION = "http://ws.sudseguros.com/BuscaComprobante";
        String METHOD_NAME = "BuscaComprobante";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil_jose_manuel/ServiceSud.asmx";

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_recibo", tnum_rec);
            Request.addProperty("id_empleado", 404);
            Request.addProperty("imei_dispositivo", "357136081571342");
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");


            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            SoapArray = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            String a = SoapArray.toString();

            tv_respu.setText(a);

            resultado = a;

        } catch (Exception ex) {
            resultado = "ERROR 12: " + ex.getMessage() + " " + ex.getLocalizedMessage();
            //Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
        }


    }


}

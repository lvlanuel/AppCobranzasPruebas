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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmisionRecPedya extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;
    //para el webservice
    SoapObject resultString;
    //definimos el txt que estara a la escucha, para almacenar en base de datos
    EditText textoConcepto, textoNumeroCheque, textoMonto;
    Spinner spinner, spinner2, spinner3, spinner_sucursal;
    String spbanco, spformaPago, spmoneda, spsucursal;
    TextView ci_cliente;

    Button guarda;
    //variables globales
    String exito, mensaje2, parametro_tipo_actividad, parametro_id_cliente,parametro_nombre_cliente,parametro_apellido_paterno_cliente,parametro_apellido_materno_cliente,parametro_ci_nit_cliente, parametro_tipo_cliente;
    String nomcliente,conceptorec ,moneda ,tipopag ,entidadfin ,numcheque,montocheque  ,sucursalpago ;
    //variables globales
    String id_sucursal, id_empleado, id_llave_unica_recibo, parametro_numero_liquidacion;

    //variables para la edicion
    String parametro_id_recibo, parametro_concepto, parametro_monto, parametro_numero_cheque, parametro_forma_pago, parametro_moneda, parametro_banco, parametro_recibo_sucusal, parametro_email_cliente, parametro_total_a_pagar, contenedor_combo_bancos;

    String mensaje,respuesta_recibido,mensaje_recibido,id_recibo_recibido,numero_recibo_recibido,anio_recibo_recibido,usuario_reg;
    List<String> list_bancos;


    SoapObject SoapArray;
    private ZebraPrinter zebraPrinter;
    private Connection connection;
    String resultado,string_cuotas,todo_pagado,codigo_mac_impresora;
    int cantidad_cuotas=0;
    int cantidad_cuotas_aux=0;
    ProgressDialog dialog, dialog2;


    private int flag;
    public int numero;
    public String importe_parcial;
    public String num;
    public String num_letra;
    public String num_letras;
    public String num_letram;
    public String num_letradm;
    public String num_letracm;
    public String num_letramm;
    public String num_letradmm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emision_rec_pedya);


        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Imprimir Recibo Pedidos Ya");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);

        //recuperamos variables de la interfaz
        textoConcepto=(EditText)findViewById(R.id.concepto);
        textoNumeroCheque=(EditText)findViewById(R.id.numero_cheque);
        textoMonto=(EditText)findViewById(R.id.monto);
        guarda = (Button)findViewById(R.id.guardarpd);





        //recibimos variables
        Bundle datos=getIntent().getExtras();
        parametro_tipo_actividad=datos.getString("parametro_tipo_actividad");
        parametro_id_cliente=datos.getString("parametro_id_cliente");
        parametro_nombre_cliente=datos.getString("parametro_nombre_cliente");
        parametro_apellido_paterno_cliente=datos.getString("parametro_apellido_paterno_cliente");
        parametro_apellido_materno_cliente=datos.getString("parametro_apellido_materno_cliente");
        parametro_ci_nit_cliente=datos.getString("parametro_ci_nit_cliente");
        parametro_tipo_cliente=datos.getString("parametro_tipo_cliente");
        parametro_email_cliente=datos.getString("parametro_email_cliente");
        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");
        usuario_reg=datos.getString("usuario_reg");
        // parametro_total_a_pagar=datos.getString("parametro_total_a_pagar");
        // parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");


        //recuperamos la variable ci_cliente
        ci_cliente=(TextView)findViewById(R.id.textView_ci);

        //ponemos las variables recibidas de la otra actividad en los campos de texto

        TextView txtCambiado = (TextView)findViewById(R.id.nombre_cliente);
        txtCambiado.setText(parametro_nombre_cliente+" "+parametro_apellido_paterno_cliente+" "+parametro_apellido_materno_cliente);
        TextView txtCambiado2 = (TextView)findViewById(R.id.textView_ci);
        txtCambiado2.setText(parametro_ci_nit_cliente);
        TextView txtCambiado3 = (TextView)findViewById(R.id.textView_nombre);
        txtCambiado3.setText(parametro_nombre_cliente+" "+parametro_apellido_paterno_cliente+" "+parametro_apellido_materno_cliente);
        EditText txtCambiadoEmail = (EditText)findViewById(R.id.email_cliente);
        txtCambiadoEmail.setText(parametro_email_cliente);


        //obtenemos la fecha actual
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date2 = new Date();
        String fecha2 = dateFormat2.format(date2);

        TextView txtCambiado4 = (TextView)findViewById(R.id.fecha);
        txtCambiado4.setText(fecha2.toString());

        //combobox tipo_pago
        spinner = (Spinner) findViewById(R.id.tipo_pago);
        String[] letra = {"Contado","Cheque"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        //combobox banco con bd

        try {
            //importamos la clase BB_DD_HELPER
            final BBDD_Helper helper = new BBDD_Helper(this);


            SQLiteDatabase db = helper.getReadableDatabase();

            String[] projection = {
                    //que columnas queremos que saque nuestra consulta
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA1,//1 id
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA2,//2
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA3,//3

            };

            //COLUMNA QUE COMPARAREMOS (WHERE)
            String selection = Estructura_BBDD_Bancos.NOMBRE_COLUMNA1 + " != ? ";

            String[] selectionArgs = { "" };


            try {

                Cursor c = db.query(
                        Estructura_BBDD_Bancos.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                // c.moveToFirst();
                if (c.getCount() > 0) {
                    list_bancos = new ArrayList<String>();
                    while (c.moveToNext()) {
                        list_bancos.add("0-SIN BANCO");
                        contenedor_combo_bancos = c.getString(1).toString()+"-"+c.getString(2).toString();
                        list_bancos.add(contenedor_combo_bancos);
                        //Toast.makeText(getApplicationContext(), c.getString(1).toString(), Toast.LENGTH_LONG).show();
                    }

                }


            }
            catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error al cargar los Bancos", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error base datos de Bancos", Toast.LENGTH_LONG).show();
        }

        spinner2 = (Spinner) findViewById(R.id.banco);
        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_bancos));

        //combobox moneda
        spinner3 = (Spinner) findViewById(R.id.moneda);
        String[] letra3 = {"$us", "Bs"};
        spinner3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra3));

        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");

        //combobox recibo sucursal
        spinner_sucursal = (Spinner) findViewById(R.id.spinner_sucursal);
        if(id_sucursal.equals("1")){
            String[] letra4 = {"La Paz","Santa Cruz","Cochabamba", "Tarija"};
            //String[] letra4 = {"La Paz"};
            spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
        }else{
            if(id_sucursal.equals("2")){
                String[] letra4 = {"Santa Cruz", "La Paz", "Cochabamba", "Tarija"};
                spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
            }else {
                if(id_sucursal.equals("3")){
                    String[] letra4 = {"Cochabamba", "Santa Cruz", "La Paz", "Tarija"};
                    spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
                }else {
                    String[] letra4 = {"Tarija", "Cochabamba", "Santa Cruz", "La Paz"};
                    spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
                }
            }
        }



        //obtenemos la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-ss", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        //creamos llave unica para recibos
        String[] parts = fecha.split("-");
        id_llave_unica_recibo = parts[0]+parts[1]+parts[2]+parts[3]+id_empleado+id_sucursal;

        EditText txtMonto = (EditText)findViewById(R.id.monto);
        txtMonto.setText(parametro_total_a_pagar);



     /*   guarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrar_recibo();
            }
        });
*/
        //preguntamos si es nuevo o editar
        if(parametro_tipo_actividad.equals("Editar")){
            parametro_concepto=datos.getString("parametro_concepto");
            parametro_monto=datos.getString("parametro_monto");
            parametro_numero_cheque=datos.getString("parametro_numero_cheque");
            parametro_forma_pago=datos.getString("parametro_forma_pago");
            parametro_moneda=datos.getString("parametro_moneda");
            parametro_banco=datos.getString("parametro_banco");
            parametro_recibo_sucusal=datos.getString("parametro_recibo_sucusal");
            parametro_id_recibo=datos.getString("parametro_id_recibo");
            Toast.makeText(getApplicationContext(), "Editara datos del recibo.", Toast.LENGTH_LONG).show();
            //ponemos a la caja de texto los valores recibidos
            EditText txtConcepto = (EditText)findViewById(R.id.concepto);
            txtConcepto.setText(parametro_concepto);

            txtMonto.setText(parametro_monto);

            EditText txtNumeroCheque = (EditText)findViewById(R.id.numero_cheque);
            txtNumeroCheque.setText(parametro_numero_cheque);
            //seleccionamos los spinners de acuerdo al valor recibido
            selectValue(spinner, parametro_forma_pago);
            selectValue(spinner3, parametro_moneda);
            selectValue(spinner2, parametro_banco);
            selectValue(spinner_sucursal, parametro_recibo_sucusal);
        }

        //si cambiamos valor del combo(spinner)
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                //sacamos valores de los spinners
                spformaPago = spinner.getSelectedItem().toString();
                if(spformaPago.equals("Cheque")){
                    TextView textcargando2 = (TextView) findViewById(R.id.text_view_cheque);
                    textcargando2.setVisibility(View.VISIBLE);
                    TextView textcargando3 = (TextView) findViewById(R.id.numero_cheque);
                    textcargando3.setVisibility(View.VISIBLE);

                    TextView textcargando4 = (TextView) findViewById(R.id.text_view_banco);
                    textcargando4.setVisibility(View.VISIBLE);
                    Spinner textcargando5 = (Spinner) findViewById(R.id.banco);
                    textcargando5.setVisibility(View.VISIBLE);
                }else{
                    TextView textcargando2 = (TextView) findViewById(R.id.text_view_cheque);
                    textcargando2.setVisibility(View.GONE);
                    EditText textcargando3 = (EditText) findViewById(R.id.numero_cheque);
                    textcargando3.setVisibility(View.GONE);

                    TextView textcargando4 = (TextView) findViewById(R.id.text_view_banco);
                    textcargando4.setVisibility(View.GONE);
                    Spinner textcargando5 = (Spinner) findViewById(R.id.banco);
                    textcargando5.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
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
            //llamamos a la funcion ejecutar info que es de tipo vista, y si no hay vista poner null
            pantalla_inicio(null);
            return true;
        }
        if(id==R.id.cerrar_sesion){
            //llamamos a la funcion ejecutar info que es de tipo vista, y si no hay vista poner null
            cerrarSesion();
            //salimos de la aplicacion
            return true;
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
    public void pantalla_inicio(View view){
        super.finish();
    }


    public void cerrarSesion() {
        String comparador_master="12345678";
        final BBDD_Helper helper_master = new BBDD_Helper(getApplicationContext());
        SQLiteDatabase db_master = helper_master.getWritableDatabase();

        String selection_master = Estructura_BBDD.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD.TABLE_NAME, selection_master, selectionArgs_master);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD", Toast.LENGTH_SHORT).show();

        String selection_master_2 = Estructura_BBDD_Bancos.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_2 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Bancos.TABLE_NAME, selection_master_2, selectionArgs_master_2);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Bancos", Toast.LENGTH_SHORT).show();

        String selection_master_3 = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_3 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME, selection_master_3, selectionArgs_master_3);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Cuotas_Canceladas", Toast.LENGTH_SHORT).show();

        String selection_master_4 = Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_4 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Pre_Cuotas_Canceladas.TABLE_NAME, selection_master_4, selectionArgs_master_4);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Pre_Cuotas_Canceladas", Toast.LENGTH_SHORT).show();

        String selection_master_5 = Estructura_BBDD_Recibos.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_5 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Recibos.TABLE_NAME, selection_master_5, selectionArgs_master_5);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Recibos", Toast.LENGTH_SHORT).show();

        String selection_master_6 = Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_6 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Recibos_Pagados.TABLE_NAME, selection_master_6, selectionArgs_master_6);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Recibos_Pagados", Toast.LENGTH_SHORT).show();

    }

    private void selectValue(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }


    public void guardaDatos(View view){
        EmisionRecPedya.primerplano tarea_antes = new EmisionRecPedya.primerplano();
        tarea_antes.execute();
    }


    //llamamos al metodo que lista preamortizaciones
    class primerplano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga

            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);
            dialog2 = new ProgressDialog(EmisionRecPedya.this);
            dialog2.setMessage("Imprimiendo...");
            dialog2.setCancelable (false);
            dialog2.show();

        }

        @Override
        protected Void doInBackground(Void... params){


            mostrar_recibo();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

          EmisionRecPedya.segundoPlanoImpresion tarea_antes = new EmisionRecPedya.segundoPlanoImpresion();
          tarea_antes.execute();
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.GONE);
            dialog2.hide();


        }
    }


    private void mostrar_recibo() {
        //importamos la clase BB_DD_HELPER

        String SOAP_ACTION = "http://ws.sudseguros.com/SaveReceipt";
        String METHOD_NAME = "SaveReceipt";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil_jose_manuel/ServiceSud.asmx";

        nomcliente= parametro_nombre_cliente+" "+parametro_apellido_paterno_cliente+" "+parametro_apellido_materno_cliente;
        conceptorec = textoConcepto.getText().toString();
        moneda = spinner3.getSelectedItem().toString();
        tipopag = spinner.getSelectedItem().toString();

        numcheque = textoNumeroCheque.getText().toString();
        montocheque = textoMonto.getText().toString();
        sucursalpago = spinner_sucursal.getSelectedItem().toString();
        String[] parts2 = spinner2.getSelectedItem().toString().split("-");
        entidadfin = parts2[0];

        //enviamos parametros al ws SaveReceipt

        try {

            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

            RequestWs.addProperty("id_cliente", parametro_id_cliente);
            RequestWs.addProperty("id_sucursal", "6");
            RequestWs.addProperty("recibido_de", nomcliente);
            RequestWs.addProperty("concepto", conceptorec);
            RequestWs.addProperty("tipo_pago",tipopag);
            RequestWs.addProperty("moneda", moneda);
            RequestWs.addProperty("id_entidad_financiera", entidadfin);
            RequestWs.addProperty("cheque_numero", numcheque);
            RequestWs.addProperty("importe", montocheque);
            RequestWs.addProperty("id_empleado", id_empleado);
            RequestWs.addProperty("imei_dispositivo", "357136081571342");
            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
            RequestWs.addProperty("password_ws", "AXr53.o1");
            RequestWs.addProperty("sucursal_recibo", sucursalpago);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(RequestWs);


            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
            mensaje = "" + resultString.getProperty(1);
            //almacenamos variables recibidas del ws
            respuesta_recibido = "" + resultString.getProperty(0);
            mensaje_recibido = "" + resultString.getProperty(1);
            id_recibo_recibido = "" + resultString.getProperty(2);
            numero_recibo_recibido = "" + resultString.getProperty(3);
            anio_recibo_recibido = "" + resultString.getProperty(4);
        } catch (Exception ex) {
            mensaje = "ERROR 2: " + ex.getMessage();
        }

    }

    private void receipPrint(String id_recibo, String id_empleado, String imei_dispositivo, String usuario_ws, String password_ws) {

        String SOAP_ACTION = "http://ws.sudseguros.com/PrintReceipt";
        String METHOD_NAME = "PrintReceipt";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        String codigo = "";
        String mensaje = "";
        String id_sucursall = "";
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

        String sucursal = "";
        String sucursal_abreviatura = "";

        String datos_cabecera = "";

        String importe_final;
        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_recibo", id_recibo);
            Request.addProperty("id_empleado", id_empleado);
            Request.addProperty("imei_dispositivo", imei_dispositivo);
            Request.addProperty("usuario_ws", usuario_ws);
            Request.addProperty("password_ws", password_ws);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            //Agarra todo el Objeto
            SoapArray = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo


            codigo = SoapArray.getProperty(0).toString();
            mensaje = SoapArray.getProperty(1).toString();
            id_sucursall = SoapArray.getProperty(2).toString();
            id_recibo = SoapArray.getProperty(3).toString();
            numero_recibo = SoapArray.getProperty(4).toString();
            fecha_emision = SoapArray.getProperty(5).toString();
            hora_emision = SoapArray.getProperty(6).toString();
            ci_nit = SoapArray.getProperty(7).toString();
            cliente = SoapArray.getProperty(8).toString();
            recibido_de = SoapArray.getProperty(9).toString();
            moneda = SoapArray.getProperty(10).toString();
            importe = SoapArray.getProperty(11).toString();
            importe_bs = SoapArray.getProperty(12).toString();
            monto_literal = SoapArray.getProperty(13).toString();
            concepto = SoapArray.getProperty(14).toString();
            tipo_pago = SoapArray.getProperty(15).toString();
            cheque_banco = SoapArray.getProperty(16).toString();
            cheque_numero = SoapArray.getProperty(17).toString();
            cobrador = SoapArray.getProperty(18).toString();

            sucursal = SoapArray.getProperty(19).toString();
            sucursal_abreviatura = SoapArray.getProperty(20).toString();

            todo_pagado = SoapArray.getProperty(22).toString();
            string_cuotas = todo_pagado.toString();


            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(cheque_numero.toString().trim().isEmpty() || cheque_numero.toString().equals("anyType{}")) {
                cheque_numero="------";
            }

            resultado = "Respuesta: " + codigo;

        } catch (Exception ex) {
            resultado = "ERROR 1: " + ex.getMessage() + " " + ex.getLocalizedMessage();
        }

        connectPrint(); //Conexion con la Impresora


        if (codigo.equals("1") && zebraPrinter != null) {

            try {

                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                PrinterStatus printerStatus = printer.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {

                    resultado = "Listo para imprimir ";


                    if(moneda.equals("$us")){
                        importe_final = importe;
                    }else{
                        importe_final = importe_bs;
                    }

                    if(id_sucursall.equals("1")) {
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    }
                    else if(id_sucursall.equals("2")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 3-3416055 Fax: (591) 3-3416055^FS\n" +
                                "^FT144,104^A0N,17,16^FH\\^FDCalle La Plata # 25 - Equipetrol, Santa Cruz^FS\n" +
                                "^FT228,77^A0N,17,16^FH\\^FDSucursal Santa Cruz^FS";
                    }
                    else if(id_sucursall.equals("3")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 4-4374220 ^FS\n" +
                                "^FT124,104^A0N,17,16^FH\\^FDEdificio Torre Empresarial Torre 42 \\A2n Piso 7 Calle Papa Paulo esquina Ramón Rivero # 604^FS\n" +
                                "^FT220,77^A0N,17,16^FH\\^FDSucursal Cochabamba^FS";;
                    }
                    else if(id_sucursall.equals("5")) {
                        datos_cabecera = "^FT213,133^A0N,17,16^FH\\^FDTelf.: (591) 4 - 6672014^FS\n" +
                                "^FT19,104^A0N,17,16^FH\\^FDAvenida La Paz N\\A7 249 Edif Concordia 1ra planta, entre calles Ciro Trigo y Abaroa^FS\n" +
                                "^FT244,77^A0N,17,16^FH\\^FDSucursal Tarija^FS";
                    }else{
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    }


                    String texto = "\u0010CT~~CD,~CC^~CT~\n" +
                            "^XA~TA000~JSN^LT0^MNN^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD15^JUS^LRN^CI0^XZ\n" +
                            "^XA\n" +
                            "^MMT\n" +
                            "^PW591\n";
                    if(id_sucursall.equals(id_sucursal)) {
                        int tamano_vertical=830;
                        String[] partsaux = string_cuotas.split("CUOTA");
                        cantidad_cuotas_aux = partsaux.length - 1;
                        for (int i = 1; i <= cantidad_cuotas_aux; i++) {
                            tamano_vertical=tamano_vertical+20;
                            texto += "^LL0"+tamano_vertical+"\n";
                        }
                    }else {
                        texto+= "^LL0830\n";
                    }

                    texto+= "^LS0\n" +
                            "^FT292,237^A0N,21,21^FH\\^FD"+numero_recibo+"/"+sucursal_abreviatura+"^FS\n" +
                            "^FT221,406^A0N,21,21^FH\\^FDFORMA DE PAGO^FS\n" +
                            "^FT193,237^A0N,21,21^FH\\^FDN\\F8 Recibo^FS\n" +

                            "^FT217,133^A0N,25,24^FH\\^FDRECIBO OFICIAL PEDIDOS YA^FS\n" +
                            "^FT190,48^A0N,25,24^FH\\^FDSUDAMERICANA S.R.L^FS\n" +
                            "^FO9,251^GB574,0,1^FS\n" +
                            "^FO5,494^GB575,0,1^FS\n" +
                            "^FO7,377^GB572,0,0^FS\n" +
                            "^FO9,208^GB574,0,1^FS\n" +
                            "^FT458,277^A0N,17,16^FH\\^FD"+hora_emision+"^FS\n" +
                            "^FT415,277^A0N,17,16^FH\\^FDHora:^FS\n" +
                            "^FT65,278^A0N,17,16^FH\\^FD"+fecha_emision+"^FS\n" +
                            "^FT15,278^A0N,17,16^FH\\^FDFecha:^FS\n" +



                            "^FT15,321^A0N,21,21^FH\\^FDHe recibido de:^FS\n" +
                            "^FT158,321^A0N,21,21^FH\\^FD"+recibido_de+"^FS\n" +

                            "^FT16,361^A0N,21,21^FH\\^FDPor Concepto de:^FS\n" +
                            "^FT170,361^A0N,21,21^FH\\^FD"+concepto+"^FS\n" +

                            "^FT19,474^A0N,17,16^FH\\^FD"+tipo_pago+"^FS\n" +
                            "^FT129,473^A0N,17,16^FH\\^FD"+cheque_numero+"^FS\n" +
                            "^FT352,474^A0N,17,16^FH\\^FD"+moneda+"^FS\n" +
                            "^FT464,475^A0N,17,16^FH\\^FD"+importe_final+"^FS\n" +
                            "^FT354,442^A0N,21,21^FH\\^FDMoneda^FS\n" +
                            "^FT464,441^A0N,21,21^FH\\^FDMonto^FS\n" +
                            "^FT129,443^A0N,21,21^FH\\^FDCheque Nro^FS\n" +
                            "^FT18,442^A0N,21,21^FH\\^FDTipo^FS\n";

                    //CUOTAS PRE AMORTIZDAS
                    if(id_sucursall.equals(id_sucursal)) {

                        texto +=     "^FT18,543^A0N,17,17^FH\\^FDNro Poliza^FS\n" +
                                "^FT274,543^A0N,17,17^FH\\^FDRamo^FS\n" +
                                "^FT334,543^A0N,17,17^FH\\^FDCuota^FS\n" +
                                "^FT394,543^A0N,17,17^FH\\^FDFecha Cuota^FS\n" +
                                "^FT494,543^A0N,17,17^FH\\^FDMonto^FS\n";
                        //"^FT18,542^A0N,21,21^FH\\^FDNro^FS\n";
                        int espacio_vertical_col1 = 542;
                        int espacio_vertical_col2 = 541;
                        int espacio_vertical_col3 = 543;
                        int espacio_vertical_col4 = 542;

                        String[] parts = string_cuotas.split("CUOTA");
                        cantidad_cuotas = parts.length - 1;

                        for (int i = 1; i <= cantidad_cuotas; i++) {
                            espacio_vertical_col1 = espacio_vertical_col1 + 20;
                            espacio_vertical_col2 = espacio_vertical_col2 + 20;
                            espacio_vertical_col3 = espacio_vertical_col3 + 20;
                            espacio_vertical_col4 = espacio_vertical_col4 + 20;

                            String[] parts2 = parts[i].toString().split("\\*");
                            String no_poliza_detalle = parts2[2];
                            String no_cuota_detalle = parts2[3];
                            String monto_pagar_detalle = parts2[5];

                            String ramo = parts2[12];
                            //FECHA
                            String fecha_pago_cuota = parts2[11];
                            String[] parts3 = fecha_pago_cuota.toString().split(" ");
                            String fecha_pago_cuota_sin_hora=parts3[0];

                            cantidad_cuotas = parts.length - 1;

                            texto +=
                                    "^FT18," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD" + no_poliza_detalle + "^FS\n" +
                                            "^FT274," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD" + ramo + "^FS\n" +
                                            "^FT350," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD"+no_cuota_detalle+"^FS\n" +
                                            "^FT394," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD"+fecha_pago_cuota_sin_hora+"^FS\n" +
                                            "^FT494," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD" + monto_pagar_detalle + " " + moneda + "^FS\n";
                            //"^FT18," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD"+i+"^FS\n";
                            if (i == cantidad_cuotas) {
                                texto += "^FT16," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FDCobrador:^FS\n" +
                                        "^FT69," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FD" + cobrador + "^FS\n" +


                                        "^FT43," + (espacio_vertical_col3 + 34) + "^A0N,13,12^FH\\^FD" + monto_literal + "^FS\n" +
                                        "^FT16," + (espacio_vertical_col3 + 33) + "^A0N,13,12^FH\\^FDSon:^FS\n" +
                                        "^FT18," + (espacio_vertical_col3 + 271) + "^A0N,17,16^FH\\^FDSeguros^FS\n" +
                                        "^FT57," + (espacio_vertical_col3 + 256) + "^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                                        "^FT18," + (espacio_vertical_col3 + 243) + "^BQN,2,5\n" +
                                        "^FDMA," + ci_nit + "|" + importe_final + "|" + fecha_emision + "|" + id_recibo + "^FS\n" +//qr
                                        "^FT17," + (espacio_vertical_col3 + 255) + "^A0N,17,16^FH\\^FDNota:^FS\n" +
                                        "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                                        "^FT382," + (espacio_vertical_col3 + 188) + "^A0N,17,16^FH\\^FDFirma Cliente^FS\n" +
                                        "^PQ1,0,1,Y^XZ\n";
                            }
                        }
                    }else{
                        int espacio_vertical_col1 = 542;
                        int espacio_vertical_col2 = 541;
                        int espacio_vertical_col3 = 543;
                        int espacio_vertical_col4 = 542;

                        texto += "^FT354," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD-^FS\n" +
                                "^FT464," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD-^FS\n" +
                                "^FT18," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD-^FS\n";

                        texto += "^FT16," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FDCobrador:^FS\n" +
                                "^FT69," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FD" + cobrador + "^FS\n" +


                                "^FT43," + (espacio_vertical_col3 + 34) + "^A0N,13,12^FH\\^FD" + monto_literal + "^FS\n" +
                                "^FT16," + (espacio_vertical_col3 + 33) + "^A0N,13,12^FH\\^FDSon:^FS\n" +
                                "^FT18," + (espacio_vertical_col3 + 271) + "^A0N,17,16^FH\\^FDSeguros^FS\n" +
                                "^FT57," + (espacio_vertical_col3 + 256) + "^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                                "^FT18," + (espacio_vertical_col3 + 243) + "^BQN,2,5\n" +
                                "^FDMA," + ci_nit + "|" + importe_final + "|" + fecha_emision + "|" + id_recibo + "^FS\n" +//qr
                                "^FT17," + (espacio_vertical_col3 + 255) + "^A0N,17,16^FH\\^FDNota:^FS\n" +
                                "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                                "^FT382," + (espacio_vertical_col3 + 188) + "^A0N,17,16^FH\\^FDFirma Cliente^FS\n" +
                                "^PQ1,0,1,Y^XZ\n";
                    }
                    ///////////////////////



                    byte[] rcpt = texto.getBytes();
                    try {
                        connection.write(rcpt);
                        resultado = "Documento Impreso ";
                    } catch (ConnectionException e) {
                        resultado = "Error al Imprimir "+ e.getMessage();
                    }

                } else if (printerStatus.isPaused) {
                    resultado = "No se puede imprimir por que el dipositivo detenido.";
                } else if (printerStatus.isHeadOpen) {
                    resultado = "No se puede imprimir por que la cabecera esta abierta.";
                } else if (printerStatus.isPaperOut) {
                    resultado = "No se puede imprimir por que el papel esta afuera.";
                } else {
                    resultado = "No se puede imprimir.";
                }

            } catch (ConnectionException e) {
                e.printStackTrace();

            } catch (ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }


        }
        else {
            //resultado = "No se puede imprimir, error de Impresora 5";
        }
    }

    public void connectPrint() {
        //OBTENEMOS MAC DE LA IMPRESORA
        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);


        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                //que columnas queremos que saque nuestra consulta
                Estructura_BBDD.NOMBRE_COLUMNA9,//1 id

        };

        //COLUMNA QUE COMPARAREMOS (WHERE)
        String selection = Estructura_BBDD.NOMBRE_COLUMNA1 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { id_empleado.toString() };

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

            resultado = "Conectado a la Impresora bt " + pl;
        }

        catch (ConnectionException ex)
        {
            resultado = "No pudo conectarse a la Impresora1 " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();

        } catch(Exception ex)
        {
            resultado = "No pudo conectarse a la Impresora2  " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();
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


    class segundoPlanoImpresion extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
         /*   ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);
             dialog2 = new ProgressDialog(EmisionRecPedya.this);
            dialog2.setMessage("Imprimiendouuuuuyyy...");
            dialog2.setCancelable (false);
            dialog2.show();*/

        }

        @Override
        protected Void doInBackground(Void... params){

            String id_recibo = id_recibo_recibido;
            String id_empleado = parametro_id_cliente;
            String imei_dispositivo = "357136081571342";
            String usuario_ws = "SUdMOv1l3";
            String password_ws = "AXr53.o1";
           // Toast.makeText(this, "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();

            if (!compruebaConexion(getApplicationContext())) {
                //    Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                    finish();
            }
            else {
              // Toast.makeText(getBaseContext(), "posi ", Toast.LENGTH_SHORT).show();
               receipPrint_manual(id_recibo, id_empleado, imei_dispositivo, usuario_ws, password_ws);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
           //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
        /*    ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.GONE);
           dialog2.hide();


*/

          //  EmisionRecPedya.tercertoEnvioMail tarea_antes = new EmisionRecPedya.tercertoEnvioMail();
         //   tarea_antes.execute();


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


    private void receipPrint_manual(String id_recibo, String id_empleado, String imei_dispositivo, String usuario_ws, String password_ws) {

        String codigo = "";
        String mensaje = "";
       // String id_sucursal = "";
        String numero_recibo = "";
        String fecha_emision = "";
        String hora_emision = "";
        String ci_nit = "";
        String cliente = "";
        String recibido_de = "";
        String monedas = "";
        String tipo_monedas = "";
        String importe = "";
        String importe_bs = "";
        String monto_literal = "";
        String concepto = "";
        String tipo_pago = "";
        String cheque_banco = "";
        String cheque_numero = "";
        String cobrador = "";

        String sucursal = "";
        String sucursal_abreviatura = "";

        String datos_cabecera = "";

        String importe_final;

        connectPrint(); //Conexion con la Impresora



            //almacenamos variables que se imprimiran
            codigo = "";
            mensaje = "Impresion manual";
            id_sucursal = id_sucursal;
            id_recibo = id_recibo;
            //creamos llave unica para recibos
            //obtenemos la fecha actual
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-ss", Locale.getDefault());
            Date date = new Date();
            String fecha = dateFormat.format(date);
            String[] parts = fecha.split("-");


            //fecha actual
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());;
            Date date2 = new Date();
            String fecha2 = dateFormat2.format(date2);
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());;
            Date date3 = new Date();
            String fecha3 = dateFormat3.format(date3);


            fecha_emision = fecha2;
            hora_emision = fecha3;


            ci_nit = "";
            cliente = nomcliente;
            recibido_de = nomcliente;
            monedas = moneda;
            importe = montocheque;
            importe_bs = "";

            String literal_moneda="";
            if (monedas.equals("Bs")){
                tipo_monedas = "Bolivianos";
            }else {
                tipo_monedas = "Dolares";
            }


            concepto = conceptorec ;
            tipo_pago = tipopag;
            cheque_banco = entidadfin;
            cheque_numero =  numcheque;
            cobrador = usuario_reg;

            sucursal = id_sucursal;
            if (sucursal.equals("1")){
                sucursal_abreviatura="LPZ";
            }
            if (sucursal.equals("2")){
                sucursal_abreviatura="SCZ";
            }
            if (sucursal.equals("3")){
                sucursal_abreviatura="CBBA";
            }
            if (sucursal.equals("4")){
                sucursal_abreviatura="SRC";
            }
            if (sucursal.equals("5")){
                sucursal_abreviatura="TRJ";
            }

            numero_recibo = numero_recibo_recibido+"/"+sucursal_abreviatura;
            sucursal_abreviatura = id_sucursal;

            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(cheque_numero.toString().trim().isEmpty() || cheque_numero.toString().equals("anyType{}")) {
                cheque_numero="------";
            }

            monto_literal = convertirLetras(Integer.parseInt(importe))+" 00/100.- "+literal_moneda;


            if (zebraPrinter != null) {

                try {

                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                    PrinterStatus printerStatus = printer.getCurrentStatus();
                    if (printerStatus.isReadyToPrint) {

                        resultado = "Listo para imprimir ";


                        if (moneda.equals("$us")) {
                            importe_final = importe;
                        } else {
                            importe_final = importe_bs;
                        }

                        if (id_sucursal.equals("1")) {
                            datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                    "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                    "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                        } else if (id_sucursal.equals("2")) {
                            datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 3-3416055 Fax: (591) 3-3416055^FS\n" +
                                    "^FT144,104^A0N,17,16^FH\\^FDCalle La Plata # 25 - Equipetrol, Santa Cruz^FS\n" +
                                    "^FT228,77^A0N,17,16^FH\\^FDSucursal Santa Cruz^FS";
                        } else if (id_sucursal.equals("3")) {
                            datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 4-4374220 ^FS\n" +
                                    "^FT124,104^A0N,17,16^FH\\^FDEdificio Torre Empresarial Torre 42 \\A2n Piso 7 Calle Papa Paulo esquina Ramón Rivero # 604^FS\n" +
                                    "^FT220,77^A0N,17,16^FH\\^FDSucursal Cochabamba^FS";;
                        } else if (id_sucursal.equals("5")) {
                            datos_cabecera = "^FT213,133^A0N,17,16^FH\\^FDTelf.: (591) 4 - 6672014^FS\n" +
                                    "^FT19,104^A0N,17,16^FH\\^FDAvenida La Paz N\\A7 249 Edif Concordia 1ra planta, entre calles Ciro Trigo y Abaroa^FS\n" +
                                    "^FT244,77^A0N,17,16^FH\\^FDSucursal Tarija^FS";
                        } else {
                            datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                    "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                    "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                        }


                        String texto = "\u0010CT~~CD,~CC^~CT~\n" +
                                "^XA~TA000~JSN^LT0^MNN^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD15^JUS^LRN^CI0^XZ\n" +
                                "^XA\n" +
                                "^MMT\n" +
                                "^PW591\n" +
                                "^LL0768\n" +
                                "^LS0\n" +
                                "^FT292,237^A0N,21,21^FH\\^FD" + numero_recibo + "^FS\n" +
                                "^FT221,406^A0N,21,21^FH\\^FDFORMA DE PAGO^FS\n" +
                                "^FT193,237^A0N,21,21^FH\\^FDN\\F8 Recibo^FS\n" +

                                "^FT150,197^A0N,25,24^FH\\^FDRECIBO OFICIAL - PEDIDOS YA^FS\n" +
                                "^FT190,48^A0N,25,24^FH\\^FDSUDAMERICANA S.R.L^FS\n" +
                                "^FO9,251^GB574,0,1^FS\n" +
                                "^FO5,494^GB575,0,1^FS\n" +
                                "^FO7,377^GB572,0,0^FS\n" +
                                "^FO9,208^GB574,0,1^FS\n" +
                                "^FT458,277^A0N,17,16^FH\\^FD" + hora_emision + "^FS\n" +
                                "^FT415,277^A0N,17,16^FH\\^FDHora:^FS\n" +
                                "^FT65,278^A0N,17,16^FH\\^FD" + fecha_emision + "^FS\n" +
                                "^FT15,278^A0N,17,16^FH\\^FDFecha:^FS\n" +
                                "^FT170,361^A0N,21,21^FH\\^FD" + concepto + "^FS\n" +
                                "^FT69,538^A0N,13,12^FH\\^FD" + cobrador + "^FS\n" +
                                "^FT158,321^A0N,21,21^FH\\^FD" + recibido_de + "^FS\n" +
                                "^FT19,474^A0N,17,16^FH\\^FD" + tipo_pago + "^FS\n" +
                                "^FT129,473^A0N,17,16^FH\\^FD" + cheque_numero + "^FS\n" +
                                "^FT352,474^A0N,17,16^FH\\^FD" + monedas + "^FS\n" +
                                "^FT464,475^A0N,17,16^FH\\^FD" + importe + "^FS\n" +
                                "^FT354,442^A0N,21,21^FH\\^FDMoneda^FS\n" +
                                "^FT464,441^A0N,21,21^FH\\^FDMonto^FS\n" +
                                "^FT129,443^A0N,21,21^FH\\^FDCheque Nro^FS\n" +
                                "^FT18,442^A0N,21,21^FH\\^FDTipo^FS\n" +
                                "^FT16,537^A0N,13,12^FH\\^FDCobrador:^FS\n" +
                                "^FT16,361^A0N,21,21^FH\\^FDPor Concepto de:^FS\n" +
                                "^FT15,320^A0N,21,21^FH\\^FDHe recibido de:^FS\n" +
                                "^FT43,514^A0N,13,12^FH\\^FD" + monto_literal + tipo_monedas+ "^FS\n" +
                                "^FT16,513^A0N,13,12^FH\\^FDSon:^FS\n" +
                                "^FT18,751^A0N,17,16^FH\\^FDSeguros^FS\n" +
                                "^FT57,726^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                                "^FT18,713^BQN,2,5\n" +
                                "^FDMA," + ci_nit + "|" + importe_final + "|" + fecha_emision + "|" + id_recibo + "^FS\n" +//qr
                                "^FT17,725^A0N,17,16^FH\\^FDNota:^FS\n" +
                                "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                                "^FT382,668^A0N,17,16^FH\\^FDFirma Cliente^FS\n" +
                                "^PQ1,0,1,Y^XZ\n";


                        byte[] rcpt = texto.getBytes();
                        try {
                            connection.write(rcpt);
                            resultado = "Documento Impreso ";
                        } catch (ConnectionException e) {
                            resultado = "Error al Imprimir " + e.getMessage();
                        }

                    } else if (printerStatus.isPaused) {
                        resultado = "No se puede imprimir por que el dipositivo detenido.";
                    } else if (printerStatus.isHeadOpen) {
                        resultado = "No se puede imprimir por que la cabecera esta abierta.";
                    } else if (printerStatus.isPaperOut) {
                        resultado = "No se puede imprimir por que el papel esta afuera.";
                    } else {
                        resultado = "No se puede imprimir.";
                    }

                    resultado = "Documento impreso ";
                } catch (ConnectionException e) {
                    e.printStackTrace();
                } catch (ZebraPrinterLanguageUnknownException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }


            }
            else {
                resultado = "No se puede imprimir, error de Impresora 4";
            }
        }


    private String unidad(int numero){

        switch (numero){
            case 9:
                num = "nueve";
                break;
            case 8:
                num = "ocho";
                break;
            case 7:
                num = "siete";
                break;
            case 6:
                num = "seis";
                break;
            case 5:
                num = "cinco";
                break;
            case 4:
                num = "cuatro";
                break;
            case 3:
                num = "tres";
                break;
            case 2:
                num = "dos";
                break;
            case 1:
                if (flag == 0)
                    num = "uno";
                else
                    num = "un";
                break;
            case 0:
                num = "";
                break;
        }
        return num;
    }

    private String decena(int numero){

        if (numero >= 90 && numero <= 99)
        {
            num_letra = "noventa ";
            if (numero > 90)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 90));
        }
        else if (numero >= 80 && numero <= 89)
        {
            num_letra = "ochenta ";
            if (numero > 80)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 80));
        }
        else if (numero >= 70 && numero <= 79)
        {
            num_letra = "setenta ";
            if (numero > 70)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 70));
        }
        else if (numero >= 60 && numero <= 69)
        {
            num_letra = "sesenta ";
            if (numero > 60)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 60));
        }
        else if (numero >= 50 && numero <= 59)
        {
            num_letra = "cincuenta ";
            if (numero > 50)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 50));
        }
        else if (numero >= 40 && numero <= 49)
        {
            num_letra = "cuarenta ";
            if (numero > 40)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 40));
        }
        else if (numero >= 30 && numero <= 39)
        {
            num_letra = "treinta ";
            if (numero > 30)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 30));
        }
        else if (numero >= 20 && numero <= 29)
        {
            if (numero == 20)
                num_letra = "veinte ";
            else
                num_letra = "veinti".concat(unidad(numero - 20));
        }
        else if (numero >= 10 && numero <= 19)
        {
            switch (numero){
                case 10:

                    num_letra = "diez ";
                    break;

                case 11:

                    num_letra = "once ";
                    break;

                case 12:

                    num_letra = "doce ";
                    break;

                case 13:

                    num_letra = "trece ";
                    break;

                case 14:

                    num_letra = "catorce ";
                    break;

                case 15:

                    num_letra = "quince ";
                    break;

                case 16:

                    num_letra = "dieciseis ";
                    break;

                case 17:

                    num_letra = "diecisiete ";
                    break;

                case 18:

                    num_letra = "dieciocho ";
                    break;

                case 19:

                    num_letra = "diecinueve ";
                    break;

            }
        }
        else
            num_letra = unidad(numero);

        return num_letra;
    }

    private String centena(int numero){
        if (numero >= 100)
        {
            if (numero >= 900 && numero <= 999)
            {
                num_letra = "novecientos ";
                if (numero > 900)
                    num_letra = num_letra.concat(decena(numero - 900));
            }
            else if (numero >= 800 && numero <= 899)
            {
                num_letra = "ochocientos ";
                if (numero > 800)
                    num_letra = num_letra.concat(decena(numero - 800));
            }
            else if (numero >= 700 && numero <= 799)
            {
                num_letra = "setecientos ";
                if (numero > 700)
                    num_letra = num_letra.concat(decena(numero - 700));
            }
            else if (numero >= 600 && numero <= 699)
            {
                num_letra = "seiscientos ";
                if (numero > 600)
                    num_letra = num_letra.concat(decena(numero - 600));
            }
            else if (numero >= 500 && numero <= 599)
            {
                num_letra = "quinientos ";
                if (numero > 500)
                    num_letra = num_letra.concat(decena(numero - 500));
            }
            else if (numero >= 400 && numero <= 499)
            {
                num_letra = "cuatrocientos ";
                if (numero > 400)
                    num_letra = num_letra.concat(decena(numero - 400));
            }
            else if (numero >= 300 && numero <= 399)
            {
                num_letra = "trescientos ";
                if (numero > 300)
                    num_letra = num_letra.concat(decena(numero - 300));
            }
            else if (numero >= 200 && numero <= 299)
            {
                num_letra = "doscientos ";
                if (numero > 200)
                    num_letra = num_letra.concat(decena(numero - 200));
            }
            else if (numero >= 100 && numero <= 199)
            {
                if (numero == 100)
                    num_letra = "cien ";
                else
                    num_letra = "ciento ".concat(decena(numero - 100));
            }
        }
        else
            num_letra = decena(numero);

        return num_letra;
    }

    private String miles(int numero){
        if (numero >= 1000 && numero <2000){
            num_letram = ("mil ").concat(centena(numero%1000));
        }
        if (numero >= 2000 && numero <10000){
            flag=1;
            num_letram = unidad(numero/1000).concat(" mil ").concat(centena(numero%1000));
        }
        if (numero < 1000)
            num_letram = centena(numero);

        return num_letram;
    }

    private String decmiles(int numero){
        if (numero == 10000)
            num_letradm = "diez mil";
        if (numero > 10000 && numero <20000){
            flag=1;
            num_letradm = decena(numero/1000).concat("mil ").concat(centena(numero%1000));
        }
        if (numero >= 20000 && numero <100000){
            flag=1;
            num_letradm = decena(numero/1000).concat(" mil ").concat(miles(numero%1000));
        }


        if (numero < 10000)
            num_letradm = miles(numero);

        return num_letradm;
    }

    private String cienmiles(int numero){
        if (numero == 100000)
            num_letracm = "cien mil";
        if (numero >= 100000 && numero <1000000){
            flag=1;
            num_letracm = centena(numero/1000).concat(" mil ").concat(centena(numero%1000));
        }
        if (numero < 100000)
            num_letracm = decmiles(numero);
        return num_letracm;
    }

    private String millon(int numero){
        if (numero >= 1000000 && numero <2000000){
            flag=1;
            num_letramm = ("Un millon ").concat(cienmiles(numero%1000000));
        }
        if (numero >= 2000000 && numero <10000000){
            flag=1;
            num_letramm = unidad(numero/1000000).concat(" millones ").concat(cienmiles(numero%1000000));
        }
        if (numero < 1000000)
            num_letramm = cienmiles(numero);

        return num_letramm;
    }

    private String decmillon(int numero){
        if (numero == 10000000)
            num_letradmm = "diez millones";
        if (numero > 10000000 && numero <20000000){
            flag=1;
            num_letradmm = decena(numero/1000000).concat("millones ").concat(cienmiles(numero%1000000));
        }
        if (numero >= 20000000 && numero <100000000){
            flag=1;
            num_letradmm = decena(numero/1000000).concat(" milllones ").concat(millon(numero%1000000));
        }


        if (numero < 10000000)
            num_letradmm = millon(numero);

        return num_letradmm;
    }


    public String convertirLetras(int numero){
        num_letras = decmillon(numero);
        return num_letras;
    }



    private void enviar_email() {
        //WS MTODO ENVIAR CORREO SendEmail
        String SOAP_ACTION = "http://ws.sudseguros.com/SendEmail";
        String METHOD_NAME = "SendEmail";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil_jose_manuel/ServiceSud.asmx";

        //enviamos parametros al ws SaveReceipt

        try {

            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

            RequestWs.addProperty("id_cliente", parametro_id_cliente);
            RequestWs.addProperty("id_sucursal", id_sucursal);
            RequestWs.addProperty("id_sucursal_recibo", id_sucursal);
            RequestWs.addProperty("recibido_de", nomcliente);
            RequestWs.addProperty("concepto", conceptorec );
            RequestWs.addProperty("tipo_pago", tipopag );
            RequestWs.addProperty("moneda", moneda);
            RequestWs.addProperty("id_entidad_financiera", entidadfin);
            RequestWs.addProperty("cheque_numero", numcheque );
            RequestWs.addProperty("importe", montocheque );
            RequestWs.addProperty("id_empleado", id_empleado);
            RequestWs.addProperty("imei_dispositivo", "357136081571342");
            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
            RequestWs.addProperty("password_ws", "AXr53.o1");
            RequestWs.addProperty("sucursal_recibo", sucursalpago);
            RequestWs.addProperty("numero_recibo", numero_recibo_recibido);
            RequestWs.addProperty("nombre_cliente", nomcliente);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(RequestWs);


            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
            mensaje = "" + resultString.getProperty(1);
            //almacenamos variables recibidas del ws
            respuesta_recibido = "" + resultString.getProperty(0);
            mensaje_recibido = "" + resultString.getProperty(1);
        } catch (Exception ex) {
            mensaje = "ERROR 2: " + ex.getMessage();
        }
    }
    private void enviar_email_cliente() {
        //WS MTODO ENVIAR CORREO SendEmailClient
        String SOAP_ACTION = "http://ws.sudseguros.com/SendEmailClient";
        String METHOD_NAME = "SendEmailClient";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil_jose_manuel/ServiceSud.asmx";

        //enviamos parametros al ws SaveReceipt

        try {

            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

            RequestWs.addProperty("id_cliente", parametro_id_cliente);
            RequestWs.addProperty("id_sucursal", id_sucursal);
            RequestWs.addProperty("id_sucursal_recibo", id_sucursal);
            RequestWs.addProperty("recibido_de", nomcliente);
            RequestWs.addProperty("concepto", conceptorec);
            RequestWs.addProperty("tipo_pago",  tipopag );
            RequestWs.addProperty("moneda", moneda);
            RequestWs.addProperty("id_entidad_financiera", entidadfin);
            RequestWs.addProperty("cheque_numero", numcheque);
            RequestWs.addProperty("importe", montocheque );
            RequestWs.addProperty("id_empleado", id_empleado);
            RequestWs.addProperty("imei_dispositivo", "357136081571342");
            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
            RequestWs.addProperty("password_ws", "AXr53.o1");
            RequestWs.addProperty("sucursal_recibo", sucursalpago);
            RequestWs.addProperty("numero_recibo", numero_recibo_recibido);
            RequestWs.addProperty("nombre_cliente",nomcliente);
            RequestWs.addProperty("email_cliente", parametro_email_cliente);



            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(RequestWs);


            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
            mensaje = "" + resultString.getProperty(1);
            //almacenamos variables recibidas del ws
            respuesta_recibido = "" + resultString.getProperty(0);
            mensaje_recibido = "" + resultString.getProperty(1);
        } catch (Exception ex) {
            mensaje = "ERROR 3: " + ex.getMessage();
        }
    }
    class tercertoEnvioMail extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);*/

        }

        @Override
        protected Void doInBackground(Void... params){


            enviar_email();

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            EmisionRecPedya.cuartoEnvioMail tarea_antes = new EmisionRecPedya.cuartoEnvioMail();
            tarea_antes.execute();
        }
    }

    class cuartoEnvioMail extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);*/

        }

        @Override
        protected Void doInBackground(Void... params){


            enviar_email_cliente();

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();


        }
    }

}






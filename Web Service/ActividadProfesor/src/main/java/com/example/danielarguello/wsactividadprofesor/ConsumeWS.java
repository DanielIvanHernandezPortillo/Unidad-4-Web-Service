package com.example.danielarguello.wsactividadprofesor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ConsumeWS extends AppCompatActivity
        implements View.OnClickListener{

    private EditText etHorasAsignadas;
    private EditText etActividad;
    private EditText etMaestro;
    private EditText etPeriodo;

    private Button btGuardar;
    private Button btListar;

    private ActividadProfesor actividadProfesor = null;

    final String NAMESPACE =
            "http://ws.utng.edu.mx";
    final SoapSerializationEnvelope envelope =
            new SoapSerializationEnvelope(SoapEnvelope.VER11);
    static String URL =
            "http://192.168.0.22:8080/WSActividadProfesor/services/ActividadProfesorWS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciarComponentes();

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    private void iniciarComponentes(){
        etHorasAsignadas = (EditText)findViewById(R.id.et_horas);
        etActividad = (EditText)findViewById(R.id.et_actividad);
        etMaestro = (EditText)findViewById(R.id.et_maestro);
        etPeriodo = (EditText)findViewById(R.id.et_periodo);
        btGuardar = (Button) findViewById(R.id.bt_guardar);
        btListar = (Button)findViewById(R.id.bt_listar);
        btGuardar.setOnClickListener(this);
        btListar.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consume_w, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== btGuardar.getId()){
            try {
                if (getIntent().getExtras().getString("accion")
                        .equals("modificar")) {
                    TareaWSActualizacion tarea = new TareaWSActualizacion();
                    tarea.execute();
                }

            } catch (Exception e) {
                //Cuando no se haya mandado una accion por defecto es insertar.
                TareaWSInsercion tarea = new TareaWSInsercion();
                tarea.execute();
            }
        }
        if (btListar.getId() == v.getId()) {
            startActivity(new Intent(ConsumeWS.this, ListaActividadProfesor.class));
        }

    }

    private class TareaWSInsercion extends
            AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            final String METHOD_NAME = "addActividadProfesor";
            final String SOAP_ACTION = NAMESPACE +"/"+METHOD_NAME;

            SoapObject request =
                    new SoapObject(NAMESPACE, METHOD_NAME);

            actividadProfesor = new ActividadProfesor();
            actividadProfesor.setProperty(0, 0);
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("actividadProfesor");
            info.setValue(actividadProfesor);
            info.setType(actividadProfesor.getClass());
            request.addProperty(info);
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "ActividadProfesor", ActividadProfesor.class);

            /* Para serializar flotantes y otros tipos no cadenas o enteros*/
            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte  = new HttpTransportSE(URL);
            try{
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive response =
                        (SoapPrimitive)envelope.getResponse();
                String res = response.toString();
                if(!res.equals("1")){
                    result = false;
                }

            }catch (Exception e){
                Log.e("Error ", e.getMessage());
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(getApplicationContext(),
                        "Registro exitoso.",
                        Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),
                        "Error al insertar.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class TareaWSActualizacion extends
            AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "editActividadProfesor";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            actividadProfesor = new ActividadProfesor();
            actividadProfesor.setProperty(0, getIntent().getExtras().getString("valor0"));
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("actividadProfesor");
            info.setValue(actividadProfesor);
            info.setType(actividadProfesor.getClass());

            request.addProperty(info);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "ActividadProfesor", actividadProfesor.getClass());

            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml = (SoapPrimitive) envelope
                        .getResponse();
                String res = resultado_xml.toString();

                if (!res.equals("1")) {
                    result = false;
                }

            } catch (HttpResponseException e) {
                Log.e("Error HTTP", e.toString());
            } catch (IOException e) {
                Log.e("Error IO", e.toString());
            } catch (XmlPullParserException e) {
                Log.e("Error XmlPullParser", e.toString());
            }

            return result;

        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                limpiar();
                Toast.makeText(getApplicationContext(), "Actualizado OK",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Error al actualizar",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void obtenerDatos(){
        actividadProfesor.setProperty(1, Integer.parseInt(
                etHorasAsignadas.getText().toString()));
        actividadProfesor.setProperty(2, etActividad.getText().toString());
        actividadProfesor.setProperty(3, etMaestro.getText().toString());
        actividadProfesor.setProperty(4, Integer.parseInt(
                etPeriodo.getText().toString()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle datosRegreso = this.getIntent().getExtras();
        try {
            Log.i("Dato", datosRegreso.getString("valor4"));

            etHorasAsignadas.setText(datosRegreso.getString("valor1"));
            etActividad.setText(datosRegreso.getString("valor2"));
            etMaestro.setText(datosRegreso.getString("valor3"));
            etPeriodo.setText(datosRegreso.getString("valor4"));


        } catch (Exception e) {
            Log.e("Error al Recargar", e.toString());
        }

    }
    private void limpiar() {
        etHorasAsignadas.setText("");
        etActividad.setText("");
        etMaestro.setText("");
        etPeriodo.setText("");
    }


}
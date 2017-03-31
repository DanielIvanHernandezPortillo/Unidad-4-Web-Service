package com.example.danielarguello.wsmovie;

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
import android.widget.ToggleButton;

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

    private EditText etName;
    private EditText etSinopsis;
    private EditText etPrice;
    private ToggleButton tgType;

    private Button btSave;
    private Button btList;

    private Movie movie = null;

    final String NAMESPACE =
            "http://ws.utng.edu.mx";
    final SoapSerializationEnvelope envelope =
            new SoapSerializationEnvelope(SoapEnvelope.VER11);
    static String URL =
            "http://192.168.24.157:8080/WSMovie/services/MovieWS";
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
        etName = (EditText)findViewById(R.id.et_name);
        etSinopsis = (EditText)findViewById(R.id.et_sinopsis);
        etPrice = (EditText)findViewById(R.id.et_price);
        tgType = (ToggleButton)findViewById(R.id.tb_categoria);
        btSave = (Button) findViewById(R.id.bt_guardar);
        btList = (Button)findViewById(R.id.bt_listar);
        btSave.setOnClickListener(this);
        btList.setOnClickListener(this);

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
        if(v.getId()== btSave.getId()){
            try {
                if (getIntent().getExtras().getString("accion")
                        .equals("modify")) {
                    TaskWSUpdate update = new TaskWSUpdate();
                    update.execute();
                }

            } catch (Exception e) {
                //Cuando no se haya mandado una accion por defecto es insertar.
                TaskWSInsert tarea = new TaskWSInsert();
                tarea.execute();
            }
        }
        if (btList.getId() == v.getId()) {
            startActivity(new Intent(ConsumeWS.this, ListMovie.class));
        }

    }

    private class TaskWSInsert extends
            AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            final String METHOD_NAME = "addMovie";
            final String SOAP_ACTION = NAMESPACE +"/"+METHOD_NAME;

            SoapObject request =
                    new SoapObject(NAMESPACE, METHOD_NAME);

            movie = new Movie();
            movie.setProperty(0, 0);
            getDate();

            PropertyInfo info = new PropertyInfo();
            info.setName("movie");
            info.setValue(movie);
            info.setType(movie.getClass());
            request.addProperty(info);
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "Movie", Movie.class);

            /* Para serializar flotantes y otros tipos no cadenas o enteros*/
            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transport  = new HttpTransportSE(URL);
            try{
                transport.call(SOAP_ACTION, envelope);
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
                        "Successful registration.",
                        Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),
                        "Error inserting.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class TaskWSUpdate extends
            AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "editMovie";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            movie = new Movie();
            movie.setProperty(0, getIntent().getExtras().getString("value0"));
            getDate();

            PropertyInfo info = new PropertyInfo();
            info.setName("movie");
            info.setValue(movie);
            info.setType(movie.getClass());

            request.addProperty(info);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "Movie", movie.getClass());

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
                cliean();
                Toast.makeText(getApplicationContext(), "Update",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Error updating",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void getDate(){
        movie.setProperty(1, etName.getText().toString());
        movie.setProperty(2, etSinopsis.getText().toString());
        movie.setProperty(4, Float.parseFloat(
                etPrice.getText().toString()));

        if(tgType.isChecked()){
            movie.setProperty(3,2);
        }else{
            movie.setProperty(3,1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle datosRegreso = this.getIntent().getExtras();
        try {
            Log.i("Dato", datosRegreso.getString("value4"));

            etName.setText(datosRegreso.getString("value1"));
            etSinopsis.setText(datosRegreso.getString("value2"));
            etPrice.setText(datosRegreso.getString("value4"));
            if (datosRegreso.getString("value3").equals("1")) {
                tgType.setChecked(false);
            } else {
                tgType.setChecked(true);
            }
        } catch (Exception e) {
            Log.e("Error loading", e.toString());
        }

    }
    private void cliean() {
        etName.setText("");
        etSinopsis.setText("");
        tgType.setText("");
        etPrice.setText("");
    }


}


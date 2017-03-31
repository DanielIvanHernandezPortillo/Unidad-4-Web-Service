package com.example.danielarguello.wsmovie;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by DANIEL  on 29/03/2017.
 */

public class ListMovie extends ListActivity {

    final String NAMESPACE = "http://ws.utng.edu.mx";

    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
            SoapEnvelope.VER11);

    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private int idSelect;
    private int posicionSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWSConsult consulta = new TaskWSConsult();
        consulta.execute();
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_modify:

                Movie movie = movies.get(posicionSeleccionado);
                Bundle bundleMovie = new Bundle();
                for (int i = 0; i < movie.getPropertyCount(); i++) {
                    bundleMovie.putString("valor" + i, movie.getProperty(i)
                            .toString());
                }
                bundleMovie.putString("accion", "modify");
                Intent intent = new Intent(ListMovie.this, ConsumeWS.class);
                intent.putExtras(bundleMovie);
                startActivity(intent);

                return true;
            case R.id.item_remove:
                TaskSWDelete remove = new TaskSWDelete();
                remove.execute();

                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.menu_regresar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_regresar:
                startActivity(new Intent(ListMovie.this, ConsumeWS.class));
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    // Tarea Asíncrona para llamar al WS
    // de consulta en segundo plano
    private class TaskWSConsult extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "getMovies";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            movies.clear();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(ConsumeWS.URL);

            try {
                transporte.call(SOAP_ACTION, envelope);

                Vector<SoapObject> response = (Vector<SoapObject>) envelope
                        .getResponse();

                if (response != null) {

                    for (SoapObject objSoap : response) {
                        Movie movie = new Movie();

                        movie.setProperty(0, Integer.parseInt(objSoap
                                .getProperty("id").toString()));
                        movie.setProperty(1, objSoap.getProperty("name")
                                .toString());
                        movie.setProperty(2, objSoap.getProperty("sinopsis")
                                .toString());
                        movie.setProperty(
                                3,
                                Integer.parseInt(objSoap.getProperty(
                                        "type").toString()));
                        movie.setProperty(4, Float.parseFloat(objSoap
                                .getProperty("price").toString()));

                        movies.add(movie);
                    }
                }

            } catch (XmlPullParserException e) {
                Log.e("Error XMLPullParser", e.toString());
                result = false;
            } catch (HttpResponseException e) {
                Log.e("Error HTTP", e.toString());
                result = false;
            } catch (IOException e) {
                Log.e("Error IO", e.toString());
                result = false;
            } catch (ClassCastException e) {

                //Enviará aquí cuando exista un solo registro en la base.
                try {
                    SoapObject objSoap = (SoapObject) envelope.getResponse();
                    Movie movie = new Movie();
                    movie.setProperty(0, Integer.parseInt(objSoap.getProperty(
                            "id").toString()));
                    movie.setProperty(1, objSoap.getProperty("name")
                            .toString());
                    movie.setProperty(2, objSoap.getProperty("sinopsis")
                            .toString());
                    movie.setProperty(3, Integer.parseInt(objSoap.getProperty(
                            "type").toString()));
                    movie.setProperty(4, Float.parseFloat(objSoap.getProperty(
                            "price").toString()));

                    movies.add(movie);
                } catch (SoapFault e1) {
                    Log.e("Error SoapFault", e.toString());
                    result = false;
                }
            }

            return result;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                final String[] datos = new String[movies.size()];
                for (int i = 0; i < movies.size(); i++) {
                    datos[i] = movies.get(i).getProperty(0) + " - "
                            + movies.get(i).getProperty(1);
                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                        ListMovie.this,
                        android.R.layout.simple_list_item_1, datos);
                setListAdapter(adaptador);
            } else {
                Toast.makeText(getApplicationContext(), "No data found.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(getListView().getAdapter().getItem(info.position)
                .toString());
        idSelect = (Integer) movies.get(info.position).getProperty(0);
        posicionSeleccionado = info.position;

        inflater.inflate(R.menu.menu_contextual, menu);

    }



    private class TaskSWDelete extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "removeMovie";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", idSelect);

            envelope.setOutputSoapObject(request);
            HttpTransportSE transport = new HttpTransportSE(ConsumeWS.URL);
            try {
                transport.call(SOAP_ACTION, envelope);
                SoapPrimitive result_xml = (SoapPrimitive) envelope
                        .getResponse();
                String res = result_xml.toString();

                if (!res.equals("0"))
                    result = true;
            } catch (Exception e) {
                Log.e("Error", e.toString());
                result = false;
            }

            return result;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(getApplicationContext(),
                        "Delete", Toast.LENGTH_SHORT).show();
                TaskWSConsult consult = new TaskWSConsult();
                consult.execute();
            } else {
                Toast.makeText(getApplicationContext(), "Error deleting",
                        Toast.LENGTH_SHORT).show();

            }

        }
    }



}
package com.example.danielarguello.wsactividadprofesor;

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
 * Created by DANIEL on 30/03/2017.
 */

public class ListaActividadProfesor extends ListActivity {

    final String NAMESPACE = "http://ws.utng.edu.mx";

    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
            SoapEnvelope.VER11);

    private ArrayList<ActividadProfesor> actividadProfesores = new ArrayList<ActividadProfesor>();
    private int idSelect;
    private int posicionSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TareaWSConsulta consulta = new TareaWSConsulta();
        consulta.execute();
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_modificar:

                ActividadProfesor actividadProfesor = actividadProfesores.get(posicionSeleccionado);
                Bundle bundleProfesor = new Bundle();
                for (int i = 0; i < actividadProfesor.getPropertyCount(); i++) {
                    bundleProfesor.putString("valor" + i, actividadProfesor.getProperty(i)
                            .toString());
                }
                bundleProfesor.putString("accion", "modificar");
                Intent intent = new Intent(ListaActividadProfesor.this, ConsumeWS.class);
                intent.putExtras(bundleProfesor);
                startActivity(intent);

                return true;
            case R.id.item_eliminar:
                TareaWSEliminar eliminar = new TareaWSEliminar();
                eliminar.execute();

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
                startActivity(new Intent(ListaActividadProfesor.this, ConsumeWS.class));
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    // Tarea Asíncrona para llamar al WS
    // de consulta en segundo plano
    private class TareaWSConsulta extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "getActividadProfesores";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            actividadProfesores.clear();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(ConsumeWS.URL);

            try {
                transporte.call(SOAP_ACTION, envelope);

                Vector<SoapObject> response = (Vector<SoapObject>) envelope
                        .getResponse();

                if (response != null) {

                    for (SoapObject objSoap : response) {
                        ActividadProfesor actividadProfesor = new ActividadProfesor();

                        actividadProfesor.setProperty(0, Integer.parseInt(objSoap
                                .getProperty("id").toString()));
                        actividadProfesor.setProperty(1, Integer.parseInt(objSoap
                                .getProperty("horasAsignadas").toString()));
                        actividadProfesor.setProperty(2, objSoap.getProperty("actividad")
                                .toString());
                        actividadProfesor.setProperty(3, objSoap.getProperty("maestro")
                                .toString());
                        actividadProfesor.setProperty(4,Integer.parseInt(objSoap
                                .getProperty("periodo").toString()));

                        actividadProfesores.add(actividadProfesor);
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
                    ActividadProfesor actividadProfesor = new ActividadProfesor();

                    actividadProfesor.setProperty(0, Integer.parseInt(objSoap
                            .getProperty("id").toString()));
                    actividadProfesor.setProperty(1, Integer.parseInt(objSoap
                            .getProperty("horasAsignadas").toString()));
                    actividadProfesor.setProperty(2, objSoap.getProperty("actividad")
                            .toString());
                    actividadProfesor.setProperty(3, objSoap.getProperty("maestro")
                            .toString());
                    actividadProfesor.setProperty(4,Integer.parseInt(objSoap
                            .getProperty("periodo").toString()));

                    actividadProfesores.add(actividadProfesor);
                } catch (SoapFault e1) {
                    Log.e("Error SoapFault", e.toString());
                    result = false;
                }
            }

            return result;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                final String[] datos = new String[actividadProfesores.size()];
                for (int i = 0; i < actividadProfesores.size(); i++) {
                    datos[i] = actividadProfesores.get(i).getProperty(0) + " - "
                            + actividadProfesores.get(i).getProperty(1);
                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                        ListaActividadProfesor.this,
                        android.R.layout.simple_list_item_1, datos);
                setListAdapter(adaptador);
            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron datos.",
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
        idSelect = (Integer) actividadProfesores.get(info.position).getProperty(0);
        posicionSeleccionado = info.position;

        inflater.inflate(R.menu.menu_contextual, menu);

    }



    private class TareaWSEliminar extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "removeActividadProfesor";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", idSelect);

            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(ConsumeWS.URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive resultado_xml = (SoapPrimitive) envelope
                        .getResponse();
                String res = resultado_xml.toString();

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
                        "Eliminado", Toast.LENGTH_SHORT).show();
                TareaWSConsulta consulta = new TareaWSConsulta();
                consulta.execute();
            } else {
                Toast.makeText(getApplicationContext(), "Error al eliminar",
                        Toast.LENGTH_SHORT).show();

            }

        }
    }



}

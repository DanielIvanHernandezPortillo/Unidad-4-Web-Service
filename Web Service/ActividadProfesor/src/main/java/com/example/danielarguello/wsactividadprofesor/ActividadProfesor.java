package com.example.danielarguello.wsactividadprofesor;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by DANIEL  on 30/03/2017.
 */

public class ActividadProfesor implements KvmSerializable {
    private int id;
    private int horasAsignadas;
    private String actividad;
    private String maestro;
    private int periodo;

    public ActividadProfesor(int id, int horasAsignadas, String actividad, String maestro, int periodo) {
        this.id = id;
        this.horasAsignadas = horasAsignadas;
        this.actividad = actividad;
        this.maestro = maestro;
        this.periodo = periodo;
    }

    public ActividadProfesor() {

        this(0,0,"","",0);
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return id;
            case 1:
                return horasAsignadas;
            case 2:
                return actividad;
            case 3:
                return maestro;
            case 4:
                return periodo;
        }

        return  null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                id =Integer.parseInt(o.toString());
                break;
            case 1:
                horasAsignadas = Integer.parseInt(o.toString());
                break;
            case 2:
                actividad = o.toString();
                break;
            case 3:
                maestro = o.toString();
                break;
            case 4:
                periodo = Integer.parseInt(o.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "id";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "horasAsignadas";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "actividad";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "maestro";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "periodo";
                break;
            default:
                break;
        }


    }
}

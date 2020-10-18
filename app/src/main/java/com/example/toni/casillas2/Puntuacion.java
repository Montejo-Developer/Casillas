package com.example.toni.casillas2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Puntuacion {

    @SerializedName("indice")
    @Expose
    private String indice;
    @SerializedName("Tocs")
    @Expose
    private String tocs;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("cordX")
    @Expose
    private String cordX;
    @SerializedName("cordY")
    @Expose
    private String cordY;

    public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    public String getTocs() {
        return tocs;
    }

    public void setTocs(String tocs) {
        this.tocs = tocs;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCordX() {
        return cordX;
    }

    public void setCordX(String cordX) {
        this.cordX = cordX;
    }

    public String getCordY() {
        return cordY;
    }

    public void setCordY(String cordY) {
        this.cordY = cordY;
    }



}
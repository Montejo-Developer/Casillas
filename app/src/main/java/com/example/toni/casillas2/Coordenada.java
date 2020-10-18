package com.example.toni.casillas2;

/**
 * Created by toni on 01/01/2018.
 */



// La latitud es la coordenada Y (ecuador)
// La longitud es la coordenada X (meridianos)
public class Coordenada {
    double longitud;
    double latitud;

    public Coordenada(double longitud, double latitud) {
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    @Override
    public String toString() {
        return "Coordenada{" +
                "longitud=" + longitud +
                ", latitud=" + latitud +
                '}';
    }

    public String latitudToString(){
        return latitud + "";
    }

    public  String longitudToString(){
        return longitud + "";
    }
}

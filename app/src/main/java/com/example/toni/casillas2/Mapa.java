package com.example.toni.casillas2;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    /*Variables para volley*/
    private ProgressDialog dialog = null;
    private ProgressDialog dialog1 = null;
    String url ="http://tonidefez.000webhostapp.com/obtenerpunt.php";
    RequestQueue mRequestQueu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        dialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        dialog.setMessage("Loading...");
        dialog.show();

        dialog1 = new ProgressDialog(this);
        // Showing progress dialog before making http request
        dialog1.setMessage("Colocando puntos en el mapa");



        mRequestQueu = VolleySingleton.getInstance().getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /*TODO cuando no hay fotos salta excpecion controlar en el php*/
                if(response.equals("[null]"))//el usuario no existe en la base de datos
                {
                    dialog.dismiss();
                }
                else {
                    String json = response;
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    /*Consiguiendo las fotos del servidor*/
                    List<Puntuacion> posts = Arrays.asList(gson.fromJson(response, Puntuacion[].class));
                    //resultado.setText(posts.get(0).toString());
                    dialog.dismiss();

                    dialog1.show();

                    for(int i=0 ;i<posts.size();i++)
                    {
                        addMarcador(posts.get(i));
                    }
                  dialog1.dismiss();
             }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fallo", "onErrorResponse: ");
            }

        });
        mRequestQueu.add(stringRequest);
   /*Aumentando el tiempo limite para conexiones lentas*/
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueu.add(stringRequest);
    }




    public void addMarcador(Puntuacion t)
    {

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.valueOf(t.getCordX()), Double.valueOf(t.getCordY()));
        Marker melbourne = mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Melbourne"));
        melbourne.showInfoWindow();


    }
}

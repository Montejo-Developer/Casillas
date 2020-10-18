package com.example.toni.casillas2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class GameField extends Activity  implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{
    //colores
    private static final int[] colors = new int[]{
            R.drawable.ic_1c,
            R.drawable.ic_2c,
            R.drawable.ic_3c,
            R.drawable.ic_4c,
            R.drawable.ic_5c,
            R.drawable.ic_6c
    };
    //numeros
    private static final int[] numbers = new int[]{
            R.drawable.ic_1n,
            R.drawable.ic_2n,
            R.drawable.ic_3n,
            R.drawable.ic_4n,
            R.drawable.ic_5n,
            R.drawable.ic_6n
    };
    //mantener el array que el usuario hay decidido utilizar
    private int[] pictures = null;
    // Número máximo de celdas horizontales y verticales
    private int topTileX = 3;
    private int topTileY = 3;
    // Número máximo de elementos a utilizar
    private int topElements = 2;
    // Si ha seleccionado o no usar sonido y vibración
    private boolean hasSound = false;
    private boolean hasVibration = false;
    // Array con los identificadores de las celdas cuando se añadan al layout,
// para poder recuperarlos durante la partida
    private int ids[][] = null;
    // Array para guardar los valores de los índices de cada una de las celdas.
// se utilizará para agilizar la comprobación de si la partida ha acabado o no.
    private int values[][] = null;
    // Contador con el número de pulsaciones que ha realizado el jugador
    private int numberOfClicks = 0;
    // Para reproducir un sonido cuando el usuario pulse una celda
    private MediaPlayer mp = null;
    // Para hacer vibrar el dispositivo cuando el usuario pulse una celda
    private Vibrator vibratorService = null;
    // Mostrará en pantalla el las veces que el usuario ha pulsado una celda
    private TextView tvNumberOfClicks = null;

    SharedPreferences prefs;

    //para la geolocalizacion
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private Coordenada coordenadaFoto;

    //variables para la subida por volley
    RequestQueue mRequestQueu;
    String url ="http://tonidefez.000webhostapp.com/insertarpunt.php";
    private ProgressDialog dialog = null;


    private String usuario;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);
        vibratorService = (Vibrator) (getSystemService(Service.VIBRATOR_SERVICE));

        tvNumberOfClicks = (TextView) findViewById(R.id.clicksTxt);
        //obtención de parámetros de configuración
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ObtenerConfiguracion();

        //limpiar el tablero
        LinearLayout ll = (LinearLayout) findViewById(R.id.fieldLandscape);
        ll.removeAllViews();
        //obtención de tamaño de pantalla
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / topTileX;
        int height = (dm.heightPixels - 200) / topTileY;
        //inicialización de arrays
        ids = new int[topTileX][topTileY];
        values = new int[topTileX][topTileY];
        //inicialización de números aleatorio
        Random r = new Random(System.currentTimeMillis());
        int tilePictureToShow = r.nextInt(topElements);
        // crear celdas
        int ident = 0;

        for (int i = 0; i < topTileY; i++) {
            LinearLayout l2 = new LinearLayout(this);
            l2.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < topTileX; j++) {
                tilePictureToShow = r.nextInt(topElements);
                // guardamos la trama a mostrar
                values[j][i] = tilePictureToShow;
                TileView tv = new TileView(this.getApplicationContext(), j, i, topElements,
                        tilePictureToShow, pictures[tilePictureToShow]);
                ident++;
                // se asigna un identificador al objeto creado
                tv.setId(ident);
                // se guarda el identificador en una matriz
                ids[j][i] = ident;
                tv.setHeight(height);
                tv.setWidth(width);
                tv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        hasClick(((TileView) view).x, ((TileView) view).y);
                        numberOfClicks++;
                        tvNumberOfClicks.setText("Pulsaciones:" + numberOfClicks);
                    }
                });
                l2.addView(tv);
            }
            ll.addView(l2);
        }
        // cronómetro
        Chronometer t = (Chronometer) findViewById(R.id.Chronometer);
        t.start();

        //iniciando los objetos de coordenada
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Subiendo puntuacion.");
        dialog.setCancelable(false);
    }




    private void ObtenerConfiguracion() {
        String columna = prefs.getString("element_X", "3");
        topTileX = Integer.parseInt(columna);

        String filas = prefs.getString("element_Y", "3");
        topTileY = Integer.parseInt(filas);

        String tramas = prefs.getString("element_trama", "3");
        topElements = Integer.parseInt(tramas);

        usuario = prefs.getString("usuario","invitado");

        String colores_num = prefs.getString("list_color_num", "colores");
        if (colores_num.contains("c")) {
            pictures = colors;
        } else {
            pictures = numbers;
        }

        hasVibration = prefs.getBoolean("ck_vib", true);
        hasSound = prefs.getBoolean("ck_son", true);
        String cancion_elegida =
                prefs.getString("example_list", "musica1");
        iniciarMusica(cancion_elegida);
    }

    private void iniciarMusica(String cancion_elegida) {

        if (cancion_elegida.contains("musica1")) {
            mp = MediaPlayer.create(this, R.raw.musica1);
        } else if (cancion_elegida.contains(("musica2"))) {
            mp = MediaPlayer.create(this, R.raw.musica2);
        } else {
            mp = MediaPlayer.create(this, R.raw.musica3);
        }
        mp.setLooping(true);
        mp.start();
        mp.pause();
    }

    @Override
    protected void onPause() {
        mp.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mp.seekTo(0);
        mp.start();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }





    private boolean esEsquina(int x, int y) {
        return ((x == 0 && y == 0)
                || (x == 0 && y == ids[0].length - 1)
                || (x == ids.length - 1 && y == 0)
                || (x == ids.length - 1 && y == ids[0].length - 1)
        );
    }

    private void hasClick(int x, int y) {


        Log.d("Click", "hasClick: asdf");
        //reproducirmos el sonido
        SoundPool soundPool;
        soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
        int touch = soundPool.load(this.getApplicationContext(), R.raw.touch, 0);
        soundPool.play(touch, 1, 1, 1, 0, 1);
        if(esEsquina(x,y))
        {
            for(int i=-1;i<=1;i++)
            {
                for(int j=-1;j<=1;j++)
                {
                    if((x+i>=0 && x+i<ids.length  ) &&(y+j>=0 && y+j<ids[0].length))
                    {
                        IncrementarValor(x+i,y+j);
                    }
                }
            }
        }
        else {
            //cambio el elemento actual
            IncrementarValor(x, y);
            //cambio en el ejeX
            for (int i = -1; i <= 1; i++) {
                if (x + i >= 0 && x + i < ids.length && i != 0) {
                    IncrementarValor(x + i, y);
                }
            }
            //cambio en el ejeY
            for (int i = -1; i <= 1; i++) {
                if (y + i >= 0 && y + i < ids[0].length && i != 0) {
                    IncrementarValor(x, y + i);
                }
            }
        }

        if(ComprobarPartidaTerminada())
        {
            coordenadaFoto = obtenerCoordenadas();
            Toast.makeText(this,"Fin de la partida!!!"+ coordenadaFoto.latitudToString(),
                    Toast.LENGTH_LONG).show();

            //Enviar subida por volley
            subirPuntuacion();


            //volver a aplicacion anterior


        }

    }

    private void subirPuntuacion() {

        dialog.show();
        //CREO EL OBJETO QUE VOY A INSERTAR
        //CON SUS DATOS
        JSONObject obj = new JSONObject();
        try {
            obj.put("nombre", usuario.toString().trim());
            obj.put("tocs",String.valueOf(numberOfClicks).toString().trim() );
            obj.put("cordX",coordenadaFoto.latitudToString().toString().trim());
            obj.put("cordY",coordenadaFoto.longitudToString().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("click","he pulsado");

        mRequestQueu = VolleySingleton.getInstance().getRequestQueue();
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url,
                obj //EL OBJETO QUE HE CREADO ANTES
                , new Response.Listener<JSONObject >() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                dialog.dismiss();
                finDePartida();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.d("Error.Response", error.toString());

            }
        })
        {
        };

        /*Aumentando el tiempo limite para conexiones lentas*/
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueu.add(stringRequest);



    }

    private void finDePartida() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean ComprobarPartidaTerminada() {

        boolean todos_iguales = true;
        int primerId = ids[0][0];
        TileView primero =(TileView) findViewById(primerId);
        int valorComprobante=primero.getIndex();
        for (int i = 0; i < topTileY  && todos_iguales; i++) {

            for (int j = 0; j < topTileX && todos_iguales; j++) {
                int idActual = ids[i][j];
                TileView actual =(TileView) findViewById(idActual);
                int valorActual=actual.getIndex();
                if(valorActual!=valorComprobante)
                {
                    todos_iguales=false;
                }
            }
        }
        return todos_iguales;
    }


    private void IncrementarValor(int x,int y)
    {
        int idActual = ids[x][y];
        TileView actual =(TileView) findViewById(idActual);
        int siguientImagen=actual.getNewIndex();

        actual.setBackground(pictures[siguientImagen]);

    }

    /*******************************************/

    /*METODOS PARA LA GEOLOCALIZACION
    * */
    /********************************************/

    public Coordenada obtenerCoordenadas() {
        Coordenada coordenada = new Coordenada(mLocation.getLongitude(), mLocation.getLatitude());
        Log.v("##COORDENADA", "Longitud: " + coordenada.getLongitud() + ", Latitud: " + coordenada.getLatitud());

        return coordenada;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("##CONNECTION", "Connection Suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("##CONNECTION", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        Log.v("##LOCATION", msg);
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    protected void startLocationUpdates() {

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }



    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }







}
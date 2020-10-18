package com.example.toni.casillas2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,Autor_info.OnFragmentInteractionListener,
        Tutorial_info.OnFragmentInteractionListener
            {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean fragmentTransaction = false;
         fragment =null;
        if (id == R.id.nav_slideshow) {
            Intent i = new Intent(this, GameField.class );
            startActivity(i);
            Log.i("GameField","Entro opcion juego");
        } else if (id == R.id.nav_manage) {

            Intent i = new Intent(this, SettingsActivity.class );
            startActivity(i);
            Log.i("NavigationDrawer","Entro opcion nav_manage");
        }else if(id==R.id.help)
        {
            fragment = new Autor_info();
            fragmentTransaction=true;
        }
        else if(id==R.id.autor)
        {
            fragment = new Tutorial_info();
            fragmentTransaction=true;
        }
        else if(id == R.id.mapa)
        {
            Intent i = new Intent(this, Mapa.class );
            startActivity(i);
            Log.i("NavigationDrawer","Entro opcion nav_manage");
        }

        //si se ha activado un fragment lo lanzamos
        if(fragmentTransaction)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main,fragment)
                    .commit();

            item.setChecked(true);
            //cambia el titulo del la barra
            getSupportActionBar().setTitle(item.getTitle());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
                @Override
                public void onFragmentInteraction(Uri uri) {


                }
            }

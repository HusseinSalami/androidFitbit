package com.example.generals.fitbitmoniteringapplicationfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MyPreferences="MyPrefs";
    public static final String toRegister="my_first_time";
    public static final String imagePath="profilePath";

    Context myContext;
    SharedPreferences sharedPreferences;

    Bundle bundle=new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myContext=getApplicationContext();

        sharedPreferences=getSharedPreferences(MyPreferences, 0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //inflate header into navigation drawer;
        View header= LayoutInflater.from(this).inflate(R.layout.nav_header_main,null);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(header);
        navigationView.setNavigationItemSelectedListener(this);

        TextView navUsername=(TextView)navigationView.findViewById(R.id.nav_username_id);
        navUsername.setText(" Username");
        ImageView navImage=(ImageView)navigationView.findViewById(R.id.profile);
        navImage.setImageResource(R.mipmap.ic_launcher);


        if(sharedPreferences.getBoolean(toRegister,true))
        {
            Toast.makeText(MainActivity.this, "first time", Toast.LENGTH_SHORT).show();
            RegisterFragment register_fragment=new RegisterFragment();
            FragmentManager main_manager=getSupportFragmentManager();

            bundle.putBoolean("premiere",true);
            register_fragment.setArguments(bundle);

            main_manager.beginTransaction().replace(R.id.container, register_fragment).commit();

            sharedPreferences.edit().putBoolean(toRegister, false).commit();
        }

        else
        {
            MainFragment mainFragment=new MainFragment();
            FragmentManager main_manager=getSupportFragmentManager();

            main_manager.beginTransaction().replace(R.id.container,mainFragment).commit();

            Toast.makeText(MainActivity.this, "Not first time", Toast.LENGTH_SHORT).show();
        }
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if(id==R.id.history_id)
        {
            HistoryFragment h=new HistoryFragment();

            FragmentManager h_manager=getSupportFragmentManager();

            h_manager.beginTransaction().replace(R.id.container,h).addToBackStack(null).commit();

        }

        if(id==R.id.edit_profile_id)
        {

                RegisterFragment a=new RegisterFragment();

                FragmentManager main_manager=getSupportFragmentManager();

            main_manager.beginTransaction().replace(R.id.container,a).addToBackStack(null).commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

}

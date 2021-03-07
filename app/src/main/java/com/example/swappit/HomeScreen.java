package com.example.swappit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swappit.Fragment.BellFragment;
import com.example.swappit.Fragment.HomeFragment;
import com.example.swappit.Fragment.ProfileFragment;
import com.example.swappit.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    //Object
    ConnectivityManager connectivityManager;
    BroadcastReceiver broadcastReceiver;

    //Variables
    boolean isConnected;
    boolean isConnectionSignal;

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        swipeRefreshLayout= findViewById(R.id.swipe);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                broadCast();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }



    }//end of on create

    public void broadCast(){
        conStatus();
        if(isConnected){
            if(isConnectionSignal){
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "No Connection!", Toast.LENGTH_SHORT).show();

        }
    }



    //Methods
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

  /*  public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }*/

    public void conStatus(){
        connectivityManager= (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();

        isConnected=networkInfo != null && networkInfo.isConnectedOrConnecting();//Boolean
        if(isConnected){
            isConnectionSignal=conSignal(networkInfo.getType(),networkInfo.getSubtype());
            isConnected=true;
        }else{
            isConnected=false;
        }



    }


    public static boolean conSignal ( int type, int subType){
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return true; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return true; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return true; // ~ 50-100 kbps //false kasi di magload ng maayos yung mga items
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return true; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // I added this one to detect may 3g connection
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }//end of net detect


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){

                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.nav_add:
                    startActivity(new Intent(HomeScreen.this, PostActivity.class));
                    break;
                case R.id.nav_bell:
                    selectedFragment = new BellFragment();
                    break;
                case R.id.nav_profile:
                   SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                   editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                   editor.apply();

                    selectedFragment = new ProfileFragment();
                    break;

        } if (selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
            }
            return true;

    };


    };//end



}
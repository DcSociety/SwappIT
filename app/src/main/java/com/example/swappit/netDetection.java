package com.example.swappit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class netDetection extends AppCompatActivity {


    ConnectivityManager connectivityManager;
    BroadcastReceiver broadcastReceiver;

    boolean isConnected;
    boolean isConnectionSignal;



    TextView conStat;

    Button detect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_detection);

        conStat = findViewById(R.id.Stat);
        detect = findViewById(R.id.buttonDetect);
        conStat.setVisibility(View.VISIBLE);
        conStat.setText("No Connection!");

        broadcastReceiver = new BroadcastReceiver() {

            @Override

            public void onReceive(Context context, Intent intent) {
                detect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conStatus();
                        if (isConnected) {
                            if (isConnectionSignal) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        conStat.setVisibility(View.VISIBLE);
                                        conStat.setText("Connected");



                                    }
                                }, 2000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(netDetection.this, "Connected! Welcome back!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }, 3000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        conStat.setVisibility(View.VISIBLE);
                                        conStat.setText("Connecting...");


                                    }
                                }, 2000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        conStat.setVisibility(View.GONE);
                                    }
                                }, 5000);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    conStat.setVisibility(View.VISIBLE);
                                   conStat.setText("No Connection!");


                                }
                            }, 2000);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    conStat.setVisibility(View.GONE);
                                }
                            }, 10000);
                        }

                    }//end on click
                });


            }
        };//end broad




    }
    public void onResume () {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause () {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void conStatus () {
        connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();//Boolean
        if (isConnected) {
            isConnectionSignal = conSignal(networkInfo.getType(), networkInfo.getSubtype());
            isConnected = true;
        } else {
            isConnected = false;
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

    }


}
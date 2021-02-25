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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeScreen extends AppCompatActivity {

    //variables for log in btn
    Button btnLog;
    TextView txtSign;
    FirebaseAuth forAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        //calling ids
        btnLog = findViewById(R.id.btnLogIn);
        txtSign = findViewById(R.id.signUptxt);

        //calling firebase authentication
        forAuth = FirebaseAuth.getInstance();


        //detect nya if currently logged in ka
        if(forAuth.getCurrentUser() != null){
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
            finish();
        }

        txtSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpScreen.class));
                finish();
            }
        });






    }

    public void ClickLog(View view){
        startActivity(new Intent(getApplicationContext(),LogInScreen.class));
        finish();
    }






}
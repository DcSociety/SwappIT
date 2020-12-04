package com.example.swappit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }//end of on create

    public void onProfile(View view){
       startActivity(new Intent(getApplicationContext(),ProfileScreen.class));
        //finish();
    }//end of profile

}
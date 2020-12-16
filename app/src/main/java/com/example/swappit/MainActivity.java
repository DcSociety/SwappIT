package com.example.swappit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //splash screen duration

    private static int Splash_Screen =5000;

    //variables for animation
    Animation topAnim, bottomAnim;
    SharedPreferences OnBoardingScreen;//to check if user use app for the first time
    //variables for logo and title splash screen
    ImageView appLogo;
    TextView appTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //calling logo and title
        appLogo = findViewById(R.id.appLogo);
        appTitle = findViewById(R.id.appTitle);

        //calling animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.botton_animation);

        appLogo.setAnimation(topAnim);
        appTitle.setAnimation(bottomAnim);

        //calling welcome screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                OnBoardingScreen = getSharedPreferences("OnBoardingScreen",MODE_PRIVATE);
                boolean isFirstTime = OnBoardingScreen.getBoolean("firstTime",true);

                 if(isFirstTime){
                    SharedPreferences.Editor editor2 = OnBoardingScreen.edit();
                    editor2.putBoolean("firstTime",false);
                    editor2.commit();

                    Intent intent = new Intent(MainActivity.this, OnBoarding.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
                    startActivity(intent);
                    finish();
                }


            }
        },Splash_Screen);

    }
}
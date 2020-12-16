package com.example.swappit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoarding extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dots;
    SliderAdapter sliderAdapter;
    TextView[] Dots;
    Button letsGetStarted;
    Animation animation;
    int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.onboarding);
        //calling ids
        viewPager = findViewById(R.id.Slider);
        dots = findViewById(R.id.Dots);
        letsGetStarted = findViewById(R.id.getStarted);
        //Calling slider adapter
        sliderAdapter = new SliderAdapter(this);

        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void skip(View view){
        startActivity(new Intent(this, WelcomeScreen.class));
        finish();
    }
    public void next(View view){
        viewPager.setCurrentItem(currentPosition+1);
    }
    public void getStart(View view){
        startActivity(new Intent(this, WelcomeScreen.class));
        finish();
    }
    private void addDots(int position){
        Dots = new TextView[4];
        dots.removeAllViews();
        for(int i=0; i<Dots.length; i++){
            Dots[i] = new TextView(this);
            Dots[i].setText(Html.fromHtml("&#8226;"));
            Dots[i].setTextSize(35);

            dots.addView(Dots[i]);
        }
        if(Dots.length > 0){
            Dots[position].setTextColor(getResources().getColor(R.color.dark_blue));
        }
    }
    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPosition = position;
            //to hide lets get started button on first to third slider
            if(position == 0){
                letsGetStarted.setVisibility(View.INVISIBLE);
            }
            else if(position == 1){
                letsGetStarted.setVisibility(View.INVISIBLE);
            }
            else if(position ==2){
                letsGetStarted.setVisibility(View.INVISIBLE);
            }
            else {
                animation = AnimationUtils.loadAnimation(OnBoarding.this,R.anim.button_animation);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
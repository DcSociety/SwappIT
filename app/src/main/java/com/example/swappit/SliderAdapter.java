package com.example.swappit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context) {
        this.context = context;
    }

    int images[] = {
            R.drawable.app_logo,
            R.drawable.slider_one,
            R.drawable.slider_two,
            R.drawable.slider_three,

    };
    int headings[]= {
            R.string.headings1,
            R.string.headings2,
            R.string.headings3,
            R.string.headings4,
    };

    int descriptions[]={
            R.string.desc1,
            R.string.desc2,
            R.string.desc3,
            R.string.desc4,
    };

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout,container,false);
        //calling id of slider
        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView desc = view.findViewById(R.id.slider_des);
        TextView heading = view.findViewById(R.id.slider_heading);
        //callings arrays
        imageView.setImageResource(images[position]);
        heading.setText(headings[position]);
        desc.setText(descriptions[position]);
        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}

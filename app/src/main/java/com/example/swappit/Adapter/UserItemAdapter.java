package com.example.swappit.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.swappit.Fragment.PostDetailFragment;
import com.example.swappit.Fragment.ProfileFragment;
import com.example.swappit.Model.Post;
import com.example.swappit.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.ViewHolder>{

    private Context context;
    private List<Post> mPosts;

    public UserItemAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_items, viewGroup, false);
        return new UserItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        Post post = mPosts.get(i);

        Glide.with(context).load(post.getPostimage()).apply(RequestOptions.placeholderOf(R.drawable.placeholder)).into(holder.post_image1);

        holder.post_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();


                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();

            }
        });


    }

    @Override
    public int getItemCount()
    {
        return mPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image1;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image1 = itemView.findViewById(R.id.post_image1);

       }
    }


}

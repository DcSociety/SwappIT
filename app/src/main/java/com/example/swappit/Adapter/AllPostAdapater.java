package com.example.swappit.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.swappit.Fragment.PostDetailFragment;
import com.example.swappit.Fragment.ProfileFragment;
import com.example.swappit.Model.Post;
import com.example.swappit.Model.User;
import com.example.swappit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AllPostAdapater extends RecyclerView.Adapter<AllPostAdapater.ViewHolder>{
//dito magload yung post sa home page only the post image, user fullname, pfp at title ng post and if gusto save lng magloload
    private Context context2;
    private List<Post> mPosts2;
    private FirebaseUser firebaseUser;
    public AllPostAdapater(Context context2, List<Post> mPosts2) {
        this.context2 = context2;
        this.mPosts2 = mPosts2;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context2).inflate(R.layout.all_post, viewGroup, false);
        return new AllPostAdapater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder itemV, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPosts2.get(i);
        Glide.with(context2).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(itemV.post_image1);

        itemV.title.setText(post.getTitle());

        publisherInfo(itemV.image_profile, itemV.fullname,post.getPublisher());

        isSaved(post.getPostid(),itemV.save);

        itemV.post_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context2.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity)context2).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();

            }
        });
        itemV.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemV.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                    Toast.makeText(context2, "Item saved", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                    Toast.makeText(context2, "Item unsaved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        itemV.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context2.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)context2).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

            }
        });

        itemV.fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context2.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)context2).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

            }
        });


    }

    @Override
    public int getItemCount()
    {
        return mPosts2.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image1;
        public ImageView image_profile,save;
        public TextView fullname,title;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image1 = itemView.findViewById(R.id.post_image);
            image_profile = itemView.findViewById(R.id.image_profile);
            save = itemView.findViewById(R.id.save);
            fullname = itemView.findViewById(R.id.name);
            title = itemView.findViewById(R.id.title);


        }
    }
    private void publisherInfo(ImageView image_profile, TextView fullname, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context2).load(user.getImageurl()).into(image_profile);
                fullname.setText(user.getFullname());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void isSaved(String postid, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_save_dark);
                    imageView.setTag("saved");

                }else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




}

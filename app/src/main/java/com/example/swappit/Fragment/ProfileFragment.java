package com.example.swappit.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.swappit.Adapter.UserItemAdapter;
import com.example.swappit.EditProfileActivity;
import com.example.swappit.LogInScreen;
import com.example.swappit.Model.Post;
import com.example.swappit.Model.User;
import com.example.swappit.OptionsActivity;
import com.example.swappit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ProfileFragment extends Fragment {
    ImageView image_profile, options;
    TextView items, followers, following, contact, bio_location, fullname, myEmail;
    Button edit_profile;

    private List<String>mySaves;
    RecyclerView recyclerView_saves;
    UserItemAdapter myUserAdapter_saves;
    List<Post> postList_saves;


    RecyclerView recyclerView;
    UserItemAdapter myUserAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton my_items, saved_interest;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        items = view.findViewById(R.id.items);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        contact = view.findViewById(R.id.contact);
        bio_location = view.findViewById(R.id.bio_location);
        fullname = view.findViewById(R.id.fullname);
        edit_profile = view.findViewById(R.id.edit_profile);
        my_items = view.findViewById(R.id.my_items);
        saved_interest = view.findViewById(R.id.my_interests);
        myEmail = view.findViewById(R.id.my_email);

        //to display user's item list in profile gridview sya
        recyclerView = view.findViewById(R.id.recycle_viewer_items);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myUserAdapter = new UserItemAdapter(getContext(), postList);
        recyclerView.setAdapter(myUserAdapter);

        //ito naman para sa saved items or posts ng user grid view din
        recyclerView_saves = view.findViewById(R.id.recycle_viewer_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myUserAdapter_saves = new UserItemAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myUserAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);



        //calling methods
        userInfo();
        getFollowers();
        getNrPosts();
        myItems(); // call to detect user's post only
        mysaves();//call to detect user's saved items


        //detects if u visit ur profile so u can edit
        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
            myEmail.setVisibility(View.VISIBLE);
        }else{
            checkFollow();
            saved_interest.setVisibility(View.GONE);

        }





        // for edit profile button
         edit_profile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String btn = edit_profile.getText().toString();

                 if(btn.equals("Edit Profile")){ //detects if its ur profile
                     //go to edit
                     startActivity(new Intent(getContext(), EditProfileActivity.class));

                 } else if (btn.equals("follow")){ // if you don't follow
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                             .child("following").child(profileid).setValue(true);
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                             .child("followers").child(firebaseUser.getUid()).setValue(true);
                 } else if (btn.equals("following")){//if u follow
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                             .child("following").child(profileid).removeValue();
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                             .child("followers").child(firebaseUser.getUid()).removeValue();
                 }

             }
         });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });
        //pagclick yung items button sa user profile
        my_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });
        //pagclick yung saved items button or bookmark icon sa user profile
        saved_interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });


        return view;
    }

        private void userInfo(){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(getContext() == null){
                        return;
                    }
                    User user = snapshot.getValue(User.class);

                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                    fullname.setText((user.getFullname()));
                    contact.setText(user.getContact());
                    bio_location.setText(user.getBio());
                    myEmail.setText(user.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void checkFollow(){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("following");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) { // to detect if you already follow someone
                    if (snapshot.child(profileid).exists()){
                        edit_profile.setText("following");

                    }else {
                        edit_profile.setText("follow");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        //count and display followers
    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post =dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        i++;                    }

                }

                items.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myItems(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //method to see if you saved items then read it
    private void mysaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    mySaves.add(dataSnapshot.getKey());
                }
                Collections.reverse(mySaves);
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //itong method para mabilang yung sinasave ni user na items sa user profile nya
    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_saves.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : mySaves){
                        if(post.getPostid().equals(id)){
                            postList_saves.add(post);
                        }
                    }
                }
                myUserAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
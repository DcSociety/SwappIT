package com.example.swappit.Fragment;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swappit.Adapter.AllPostAdapater;
import com.example.swappit.Adapter.PostAdapter;
import com.example.swappit.Model.Post;
import com.example.swappit.Model.User;
import com.example.swappit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView,recyclerView_search;
   // private PostAdapter postAdapter;
    private AllPostAdapater postAdapter;
    private List<Post> postLists;

    private List<String> followingList;

    ProgressBar progressBar;
    ImageButton following, home;
    EditText search_bar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home, container,false);


       recyclerView = view.findViewById(R.id.recycle_viewer);
       recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
        //linearLayoutManager.setReverseLayout(true);
       //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new AllPostAdapater(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);
        following = view.findViewById(R.id.following_items);
        home = view.findViewById(R.id.all_items);
        progressBar = view.findViewById(R.id.progress_circular);
        search_bar = view.findViewById(R.id.search_bar2);

        readPosts();

       following.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               checkFollowing();
           }
       });

       home.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               readPosts();
           }
       });

       search_bar.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence j, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence j, int start, int before, int count) {
               searchPosts(j.toString().toLowerCase());
           }

           @Override
           public void afterTextChanged(Editable j) {


           }
       });

        return view;
    }//end of oncreate








    //ito para mafilter yung post kung ano yung sinearch na title ng post
    private void searchPosts(String s){


        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("title")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Post post =snapshot1.getValue(Post.class);
                    postLists.add(post);
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


   private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    followingList.add(snapshot1.getKey());

                }
                readPosts_following();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Post post = snapshot1.getValue(Post.class);

                            postLists.add(post);


                }
                Collections.reverse(postLists);
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readPosts_following(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Post post = snapshot1.getValue(Post.class);
                         for(String id : followingList){
                        if(post.getPublisher().equals(id)){
                    postLists.add(post);
                      }
                     }
                }
                Collections.reverse(postLists);
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
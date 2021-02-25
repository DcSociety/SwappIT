 package com.example.swappit.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.swappit.Adapter.PostAdapter;
import com.example.swappit.HomeScreen;
import com.example.swappit.Model.Post;
import com.example.swappit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


 public class PostDetailFragment extends Fragment {

        String postid;
        private RecyclerView recyclerView;
        private PostAdapter postAdapter;
        private List<Post> postList;
        ImageButton back_button;
        ImageView chat;
        TextView publisher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        View view2 = inflater.inflate(R.layout.post_item, container, false);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid = preferences.getString("postid", "none");

        recyclerView = view.findViewById(R.id.recycle_view_detail);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        back_button = view.findViewById(R.id.back_button);
        publisher = view2.findViewById(R.id.publisher);
        chat = view2.findViewById(R.id.chat);
        publisher.setVisibility(View.VISIBLE);
        chat.setVisibility(View.VISIBLE);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit(); // para pagback button hindi magclose activity yung fragment lng replace sa default home fragment
            }
        });

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        readPost();
        return view;
    }

     private void readPost() {
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                 .child(postid);

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 postList.clear();
                 Post post = snapshot.getValue(Post.class);
                 postList.add(post);
                 chat.setVisibility(View.GONE);
                 postAdapter.notifyDataSetChanged();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
     }
 }
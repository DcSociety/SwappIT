package com.example.swappit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileScreen extends AppCompatActivity {
    //variables again
    TextView Email,Contact,FullName;
    //calling firebase for retrieval
    FirebaseAuth forAuth;
    FirebaseFirestore forStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        //calling firebase
        forAuth = FirebaseAuth.getInstance();
        forStore = FirebaseFirestore.getInstance();

        //calling ids
        Email = findViewById(R.id.txtMail);
        Contact = findViewById(R.id.txtContact);
        FullName = findViewById(R.id.txtName);

        //retrieve data from current user
        userID = forAuth.getCurrentUser().getUid();
        DocumentReference documentReference = forStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                Contact.setText(documentSnapshot.getString("Contact"));
                FullName.setText(documentSnapshot.getString("FName"));
                Email.setText(documentSnapshot.getString("Email"));

            }
        });



    }//end of on create

    public void onBack(View view){
        startActivity(new Intent(getApplicationContext(),HomeScreen.class));
        //finish();
    }//end of back

    public void LogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LogInScreen.class));
        finish();
    }// end of log out




}
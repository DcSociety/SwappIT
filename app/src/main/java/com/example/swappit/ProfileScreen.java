package com.example.swappit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileScreen extends AppCompatActivity {
    //variables again
    TextView pEmail,pContact,pFullName;
    //calling firebase for retrieval
    FirebaseAuth forAuth;
    FirebaseFirestore forStore;
    String userID;
    ImageView imgPfp;
    Button btnChangePic;
    ProgressBar loadBar;
    //storage ref for cloud storage
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        //calling firebase
        forAuth = FirebaseAuth.getInstance();
        forStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        //another storage ref to load image when logged in again
        StorageReference profileRef = storageReference.child("users/"+forAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgPfp);
            }
        });

        //calling ids
        pEmail = findViewById(R.id.pfpMail);
        pContact = findViewById(R.id.pfpContact);
        pFullName = findViewById(R.id.pfpName);
        imgPfp = findViewById(R.id.imageView2);
        btnChangePic = findViewById(R.id.btnChangePFP);
        loadBar = findViewById(R.id.progressBar3);

        //retrieve data from current user
        userID = forAuth.getCurrentUser().getUid();
        DocumentReference documentReference = forStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                pFullName.setText(value.getString("FName"));
                pEmail.setText(value.getString("Email"));
                pContact.setText(value.getString("Contact"));

            }
        });
        //btn upload profile pic or change profile
        btnChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open user gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
                loadBar.setVisibility(View.VISIBLE);
            }
        });

    }//end of on create

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                //imgPfp.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }

        }
    }//end of  activity result
    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase cloud storage
        StorageReference fileRef = storageReference.child("users/"+forAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       Picasso.get().load(uri).into(imgPfp);//get image to display into image view
                        Toast.makeText(ProfileScreen.this, "Profile picture uploaded!", Toast.LENGTH_SHORT).show();
                        loadBar.setVisibility(View.INVISIBLE);
                    }
                });
             }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileScreen.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                loadBar.setVisibility(View.INVISIBLE);
            }
        });
    }

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
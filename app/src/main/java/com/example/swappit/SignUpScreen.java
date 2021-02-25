package com.example.swappit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    public static final String TAG = "TAG";
    //variables for log in btn
    Button btnSign;
    TextView txtLog;
    EditText inputName, inputEmail, inputPass, inputConfirm, inputNum;
    ProgressBar progress2;
    FirebaseAuth forAuth;//for firebase authentication
    // FirebaseFirestore forStore; //for firebase fireStore storing of user data
    //String userID; //for users' data identification
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        //calling ids
        btnSign = findViewById(R.id.btnSignUp);
        txtLog = findViewById(R.id.txtLogIn);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputMail);
        inputPass = findViewById(R.id.inputPass);
        inputConfirm = findViewById(R.id.inputConfirm);
        inputNum = findViewById(R.id.inputContact);
        progress2 = findViewById(R.id.progressBar2);

        //calling firebase authentication
        forAuth = FirebaseAuth.getInstance();

        //calling firebase fireStore
        // forStore = FirebaseFirestore.getInstance();

        //detect nya if currently logged in ka
        if (forAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
            finish();
        }

        //onclick for btn Sign Up
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //create ako string to get the text
                String sEmail = inputEmail.getText().toString().trim();
                String sPass = inputPass.getText().toString().trim();
                String sConfirm = inputConfirm.getText().toString().trim();
                String sName = inputName.getText().toString();
                String sNumber = inputNum.getText().toString();


                //validation para sa inputs
                if (TextUtils.isEmpty(sEmail) || TextUtils.isEmpty(sPass)|| TextUtils.isEmpty(sConfirm)||
                        TextUtils.isEmpty(sNumber)||TextUtils.isEmpty(sName)) {
                    Toast.makeText(SignUpScreen.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                }else if (sPass.length() < 6) {
                    inputPass.setError("Password must be at least 6 characters");
                    Toast.makeText(SignUpScreen.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();


                } else {
                    if (TextUtils.equals(sPass, sConfirm)) {
                        progress2.setVisibility(View.VISIBLE);
                        register(sName, sEmail, sPass, sNumber);

                    }
                    else{
                        inputPass.setError("Password doesn't match!");
                        inputConfirm.setError("Password doesn't match!");
                        Toast.makeText(SignUpScreen.this, "Password doesn't match", Toast.LENGTH_SHORT).show();

                    }

                }


               ;
                //progressbar visible

                //pd.setMessage("Please wait...");
                // pd.show();
                //sign up the user input in firebase hope this work


            }


        });


        //setting on click on txt log in here
        txtLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LogInScreen.class));
            }
        });

    }

    private void register(String fullname, String email, String password, String contact) {
        forAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpScreen.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = forAuth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("fullname", fullname);
                            hashMap.put("email", email);
                            hashMap.put("contact", contact);
                            hashMap.put("bio", "");
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/swappit-8645e.appspot.com/o/pfp.png?alt=media&token=a0187d67-1380-412b-b863-8ff583ca6822");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Intent intent = new Intent(SignUpScreen.this, HomeScreen.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                }
                            });
                        } else {
                            progress2.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpScreen.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
package com.example.swappit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    public static final String TAG = "TAG";
    //variables for log in btn
    Button btnSign;
    TextView txtLog;
    EditText inputName,inputEmail,inputPass,inputConfirm,inputNum;
    ProgressBar progress2;
    FirebaseAuth forAuth;//for firebase authentication
    FirebaseFirestore forStore; //for firebase fireStore storing of user data
    String userID; //for users' data identification

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
        progress2 =findViewById(R.id.progressBar2);

        //calling firebase authentication
        forAuth = FirebaseAuth.getInstance();

        //calling firebase fireStore
        forStore = FirebaseFirestore.getInstance();

        //detect nya if currently logged in ka
        if(forAuth.getCurrentUser() != null){
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
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
                if(TextUtils.isEmpty(sEmail)){
                    inputEmail.setError("Please fill up E-mail");
                    Toast.makeText(SignUpScreen.this, "Please fill up E-mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sPass)){
                    inputPass.setError("Please fill up Password");
                    Toast.makeText(SignUpScreen.this, "Please fill up Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sConfirm)){
                    inputConfirm.setError("Please confirm your password");
                    Toast.makeText(SignUpScreen.this, "Please fill up confirm your Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(sPass.length() <6){
                    inputPass.setError("Password must be at least 6 characters");
                    Toast.makeText(SignUpScreen.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.equals(sPass,sConfirm)){

                }
                else{
                    Toast.makeText(SignUpScreen.this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //progressbar visible
                progress2.setVisibility(View.VISIBLE);

                //sign up the user input in firebase hope this work
                forAuth.createUserWithEmailAndPassword(sEmail,sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpScreen.this, "Successfully Signed Up!", Toast.LENGTH_SHORT).show();
                            userID = forAuth.getCurrentUser().getUid();//to retrieve user id of the current logged in user
                            DocumentReference documentReference = forStore.collection("users").document(userID);//create document ref

                            //creating and storing user data
                            Map<String,Object> user = new HashMap<>();
                            user.put("FName",sName);
                            user.put("Email",sEmail);
                            user.put("Contact",sNumber);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess: User Profile is created for "+ userID);
                                }
                            });//end tag of document ref
                            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                        }else{
                            Toast.makeText(SignUpScreen.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progress2.setVisibility(View.INVISIBLE);
                        }

                    }
                });

            }


        });

        //setting on click on txt log in here
        txtLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LogInScreen.class));
            }
        });
    }
}
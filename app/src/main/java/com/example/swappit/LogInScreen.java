package com.example.swappit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

public class LogInScreen extends AppCompatActivity {

    //variables for log in btn
    Button btnLog;
    TextView txtSign1,txtReset;
    EditText logEmail,logPass;
    ProgressBar pBar;
    //firebase authentication
    FirebaseAuth forAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        //calling ids
        btnLog = findViewById(R.id.btnLog2);
        txtSign1 = findViewById(R.id.txtSign);
        logPass = findViewById(R.id.logPass);
        logEmail = findViewById(R.id.logMail);
        pBar = findViewById(R.id.progressBar);
        txtReset= findViewById(R.id.txtForgot);


        //for firebase authentication
        forAuth = FirebaseAuth.getInstance();

        //detect nya if currently logged in ka
        if(forAuth.getCurrentUser() != null){
            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
            finish();
        }

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create ako string to get the text
                String sEmail1 = logEmail.getText().toString().trim();
                String sPass1 = logPass.getText().toString().trim();

                //validation para sa inputs
                if(TextUtils.isEmpty(sEmail1)){
                    logEmail.setError("Please fill up E-mail");
                    Toast.makeText(LogInScreen.this, "Please fill up E-mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(sPass1)){
                    logPass.setError("Please fill up Password");
                    Toast.makeText(LogInScreen.this, "Please fill up Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sPass1.length() <6){
                    logPass.setError("Password must be at least 6 characters");
                    Toast.makeText(LogInScreen.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                //loading bar visible na
                pBar.setVisibility(View.VISIBLE);

                //for authentication in log in
                forAuth.signInWithEmailAndPassword(sEmail1,sPass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LogInScreen.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeScreen.class));


                        }else{
                            Toast.makeText(LogInScreen.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            pBar.setVisibility(View.INVISIBLE);//hide pBar when error
                        }
                    }
                });
            }
        });// end tag of sign in for authentication


        //setting on click on txt sign up here
        txtSign1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUpScreen.class));

            }
        });//end tag of txtSign1

        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your email to received reset link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //extract the email and send reset link
                        String mail = resetMail.getText().toString();

                        //validation if email is not entered yeah!
                        if(TextUtils.isEmpty(mail)){
                            resetMail.setError("Please Enter Your Email.");
                            Toast.makeText(LogInScreen.this, "You didn't enter your email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        forAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LogInScreen.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LogInScreen.this, "Error! Reset Link is Not Sent! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });//end of failure listener



                    }
                });//end tag of positive button resetPass

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close dialog
                    }
                });
                passwordResetDialog.create().show();//to display alert dialog

            }
        });//end tag of txtReset

    }//end tag of on create
}
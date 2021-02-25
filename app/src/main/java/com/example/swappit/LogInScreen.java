package com.example.swappit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInScreen extends AppCompatActivity {

    ConnectivityManager connectivityManager;
    BroadcastReceiver broadcastReceiver;

    boolean isConnected;
    boolean isConnectionSignal;

    //variables for log in btn
    Button btnLog;
    TextView txtSign1,txtReset;
    EditText logEmail,logPass;
    ProgressBar pBar;
    //firebase authentication
    FirebaseAuth forAuth;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

    }

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

        broadcastReceiver = new BroadcastReceiver() {

            @Override

            public void onReceive(Context context, Intent intent) {
                btnLog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conStatus();
                        if (isConnected) {
                            if (isConnectionSignal) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LogInScreen.this, "Please wait...", Toast.LENGTH_SHORT).show();


                                    }
                                }, 1000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        String sEmail1 = logEmail.getText().toString().trim();
                                        String sPass1 = logPass.getText().toString().trim();

                                        //validation para sa inputs
                                        if(TextUtils.isEmpty(sEmail1) || TextUtils.isEmpty(sPass1)){

                                            Toast.makeText(LogInScreen.this, "Please fill up both fields", Toast.LENGTH_SHORT).show();

                                        }else {

                                            //loading bar visible na
                                            pBar.setVisibility(View.VISIBLE);
                                            //for authentication in log in
                                            forAuth.signInWithEmailAndPassword(sEmail1,sPass1).addOnCompleteListener(LogInScreen.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()){
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(forAuth.getCurrentUser().getUid());
                                                        reference.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                Intent intent = new Intent(LogInScreen.this,HomeScreen.class);
                                                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {


                                                            }
                                                        });
                                                    } else {
                                                        pBar.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(LogInScreen.this, "Login Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } // end tag of sign in for authentication

                                    }
                                }, 2000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(LogInScreen.this, "Connecting...", Toast.LENGTH_SHORT).show();

                                    }
                                }, 2000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LogInScreen.this, "Connecting...", Toast.LENGTH_SHORT).show();
                                    }
                                }, 4000);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LogInScreen.this, "No Connection!", Toast.LENGTH_SHORT).show();


                                }
                            }, 1000);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(),netDetection.class));
                                }
                            }, 2000);
                        }

                    }//end on click
                });


            }
        };//end broad





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

    public void onResume () {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause () {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void conStatus () {
        connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();//Boolean
        if (isConnected) {
            isConnectionSignal = conSignal(networkInfo.getType(), networkInfo.getSubtype());
            isConnected = true;
        } else {
            isConnected = false;
        }


    }


    public static boolean conSignal ( int type, int subType){
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return true; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return true; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return true; // ~ 50-100 kbps //false kasi di magload ng maayos yung mga items
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return true; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // I added this one to detect may 3g connection
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }

    }
}


package com.example.swappit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import javax.crypto.spec.PSource;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl ="";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView  image_added;
    Button post,close;
    EditText description,title,location;

    ConnectivityManager connectivityManager;
    BroadcastReceiver broadcastReceiver;

    boolean isConnected;
    boolean isConnectionSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close =findViewById(R.id.close);
        image_added =findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description =findViewById(R.id.description);
        title =findViewById(R.id.title);
        location =findViewById(R.id.location);


        storageReference = FirebaseStorage.getInstance().getReference("posts");





        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this,HomeScreen.class));
                finish();
            }
        });


        broadcastReceiver = new BroadcastReceiver() {

            @Override

            public void onReceive(Context context, Intent intent) {
               post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conStatus();
                        if (isConnected) {
                            if (isConnectionSignal) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PostActivity.this, "Posting...", Toast.LENGTH_SHORT).show();



                                    }
                                }, 1000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        String ti = title.getText().toString();
                                        String des = description.getText().toString();
                                        String loc = location.getText().toString();
                                        //validation para sa inputs
                                        if(TextUtils.isEmpty(ti) || TextUtils.isEmpty(des) || TextUtils.isEmpty(loc)){

                                            Toast.makeText(PostActivity.this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();

                                        } else {
                                            uploadImage();
                                        }

                                    }
                                }, 2000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PostActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();


                                    }
                                }, 1000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PostActivity.this, "Connecting..", Toast.LENGTH_SHORT).show();
                                    }
                                }, 2000);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PostActivity.this, "No Connection!", Toast.LENGTH_SHORT).show();


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

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }//end of on create

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

    }//end of net detect

    private String  getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime =  MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType((contentResolver.getType(uri)));

    }
    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting... Please Wait");
        progressDialog.show();

        if (imageUri != null){
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()+ "." + getFileExtension(imageUri));
            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri>task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = reference.push().getKey();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("title", title.getText().toString().toLowerCase());
                        hashMap.put("location", location.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());


                        reference.child(postid).setValue(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(PostActivity.this, HomeScreen.class));
                        finish();
                    }else{
                        Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "No Image selected!", Toast.LENGTH_SHORT).show();
        }

    }

    //ctrl+0



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode  == RESULT_OK){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri =result.getUri();

                image_added.setImageURI(imageUri);
            }else {
                Toast.makeText(this, "Post Cancelled!" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PostActivity.this, HomeScreen.class));
                finish();
            }

    }
}
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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.swappit.Fragment.ProfileFragment;
import com.example.swappit.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    ImageView close, image_profile;
    TextView save, tv_change;
    MaterialEditText fullname, contact, bio_location;

    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;


    ConnectivityManager connectivityManager;
    BroadcastReceiver broadcastReceiver;

    boolean isConnected;
    boolean isConnectionSignal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        fullname = findViewById(R.id.fullname);
        contact = findViewById(R.id.contact);
        bio_location = findViewById(R.id.bio_location);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                contact.setText(user.getContact());
                bio_location.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                      //  .setAspectRatio(1,1)
                        //.setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        //.setAspectRatio(1,1)
                       // .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });



        broadcastReceiver = new BroadcastReceiver() {

            @Override

            public void onReceive(Context context, Intent intent) {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conStatus();
                        if (isConnected) {
                            if (isConnectionSignal) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EditProfileActivity.this, "saving...", Toast.LENGTH_SHORT).show();



                                    }
                                }, 1000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateProfile(fullname.getText().toString(),bio_location.getText().toString(),contact.getText().toString());
                                        Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                    }
                                }, 2000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EditProfileActivity.this, "Saving...", Toast.LENGTH_SHORT).show();


                                    }
                                }, 1000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EditProfileActivity.this, "Connecting..", Toast.LENGTH_SHORT).show();
                                    }
                                }, 2000);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EditProfileActivity.this, "No Connection!", Toast.LENGTH_SHORT).show();


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
    }// end of on create

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

    private  void updateProfile(String fullname, String bio_location, String contact){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fullname", fullname);
        hashMap.put("bio", bio_location);
        hashMap.put("contact", contact);


        reference.updateChildren(hashMap);

    }
    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if(mImageUri != null){
            StorageReference filereference = storageRef.child(System.currentTimeMillis()
                    +"."+getFileExtension(mImageUri));

            uploadTask = filereference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                   if (!task.isSuccessful()){
                       throw task.getException();
                   }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String myUrl = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl",""+myUrl);

                        reference.updateChildren(hashMap);
                        pd.dismiss();
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();
        }else{
            Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show();
        }

    }
}
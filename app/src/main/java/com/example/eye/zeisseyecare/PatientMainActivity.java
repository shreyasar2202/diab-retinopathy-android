package com.example.eye.zeisseyecare;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PatientMainActivity extends AppCompatActivity {
    ImageView imageView;
    File localFile=null;
    String name;
    TextView textView;
    FirebaseAuth firebaseAuth;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout:
                firebaseAuth.signOut();
                finish();

                startActivity(new Intent(this, LoginActivity.class));

                return true;
        }
        return  false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);
        imageView=(ImageView)findViewById(R.id.fbimage2);
        textView=(TextView)findViewById(R.id.report);
            firebaseAuth=FirebaseAuth.getInstance();

    Intent intent=getIntent();
        int pos=intent.getIntExtra("Pos",0);
       String email=intent.getStringExtra("Email");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Patients").child("Name").child(pos+"");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.getValue(String.class);
                loadimg(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        DatabaseReference myReference=FirebaseDatabase.getInstance().getReference("Coordinates").child(email);
        if(myReference!=null)
        {
            myReference.child("Image").child("fire").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int fire=dataSnapshot.getValue(Integer.class);
                    if(fire==0)
                    {
                        textView.setText(name+", your treatment is scheduled on 24-01-2018");
                    }
                    else
                    {
                        textView.setText("Congratulations "+name+", treatment successful");
                        textView.setTextColor(Color.parseColor("#008000"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            textView.setText(name+", your eyes are completely healthy");
        }

    }
    void loadimg(String name)
    {if(name!=null)

    {
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(name+"_leak_detect.jpg");


        try {
            localFile = File.createTempFile("images", "jpg");

        } catch (IOException e) {
            e.printStackTrace();
        }
        storageReference.getFile(localFile)
                .addOnSuccessListener(

                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Log.e("sasa","success");
                                Glide.with(getApplicationContext()).load(localFile).into(imageView);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }

        });}
    }
    }









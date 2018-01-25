package com.example.eye.zeisseyecare;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class PatientImage extends AppCompatActivity {
    Button button;
    int finished=0;
    int flag=0;
    File localFile = null;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_image);
        Intent intent=getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String email=intent.getStringExtra("Email");
        final String name=intent.getStringExtra("Name");
       getSupportActionBar().setTitle(name+"'s - Visucam Image");
        button=(Button)findViewById(R.id.fire);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Coordinates");
        reference.child(email).child("Image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int fired=dataSnapshot.child("fire").getValue(Integer.class);
                if(fired==1)
                {
                 button.setText("FIRED");
                    button.setAlpha((float)0.7);
                    button.setBackgroundColor(Color.parseColor("#54CADA"));
                   flag=1;

                }
                finished=1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
        finished=1;
            }
        });
       // while(finished==0);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire(email);
            }
        });
        imageView=(ImageView)findViewById(R.id.fbimage);

        StorageReference storageReference= FirebaseStorage.getInstance().getReference(name+"_leak_detect.jpg");

        try {
            localFile = File.createTempFile("images", "jpg");

        } catch (IOException e) {
            e.printStackTrace();
        }
        storageReference.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Log.e("sasa","success");
                        Glide.with(getApplicationContext()).load(localFile).into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
    void fire(String email)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Coordinates");
        reference.child(email).child("Image").child("fire").setValue(1);

        button.setText("FIRED");
        button.setBackgroundColor(Color.parseColor("#54CADA"));
        flag=1;
    }
}

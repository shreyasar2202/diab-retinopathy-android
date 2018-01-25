package com.example.eye.zeisseyecare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Shreyas on 21-01-2018.
 */

public class DoctorAdapter extends ArrayAdapter<String> {
    private String emailDecode(String emailID) {
        return emailID.replace(",",".");
    }
        public DoctorAdapter(Context context, ArrayList<String> list) {
            super(context,0,list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItemView = convertView;

            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_row, parent, false);
            }
            final TextView textView=(TextView)listItemView.findViewById(R.id.day);
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Patients").child("Name").child(position+"");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String email=dataSnapshot.getValue(String.class);
                    textView.setText(emailDecode(email));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            return listItemView;







        }
    }



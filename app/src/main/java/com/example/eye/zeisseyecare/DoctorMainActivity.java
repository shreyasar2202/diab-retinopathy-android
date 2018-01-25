package com.example.eye.zeisseyecare;

import android.app.ActionBar;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorMainActivity extends AppCompatActivity {
    ListView listView;
    DoctorAdapter doctorAdapter;
    String name;
    FirebaseAuth firebaseAuth;
    private String emailDecode(String emailID) {
        return emailID.replace(",",".");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);
        listView=(ListView)findViewById(R.id.patientListView);
        listView.bringToFront();
        ImageView imageView=(ImageView)findViewById(R.id.root);
        firebaseAuth=FirebaseAuth.getInstance();
        //firebaseAuth.getCurrentUser();
        ArrayList<String>abc=new ArrayList<>();
        abc.add("dasas");



       new LoadFromFirebase().execute("");
    }
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


    private class LoadFromFirebase extends AsyncTask<String,Void,ArrayList<String>>
    {   final ArrayList<String> emailList=new ArrayList<>();
        int flag=0;
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Patients");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int number=dataSnapshot.child("number").getValue(Integer.class);
                    DataSnapshot snap=dataSnapshot.child("Name");

                    for (int i = 0; i < number; i++) {
                        String emailID = snap.child(i + "").getValue(String.class);
                        //  Log.e("kitcehn",kitchenID);
                        Log.e("ids", number + "" + emailID);
                        emailList.add(emailDecode(emailID));

                        }
                        flag=1;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            while(flag==0);
            return emailList;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> strings) {
            doctorAdapter=new DoctorAdapter(DoctorMainActivity.this,strings);

            doctorAdapter.notifyDataSetChanged();
            listView.setAdapter(doctorAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Intent intent=new Intent(DoctorMainActivity.this,PatientImage.class);
                    intent.putExtra("Name",strings.get(position));
                    intent.putExtra("Number",position);


                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Patients").child("List").child(position+"");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                        name=  dataSnapshot.getValue(String.class);
                            Log.e("name",name);
                            intent.putExtra("Email",name);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });




        }
    }
}

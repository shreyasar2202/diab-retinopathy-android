package com.example.eye.zeisseyecare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    int pos;
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    //private TextView textViewSignup;

    private FirebaseAuth firebaseAuth;
    private Spinner spinner;
    private ProgressDialog progressDialog;
    int flag=0;

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if(firebaseAuth.getCurrentUser() != null){
            //close this activity
            RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.parentView);
            relativeLayout.setVisibility(View.INVISIBLE);
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Loading home page");
            progressDialog.setIcon(R.drawable.logo);
            progressDialog.show();
            FirebaseUser user=firebaseAuth.getCurrentUser();
           final String email =user.getEmail();
            new Thread(new Runnable() {
                @Override
                public void run() {


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Patients");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int no = dataSnapshot.child("number").getValue(Integer.class);
                            for (int i = 0; i < no; i++) {
                                if (dataSnapshot.child("List").child(i + "").getValue(String.class).equals(emailEncode(email))) {



                                    Intent intent=new Intent(getApplicationContext(),PatientMainActivity.class);
                                    intent.putExtra("Email",emailEncode(email));
                                    intent.putExtra("Pos",i);

                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Doctors");
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int no = dataSnapshot.child("number").getValue(Integer.class);
                            for (int i = 0; i < no; i++) {
                                if (dataSnapshot.child("List").child(i + "").getValue(String.class).equals(emailEncode(email))) {
                                    finish();
                                    progressDialog.dismiss();
                                    //opening profile activity
                                    startActivity(new Intent(getApplicationContext(), DoctorMainActivity.class));
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }).start();
        }
        setupUI(findViewById(R.id.parentView));
        spinner=(Spinner)findViewById(R.id.loginType);
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        textView=(TextView)findViewById(R.id.sign_up);
        textView.setOnClickListener(this);
        //if the objects getcurrentuser method is not null
        //means user is already logged in
//        if(firebaseAuth.getCurrentUser() != null){
//            //close this activity
//            finish();
//            //opening profile activity
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        }
        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);


        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);

    }
    private String emailEncode(String kitchenID) {
        return kitchenID.replace(".",",");
    }
    //method for user login
    private void userLogin() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter Email ID", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        //logging in the user
        String loginType = spinner.getSelectedItem().toString();
        if (loginType.equals("Patient")) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Patients");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int number = dataSnapshot.child("number").getValue(Integer.class);
                    //int number=11;
                    Log.e("Number of email", number + "");
                    DataSnapshot snap = dataSnapshot.child("List");
                    for (int i = 0; i < number; i++) {
                        String emailID = snap.child(i + "").getValue(String.class);
                        //  Log.e("kitcehn",kitchenID);
                        Log.e("ids", number + "" + emailID);
                        if (emailID.equals(emailEncode(email))) {
                            flag = 1;
                            pos=i;
                            break;
                        }
                    }
                    login(email,password,flag,pos);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int number = dataSnapshot.child("number").getValue(Integer.class);
                        //int number=11;
                        Log.e("Number of email", number + "");
                        DataSnapshot snap = dataSnapshot.child("List");
                        for (int i = 0; i < number; i++) {
                            String emailID = snap.child(i + "").getValue(String.class);
                            //  Log.e("kitcehn",kitchenID);
                            Log.e("ids", number + "" + emailID);
                            if (emailID.equals(emailEncode(email))) {
                                flag = 1;
                                pos=i;
                                break;
                            }
                        }login(email,password,flag,pos);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }



    }
    void login(final String email,String password,int flag,final int pos) {
        if (flag == 1) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            //if the task is succesfull
                            if (task.isSuccessful()) {
                                //start the profile activity
                                //finish();
                                String loginType = spinner.getSelectedItem().toString();
                                Log.e("Type", loginType);

                                if (loginType.equals("Patient")) {
                                    Intent intent=new Intent(getApplicationContext(),PatientMainActivity.class);
                                    intent.putExtra("Email",emailEncode(email));
                                    intent.putExtra("Pos",pos);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), DoctorMainActivity.class));
                                    finish();
                                }
                            }
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Please check EmailID/password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
        else {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this,"Check your details",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogin();
        }
        else if(view==textView)
        {   Log.e("asdas","sdasa");
            startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LoginActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}

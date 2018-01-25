package com.example.eye.zeisseyecare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    EditText editTextPassword;
    EditText editTextEmailID;
    EditText editTextReEnterPassword;
    EditText editTextName;
    Spinner spinner;
    Button buttonSignup;
    ProgressDialog progressDialog;
    TextView textView;
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setupUI(findViewById(R.id.parentView2));
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();


        editTextEmailID = (EditText) findViewById(R.id.editTextEmail);
        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextReEnterPassword = (EditText) findViewById(R.id.editTextReEnterPassword);
        buttonSignup = (Button) findViewById(R.id.buttonSignIn);
        textView=(TextView)findViewById(R.id.log);
        spinner=(Spinner)findViewById(R.id.loginType);
        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textView.setOnClickListener(this);
    }

    private void registerUser() {

        //getting email and password from edit texts
        final String emailID = editTextEmailID.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String name =editTextName.getText().toString().trim();
        String reEnterPassword = editTextReEnterPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(emailID)) {
            Toast.makeText(this, "Please enter email ID", Toast.LENGTH_LONG).show();
            return;
        }


        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(reEnterPassword)) {
            Toast.makeText(this, "Please re-enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(reEnterPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }
        //if the email and password are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        adduserdetails(emailID,password);

    }
    private String emailEncode(String kitchenID) {
        return kitchenID.replace(".",",");
    }


    @Override
    public void onClick(View view) {
        //calling register method on click
        if(view==buttonSignup) {
            registerUser();
        }
        if(view==textView)
        {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
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
                    hideSoftKeyboard(SignUpActivity.this);
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
    void adduserdetails(final String email,final String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {



                            editTextPassword.setText("");
                            editTextReEnterPassword.setText("");
                            editTextEmailID.setText("");
                            editTextName.setText("");
                            String type=spinner.getSelectedItem().toString();

                            if(type.equals("Patient")) {


                               final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Patients");
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        number=dataSnapshot.child("number").getValue(Integer.class);
                                        reference.child("List").child(number+"").setValue(emailEncode(email));
                                        reference.child("Name").child(number+"").setValue(emailEncode(email));
                                        reference.child("number").setValue(number+1);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            else{


                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors");
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        number=dataSnapshot.child("number").getValue(Integer.class);
                                        reference.child("List").child(number+"").setValue(emailEncode(email));
                                        reference.child("Name").child(number+"").setValue(emailEncode(email));
                                        reference.child("number").setValue(number+1);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();



                        } else {
                            //display some message here
                            Toast.makeText(SignUpActivity.this, "Registration Error", Toast.LENGTH_LONG).show();


                        }
                        progressDialog.dismiss();
                    }
                });

    }





}

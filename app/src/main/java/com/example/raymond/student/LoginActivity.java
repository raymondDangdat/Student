package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymond.student.Common.Common;
import com.example.raymond.student.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private TextView mRegister;
    private EditText mEmail, mPassword;
    private Button mButtonLogin, btnForgotPassword;

    private Toolbar loginToolBar;

    private ProgressDialog mProgress;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseStudents, chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        chatRef = FirebaseDatabase.getInstance().getReference().child("ChatUsers");
        mDatabaseStudents = FirebaseDatabase.getInstance().getReference().child("Students");
        mDatabaseStudents.keepSynced(true);


        mRegister = findViewById(R.id.textviewRegister);
        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPassword);
        mButtonLogin = findViewById(R.id.buttonLogin);
        btnForgotPassword = findViewById(R.id.buttonForgottenPassword);

        //initialize our toolBar
        loginToolBar = findViewById(R.id.login_toolbar);
        setSupportActionBar(loginToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("Accommodation At Your Convenience");



        mProgress = new ProgressDialog(this);



        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SetUpActivity.class));
                finish();
            }
        });


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });
    }

    private void sendForgotPasswordLink() {

    }

    private void checkLogin() {
        mProgress.setMessage("Logging in...");
        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    //check if userExist in our database
                   // checkUserExist();

                    //verify email
                    //checkEmailVerification();
                    mProgress.dismiss();

                    startActivity(new Intent(LoginActivity.this, Home.class));
                    finish();




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgress.dismiss();
                    Toasty.error(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }else{
            Toasty.info(this, "Sorry, can't login with empty field(s)", Toast.LENGTH_SHORT).show();
        }
    }

    //verify email before login
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();
        if (emailFlag){
            String currentUserId = mAuth.getCurrentUser().getUid();
            String deviceToken = FirebaseInstanceId.getInstance().getToken();
            chatRef.child(currentUserId).child("device_token")
                    .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        finish();
                        startActivity(new Intent(LoginActivity.this, Home.class));

                    }else {

                    }

                }
            });

        }else {
            //user has to verify email
            Toast.makeText(this, "Verify your email first", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }
}

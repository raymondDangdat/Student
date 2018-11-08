package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymond.student.Common.Common;
import com.example.raymond.student.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private TextView mRegister;
    private EditText mEmail, mPassword;
    private Button mButtonLogin;

    private ProgressDialog mProgress;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        mDatabaseStudents = FirebaseDatabase.getInstance().getReference().child("Students");
        mDatabaseStudents.keepSynced(true);


        mRegister = findViewById(R.id.textviewRegister);
        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPassword);
        mButtonLogin = findViewById(R.id.buttonLogin);


        mProgress = new ProgressDialog(this);



        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SetUpActivity.class));
            }
        });


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
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
                    checkEmailVerification();


                    mProgress.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }else{
            Toast.makeText(this, "Sorry, can't login with empty field(s)", Toast.LENGTH_SHORT).show();
        }
    }

////    //check if user exist
////    private void checkUserExist() {
////        final String user_id = mAuth.getCurrentUser().getUid();
////        mDatabaseStudents.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.hasChild(user_id)){
////                    startActivity(new Intent(LoginActivity.this, Profile.class));
////
////                }else{
////                    startActivity(new Intent(LoginActivity.this, SetUpActivity.class));
////                }
////
////            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//
    //verify email before login
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();
        if (emailFlag){
            finish();

//
//            databaseReferenceUser.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
            //Intent loginIntent = new Intent(LoginActivity.this, Profile.class);
            startActivity(new Intent(LoginActivity.this, Home.class));
        }else {
            //user has to verify email
            Toast.makeText(this, "Verify your email first", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }
}

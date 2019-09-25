package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private Toolbar forgotPasswordToolBar;
    private Button btnResetPassword;
    private EditText editTextEmail;
    private ProgressDialog dialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnResetPassword = findViewById(R.id.buttonReset);
        editTextEmail = findViewById(R.id.editTextEmail);

        dialog = new ProgressDialog(this);
        auth =FirebaseAuth.getInstance();

        //initialize our toolBar
        forgotPasswordToolBar = findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(forgotPasswordToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("Forgotten Password");


        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Sending password reset email...");
                String userEmail = editTextEmail.getText().toString().trim();

                if (userEmail.equals("")){
                    Toast.makeText(ForgotPassword.this, "Please enter the email you created account with", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.show();
                    auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(ForgotPassword.this, "Check your email for the link to reset your password", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


    }
}

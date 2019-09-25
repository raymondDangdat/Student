package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassword extends AppCompatActivity {
    private Toolbar changePasswordToolBar;

    private Button btnResetPassword;
    private FirebaseAuth mAuth;
    private String userEmail;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dialog = new ProgressDialog(this);


        //initialize our toolBar
        changePasswordToolBar = findViewById(R.id.change_password_toolbar);
        setSupportActionBar(changePasswordToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        btnResetPassword = findViewById(R.id.buttonResetPassword);
        mAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Sending password reset email");
                dialog.show();
                userEmail = mAuth.getCurrentUser().getEmail();

                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(ChangePassword.this, "Reset email sent to "+ userEmail, Toast.LENGTH_SHORT).show();
                            //logout
                            Intent signIn = new Intent(ChangePassword.this, LoginActivity.class);
                            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mAuth.signOut();
                            startActivity(signIn);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChangePassword.this, Home.class));
                        finish();

                    }
                });
            }
        });
    }
}

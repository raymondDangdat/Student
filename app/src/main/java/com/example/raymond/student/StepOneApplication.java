package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StepOneApplication extends AppCompatActivity {
    private EditText editTextCode;
    private Button btnSubmit;
    private ProgressDialog dialog;

    private Toolbar codeToolBar;

    private DatabaseReference codes, students;
    String uId = "";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_one_application);


        //initialize our toolBar
        codeToolBar= findViewById(R.id.code_toolbar);
        setSupportActionBar(codeToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Reg. Code");


        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        dialog = new ProgressDialog(this);

        codes = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("registrationCodes");
        students = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("users").child("Students");



        editTextCode = findViewById(R.id.editTextCode);
        btnSubmit = findViewById(R.id.submit);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = editTextCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)){
                    Toast.makeText(StepOneApplication.this, "Enter your code please", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setMessage("Verifying code...");
                    dialog.show();
                    codes.child(code).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                String status = dataSnapshot.child("status").getValue().toString();
                                if (status.equals("Used")){
                                    dialog.dismiss();
                                    Toast.makeText(StepOneApplication.this, "Your code has been used", Toast.LENGTH_SHORT).show();
                                }else {
                                    codes.child(code).child("status").setValue("Used");
                                    students.child(uId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String gender = dataSnapshot.child("gender").getValue().toString();
                                            if (gender.equals("Male")){
                                                dialog.dismiss();
                                                startActivity(new Intent(StepOneApplication.this, BoysRoomsList.class));
                                                finish();
                                            }else {
                                                dialog.dismiss();
                                                startActivity(new Intent(StepOneApplication.this, GirlsRoomsList.class));
                                                finish();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }else {
                                dialog.dismiss();
                                Toast.makeText(StepOneApplication.this, "Invalid code", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}

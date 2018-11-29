package com.example.raymond.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class JambValidity extends AppCompatActivity {
    private Button btnConfirm;
    private EditText editTextJamb;

    private DatabaseReference confirmStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jamb_validity);

        confirmStudent = FirebaseDatabase.getInstance().getReference().child("confirmJambNumbers");


        btnConfirm = findViewById(R.id.btnConfirm);
        editTextJamb = findViewById(R.id.edtMatOrJamb);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStudent();
            }
        });
    }

    private void verifyStudent() {
        final String jambNo = editTextJamb.getText().toString().trim();
        if (TextUtils.isEmpty(jambNo)){
            Toast.makeText(this, "Empty JAMB No.", Toast.LENGTH_SHORT).show();
        }else {
            confirmStudent.child(jambNo).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String status = dataSnapshot.child("status").getValue().toString();
                        if (status.equals("valid")){
                            confirmStudent.child(jambNo).child("status").setValue("invalid");
                            startActivity(new Intent(JambValidity.this, BoysChalets.class));
                            finish();
                        }else {
                            Toast.makeText(JambValidity.this, "Number already used", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(JambValidity.this, "Invalid JAMB number", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}

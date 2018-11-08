package com.example.raymond.student;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button updateAccountButton;
    private EditText editTextUserName, editTextStatus;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference chatUsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        chatUsersRef = FirebaseDatabase.getInstance().getReference("ChatUsers");

        initializeFields();
        editTextUserName.setVisibility(View.INVISIBLE);


        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveUserInfo();
    }




    private void initializeFields() {
        updateAccountButton = findViewById(R.id.btn_update_settings);
        editTextStatus = findViewById(R.id.set_profile_status);
        editTextUserName = findViewById(R.id.set_user_name);
        userProfileImage = findViewById(R.id.set_profile_image);
    }

    private void updateSettings() {
        String setUserName = editTextUserName.getText().toString();
        String setUserStatus = editTextStatus.getText().toString();
        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please your username is required", Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(this, "Please write your status", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);

            chatUsersRef.child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                Intent mainActivity = new Intent(SettingsActivity.this, PlasuChat.class);
                                startActivity(mainActivity);
                            }else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void retrieveUserInfo() {
        chatUsersRef.child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){
                            String retrievedUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievedUserStatus = dataSnapshot.child("status").getValue().toString();
                            String retrievedProfileImage = dataSnapshot.child("image").getValue().toString();

                            editTextUserName.setText(retrievedUserName);
                            editTextStatus.setText(retrievedUserStatus);

                        }else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrievedUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievedUserStatus = dataSnapshot.child("status").getValue().toString();


                            editTextUserName.setText(retrievedUserName);
                            editTextStatus.setText(retrievedUserStatus);
                        }else {
                            editTextUserName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set and update your profile information", Toast.LENGTH_SHORT).show();


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}

package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {
    private Button updateAccountButton;
    private EditText editTextUserName, editTextStatus;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference chatUsersRef;
    private StorageReference userProfileImagesRef;
    private ProgressDialog progressDialog;

    private Toolbar settingsToolBar;

    private static final int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        chatUsersRef = FirebaseDatabase.getInstance().getReference("ChatUsers");
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        initializeFields();
        editTextUserName.setVisibility(View.INVISIBLE);


        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);

            }
        });

    }




    private void initializeFields() {
        updateAccountButton = findViewById(R.id.btn_update_settings);
        editTextStatus = findViewById(R.id.set_profile_status);
        editTextUserName = findViewById(R.id.set_user_name);
        userProfileImage = findViewById(R.id.set_profile_image);
        progressDialog = new ProgressDialog(this);

        //initialize our toolBar
        settingsToolBar = findViewById(R.id.settings_tool_bar);
        setSupportActionBar(settingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Settings");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data !=null){
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode ==RESULT_OK){
                progressDialog.setTitle("Setting Profile Image");
                progressDialog.setMessage("Please wait, profile image updating");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImagesRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toasty.info(SettingsActivity.this, "Profile image uploaded successfully", Toast.LENGTH_SHORT).show();
                            String downloadUrl = task.getResult().getDownloadUrl().toString();
                            chatUsersRef.child(currentUserId).child("image").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toasty.info(SettingsActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                                            }else{
                                                progressDialog.dismiss();
                                                String message = task.getException().toString();
                                                Toasty.error(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }else {
                            progressDialog.dismiss();
                            String message = task.getException().toString();
                            Toasty.error(SettingsActivity.this, "ERROR: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    private void updateSettings() {
        String setUserName = editTextUserName.getText().toString();
        String setUserStatus = editTextStatus.getText().toString();
        if (TextUtils.isEmpty(setUserName)){
            Toasty.info(this, "Please your username is required", Toast.LENGTH_SHORT).show();
        }if (TextUtils.isEmpty(setUserStatus)){
            Toasty.info(this, "Please write your status", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);

            chatUsersRef.child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toasty.success(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                Intent mainActivity = new Intent(SettingsActivity.this, PlasuChat.class);
                                startActivity(mainActivity);
                            }else {
                                String message = task.getException().toString();
                                Toasty.error(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                            Picasso.get().load(retrievedProfileImage).into(userProfileImage);

                        }else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrievedUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievedUserStatus = dataSnapshot.child("status").getValue().toString();


                            editTextUserName.setText(retrievedUserName);
                            editTextStatus.setText(retrievedUserStatus);
                        }else {
                            editTextUserName.setVisibility(View.VISIBLE);
                            Toasty.info(SettingsActivity.this, "Please set and update your profile information", Toast.LENGTH_SHORT).show();


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}

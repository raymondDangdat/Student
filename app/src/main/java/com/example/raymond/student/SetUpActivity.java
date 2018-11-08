package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetUpActivity extends AppCompatActivity {
    private EditText setUName;
    private Button setUpUpdate;
    private ImageView setUpProfileImage;
    private Spinner mFaculty;
    private Spinner mDepartment;
    private EditText editTextSname;
    private EditText editTextFname;
    private EditText editTextLname;
    private Spinner spinnerGender;
    private EditText editTextMatriculation;
    private EditText editTextPhone;
    private EditText editTextEmergencyNo;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCPassword;
    private TextView textViewSignIn;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;

    ProgressDialog mProgress;
    private ProgressDialog verificationProgress;

    private DatabaseReference mDatabaseStudents;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

//        //Action bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Registration");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//


        mProgress = new ProgressDialog(this);
        verificationProgress = new ProgressDialog(this);


        mDatabaseStudents = FirebaseDatabase.getInstance().getReference().child("Students");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("StudentsProfile");


        setUName = findViewById(R.id.setUpUsername);
        setUpUpdate = findViewById(R.id.buttonUpdate);
        setUpProfileImage = findViewById(R.id.setUpImageButton);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        mFaculty = findViewById(R.id.faculty);
        mDepartment = findViewById(R.id.department);
        editTextFname = findViewById(R.id.editTextFname);
        editTextSname = findViewById(R.id.editTextname);
        editTextLname = findViewById(R.id.editTextLname);
        spinnerGender = findViewById(R.id.gender);
        editTextMatriculation = findViewById(R.id.editTextMatriculation);
        editTextPhone = findViewById(R.id.phone);
        editTextEmergencyNo = findViewById(R.id.editTextEmergencyNo);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCPassword = findViewById(R.id.editTextCPassword);



        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetUpActivity.this, LoginActivity.class));
            }
        });

        setUpUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startSetUpAccount();
                registerUser();
            }
        });


        setUpProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

    }

    private void startSetUpAccount() {
        mProgress.setMessage("Updating...");
       final String name = setUName.getText().toString().trim();
       final String user_id = mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(name) && mImageUri !=null){
            mProgress.show();

            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseStudents.child(user_id).child("name").setValue(name);
                    mDatabaseStudents.child(user_id).child("image").setValue(downloadUrl);
                    mProgress.dismiss();
                    Toast.makeText(SetUpActivity.this, "You have successfully updated your profile", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetUpActivity.this, Profile.class));


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgress.dismiss();
                    Toast.makeText(SetUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();

                }
            });






        }else{
            Toast.makeText(this, "You can't update your profile without choosing and image and a username", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //to set it to square
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                setUpProfileImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    //trial
    private void registerUser() {
        mProgress.setMessage("Registering...");
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextCPassword.getText().toString();
        final String Surname = editTextSname.getText().toString().trim();
        final String faculty = mFaculty.getSelectedItem().toString().trim();
        final String department = mDepartment.getSelectedItem().toString().trim();
        final String firstName = editTextFname.getText().toString().trim();
        final String lastName = editTextLname.getText().toString().trim();
        final String matriculationNo = editTextMatriculation.getText().toString().trim();
        final String gender = spinnerGender.getSelectedItem().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String emergencyNo = editTextEmergencyNo.getText().toString().trim();
        final String username = setUName.getText().toString().trim();


        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && mImageUri != null
                && !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(matriculationNo) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(emergencyNo) && !TextUtils.isEmpty(username)){
            if (password.equals(confirmPassword) && !TextUtils.isEmpty(Surname)){
                mProgress.show();
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
                        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final String user_id = mAuth.getCurrentUser().getUid();
                                String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                                mDatabaseStudents.child(user_id).child("email").setValue(email);
                                mDatabaseStudents.child(user_id).child("username").setValue(username);
                                mDatabaseStudents.child(user_id).child("surname").setValue(Surname);
                                mDatabaseStudents.child(user_id).child("firstName").setValue(firstName);
                                mDatabaseStudents.child(user_id).child("lastName").setValue(lastName);
                                mDatabaseStudents.child(user_id).child("department").setValue(department);
                                mDatabaseStudents.child(user_id).child("faculty").setValue(faculty);
                                mDatabaseStudents.child(user_id).child("gender").setValue(gender);
                                mDatabaseStudents.child(user_id).child("matNo").setValue(matriculationNo);
                                mDatabaseStudents.child(user_id).child("phone").setValue(phone);
                                mDatabaseStudents.child(user_id).child("emergencyNo").setValue(emergencyNo);

                                mDatabaseStudents.child(user_id).child("image").setValue(downloadUrl);
                                mProgress.dismiss();
                                Toast.makeText(SetUpActivity.this, "You have successfully updated your profile", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SetUpActivity.this, LoginActivity.class));
                                sendEmailVerification();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgress.dismiss();
                                Toast.makeText(SetUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                finish();


                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
//                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        String user_id = mAuth.getCurrentUser().getUid();
//                        DatabaseReference current_user =  mDatabaseStudents.child(user_id);
//                        current_user.child("name").setValue(Surname);
//                        current_user.child("image").setValue("default for now");
//                        mProgress.dismiss();
//                        Toast.makeText(SetUpActivity.this, "Registration successful you can proceed to login", Toast.LENGTH_SHORT).show();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        mProgress.dismiss();
//                        Toast.makeText(SetUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
            }else {
                Toast.makeText(this, "Password miss match", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "Sorry can register with empty field(s) or without picture", Toast.LENGTH_SHORT).show();
        }

    }

    //send verification to the user registering
    private void sendEmailVerification(){
        verificationProgress.setMessage("Sending verification mail...");
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null){
            verificationProgress.show();
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        verificationProgress.dismiss();
                        Toast.makeText(SetUpActivity.this, "Registered successfully!! check verification mail sent to you", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    verificationProgress.dismiss();
                    Toast.makeText(SetUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }
    }
}

package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymond.student.Model.BoysRooms;
import com.example.raymond.student.Model.GirlsRooms;
import com.example.raymond.student.Model.JambConfirmation;
import com.example.raymond.student.Model.Pin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class BoysRoomDetail extends AppCompatActivity {
    private TextView room_name, status, bed_number;
    private ImageView img_room;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Button btnApply1;

    private String roomId = "";
    private String chaletId = "";

    private FirebaseDatabase database;
    private DatabaseReference boysRooms, eligibleStudents;
    private DatabaseReference pins;


    //reference to applications
    private DatabaseReference students;
    private DatabaseReference applications;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boys_room_detail);

        //int firebase
        database = FirebaseDatabase.getInstance();
        boysRooms = database.getReference("BoysRooms");
        eligibleStudents = database.getReference().child("confirmJambNumbers");


        //get  currentUser
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        applications = FirebaseDatabase.getInstance().getReference().child("Applications");
        pins = database.getReference("Pins");
        students = database.getReference("Students");

        uId = mAuth.getUid();

        //initialize views
        room_name = findViewById(R.id.room_name);
        bed_number = findViewById(R.id.bed_number);
        img_room = findViewById(R.id.img_room);
        status = findViewById(R.id.status);


        btnApply1 = findViewById(R.id.btnApply1);



        btnApply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });



        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //get room id from Intent
        if (getIntent() != null){
            roomId = getIntent().getStringExtra("roomId");
            chaletId = getIntent().getStringExtra("chaletId");
            if (!roomId.isEmpty()){
                getDetailRoom(roomId);
            }

        }


    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BoysRoomDetail.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your application pin ");

        final EditText editTextJambNo = new EditText(BoysRoomDetail.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editTextJambNo.setLayoutParams(lp);
        alertDialog.setView(editTextJambNo);// add edittext to alert dialog
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog mDialog = new ProgressDialog(BoysRoomDetail.this);
                mDialog.setMessage("Applying please wait...");
                mDialog.show();

                final String jamNo = editTextJambNo.getText().toString().trim();
                if (TextUtils.isEmpty(jamNo)){
                    mDialog.dismiss();
                    Toasty.info(BoysRoomDetail.this, "You can not proceed without providing yoour JAMB number", Toast.LENGTH_LONG).show();
                }else {


                    eligibleStudents.child(jamNo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                final String status = dataSnapshot.child("status").getValue().toString();
                                //final JambConfirmation jambConfirmation = dataSnapshot.child(jamNo).getValue(JambConfirmation.class);
                                //jambConfirmation.setJambNo(jamNo);
                                if (status.equals("valid")){
                                    //yet to apply for hostel

                                    students.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {


                                            final String email = dataSnapshot.child(uId).child("email").getValue(String.class);
                                            final String fullName = dataSnapshot.child(uId).child("fullName").getValue(String.class);
                                            final String department = dataSnapshot.child(uId).child("department").getValue(String.class);
                                            final String matNo = dataSnapshot.child(uId).child("matNo").getValue(String.class);
                                            final String phone = dataSnapshot.child(uId).child("phone").getValue(String.class);
                                            final String emergenNo = dataSnapshot.child(uId).child("emergencyNo").getValue(String.class);
                                            final String profileUri = dataSnapshot.child(uId).child("image").getValue(String.class);
                                            final String level = dataSnapshot.child(uId).child("level").getValue(String.class);
                                            final String gender = dataSnapshot.child(uId).child("gender").getValue(String.class);

                                            if (!fullName.isEmpty() || !department.isEmpty() || !matNo.isEmpty() ||
                                                    !phone.isEmpty() || !emergenNo.isEmpty() || !profileUri.isEmpty() || !gender.isEmpty()){
                                                boysRooms.child(roomId).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        final GirlsRooms girlsRooms = dataSnapshot.getValue(GirlsRooms.class);


                                                        String chaletName = girlsRooms.getRoomDescription();
                                                        String bed_number = girlsRooms.getBedNumber();
                                                        applications.child(uId).child("department").setValue(department);
                                                        applications.child(uId).child("chaletId").setValue(chaletId);
                                                        applications.child(uId).child("level").setValue(level);
                                                        applications.child(uId).child("JAMB").setValue(jamNo);
                                                        applications.child(uId).child("chaletName").setValue(chaletName);
                                                        applications.child(uId).child("bedNumber").setValue(bed_number);
                                                        applications.child(uId).child("email").setValue(email);
                                                        applications.child(uId).child("chaletName").setValue(chaletName);
                                                        applications.child(uId).child("bedNumber").setValue(bed_number);
                                                        applications.child(uId).child("phone").setValue(phone);
                                                        applications.child(uId).child("fullName").setValue(fullName);
                                                        applications.child(uId).child("parentNo").setValue(emergenNo);
                                                        applications.child(uId).child("profilePic").setValue(profileUri);
                                                        applications.child(uId).child("gender").setValue(gender);
                                                        applications.child(uId).child("matNo").setValue(matNo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    //change the status to invalid so that same matriculation number cannot be used twice
                                                                    eligibleStudents.child(jamNo).child("status").setValue("invalid").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                mDialog.dismiss();
                                                                                updateRoomDetails(roomId);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });


                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Toasty.error(BoysRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }



                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toasty.error(BoysRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });




                                }else{
                                    mDialog.dismiss();
                                    Toasty.warning(BoysRoomDetail.this, "Sorry this JAMB number has already been used", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            }else {
                                mDialog.dismiss();
                                Toasty.error(BoysRoomDetail.this, "Invalid JAMB number please check and type again", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                    }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


//update room detail after a successful application
    private void updateRoomDetails(final String roomId) {
        boysRooms.child(roomId).child("status").setValue("occupied").addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(BoysRoomDetail.this, "Application successful", Toast.LENGTH_SHORT).show();
                        getDetailRoom(roomId);
                    }
                }
        );

    }

    private void getDetailRoom(final String roomId) {
        boysRooms.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BoysRooms boysRooms = dataSnapshot.getValue(BoysRooms.class);

                //set Image
//                Picasso.with(getBaseContext()).load(boysRooms.getImage())
//                        .into(img_room);
                Picasso.get().load(boysRooms.getImage()).into(img_room);
                collapsingToolbarLayout.setTitle(boysRooms.getRoomDescription());
                room_name.setText(boysRooms.getRoomDescription());
                bed_number.setText(boysRooms.getBedNumber());
                status.setText(boysRooms.getStatus());
                final String Status = boysRooms.getStatus();

                if (Status.equals("available")){
                    btnApply1.setEnabled(true);
                }else{
                    btnApply1.setEnabled(false);
                    btnApply1.setText("ROOM OCCUPIED");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

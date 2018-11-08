package com.example.raymond.student;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymond.student.Model.BoysRooms;
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

public class BoysRoomDetail extends AppCompatActivity {
    private TextView room_name, status, bed_number;
    private ImageView img_room;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Button btnApply1;

    private String roomId = "";

    private FirebaseDatabase database;
    private DatabaseReference boysRooms;
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
        if (getIntent() != null)
            roomId = getIntent().getStringExtra("roomId");
        if (!roomId.isEmpty()){
            getDetailRoom(roomId);

        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BoysRoomDetail.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your application pin ");

        final EditText editTextPin = new EditText(BoysRoomDetail.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editTextPin.setLayoutParams(lp);
        alertDialog.setView(editTextPin);// add edittext to alert dialog
        alertDialog.setIcon(R.drawable.ic_home_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                        final String pin =  editTextPin.getText().toString().trim();
                        if (pin.isEmpty()){
                            Toast.makeText(BoysRoomDetail.this, "Type in your application PIN", Toast.LENGTH_SHORT).show();
                        }else{
                            Query query = FirebaseDatabase.getInstance().getReference("Pins").orderByChild("pin").equalTo(1468312135);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if ((dataSnapshot.exists())){
                                        DatabaseReference pinss = FirebaseDatabase.getInstance().getReference("Pins");
//                                        pinss.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                Pin ppin = dataSnapshot.getValue(Pin.class);
//                                                final String getStatus = ppin.getStatus();
//                                                if (getStatus.equals("unused")){
//                                                    Toast.makeText(BoysRoomDetail.this, "another hope", Toast.LENGTH_SHORT).show();
//                                                }
//
////
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
                                        Pin ppin = dataSnapshot.getValue(Pin.class);
                                        final String getStatus = ppin.getStatus();
                                        //if (!getStatus.equals("unused")){
                                            students.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    final String email = dataSnapshot.child(uId).child("email").getValue(String.class);
                                                    final String surname = dataSnapshot.child(uId).child("surname").getValue(String.class);
                                                    final String firstName = dataSnapshot.child(uId).child("firstName").getValue(String.class);
                                                    final String lastName = dataSnapshot.child(uId).child("lastName").getValue(String.class);
                                                    final String department = dataSnapshot.child(uId).child("department").getValue(String.class);
                                                    final String matNo = dataSnapshot.child(uId).child("matNo").getValue(String.class);
                                                    final String phone = dataSnapshot.child(uId).child("phone").getValue(String.class);
                                                    final String emergenNo = dataSnapshot.child(uId).child("emergencyNo").getValue(String.class);
                                                    final String profileUri = dataSnapshot.child(uId).child("image").getValue(String.class);
                                                    final String level = dataSnapshot.child(uId).child("level").getValue(String.class);
                                                    final String gender = dataSnapshot.child(uId).child("gender").getValue(String.class);

                                                    if (!surname.isEmpty() || !firstName.isEmpty() || !lastName.isEmpty() || !department.isEmpty() || !matNo.isEmpty() ||
                                                            !phone.isEmpty() || !emergenNo.isEmpty() || !profileUri.isEmpty() || !gender.isEmpty()){
                                                        boysRooms.child(roomId).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                final BoysRooms boysRooms = dataSnapshot.getValue(BoysRooms.class);

                                                                String chaletName = boysRooms.getRoomDescription();
                                                                String bed_number = boysRooms.getBedNumber();
                                                                applications.child(uId).child("PIN").setValue(pin);
                                                                applications.child(uId).child("email").setValue(email);
                                                                applications.child(uId).child("chaletName").setValue(chaletName);
                                                                applications.child(uId).child("bedNumber").setValue(bed_number);
                                                                applications.child(uId).child("phone").setValue(phone);
                                                                applications.child(uId).child("surname").setValue(surname);
                                                                applications.child(uId).child("firstName").setValue(firstName);
                                                                applications.child(uId).child("lastName").setValue(lastName);
                                                                applications.child(uId).child("parentNo").setValue(emergenNo);
                                                                applications.child(uId).child("profilePic").setValue(profileUri);
                                                                applications.child(uId).child("matNo").setValue(matNo).addOnCompleteListener(
                                                                        new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    updateRoomDetails(roomId);
                                                                                }
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });//second boys room value event listener
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });//second value eventListener

//                                        }else if (getStatus.equals("used")){
//                                            Toast.makeText(BoysRoomDetail.this, "PIN has aleady been used", Toast.LENGTH_SHORT).show();
//                                        }
                                    }else{
                                        Toast.makeText(BoysRoomDetail.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });//trial

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
                        Toast.makeText(BoysRoomDetail.this, "Application successful", Toast.LENGTH_SHORT).show();
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
                Picasso.with(getBaseContext()).load(boysRooms.getImage())
                        .into(img_room);
                collapsingToolbarLayout.setTitle(boysRooms.getRoomDescription());
                room_name.setText(boysRooms.getRoomDescription());
                bed_number.setText(boysRooms.getBedNumber());
                status.setText(boysRooms.getStatus());
                final String Status = boysRooms.getStatus();

                if (Status.equals("available")){
                    btnApply1.setEnabled(true);
                }else{
                    btnApply1.setEnabled(false);
                    btnApply1.setText("Button disabled, room occupied");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

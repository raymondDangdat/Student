package com.example.raymond.student;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class GirlsRoomDetail extends AppCompatActivity {

    private TextView room_name, bed_number, status;
    private ImageView img_room;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Button btnApply1;

    private String roomId = "";
    private ProgressDialog dialog;

    private FirebaseDatabase database;
    private DatabaseReference girlsRooms, students, regCodes;

    //reference to applications
    private DatabaseReference applications;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String uId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girls_room_detail);

        dialog = new ProgressDialog(this);

        //int firebase
        database = FirebaseDatabase.getInstance();
        girlsRooms = database.getReference().child("plasuHostel2019").child("hostels").child("girlsHostel").child("GirlsRooms");;


        //get  currentUser
        mAuth = FirebaseAuth.getInstance();
        applications = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("Occupants");
        students = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("users").child("Students");
        regCodes = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("registrationCodes");

        uId = mAuth.getUid();

        //initialize views
        room_name = findViewById(R.id.room_name);
        bed_number = findViewById(R.id.bed_number);
        img_room = findViewById(R.id.img_room);
        btnApply1 = findViewById(R.id.btnApply1);
        status = findViewById(R.id.status);

        //get room id from Intent
        if (getIntent() != null){
            roomId = getIntent().getStringExtra("roomId");

            if (!roomId.isEmpty()){
                getDetailRoom(roomId);
            }

        }


        btnApply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allocateStudent();

//                applications.child(uId).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()){
//                            Toasty.info(GirlsRoomDetail.this, "You have been allocated, please check your status",Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(GirlsRoomDetail.this, Home.class));
//                            finish();
//                        }else {
////
////                            Intent myIntent = new Intent(GirlsRoomDetail.this, CompleteApplication.class);
////                            myIntent.putExtra("roomId",roomId );
////                            myIntent.putExtra("uId", uId);
////                            startActivity(myIntent);
                                //showAlertDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }
        });



        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


    }

    private void allocateStudent() {
        dialog.setMessage("Allocating please wait...");
        dialog.show();
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
                    girlsRooms.child(roomId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final GirlsRooms girls_Rooms = dataSnapshot.getValue(GirlsRooms.class);
                            final String chaletId = dataSnapshot.child("room").getValue().toString();

                            String chaletName = girls_Rooms.getRoomDescription();
                            String bed_number = girls_Rooms.getBedNumber();
                            applications.child(uId).child("department").setValue(department);
                            applications.child(uId).child("chaletId").setValue(chaletId);
                            applications.child(uId).child("level").setValue(level);
                            applications.child(uId).child("chaletName").setValue(chaletName);
                            applications.child(uId).child("bedNumber").setValue(bed_number);
                            applications.child(uId).child("email").setValue(email);
                            applications.child(uId).child("chaletName").setValue(chaletName);
                            applications.child(uId).child("bedNumber").setValue(bed_number);
                            applications.child(uId).child("phone").setValue(phone);
                            applications.child(uId).child("fullName").setValue(fullName);
                            applications.child(uId).child("parentNo").setValue(emergenNo);
                            applications.child(uId).child("profilePic").setValue(profileUri);
                            applications.child(uId).child("status").setValue("No");
                            applications.child(uId).child("gender").setValue(gender);
                            applications.child(uId).child("matNo").setValue(matNo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        dialog.dismiss();
                                        Toasty.success(GirlsRoomDetail.this, "Allocated successfully", Toast.LENGTH_SHORT).show();
                                        girlsRooms.child(roomId).child("status").setValue("occupied");
                                        finish();
                                    }
                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toasty.error(GirlsRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toasty.error(GirlsRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
//
//    private void showAlertDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GirlsRoomDetail.this);
//        alertDialog.setTitle("One more step!");
//        alertDialog.setMessage("Enter your Reg. Code ");
//
//        final EditText editTextJambNo = new EditText(GirlsRoomDetail.this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT
//        );
//
//        editTextJambNo.setLayoutParams(lp);
//        alertDialog.setView(editTextJambNo);// add edittext to alert dialog
//        alertDialog.setIcon(R.drawable.ic_home_black_24dp);
//
//        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                final ProgressDialog mDialog = new ProgressDialog(GirlsRoomDetail.this);
//                mDialog.setMessage("Applying please wait...");
//                mDialog.show();
//
//                final String code = editTextJambNo.getText().toString().trim();
//                if (TextUtils.isEmpty(code)){
//                    mDialog.dismiss();
//                    Toasty.info(GirlsRoomDetail.this, "You can not proceed without providing your JAMB number", Toast.LENGTH_LONG).show();
//                    finish();
//                }else {
//                    regCodes.child(code).addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.exists()){
//                                            final String status = dataSnapshot.child("status").getValue().toString();
//                                            //final JambConfirmation jambConfirmation = dataSnapshot.child(jamNo).getValue(JambConfirmation.class);
//                                            //jambConfirmation.setJambNo(jamNo);
//                                            if (status.equals("valid")){
//                                                //yet to apply for hostel
//
//                                                students.addValueEventListener(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                                                        final String email = dataSnapshot.child(uId).child("email").getValue(String.class);
//                                                        final String fullName = dataSnapshot.child(uId).child("fullName").getValue(String.class);
//                                                        final String department = dataSnapshot.child(uId).child("department").getValue(String.class);
//                                                        final String matNo = dataSnapshot.child(uId).child("matNo").getValue(String.class);
//                                                        final String phone = dataSnapshot.child(uId).child("phone").getValue(String.class);
//                                                        final String emergenNo = dataSnapshot.child(uId).child("emergencyNo").getValue(String.class);
//                                                        final String profileUri = dataSnapshot.child(uId).child("image").getValue(String.class);
//                                                        final String level = dataSnapshot.child(uId).child("level").getValue(String.class);
//                                                        final String gender = dataSnapshot.child(uId).child("gender").getValue(String.class);
//
//
//                                                        if (!fullName.isEmpty() || !department.isEmpty() || !matNo.isEmpty() ||
//                                                                !phone.isEmpty() || !emergenNo.isEmpty() || !profileUri.isEmpty() || !gender.isEmpty()){
//                                                            girlsRooms.child(roomId).addValueEventListener(new ValueEventListener() {
//                                                                @Override
//                                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                    final GirlsRooms girlsRooms = dataSnapshot.getValue(GirlsRooms.class);
//
//                                                                    final String chaletId = dataSnapshot.child("room").getValue().toString();
//
//
//                                                                    String chaletName = girlsRooms.getRoomDescription();
//                                                                    String bed_number = girlsRooms.getBedNumber();
//                                                                    applications.child(uId).child("department").setValue(department);
//                                                                    applications.child(uId).child("chaletId").setValue(chaletId);
//                                                                    applications.child(uId).child("level").setValue(level);
//                                                                    applications.child(uId).child("JAMB").setValue(code);
//                                                                    applications.child(uId).child("chaletName").setValue(chaletName);
//                                                                    applications.child(uId).child("bedNumber").setValue(bed_number);
//                                                                    applications.child(uId).child("email").setValue(email);
//                                                                    applications.child(uId).child("chaletName").setValue(chaletName);
//                                                                    applications.child(uId).child("bedNumber").setValue(bed_number);
//                                                                    applications.child(uId).child("phone").setValue(phone);
//                                                                    applications.child(uId).child("fullName").setValue(fullName);
//                                                                    applications.child(uId).child("parentNo").setValue(emergenNo);
//                                                                    applications.child(uId).child("profilePic").setValue(profileUri);
//                                                                    applications.child(uId).child("gender").setValue(gender);
//                                                                    applications.child(uId).child("matNo").setValue(matNo).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if (task.isSuccessful()){
//                                                                                Toasty.success(GirlsRoomDetail.this, "Application Successful", Toast.LENGTH_LONG).show();
//                                                                                //change the status to invalid so that same matriculation number cannot be used twice
//                                                                                regCodes.child(code).child("status").setValue("Used").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        if (task.isSuccessful()){
//                                                                                            mDialog.dismiss();
//                                                                                            updateRoomDetails(roomId);
//
//                                                                                        }
//                                                                                    }
//                                                                                });
//                                                                            }
//                                                                        }
//                                                                    });
//
//
//                                                                }
//
//                                                                @Override
//                                                                public void onCancelled(DatabaseError databaseError) {
//                                                                    Toasty.error(GirlsRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                                                }
//                                                            });
//
//                                                        }
//
//
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//                                                        Toasty.error(GirlsRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                                    }
//                                                });
//
//
//
//
//                                            }else{
//                                                mDialog.dismiss();
//                                                Toasty.info(GirlsRoomDetail.this, "Sorry this Code has already been used", Toast.LENGTH_SHORT).show();
//                                                finish();
//                                            }
//
//                                        }else {
//                                            mDialog.dismiss();
//                                            Toasty.error(GirlsRoomDetail.this, "Invalid Code please try again", Toast.LENGTH_SHORT).show();
//                                            return;
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//
//
//
//                }
//
//
//            }
//        });
//
//        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        alertDialog.show();
//    }

//    private void updateRoomDetails(final String roomId) {
//        girlsRooms.child(roomId).child("status").setValue("occupied").addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    startActivity(new Intent(GirlsRoomDetail.this, Home.class));
//                    finish();
//                }
//            }
//        });
//    }


    private void getDetailRoom(String roomId) {
        girlsRooms.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GirlsRooms girlsRooms = dataSnapshot.getValue(GirlsRooms.class);


                //Picasso.get().load(girlsRooms.getImage()).into(img_room);
                collapsingToolbarLayout.setTitle(girlsRooms.getRoomDescription());
                room_name.setText(girlsRooms.getRoomDescription());
                bed_number.setText(girlsRooms.getBedNumber());
                status.setText(girlsRooms.getStatus());


                final String Status = girlsRooms.getStatus();

                if (Status.equals("available")){
                    btnApply1.setEnabled(true);
                }else{
                    btnApply1.setEnabled(false);
                    btnApply1.setText("Room Occupied");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

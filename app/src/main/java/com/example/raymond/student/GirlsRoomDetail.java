package com.example.raymond.student;

import android.content.DialogInterface;
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
import com.example.raymond.student.Model.GirlsRooms;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GirlsRoomDetail extends AppCompatActivity {

    private TextView room_name, bed_number;
    private ImageView img_room;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Button btnApply1;

    private String roomId = "";

    private FirebaseDatabase database;
    private DatabaseReference girlsRooms;

    //reference to applications
    private DatabaseReference students;
    private DatabaseReference applications;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String uId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girls_room_detail);

        //int firebase
        database = FirebaseDatabase.getInstance();
        girlsRooms = database.getReference("GirlsRooms");


        //get  currentUser
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        applications = FirebaseDatabase.getInstance().getReference().child("Applications");
        students = database.getReference("Students");

        uId = mAuth.getUid();

        //initialize views
        room_name = findViewById(R.id.room_name);
        bed_number = findViewById(R.id.bed_number);
        img_room = findViewById(R.id.img_room);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GirlsRoomDetail.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your application pin ");

        final EditText editTextPin = new EditText(GirlsRoomDetail.this);
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

                students.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String pin =  editTextPin.getText().toString();
                        if (pin.isEmpty()){
                            Toast.makeText(GirlsRoomDetail.this, "Type in your application PIN", Toast.LENGTH_SHORT).show();
                        }else {

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
                                girlsRooms.child(roomId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final GirlsRooms girlsRooms = dataSnapshot.getValue(GirlsRooms.class);

                                        String chaletName = girlsRooms.getRoomDescription();
                                        String bed_number = girlsRooms.getBedNumber();
                                        applications.child(uId).child("PIN").setValue(pin);
                                        applications.child(uId).child("chaletName").setValue(chaletName);
                                        applications.child(uId).child("bedNumber").setValue(bed_number);
                                        applications.child(uId).child("email").setValue(email);
                                        applications.child(uId).child("chaletName").setValue(chaletName);
                                        applications.child(uId).child("bedNumber").setValue(bed_number);
                                        applications.child(uId).child("phone").setValue(phone);
                                        applications.child(uId).child("surname").setValue(surname);
                                        applications.child(uId).child("firstName").setValue(firstName);
                                        applications.child(uId).child("lastName").setValue(lastName);
                                        applications.child(uId).child("parentNo").setValue(emergenNo);
                                        applications.child(uId).child("profilePic").setValue(profileUri);
                                        applications.child(uId).child("gender").setValue(gender);
                                        applications.child(uId).child("matNo").setValue(matNo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                deleteRoom();

                                                Toast.makeText(GirlsRoomDetail.this, "Your application was submitted successful", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(GirlsRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(GirlsRoomDetail.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

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

    private void deleteRoom() {
        girlsRooms.child(roomId).removeValue();
    }


    private void getDetailRoom(String roomId) {
        girlsRooms.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GirlsRooms girlsRooms = dataSnapshot.getValue(GirlsRooms.class);

                //set Image
                Picasso.with(getBaseContext()).load(girlsRooms.getImage())
                        .into(img_room);
                collapsingToolbarLayout.setTitle(girlsRooms.getRoomDescription());
                room_name.setText(girlsRooms.getRoomDescription());
                bed_number.setText(girlsRooms.getBedNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

package com.example.raymond.student;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymond.student.Model.GirlsRooms;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompleteApplication extends AppCompatActivity {
    String roomId = "";
    String studentId = "";

    private TextView room_name, bed_number, status;
    private ImageView img_room;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Button btnApply1;

    private FirebaseDatabase database;
    private DatabaseReference girlsRooms, students, regCodes;

    //reference to applications
    private DatabaseReference applications;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_application);



        //int firebase
        database = FirebaseDatabase.getInstance();
        girlsRooms = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("hostels").child("girlsHostel").child("GirlsRooms").child(roomId);;


        //get  currentUser
        mAuth = FirebaseAuth.getInstance();
        applications = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("Occupants");
        students = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("users").child("Students");
        regCodes = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("registrationCodes");



        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


        //initialize views
        room_name = findViewById(R.id.room_name);
        bed_number = findViewById(R.id.bed_number);
        img_room = findViewById(R.id.img_room);
        btnApply1 = findViewById(R.id.btnApply1);
        status = findViewById(R.id.status);



        //get room id from Intent
        if (getIntent() != null){
            roomId = getIntent().getStringExtra("roomId");
            studentId = getIntent().getStringExtra("uId");

            Toast.makeText(this, ""+studentId, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, ""+roomId, Toast.LENGTH_SHORT).show();


            if (!roomId.isEmpty()){
                getDetailRoom(roomId);
            }

        }


    }

    private void getDetailRoom(String roomId) {
        girlsRooms.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        girlsRooms.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                GirlsRooms girlsRooms = dataSnapshot.getValue(GirlsRooms.class);
//
//
//                //Picasso.get().load(girlsRooms.getImage()).into(img_room);
//                collapsingToolbarLayout.setTitle(girlsRooms.getRoomDescription());
//                room_name.setText(girlsRooms.getRoomDescription());
//                bed_number.setText(girlsRooms.getBedNumber());
//                status.setText(girlsRooms.getStatus());
//
//
//                final String Status = girlsRooms.getStatus();
//
//                if (Status.equals("available")){
//                    btnApply1.setEnabled(true);
//                }else{
//                    btnApply1.setEnabled(false);
//                    btnApply1.setText("Room Occupied");
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}

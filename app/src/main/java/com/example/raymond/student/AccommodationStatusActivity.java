package com.example.raymond.student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AccommodationStatusActivity extends AppCompatActivity {

    private Toolbar statusToolBar;
    private CircleImageView profileImage;
    private TextView txtFullname, txtRoomDescription, txtBedNumber;
    private DatabaseReference students, applications, materialsIssued;
    private FirebaseAuth mAuth;
    private String currentUserID;

    String retrievedRoomId = "";

    //hostel materials
    private Button btnHostelMaterials;
    private TextView txtMaterials;
    private TextView txtStatus;


    private FirebaseDatabase database;
    private DatabaseReference girlsRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accommodation_status);




        //init firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

        girlsRooms = database.getReference("GirlsRooms");
        applications = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("Occupants");
        materialsIssued = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("materialsIssued");



            retrieveAccommodationStatus();





        //initialize views
        profileImage = findViewById(R.id.set_profile_image);
        txtBedNumber = findViewById(R.id.bedNumber);
        txtFullname = findViewById(R.id.fullName);
        txtRoomDescription = findViewById(R.id.roomDescription);

        btnHostelMaterials = findViewById(R.id.btn_materials);
        txtMaterials = findViewById(R.id.txt_materials);
        txtStatus = findViewById(R.id.txt_status);

        //initialize our toolBar
        statusToolBar = findViewById(R.id.accomodation_tool_bar);
        setSupportActionBar(statusToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Accommodation Status");

    }

    private void retrieveAccommodationStatus() {

        applications.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String bedNumber = dataSnapshot.child("bedNumber").getValue().toString();
                    String roomDescription = dataSnapshot.child("chaletName").getValue().toString();
                    String image = dataSnapshot.child("profilePic").getValue().toString();

                    profileImage.setVisibility(View.VISIBLE);
                    txtBedNumber.setVisibility(View.VISIBLE);
                    txtFullname.setVisibility(View.VISIBLE);
                    txtRoomDescription.setVisibility(View.VISIBLE);

                    //set button material visible
                    btnHostelMaterials.setVisibility(View.VISIBLE);

                    btnHostelMaterials.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialsIssued.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.child(currentUserID).exists()){
                                       String materials = dataSnapshot.child(currentUserID).child("materials").getValue(String.class);
                                       String status = dataSnapshot.child(currentUserID).child("status").getValue(String.class);

                                       txtMaterials.setVisibility(View.VISIBLE);
                                       txtStatus.setVisibility(View.VISIBLE);

                                       txtMaterials.setText(materials);
                                       txtStatus.setText("Status: " +status);

                                   }else
                                       Toasty.info(AccommodationStatusActivity.this, "No Hostel materials given yet", Toast.LENGTH_SHORT).show();
                                     }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
//                            txtMaterials.setVisibility(View.VISIBLE);
                        }
                    });

                    //set the retrieved user data to the views
                    txtRoomDescription.setText(roomDescription);
                    txtBedNumber.setText(bedNumber);
                    txtFullname.setText(fullName);
                    Picasso.get().load(image).placeholder(R.drawable.hostel).into(profileImage);
                }else {
                    Toasty.info(AccommodationStatusActivity.this, "Not allocated; Please apply for accommodation", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

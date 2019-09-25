package com.example.raymond.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseDatabase database;
    private DatabaseReference students, occupants;
    private FirebaseAuth mAuth;
    private TextView fullName, email;
    private ImageView imgProfile;

    private FirebaseUser currentUser;
    String uId;

    //rework
    private ImageButton img_apply, img_status, img_roommates, img_rules;
    private TextView txt_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //rework
        img_apply = findViewById(R.id.img_applyAccommodation);
        img_roommates = findViewById(R.id.img_roommates);
        img_rules = findViewById(R.id.img_hostelRules);
        img_status = findViewById(R.id.img_accommodationStatus);
        txt_username = findViewById(R.id.txt_username);

        mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser().getEmail();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PLASU Hostel" );
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();

        occupants = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("Occupants");
        students = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("users").child("Students");
        students.keepSynced(true);



        uId = mAuth.getUid();

        final FirebaseUser user = mAuth.getCurrentUser();

        students.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String retrievedUsername = dataSnapshot.child("fullName").getValue(String.class);
                txt_username.setText(retrievedUsername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //rewwork....setOnclickListener to all image buttons
        img_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, AccommodationStatusActivity.class));
            }
        });

        img_roommates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Roommates.class));
            }
        });

        img_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                occupants.child(uId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            startActivity(new Intent(Home.this, AccommodationStatusActivity.class));


                        }else {
                            startActivity(new Intent(Home.this, StepOneApplication.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                //run a check for the gender of the current user
//                students.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String gender = dataSnapshot.child(uId).child("gender").getValue(String.class);
//
//                        if (gender.equals("Male")){
//                            Intent applIntent = new Intent(Home.this, BoysRoomsList.class);
//                            startActivity(applIntent);
//
//                        }else{
//                            Intent applIntent = new Intent(Home.this, GirlsRoomsList.class);
//                            startActivity(applIntent);
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }
        });

        img_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ViewRules.class));
            }
        });





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        students.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                View headerView = navigationView.getHeaderView(0);
                email = headerView.findViewById(R.id.email);

                email.setText(user.getEmail());
                fullName = headerView.findViewById(R.id.fullName);
//                fullName.setText(dataSnapshot.child(uId).child("surname").getValue(String.class));
                String fullname = dataSnapshot.child(uId).child("fullName").getValue(String.class);


                fullName.setText(fullname);

                imgProfile = headerView.findViewById(R.id.image_view_profile);
                String profileUri = dataSnapshot.child(uId).child("image").getValue(String.class);
                Picasso.get().load(profileUri).into(imgProfile);
//                Picasso.with(getBaseContext()).load(profileUri)
//                        .into(imgProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //set name for the user
        View headerView = navigationView.getHeaderView(0);
        email = headerView.findViewById(R.id.email);
        email.setText(user.getEmail());
        fullName = headerView.findViewById(R.id.fullName);




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Home.this, ChangePassword.class));
        }else if (id == R.id.nav_logout){
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(Home.this, LoginActivity.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_roommates) {
//            // check roommates of a particular student
//            Intent roomies = new Intent(Home.this, Roommates.class);
//            startActivity(roomies);
//        } else if (id == R.id.nav_apply) {
//            occupants.child(uId).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()){
//                        startActivity(new Intent(Home.this, AccommodationStatusActivity.class));
//
//
//                    }else {
//                        startActivity(new Intent(Home.this, StepOneApplication.class));
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            //run a check for the gender of the current user
////            students.addValueEventListener(new ValueEventListener() {
////                @Override
////                public void onDataChange(DataSnapshot dataSnapshot) {
////                    String gender = dataSnapshot.child(uId).child("gender").getValue(String.class);
////
////                    if (gender.equals("Male")){
////                        Intent applIntent = new Intent(Home.this, BoysRoomsList.class);
////                        startActivity(applIntent);
////
////                    }else{
////                        Intent applIntent = new Intent(Home.this, GirlsRoomsList.class);
////                        startActivity(applIntent);
////
////                    }
////
////                }
////
////                @Override
////                public void onCancelled(DatabaseError databaseError) {
////
////                }
////            });
//        } else if (id == R.id.nav_rules) {
//
//        }else if(id == R.id.nav_accommmodation){
//
//            startActivity(new Intent(Home.this, AccommodationStatusActivity.class));
//
//
if (id == R.id.nav_chat2){
            startActivity(new Intent(Home.this, PlasuChat.class));
        }else if (id == R.id.nav_logout) {
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(Home.this, LoginActivity.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();


            //startActivity(new Intent(Home.this, LoginActivity.class));

        }else if (id == R.id.nav_change_password){
            startActivity(new Intent(Home.this, ChangePassword.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

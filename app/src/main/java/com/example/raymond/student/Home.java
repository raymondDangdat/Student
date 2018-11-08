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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseDatabase database;
    private DatabaseReference students;
    private FirebaseAuth mAuth;
    private TextView fullName, email;
    private ImageView imgProfile;

    private FirebaseUser currentUser;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        students = database.getReference("Students");
        students.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        uId = mAuth.getUid();

        final FirebaseUser user = mAuth.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Soon you will use this to apply for hostel", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                String surname = dataSnapshot.child(uId).child("surname").getValue(String.class);
                String firstName = dataSnapshot.child(uId).child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child(uId).child("lastName").getValue(String.class);

                fullName.setText(surname + " " + firstName + " " + lastName);

                imgProfile = headerView.findViewById(R.id.image_view_profile);
                String profileUri = dataSnapshot.child(uId).child("image").getValue(String.class);
                Picasso.with(getBaseContext()).load(profileUri)
                        .into(imgProfile);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_users) {
            // Handle the camera action
        } else if (id == R.id.nav_apply) {
            //run a check for the gender of the current user
            students.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String gender = dataSnapshot.child(uId).child("gender").getValue(String.class);

                    if (gender.equals("Male")){
                        Intent applIntent = new Intent(Home.this, BoysChalets.class);
                        startActivity(applIntent);

                    }else{
                        Intent applIntent = new Intent(Home.this, GirlsChalets.class);
                        startActivity(applIntent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else if (id == R.id.nav_rules) {

        } else if (id == R.id.nav_settings) {

        }else if(id == R.id.nav_chat){
            startActivity(new Intent(Home.this, ChatActivity.class));

        }else if (id == R.id.nav_chat2){
            startActivity(new Intent(Home.this, PlasuChat.class));
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            startActivity(new Intent(Home.this, LoginActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

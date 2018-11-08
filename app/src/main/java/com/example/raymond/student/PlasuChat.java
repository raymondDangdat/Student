package com.example.raymond.student;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlasuChat extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    //to check if the user register for the chat
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference chatUsersDatabase;
    private DatabaseReference groups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasu_chat);

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        chatUsersDatabase = FirebaseDatabase.getInstance().getReference("ChatUsers");
        groups = FirebaseDatabase.getInstance().getReference("Groups");


        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("PLASU Chat");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    //create an onStart method

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            sendUserToChatLoginActivity();
        }else {
            verifyUserExistance();
        }
    }

    private void verifyUserExistance() {
        //checks if user exist in the chat database
        String currentUserId = mAuth.getCurrentUser().getUid();
        chatUsersDatabase.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())){
                    Toast.makeText(PlasuChat.this, "Welcome", Toast.LENGTH_SHORT).show();

                }else {
                    //send the user to settings activity
                    Intent settingsIntent = new Intent(PlasuChat.this, SettingsActivity.class);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(settingsIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToChatLoginActivity() {
        Intent chatLoginIntent = new Intent(PlasuChat.this, ChatLoginActivity.class);
        chatLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(chatLoginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_signout){
            mAuth.signOut();
            sendUserToChatLoginActivity();

        }else if (item.getItemId() == R.id.action_find_friends_option){

        }else if (item.getItemId() == R.id.action_settings){
            Intent settingsIntent = new Intent(PlasuChat.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }
        else if (item.getItemId() == R.id.action_create_group){
            requestNewGruop();

        }
        return true;
    }

    private void requestNewGruop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlasuChat.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");
        builder.setIcon(R.drawable.ic_group);
        final EditText groupNameField = new EditText(PlasuChat.this);
        groupNameField.setHint("e.g Developers mesh");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(PlasuChat.this, "Please write group name", Toast.LENGTH_SHORT).show();
                }else {
                    createNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createNewGroup(final String groupName) {
        groups.child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(PlasuChat.this, groupName+ " was created successfully... ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}

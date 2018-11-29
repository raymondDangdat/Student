package com.example.raymond.student;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Roommates extends AppCompatActivity {
    private Toolbar mToolBar;
    private RecyclerView roommatesRecyclerList;
    private DatabaseReference applications;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private FirebaseRecyclerAdapter<com.example.raymond.student.Model.Roommates, RoommatesViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommates);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        applications = FirebaseDatabase.getInstance().getReference().child("Applications");

        roommatesRecyclerList = findViewById(R.id.roommates_recyclerview);
        roommatesRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        //toolBar
        mToolBar = findViewById(R.id.roommates_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Roommates");


        checkIfUserIsAllocated();
    }

    private void checkIfUserIsAllocated() {
        //check if the current user has been allocated to a room
        applications.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //user has been allocated so get the user's chaletId
                    final String chaletId = dataSnapshot.child("chaletId").getValue().toString();

                    //load students with the same chaletId
                    loadRoommates(chaletId);

                }else {
                    Toasty.info(Roommates.this, "You have not yet been allocated so you don't have a roommate", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadRoommates(String chaletId) {
        FirebaseRecyclerOptions<com.example.raymond.student.Model.Roommates> options
                = new FirebaseRecyclerOptions.Builder<com.example.raymond.student.Model.Roommates>()
                .setQuery(applications.orderByChild("chaletId").equalTo(chaletId), com.example.raymond.student.Model.Roommates.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<com.example.raymond.student.Model.Roommates, RoommatesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RoommatesViewHolder holder, int position, @NonNull com.example.raymond.student.Model.Roommates model) {
                holder.fullName.setText(model.getFullName());
                holder.phone.setText(model.getPhone());
                holder.parentNo.setText(model.getParentNo());
                holder.chaletName.setText(model.getChaletName());
                holder.bedNumber.setText(model.getBedNumber());

                Picasso.get().load(model.getProfilePic()).placeholder(R.drawable.profile_image).into(holder.profileImage);

            }

            @NonNull
            @Override
            public RoommatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roommates_layout, parent, false);
                RoommatesViewHolder viewHolder = new RoommatesViewHolder(view);
                return viewHolder;
            }
        };

        //set adapter
        roommatesRecyclerList.setAdapter(adapter);
        adapter.startListening();

    }

//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }

    public static class RoommatesViewHolder extends RecyclerView.ViewHolder{
        TextView fullName, phone, parentNo, bedNumber, chaletName;
        CircleImageView profileImage;

        public RoommatesViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            phone = itemView.findViewById(R.id.contact);
            fullName = itemView.findViewById(R.id.fullName);
            bedNumber = itemView.findViewById(R.id.bedNumber);
            chaletName = itemView.findViewById(R.id.chaletName);
            parentNo = itemView.findViewById(R.id.parentContact);
        }
    }


}

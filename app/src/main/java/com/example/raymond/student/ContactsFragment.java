package com.example.raymond.student;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raymond.student.Model.Contacts;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactList;
    private DatabaseReference contactsRef, usersRef;

    private FirebaseAuth mAuth;
    private String current_user_id;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(current_user_id);
        usersRef = FirebaseDatabase.getInstance().getReference().child("ChatUsers");


        // Inflate the layout for this fragment
        contactsView =  inflater.inflate(R.layout.fragment_contacts, container, false);
        myContactList = contactsView.findViewById(R.id.contacts_recycler);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef, Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                String userIDs = getRef(position).getKey();
                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")){
                            String profileImage = dataSnapshot.child("image").getValue().toString();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String ustatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(name);
                            holder.status.setText(ustatus);
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                        }else {
                            String name = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(name);
                            holder.status.setText(status);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
               ContactsViewHolder viewHolder = new ContactsViewHolder(view);
               return viewHolder;
            }
        };
        myContactList.setAdapter(adapter);
        adapter.startListening();
    }


    //create view holder class
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView username, status;
        CircleImageView profileImage;


        public ContactsViewHolder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            status = itemView.findViewById(R.id.users_status);
            username = itemView.findViewById(R.id.users_profile_name);
        }
    }
}

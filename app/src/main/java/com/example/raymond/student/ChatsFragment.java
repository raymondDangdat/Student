package com.example.raymond.student;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ChatsFragment extends Fragment {
    private View privateChatsView;
    private RecyclerView chatList;
    private DatabaseReference chatsRef, usersRef;
    private FirebaseAuth mAuth;

    //create a string to store current user ID
    private String current_userID;





    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        current_userID = mAuth.getCurrentUser().getUid();

        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(current_userID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("ChatUsers");

        chatList = privateChatsView.findViewById(R.id.chats_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatsView;
    }



    //create onStart method


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, PrivateChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, PrivateChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PrivateChatsViewHolder holder, int position, @NonNull Contacts model) {
                final String users_id = getRef(position).getKey();
                final String[] retrievedImage = {"default_image"};
                usersRef.child(users_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.exists()){
                           if (dataSnapshot.hasChild("image")){
                               retrievedImage[0] = dataSnapshot.child("image").getValue().toString();

                               //use Picasso to display the image
                               Picasso.get().load(retrievedImage[0]).into(holder.profileImage);

                           }
                           final String retrievedName = dataSnapshot.child("name").getValue().toString();
                           final String retrievedStatus = dataSnapshot.child("status").getValue().toString();

                           holder.userName.setText(retrievedName);
                           holder.userStatus.setText("Last Seen: " + "\n" + "Date " + " Time");

                           holder.itemView.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent privateChateIntent = new Intent(getContext(), PrivateChatActivity.class);
                                   //get the user information
                                   privateChateIntent.putExtra("visit_user_id", users_id);
                                   privateChateIntent.putExtra("visit_user_image", retrievedImage[0]);
                                   privateChateIntent.putExtra("visit_user_name", retrievedName);

                                   startActivity(privateChateIntent);
                               }
                           });
                       }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public PrivateChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
               return new PrivateChatsViewHolder(view);
            }
        };
        chatList.setAdapter(adapter);
        adapter.startListening();
    }


    //create a static viewHolder class
    public static class PrivateChatsViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;


        public PrivateChatsViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}

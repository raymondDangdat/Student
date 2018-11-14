package com.example.raymond.student;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatProfileActivity extends AppCompatActivity {

    private String currentState, senderUserId;
    private String receivedUserId;


    private CircleImageView profileImage;
    private TextView uProfileName, uProfileStatus;
    private Button sendMessageRequest, declineRequestButton;

    private DatabaseReference userRef, chatRequestRef, contactRef, notificationRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile);



        receivedUserId = getIntent().getExtras().get("visit_user_id").toString();

        userRef = FirebaseDatabase.getInstance().getReference().child("ChatUsers");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mAuth = FirebaseAuth.getInstance();


        profileImage = findViewById(R.id.visit_profile_image);
        uProfileName =findViewById(R.id.visit_profile_username);
        uProfileStatus = findViewById(R.id.visit_profile_status);
        sendMessageRequest = findViewById(R.id.send_message_request_button);
        declineRequestButton = findViewById(R.id.decline_message_request_button);


        currentState = "new";
        senderUserId = mAuth.getCurrentUser().getUid();


        //create a method to retrieveed user info
        retrievedUserInfo();
    }

    private void retrievedUserInfo() {
        userRef.child(receivedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(profileImage);
                    uProfileName.setText(name);
                    uProfileStatus.setText(status);

                    //method to manage chat requests
                    manageChatRequest();


                }else {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    uProfileName.setText(name);
                    uProfileStatus.setText(status);

                    //method to manage chat request
                    manageChatRequest();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest() {

        chatRequestRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receivedUserId)){
                    String request_type = dataSnapshot.child(receivedUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")){
                        currentState = "request_sent";
                        sendMessageRequest.setText("Cancel Chat Request");

                        //now check on the receiver side
                    }else if(request_type.equals("received")){
                        currentState = "request_received";
                        sendMessageRequest.setText("Accept Chat Request");
                        declineRequestButton.setVisibility(View.VISIBLE);
                        declineRequestButton.setEnabled(true);

                        declineRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatRequest();
                            }
                        });
                    }
                }else {
                    contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(receivedUserId)){
                                currentState = "friends";
                                sendMessageRequest.setText("Remove Contact");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //check if it is the same user trying to send message reques to himself
        if (!senderUserId.equals(receivedUserId)){
            sendMessageRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //disable the button after sending chat request
                    sendMessageRequest.setEnabled(false);

                    //check if the users are new to each other
                    if (currentState.equals("new")){
                        //method that will send chat request
                        sendChatRequest();
                    }if (currentState.equals("request_sent")){
                        //method to cancel chat request
                        cancelChatRequest();
                    }if (currentState.equals("request_received")){
                        //method to accept chat request
                        acceptChatRequest();
                    }if (currentState.equals("friends")){
                        //method to accept chat request
                        removeSpecificCOntact();
                    }
                }
            });

        }else {
            sendMessageRequest.setVisibility(View.INVISIBLE);

        }
    }

    private void removeSpecificCOntact() {
        contactRef.child(senderUserId).child(receivedUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //check if task is successful then remove from receiver too
                        if (task.isSuccessful()){
                            contactRef.child(receivedUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessageRequest.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequest.setText("Send Chat Request");

                                                declineRequestButton.setVisibility(View.INVISIBLE);
                                                declineRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptChatRequest() {
        contactRef.child(senderUserId).child(receivedUserId)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactRef.child(receivedUserId).child(senderUserId)
                                    .child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                chatRequestRef.child(senderUserId).child(receivedUserId)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            chatRequestRef.child(receivedUserId).child(senderUserId)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendMessageRequest.setEnabled(true);
                                                                    currentState = "friends";
                                                                    sendMessageRequest.setText("Remove Contact");
                                                                    declineRequestButton.setVisibility(View.INVISIBLE);
                                                                    declineRequestButton.setEnabled(false);

                                                                }
                                                            });
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUserId).child(receivedUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //check if task is successful then remove from receiver too
                        if (task.isSuccessful()){
                            chatRequestRef.child(receivedUserId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessageRequest.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequest.setText("Send Chat Request");

                                                declineRequestButton.setVisibility(View.INVISIBLE);
                                                declineRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    private void sendChatRequest() {
        chatRequestRef.child(senderUserId).child(receivedUserId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatRequestRef.child(receivedUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                //create push notification
                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserId);
                                                chatNotificationMap.put("type", "request");

                                                notificationRef.child(receivedUserId).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    sendMessageRequest.setEnabled(true);
                                                                    currentState = "request_sent";
                                                                    sendMessageRequest.setText("Cancel Chat Request");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}

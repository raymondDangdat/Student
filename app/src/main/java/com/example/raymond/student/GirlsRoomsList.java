package com.example.raymond.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.Model.BoysRooms;
import com.example.raymond.student.Model.GirlsRooms;
import com.example.raymond.student.ViewHolder.BoysRoomViewHolder;
import com.example.raymond.student.ViewHolder.GirlsRoomViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class GirlsRoomsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference girlsRooms;

    String chaletId = "";
    private FirebaseRecyclerAdapter<GirlsRooms, GirlsRoomViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girls_rooms_list);


        database = FirebaseDatabase.getInstance();
        girlsRooms = database.getReference("BoysRooms");


        recyclerView = findViewById(R.id.recycler_girls_rooms);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //get intent here
        if (getIntent() != null)
            chaletId = getIntent().getStringExtra("chaletId");
        if (!chaletId.isEmpty() &&  chaletId != null){
            loadListRoom(chaletId);
        }
    }

    private void loadListRoom(String chaletId) {
        adapter = new FirebaseRecyclerAdapter<GirlsRooms, GirlsRoomViewHolder>(
                GirlsRooms.class,
                R.layout.girls_rooms_item,
                GirlsRoomViewHolder.class,
                girlsRooms
        ) {
            @Override
            protected void populateViewHolder(GirlsRoomViewHolder viewHolder, GirlsRooms model, int position) {
                viewHolder.txtRoomDescription.setText(model.getRoomDescription());
                viewHolder.txtBedNumber.setText(model.getBedNumber());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent roomDetail = new Intent(GirlsRoomsList.this, GirlsRoomDetail.class);
                        roomDetail.putExtra("roomId", adapter.getRef(position).getKey()); //send room id to new activity
                        startActivity(roomDetail);
                    }
                });
            }
        };
        //set adapter
        recyclerView.setAdapter(adapter);

    }
}

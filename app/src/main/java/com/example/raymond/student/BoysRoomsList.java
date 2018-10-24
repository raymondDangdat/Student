package com.example.raymond.student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.Model.BoysRooms;
import com.example.raymond.student.ViewHolder.BoysRoomViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class BoysRoomsList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference boysRooms;
    
    String chaletId = "";
    private FirebaseRecyclerAdapter<BoysRooms, BoysRoomViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boys_rooms_list);

        database = FirebaseDatabase.getInstance();
        boysRooms = database.getReference("BoysRooms");


        recyclerView = findViewById(R.id.recycler_boys_rooms);
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
        adapter = new FirebaseRecyclerAdapter<BoysRooms, BoysRoomViewHolder>(
                BoysRooms.class,
                R.layout.boys_roms_item,
                BoysRoomViewHolder.class,
                boysRooms.orderByChild("room").equalTo(chaletId) //compare the name to the chaletId intent
                //like select * from table where name = chaletId
        ) {
            @Override
            protected void populateViewHolder(BoysRoomViewHolder viewHolder, BoysRooms model, int position) {
                viewHolder.txtRoomDescription.setText(model.getRoomDescription());
                viewHolder.txtBedNumber.setText(model.getBedNumber());
                Picasso.with(getBaseContext()).load(model.getImage())
                .into(viewHolder.imageView);

                final BoysRooms local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(BoysRoomsList.this, ""+local.getRoomDescription() + " " +local.getBedNumber(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        //set adapter
        recyclerView.setAdapter(adapter);

    }


}

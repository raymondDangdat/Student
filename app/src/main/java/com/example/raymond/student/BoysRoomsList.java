package com.example.raymond.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.Model.BoysRooms;
import com.example.raymond.student.ViewHolder.BoysRoomViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BoysRoomsList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference boysRooms;
    
    String chaletId = "";
    private FirebaseRecyclerAdapter<BoysRooms, BoysRoomViewHolder> adapter;



    //search functionality
    private FirebaseRecyclerAdapter<BoysRooms, BoysRoomViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;


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
        materialSearchBar = findViewById(R.id.searchBar);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setHint("Enter chalet description");
        materialSearchBar.setCardViewElevation(10);

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //when user type their text we change the suggestion
                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList){//loop in suggestlist
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when searchBar is close
                //restore original suggest adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<BoysRooms, BoysRoomViewHolder>(
                BoysRooms.class,
                R.layout.boys_roms_item,
                BoysRoomViewHolder.class,
                boysRooms.orderByChild("roomDescription").equalTo(text.toString())

        ) {
            @Override
            protected void populateViewHolder(BoysRoomViewHolder viewHolder, BoysRooms model, int position) {
                viewHolder.txtRoomDescription.setText(model.getRoomDescription());
                viewHolder.txtBedNumber.setText(model.getBedNumber());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent roomDetail = new Intent(BoysRoomsList.this, BoysRoomDetail.class);
                        roomDetail.putExtra("roomId", searchAdapter.getRef(position).getKey()); //send room id to new activity
                        startActivity(roomDetail);
                    }
                });

            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        boysRooms.orderByChild("room").equalTo(chaletId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    BoysRooms item = postSnapshot.getValue(BoysRooms.class);
                    suggestList.add(item.getRoomDescription());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        //start new activity
                        Intent roomDetail = new Intent(BoysRoomsList.this, BoysRoomDetail.class);
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

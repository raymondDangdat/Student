package com.example.raymond.student;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.Model.BoysRooms;
import com.example.raymond.student.ViewHolder.BoysRoomViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    private Toolbar roomToolBar;
    
    String chaletId = "";
    private FirebaseRecyclerAdapter<BoysRooms, BoysRooomViewHolder> adapter;



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


        //initialize our toolBar
        roomToolBar = findViewById(R.id.boysRoom_tool_bar);
        setSupportActionBar(roomToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Boys Rooms");

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
        FirebaseRecyclerOptions<BoysRooms> options = new
                FirebaseRecyclerOptions.Builder<BoysRooms>()
                .setQuery(boysRooms.orderByChild("roomDescription").equalTo(text.toString()), BoysRooms.class)
                .build();
        searchAdapter = new FirebaseRecyclerAdapter<BoysRooms, BoysRoomViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BoysRoomViewHolder holder, int position, @NonNull BoysRooms model) {
                holder.txtRoomDescription.setText(model.getRoomDescription());
                holder.txtBedNumber.setText(model.getBedNumber());
                Picasso.get().load(model.getImage()).into(holder.imageView);

               holder.setItemClickListener(new ItemClickListener() {
                   @Override
                   public void onClick(View view, int position, boolean isLongClick) {
                       Intent roomDetail = new Intent(BoysRoomsList.this, BoysRoomDetail.class);
                        roomDetail.putExtra("roomId", searchAdapter.getRef(position).getKey()); //send room id to new activity
                        startActivity(roomDetail);

                   }
               });

            }

            @NonNull
            @Override
            public BoysRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boys_roms_item, parent, false);
                BoysRoomViewHolder viewHolder = new BoysRoomViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(searchAdapter);
        searchAdapter.startListening();
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

    private void loadListRoom(final String chaletId) {
        FirebaseRecyclerOptions<BoysRooms> options =
                new FirebaseRecyclerOptions.Builder<BoysRooms>()
                .setQuery(boysRooms.orderByChild("room").equalTo(chaletId), BoysRooms.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<BoysRooms, BoysRooomViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BoysRooomViewHolder holder, int position, @NonNull BoysRooms model) {
                holder.txtRoomDescription.setText(model.getRoomDescription());
                holder.txtBedNumber.setText(model.getBedNumber());
                holder.txtStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.hostel).into(holder.imageView);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent roomDetail = new Intent(BoysRoomsList.this, BoysRoomDetail.class);
                        roomDetail.putExtra("roomId", adapter.getRef(position).getKey());//send room id to new activitystartActivity(roomDetail);
                        roomDetail.putExtra("chaletId",chaletId );
                        startActivity(roomDetail);

                    }
                });

            }

            @NonNull
            @Override
            public BoysRooomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boys_roms_item, parent, false);
                BoysRooomViewHolder viewHolder = new BoysRooomViewHolder(view);
                return viewHolder;

            }
        };

        //set adapter
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class BoysRooomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView txtRoomDescription, txtBedNumber, txtStatus;
        public ImageView imageView;

        private ItemClickListener itemClickListener;

        public BoysRooomViewHolder(View itemView) {
            super(itemView);
            txtRoomDescription = itemView.findViewById(R.id.txtRoomDescription);
            txtBedNumber = itemView.findViewById(R.id.txtBedNumber);
            imageView = itemView.findViewById(R.id.imageViewRoom);
            txtStatus = itemView.findViewById(R.id.txtStatus);


            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }



        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);

        }
    }


}

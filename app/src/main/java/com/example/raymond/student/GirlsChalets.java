package com.example.raymond.student;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.Model.BoysChalets;
import com.example.raymond.student.ViewHolder.BoysChaletViewHolder;
import com.example.raymond.student.ViewHolder.BoysRoomViewHolder;
import com.example.raymond.student.ViewHolder.GirlsChaletViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class GirlsChalets extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference girlsChalets;

    private FirebaseRecyclerAdapter<com.example.raymond.student.Model.GirlsChalets,GirlsChaViewHolder> adapter;

    private RecyclerView recyclerView_girls_chalets;
    RecyclerView.LayoutManager layoutManager;

    private Toolbar chaletToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girls_chalets);


        database = FirebaseDatabase.getInstance();
        girlsChalets = database.getReference("girlsHostel");


        //load boys chalets

        recyclerView_girls_chalets = findViewById(R.id.recycler_girls_chalets);
        recyclerView_girls_chalets.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_girls_chalets.setLayoutManager(layoutManager);


        //initialize our toolBar
        chaletToolBar = findViewById(R.id.girlChalet_tool_bar);
        setSupportActionBar(chaletToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Girls Chalets");


    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<com.example.raymond.student.Model.GirlsChalets> options =
                new FirebaseRecyclerOptions.Builder<com.example.raymond.student.Model.GirlsChalets>()
                        .setQuery(girlsChalets, com.example.raymond.student.Model.GirlsChalets.class)
                        .build();



        adapter = new FirebaseRecyclerAdapter<com.example.raymond.student.Model.GirlsChalets, GirlsChaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GirlsChaViewHolder holder, int position, @NonNull com.example.raymond.student.Model.GirlsChalets model) {
                holder.txtChaletName.setText(model.getRoom());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.hostel).into(holder.imageView);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get chalet id to new activity
                        Intent roomList = new Intent(GirlsChalets.this, GirlsRoomsList.class);
                        roomList.putExtra("chaletId", adapter.getRef(position).getKey());
                        startActivity(roomList);
                    }
                });
            }

            @NonNull
            @Override
            public GirlsChaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girls_chalet_item, parent,false);
                GirlsChaViewHolder viewHolder = new GirlsChaViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView_girls_chalets.setAdapter(adapter);
        adapter.startListening();


    }
//
//    private void loadChalets() {
//
//        FirebaseRecyclerOptions<com.example.raymond.student.Model.GirlsChalets>options = new
//                FirebaseRecyclerOptions.Builder<com.example.raymond.student.Model.GirlsChalets>()
//                .setQuery(girlsChalets, com.example.raymond.student.Model.GirlsChalets.class)
//                .build();
//
//        adapter = new FirebaseRecyclerAdapter<com.example.raymond.student.Model.GirlsChalets, GirlsChaletViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull GirlsChaletViewHolder holder, int position, @NonNull com.example.raymond.student.Model.GirlsChalets model) {
//                holder.txtChaletName.setText(model.getRoom());
//                Picasso.get().load(model.getImage()).into(holder.imageView);
//                //get chalet id to new activity
//                       Intent roomList = new Intent(GirlsChalets.this, GirlsRoomsList.class);
//                      roomList.putExtra("chaletId", adapter.getRef(position).getKey());
//                        startActivity(roomList);
//
//            }
//
//            @NonNull
//            @Override
//            public GirlsChaletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.girls_chalet_item, parent, false);
//                GirlsChaletViewHolder viewHolder = new GirlsChaletViewHolder(view);
//                return viewHolder;
//            }
//        };
////        adapter = new FirebaseRecyclerAdapter<com.example.raymond.student.Model.GirlsChalets, GirlsChaletViewHolder>(
////                com.example.raymond.student.Model.GirlsChalets.class,
////                R.layout.girls_chalet_item,
////                GirlsChaletViewHolder.class,
////                girlsChalets
////
////        ) {
////            @Override
////            protected void populateViewHolder(GirlsChaletViewHolder viewHolder, com.example.raymond.student.Model.GirlsChalets model, int position) {
////                viewHolder.txtChaletName.setText(model.getRoom());
////                Picasso.get().load(model.getImage()).into(viewHolder.imageView);
////                //Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
////
////
////                viewHolder.setItemClickListener(new ItemClickListener() {
////                    @Override
////                    public void onClick(View view, int position, boolean isLongClick) {
////                        //get chalet id to new activity
////                        Intent roomList = new Intent(GirlsChalets.this, GirlsRoomsList.class);
////                        roomList.putExtra("chaletId", adapter.getRef(position).getKey());
////                        startActivity(roomList);
////                    }
////                });
////            }
////        };
//        recyclerView_girls_chalets.setAdapter(adapter);
//        adapter.startListening();
//    }




    public static class GirlsChaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtChaletName;
        ImageView imageView;

        private ItemClickListener itemClickListener;

        public GirlsChaViewHolder(View itemView) {
            super(itemView);
            txtChaletName = itemView.findViewById(R.id.txtChaletNumber);
            imageView = itemView.findViewById(R.id.imageViewChalet);

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

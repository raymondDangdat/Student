package com.example.raymond.student;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.ViewHolder.BoysChaletViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class BoysChalets extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference boysChalets;

    private FirebaseRecyclerAdapter<com.example.raymond.student.Model.BoysChalets,BoysChaViewHolder > adapter;

    private RecyclerView recyclerView_boys_chalets;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boys_chalets);

        database = FirebaseDatabase.getInstance();
        boysChalets = database.getReference("boysHostel");


        //load boys chalets

        recyclerView_boys_chalets = findViewById(R.id.recycler_boys_chalets);
        recyclerView_boys_chalets.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_boys_chalets.setLayoutManager(layoutManager);

        //loadChalets();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<com.example.raymond.student.Model.BoysChalets> options =
                new FirebaseRecyclerOptions.Builder<com.example.raymond.student.Model.BoysChalets>()
                .setQuery(boysChalets, com.example.raymond.student.Model.BoysChalets.class)
                .build();



         adapter = new FirebaseRecyclerAdapter<com.example.raymond.student.Model.BoysChalets, BoysChaViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull BoysChaViewHolder holder, int position, @NonNull com.example.raymond.student.Model.BoysChalets model) {
                        holder.txtChaletName.setText(model.getRoom());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.hostel).into(holder.imageView);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                //get chalet id to new activity
                        Intent roomList = new Intent(BoysChalets.this, BoysRoomsList.class);
                        roomList.putExtra("chaletId", adapter.getRef(position).getKey());
                        startActivity(roomList);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public BoysChaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boys_chalet_item, parent,false);
                        BoysChaViewHolder viewHolder = new BoysChaViewHolder(view);
                        return viewHolder;
                    }
                };
        recyclerView_boys_chalets.setAdapter(adapter);
        adapter.startListening();
    }



    //static class to take care of viewholder
    public static class BoysChaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
         TextView txtChaletName;
         ImageView imageView;

        private ItemClickListener itemClickListener;

        public BoysChaViewHolder(View itemView) {
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

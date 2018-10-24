package com.example.raymond.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.ViewHolder.BoysChaletViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class BoysChalets extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference boysChalets;

    private FirebaseRecyclerAdapter<com.example.raymond.student.Model.BoysChalets,BoysChaletViewHolder > adapter;

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

        loadChalets();

    }

    private void loadChalets() {
         adapter = new FirebaseRecyclerAdapter<com.example.raymond.student.Model.BoysChalets, BoysChaletViewHolder>(
                com.example.raymond.student.Model.BoysChalets.class,
                R.layout.boys_chalet_item,
                BoysChaletViewHolder.class,
                boysChalets
        ) {
            @Override
            protected void populateViewHolder(BoysChaletViewHolder viewHolder, com.example.raymond.student.Model.BoysChalets model, int position) {
                viewHolder.txtChaletName.setText(model.getRoom());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);

                final com.example.raymond.student.Model.BoysChalets clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get chalet id to new activity
                        Intent roomList = new Intent(BoysChalets.this, BoysRoomsList.class);
                        roomList.putExtra("chaletId", adapter.getRef(position).getKey());
                        startActivity(roomList);
                    }
                });
            }
        };

        recyclerView_boys_chalets.setAdapter(adapter);
    }
}

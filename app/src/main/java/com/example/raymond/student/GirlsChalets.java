package com.example.raymond.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.Model.BoysChalets;
import com.example.raymond.student.ViewHolder.BoysChaletViewHolder;
import com.example.raymond.student.ViewHolder.GirlsChaletViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class GirlsChalets extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference girlsChalets;

    private FirebaseRecyclerAdapter<com.example.raymond.student.Model.GirlsChalets,GirlsChaletViewHolder> adapter;

    private RecyclerView recyclerView_girls_chalets;
    RecyclerView.LayoutManager layoutManager;


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

        loadChalets();
    }

    private void loadChalets() {
        adapter = new FirebaseRecyclerAdapter<com.example.raymond.student.Model.GirlsChalets, GirlsChaletViewHolder>(
                com.example.raymond.student.Model.GirlsChalets.class,
                R.layout.girls_chalet_item,
                GirlsChaletViewHolder.class,
                girlsChalets

        ) {
            @Override
            protected void populateViewHolder(GirlsChaletViewHolder viewHolder, com.example.raymond.student.Model.GirlsChalets model, int position) {
                viewHolder.txtChaletName.setText(model.getRoom());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get chalet id to new activity
                        Intent roomList = new Intent(GirlsChalets.this, GirlsRoomsList.class);
                        roomList.putExtra("chaletId", adapter.getRef(position).getKey());
                        startActivity(roomList);
                    }
                });
            }
        };
        recyclerView_girls_chalets.setAdapter(adapter);
    }
}

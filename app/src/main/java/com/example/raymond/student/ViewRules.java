package com.example.raymond.student;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.Model.RulesModel;
import com.example.raymond.student.ViewHolder.GirlsRoomViewHolder;
import com.example.raymond.student.ViewHolder.RulesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewRules extends AppCompatActivity {
    private Toolbar ruleToolBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference database;

    private FirebaseRecyclerAdapter<RulesModel, RulesViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rules);

        database = FirebaseDatabase.getInstance().getReference().child("plasuHostel2019").child("rules");
        database.keepSynced(true);

        recyclerView = findViewById(R.id.recycler_rules);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //initialize our toolBar
        ruleToolBar = findViewById(R.id.rule_tool_bar);
        setSupportActionBar(ruleToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Hostel Rules and Notifications");


        loadRules();
    }

    private void loadRules() {
        FirebaseRecyclerOptions<RulesModel>options = new FirebaseRecyclerOptions.Builder<RulesModel>()
                .setQuery(database,RulesModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<RulesModel, RulesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RulesViewHolder holder, int position, @NonNull RulesModel model) {
                holder.txtRules.setText(model.getRule());
                holder.txtTitle.setText(model.getTitle());
                holder.txtDate.setText(RulesUtils.dateFromLong(model.getRuleDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });




            }

            @NonNull
            @Override
            public RulesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rules_layout, parent, false);
                RulesViewHolder viewHolder = new RulesViewHolder(view);
                return viewHolder;

            }
        };

        //set adapter
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}

package com.example.raymond.student.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.R;

public class RulesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtTitle,txtRules, txtDate;
    private ItemClickListener itemClickListener;
    public RulesViewHolder(View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.text_view_title);
        txtRules = itemView.findViewById(R.id.text_view_rule);
        txtDate = itemView.findViewById(R.id.text_view_date);

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

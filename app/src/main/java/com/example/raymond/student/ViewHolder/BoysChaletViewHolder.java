package com.example.raymond.student.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.R;

public class BoysChaletViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtChaletName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;


    public BoysChaletViewHolder(View itemView) {
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

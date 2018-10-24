package com.example.raymond.student.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raymond.student.Interface.ItemClickListener;
import com.example.raymond.student.R;

public class BoysRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtRoomDescription, txtBedNumber;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public BoysRoomViewHolder(View itemView) {
        super(itemView);
        txtRoomDescription = itemView.findViewById(R.id.txtRoomDescription);
        txtBedNumber = itemView.findViewById(R.id.txtBedNumber);
        imageView = itemView.findViewById(R.id.imageViewRoom);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}

package com.example.raymond.student;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter <StudentAdapter.StudentViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;

    public StudentAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;

    }
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewSurname.setText(uploadCurrent.getSurname());
        holder.textViewFName.setText(uploadCurrent.getFirstName());
        holder.textViewUsername.setText(uploadCurrent.getUsername());
        holder.textViewGender.setText(uploadCurrent.getGender());
        holder.textViewEmail.setText(uploadCurrent.getEmail());
        holder.textViewDepartment.setText(uploadCurrent.getDepartment());
        Picasso.get().load(uploadCurrent.getImage()).fit().placeholder(R.drawable.ic_account1).centerCrop().into(holder.imageViewProfilePix);
//        Picasso.with(mContext)
//                .load(uploadCurrent.getImage())
//                .fit()
//                .placeholder(R.drawable.ic_account1)
//                .centerCrop()
//                .into(holder.imageViewProfilePix);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewSurname;
        public TextView textViewFName;
        public TextView textViewUsername;
        public TextView textViewDepartment;
        public TextView textViewGender;
        public ImageView imageViewProfilePix;
        public TextView textViewEmail;

        public StudentViewHolder(View itemView) {
            super(itemView);

            textViewSurname = itemView.findViewById(R.id.text_view_surname);
            textViewFName = itemView.findViewById(R.id.text_view_fName);
            textViewDepartment = itemView.findViewById(R.id.text_view_department);
            textViewGender = itemView.findViewById(R.id.text_view_gender);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
            imageViewProfilePix = itemView.findViewById(R.id.image_view_profile);
            textViewEmail = itemView.findViewById(R.id.text_view_email);
        }
    }
}

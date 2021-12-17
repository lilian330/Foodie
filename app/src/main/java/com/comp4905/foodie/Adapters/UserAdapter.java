package com.comp4905.foodie.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp4905.foodie.Activities.MessageActivity;
import com.comp4905.foodie.Models.User;
import com.comp4905.foodie.R;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context mContext;
    private List<User> mData;

    public UserAdapter(Context mContext, List<User> mData ) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mData.get(position);
        holder.userName.setText(user.getName());
        Glide.with(mContext).load(mData.get(position).getProfile()).into(holder.profile_img);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder{

        public TextView userName,text,time;
        public ImageView profile_img;


        public UserViewHolder(View itemVIew){
            super(itemVIew);

            userName = itemVIew.findViewById(R.id.user_item_name);
            profile_img = itemVIew.findViewById(R.id.user_item_profile);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatActivity = new Intent(mContext, MessageActivity.class);
                    int position = getAdapterPosition();
                    chatActivity.putExtra("name",mData.get(position).getName());
                    chatActivity.putExtra("id",mData.get(position).getId());
                    chatActivity.putExtra("profile",mData.get(position).getProfile());
                    chatActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(chatActivity);
                }
            });
        }

    }

}

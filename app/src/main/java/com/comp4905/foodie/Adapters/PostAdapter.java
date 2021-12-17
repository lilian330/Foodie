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

import com.comp4905.foodie.Activities.PostDetailActivity;
import com.comp4905.foodie.Models.Post;
import com.comp4905.foodie.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext,List<Post> mData){
        this.mContext = mContext;
        this.mData = mData;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.postTitle.setText(mData.get(position).getPostTitle()+mData.get(position).getPostContent());
        holder.uName.setText(mData.get(position).getuName());
        Glide.with(mContext).load(mData.get(position).getPostImg()).into(holder.postImg);
        Glide.with(mContext).load(mData.get(position).getUProfile()).into(holder.uProfile);
    }

    @Override
    public int getItemCount() {return mData.size();}

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle,uName;
        ImageView postImg,uProfile;
        public MyViewHolder(View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.item_title);
            postImg = itemView.findViewById(R.id.item_img);
            uProfile = itemView.findViewById(R.id.item_profile_img);
            uName = itemView.findViewById(R.id.item_user_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();
                    postDetailActivity.putExtra("uName",mData.get(position).getuName());
                    postDetailActivity.putExtra("postTitle",mData.get(position).getPostTitle());
                    postDetailActivity.putExtra("postImg",mData.get(position).getPostImg());
                    postDetailActivity.putExtra("postContent",mData.get(position).getPostContent());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("uID",mData.get(position).getUID());
                    postDetailActivity.putExtra("uProfile",mData.get(position).getUProfile());
                    long timestamp  = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("timeStamp",timestamp);

                    mContext.startActivity(postDetailActivity);
                }
            });

        }


    }
}

package com.comp4905.foodie.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.comp4905.foodie.Models.Comment;
import com.comp4905.foodie.Models.Message;
import com.comp4905.foodie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private Context mContext;
    private List<Message> mData;
    private FirebaseUser currUser;


    public MessageAdapter(Context mContext, List<Message> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_left,parent,false);
            return new MessageAdapter.MessageViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        holder.msg_text.setText(mData.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView msg_text;

        public MessageViewHolder(View itemView) {
            super(itemView);
            msg_text = itemView.findViewById(R.id.msg_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mData.get(position).getSender().equals(currUser.getUid())){
            return MSG_RIGHT;
        }else {
            return MSG_LEFT;
        }
    }
}


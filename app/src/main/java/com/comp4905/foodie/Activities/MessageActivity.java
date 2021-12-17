package com.comp4905.foodie.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.comp4905.foodie.Adapters.MessageAdapter;
import com.comp4905.foodie.Models.Message;
import com.comp4905.foodie.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private ImageButton btn_back;
    private TextView uName;
    Intent intent;

    private FloatingActionButton btn_send;
    private EditText input_msg;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;

    DatabaseReference ref;

    MessageAdapter messageAdapter;
    List<Message> mChat;

    RecyclerView mRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);
        btn_back = findViewById(R.id.back_message);
        uName = findViewById(R.id.user_name);
        btn_send  = findViewById(R.id.send);
        input_msg= findViewById(R.id.input);
        mRV = findViewById(R.id.chat_view);
        mRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRV.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference();
        String userId = getIntent().getExtras().getString("id");
        String userProfile = getIntent().getExtras().getString("profile");
        String userName = getIntent().getExtras().getString("name");
        uName.setText(userName);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currUser =  mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = input_msg.getText().toString();
                if (!msg.equals("")){
                    sendMessage(currUser.getUid(),userId,msg);
                }else {
                    showToast("Empty message!");
                }

                input_msg.setText("");
            }
        });
        readMessages(currUser.getUid(),userId);
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("Messages").push().setValue(hashMap);

    }
    //show toast message
    private void showToast(String message){
        Toast.makeText(MessageActivity.this,message, Toast.LENGTH_SHORT).show();
    }
    private void readMessages(String mID,String uID){
        mChat = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Messages");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Message c = ds.getValue(Message.class);
                    if((c.getReceiver().equals(mID) && c.getSender().equals(uID) )
                            ||(c.getReceiver().equals(uID) && c.getSender().equals(mID) ) ){
                        mChat.add(c);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat);
                    mRV.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
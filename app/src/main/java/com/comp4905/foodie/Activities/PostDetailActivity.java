package com.comp4905.foodie.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.comp4905.foodie.Adapters.CommentAdapter;
import com.comp4905.foodie.Models.Comment;
import com.comp4905.foodie.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    private ImageView dImg, uProfile;
    private ImageButton btn_edit_post,btn_delete_post,btn_star,btn_back;
    private Button btn_confirm_delete,btn_cancel_delete;
    private TextView uName,dTitle,dContent,dTime;
    private String postKey,postImg,postProfile,postName,postTitle,postContent;

//    private ProgressBar comment_loading;

    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private FirebaseDatabase database;

    private Dialog confirmDeleteView,popUpdatePost;

    //for update post
    private TextView update_tv;
    private ImageView update_img;
    private Button btn_update_post;
    private Uri pickedImgUri = null;
    private EditText update_title,update_text;
    private ProgressBar update_loading;
    private ImageButton btn_cancel_update;

    DatabaseReference ref ;
    Query applesQuery ;

    //for Comment
    private RecyclerView commentRV;
    private ImageView comProfile;
    TextView last_text;
    Button btn_add_comment;
    CommentAdapter commentAdapter;
    EditText inputComment;
    ArrayList<Comment> comments;
    private String currUName,currUId,currUImg,postUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        getSupportActionBar().hide();
        btn_back = findViewById(R.id.back_post);
        btn_delete_post = findViewById(R.id.delete_post);
        btn_edit_post = findViewById(R.id.edit_post);
        btn_star = findViewById(R.id.detail_star);
        uName = findViewById(R.id.detail_name);
        dImg = findViewById(R.id.detail_img);
        uProfile = findViewById(R.id.detail_profile_img);
        dContent = findViewById(R.id.detail_content);
        dTitle = findViewById(R.id.detail_title);
        dTime = findViewById(R.id.detail_time);

        last_text = findViewById(R.id.last_text);

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        btn_delete_post.setVisibility(View.INVISIBLE);
        btn_edit_post.setVisibility(View.INVISIBLE);
        String postUid = getIntent().getExtras().getString("uID");
        currUId = currUser.getUid();
        currUImg = currUser.getPhotoUrl().toString();

        if(currUser.getUid().equals(postUid)){
            btn_star.setVisibility(View.INVISIBLE);
            btn_delete_post.setVisibility(View.VISIBLE);
            btn_edit_post.setVisibility(View.VISIBLE);
        }

        commentRV = findViewById(R.id.rv_comment);
        btn_add_comment = findViewById(R.id.btn_add_comment);
        inputComment = findViewById(R.id.input_comment);
        comProfile = findViewById(R.id.curr_profile);
        Glide.with(this).load(currUImg).into(comProfile);



        postTitle = getIntent().getExtras().getString("postTitle");
        dTitle.setText(postTitle);
        postContent = getIntent().getExtras().getString("postContent");
        dContent.setText(postContent);
        postName = getIntent().getExtras().getString("uName");
        uName.setText(postName);

        postImg = getIntent().getExtras().getString("postImg");
        Glide.with(this).load(postImg).into(dImg);

        postProfile = getIntent().getExtras().getString("uProfile");
        Glide.with(this).load(postProfile).into(uProfile);
        postKey = getIntent().getExtras().getString("postKey");

        String time = timestampToString(getIntent().getExtras().getLong("timeStamp"));
        dTime.setText(time);

        initDeleteConfirm();
        initUpdatePost();


        ref = FirebaseDatabase.getInstance().getReference();
        applesQuery = ref.child("Posts").orderByChild("postKey").equalTo(postKey);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_delete_post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmDeleteView.show();
            }
        });
        btn_edit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpdatePost.show();
            }
        });

        //Add Comment
        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_add_comment.setVisibility(View.INVISIBLE);

                DatabaseReference commentRef = database.getReference("Comment").child(postKey).push();
                String comment_content = inputComment.getText().toString();
                String uid = currUser.getUid();
                String uname = currUser.getDisplayName();
                String uImg = currUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content,uid,uImg,uname);

                commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("comment added");
                        inputComment.setText("");
                        btn_add_comment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("fail to add comment : "+e.getMessage());
                    }
                });

            }
        });
        updateComment();
    }

    private void initDeleteConfirm() {
        confirmDeleteView = new Dialog(this);
        confirmDeleteView.setContentView(R.layout.confirm_delete);
        confirmDeleteView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        confirmDeleteView.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        confirmDeleteView.getWindow().getAttributes().gravity = Gravity.CENTER;


        btn_confirm_delete = confirmDeleteView.findViewById(R.id.btn_confirm_delete);
        btn_cancel_delete = confirmDeleteView.findViewById(R.id.btn_cancel_delete);

        btn_cancel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Cancelled");
                confirmDeleteView.dismiss();
            }
        });

        btn_confirm_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                postQuery = ref.child("Posts").orderByChild("postKey").equalTo(postKey);
                Query commentQuery = ref.child("Comments").orderByChild("postKey").equalTo(postKey);

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                        showToast("Successfully Deleted");
                        confirmDeleteView.dismiss();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showToast("Cancelled");
                    }
                });
                commentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot: snapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                        showToast("Successfully Deleted");
                        confirmDeleteView.dismiss();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }
    private void checkAndRequestPermission() {

        if(ContextCompat.checkSelfPermission(PostDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(PostDetailActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                showToast("Please accept for required permission");
            }else{
                ActivityCompat.requestPermissions(PostDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }else
            openGallery();
    }
    private void openGallery() {
        //open Gallery intent and waiting for pick an img
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);
    }

    //show toast message
    private void showToast(String message){
        Toast.makeText(PostDetailActivity.this,message, Toast.LENGTH_SHORT).show();
    }

    private void initUpdatePost() {

        popUpdatePost = new Dialog(this);
        popUpdatePost.setContentView(R.layout.add_post);
        popUpdatePost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popUpdatePost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.MATCH_PARENT);
        popUpdatePost.getWindow().getAttributes().gravity = Gravity.TOP;


        update_title = popUpdatePost.findViewById(R.id.add_title);
        update_text = popUpdatePost.findViewById(R.id.add_text);
        btn_update_post = popUpdatePost.findViewById(R.id.btn_add_post);
        update_loading = popUpdatePost.findViewById(R.id.add_progressBar);
        btn_cancel_update = popUpdatePost.findViewById(R.id.cancel_add_post);
        update_tv = popUpdatePost.findViewById(R.id.add_textview);
        update_tv.setText("Edit Post");
        update_loading.setVisibility(View.INVISIBLE);
        //load current post
        update_title.setText(postTitle);
        update_text.setText(postContent);

        update_img = popUpdatePost.findViewById(R.id.add_img);
        Glide.with(this).load(postImg).into(update_img);

        btn_cancel_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUpdatePost.dismiss();
            }
        });
        update_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermission();

            }
        });

        btn_update_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_update_post.setVisibility(View.INVISIBLE);
                update_loading.setVisibility(View.VISIBLE);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Posts").child(postKey);

                if(update_title.getText().toString().isEmpty() &&
                        update_text.getText().toString().isEmpty() && pickedImgUri==null){
                    showToast("Nothing Changed");
                }else if(pickedImgUri!=null) {
                    //Update Post on Firebase Database
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("post_images");
                    StorageReference imgPath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imgPath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //update Post to firebase database
                                    String imgDowloadLink = uri.toString();
                                    myRef.child("postImg").setValue(imgDowloadLink);
                                    postImg = imgDowloadLink;
                                    update_img.setImageURI(pickedImgUri);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast(e.getMessage());
                                    update_loading.setVisibility(View.INVISIBLE);
                                    btn_update_post.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }


                if(!update_title.getText().toString().isEmpty()) {

                    myRef.child("postTitle").setValue(update_title.getText().toString());
                    postTitle = update_title.getText().toString();
                    dTitle.setText(postTitle);
                }

                if(!update_text.getText().toString().isEmpty()) {
                    myRef.child("postContent").setValue(update_text.getText().toString());
                    postContent = update_text.getText().toString();
                    dContent.setText(postContent);
                }
                update_loading.setVisibility(View.INVISIBLE);
                btn_update_post.setVisibility(View.VISIBLE);
                popUpdatePost.dismiss();
                //TODO:reload post

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            pickedImgUri = data.getData();
            update_img.setImageURI(pickedImgUri);
        }
    }


    public void ChatButton(View view){
        startActivity(new Intent(PostDetailActivity.this, MessageActivity.class));

    }
    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy HH:mm:ss",calendar).toString();
        return date;
    }

    private void updateComment(){

        commentRV.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = database.getReference("Comment").child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    comments.add(comment) ;

                }
                commentAdapter = new CommentAdapter(getApplicationContext(),comments);
                commentRV.setAdapter(commentAdapter);
                if(comments.isEmpty())
                    last_text.setText("No Comment");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
package com.comp4905.foodie.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.comp4905.foodie.Fragment.HomeFragment;
import com.comp4905.foodie.Fragment.MeFragment;
import com.comp4905.foodie.Fragment.MessageFragment;
import com.comp4905.foodie.Models.Post;
import com.comp4905.foodie.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//Main Acticity handel the bottom navigation bar
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private Bundle bundle;

    private ImageButton btn_add;
    private Button btn_add_post;
    private ImageView add_img;
    private Uri pickedImgUri = null;
    private EditText add_title,add_text;
    private ProgressBar add_loading;

    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private DatabaseReference ref;
    Dialog popAddPost;
    private ImageButton btn_cancel_add;


    //block back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        btn_add = findViewById(R.id.addButton);
        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();


        initPost();
        setupAddPostPage();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //set up default fragment,Home
        Fragment defaultFragment = new HomeFragment();
        defaultFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,defaultFragment).commit();
    }

    private void setupAddPostPage() {
        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermission();

            }
        });
    }

    private void checkAndRequestPermission() {

        if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                showToast("Please accept for required permission");
            }else{
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
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
        Toast.makeText(HomeActivity.this,message, Toast.LENGTH_SHORT).show();
    }

    private void initPost() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.MATCH_PARENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        add_img = popAddPost.findViewById(R.id.add_img);
        add_title = popAddPost.findViewById(R.id.add_title);
        add_text = popAddPost.findViewById(R.id.add_text);
        btn_add_post = popAddPost.findViewById(R.id.btn_add_post);
        add_loading = popAddPost.findViewById(R.id.add_progressBar);
        btn_cancel_add = popAddPost.findViewById(R.id.cancel_add_post);
        add_loading.setVisibility(View.INVISIBLE);
        btn_cancel_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.dismiss();
            }
        });

        btn_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_add_post.setVisibility(View.INVISIBLE);
                add_loading.setVisibility(View.VISIBLE);

                if(add_title.getText().toString().isEmpty() ||
                        add_text.getText().toString().isEmpty() || pickedImgUri==null){
                    showToast("Please add any context or choose a photo");
                    btn_add_post.setVisibility(View.VISIBLE);
                    add_loading.setVisibility(View.INVISIBLE);
                }else{
                    //Create new Post and add to Firebase Database
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("post_images");
                    StorageReference imgPath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imgPath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imgDowloadLink = uri.toString();

                                    Post post = new Post(add_title.getText().toString(),
                                            add_text.getText().toString(),
                                            imgDowloadLink,
                                            currUser.getUid(),
                                            currUser.getPhotoUrl().toString(),
                                            currUser.getDisplayName()
                                            );

                                    //add Post to firebase database
                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast(e.getMessage());
                                    add_loading.setVisibility(View.INVISIBLE);
                                    btn_add_post.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void addPost(Post post) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        String key = myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Post Added Successfully");
                add_loading.setVisibility(View.INVISIBLE);
                btn_add_post.setVisibility(View.VISIBLE);
                add_img.setImageDrawable(null);
                add_title.setText("");
                add_text.setText("");
                popAddPost.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            pickedImgUri = data.getData();
            add_img.setImageURI(pickedImgUri);
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.nav_message:
                            selectedFragment = new MessageFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.nav_me:
                            selectedFragment = new MeFragment();
                            selectedFragment.setArguments(bundle);
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                            selectedFragment).commit();
                    return true;
                }
            };

    //Sign out
    public void SignOutButtonOnClick(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void ViewPostDetail(View view){
        startActivity(new Intent(HomeActivity.this, PostDetailActivity.class));

    }

    public void ChatButton(View view){
        startActivity(new Intent(HomeActivity.this, MessageActivity.class));

    }
}

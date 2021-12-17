package com.comp4905.foodie.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.comp4905.foodie.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    ImageView profilePhoto;
    Uri pickedImgUri;

    private EditText userName, userEmail, userPSW,userPSW2;
    private Button btn_reg;
    private FirebaseAuth mAuth;
    private TextView loginText;

    private ProgressBar loading;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register);

        //init Views
        profilePhoto = findViewById(R.id.regProfilePhoto);
        userName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.regEmail);
        userPSW = findViewById(R.id.regPSW);
        userPSW2 = findViewById(R.id.regPSW2);
        btn_reg = findViewById(R.id.btn_reg);
        loading = findViewById(R.id.regProgressBar);
        loginText = findViewById(R.id.regLogin);
        loading.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 32) {
                    checkAndRequestPermission();
                } else {
                    openGallery();
                }
            }
        });


        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_reg.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);

                String name = userName.getText().toString();
                String email = userEmail.getText().toString();
                String psw = userPSW.getText().toString();
                String psw2 = userPSW2.getText().toString();

                if(name.isEmpty() || email.isEmpty() || psw.isEmpty()||psw2.isEmpty()){
                    showToast("Please Verify");
                    btn_reg.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                }else if(!psw.equals(psw2)){
                    showToast("Password does not match");
                    btn_reg.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                }else{
                    createAccount(name,email,psw);
                }

            }
        });

    }

    private void openGallery() {
        //open Gallery intent and waiting for pick an img
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);
    }

    //show toast message
    private void showToast(String message){
        Toast.makeText(RegisterActivity.this,message, Toast.LENGTH_SHORT).show();
    }
    private void checkAndRequestPermission() {

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                showToast("Please accept for required permission");
            }else{
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }else
            openGallery();
    }
    private void createAccount(String name,String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showToast("Account Created!");
                            FirebaseUser currUser = mAuth.getCurrentUser();
                            String uid = currUser.getUid();
                            ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                            HashMap<String,String> newUser = new HashMap<>();
                            newUser.put("id",uid);
                            newUser.put("name", name);
                            newUser.put("profile","default");
                            newUser.put("email",email);

                            final String[] url = new String[1];
                           // updateUserInfo(name,profilePhoto);
                            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
                            //TODO: if user did not pick any image.
                            if(pickedImgUri!=null) {

                                final StorageReference imgFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
                                imgFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imgFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
//                                                url[0] = uri.toString();
                                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(name).setPhotoUri(uri).build();
                                                newUser.put("profile",uri.toString());
                                                ref.setValue(newUser);
                                                mAuth.getCurrentUser().updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            showToast("Register Complete");
                                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }else{
                                ref.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        showToast("Register Complete");
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish();
                                    }
                                });
                            }

                        }else {

                            showToast("Account creation failed" + task.getException().getMessage());
                            btn_reg.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            pickedImgUri = data.getData();
            profilePhoto.setImageURI(pickedImgUri);
        }
    }


}
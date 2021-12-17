package com.comp4905.foodie.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.comp4905.foodie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmail,userPSW;
    private Button btn_login;
    private ProgressBar loading;
    private FirebaseAuth mAuth;
    private TextView regText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.loginEmail);
        userPSW = findViewById(R.id.loginPSW);
        btn_login = findViewById(R.id.btn_login);
        loading = findViewById(R.id.loginProgressBar);
        loading.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        regText = findViewById(R.id.loginReg);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                btn_login.setVisibility(View.INVISIBLE);

                final String email = userEmail.getText().toString();
                final String password = userPSW.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    showToast("Please Verify");
                    btn_login.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                }else{
                    signIn(email,password);
                }

            }
        });
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loading.setVisibility(View.INVISIBLE);
                    btn_login.setVisibility(View.VISIBLE);
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }else{
                    showToast(task.getException().getMessage());
                    btn_login.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                }

            }
        });
    }
    //show toast message
    private void showToast(String message){
        Toast.makeText(LoginActivity.this,message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

}
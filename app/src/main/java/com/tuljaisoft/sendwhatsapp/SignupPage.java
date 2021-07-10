package com.tuljaisoft.sendwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;


public class SignupPage extends AppCompatActivity {

    EditText email, password;
    Button btn_signup;
    FirebaseAuth mAuth;

    private FirebaseUser user;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password1);
        btn_signup = findViewById(R.id.sign);
        mAuth = FirebaseAuth.getInstance();
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if(emailId.isEmpty())
                {
                    email.setError("Email is empty ");
                    email.requestFocus();
                    return;
                }else if (pass.isEmpty())
                {
                    password.setError("Password is empty");
                    password.requestFocus();
                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
                {
                    Toast.makeText(SignupPage.this, "Please Enter valid email Address", Toast.LENGTH_SHORT).show();
                    return;
                }else if(pass.length() < 6)
                {
                    Toast.makeText(SignupPage.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    return;
                }else
                {
                    mAuth.createUserWithEmailAndPassword(emailId, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(SignupPage.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();
                                userProfile();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SignupPage.this, LoginPage.class);
                                startActivity(intent);

                            }
                            else
                            {
                                Toast.makeText(SignupPage.this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void userProfile() {
        user = mAuth.getCurrentUser();
        String emailId = user.getEmail();
        String uid = user.getUid();
        Log.i("mname", user.getEmail());

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("email",emailId);
        hashMap.put("uid",uid);
        hashMap.put("access","no");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef =  db.getReference("Users");
        dbRef.child(uid).setValue(hashMap);

    }
}
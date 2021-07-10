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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    EditText user, pass1;
    Button login, register;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        user = findViewById(R.id.email);
        pass1 =findViewById(R.id.password);
        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.btn_signup);
        mAuth = FirebaseAuth.getInstance();


        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
            if (mFirebaseUser != null) {
//                Toast.makeText(LoginPage.this, "You are logged in", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(LoginPage.this, MainActivity.class);
//                startActivity(i);

                firebaseDatabase = FirebaseDatabase.getInstance();
                myref = firebaseDatabase.getReference("Users");

                Query query = myref.orderByChild("email").equalTo(mFirebaseUser.getEmail());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            String access = (String) ds.child("access").getValue();
                            Log.d("data", access);
                            if(!access.equalsIgnoreCase("Yes"))
                            {
                                mAuth.signOut();
                                Toast.makeText(LoginPage.this, "Please contact Admin", Toast.LENGTH_SHORT).show();
                                break;
                            }else
                            {
                                startActivity(new Intent(LoginPage.this, MainActivity.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(LoginPage.this, "Please Login", Toast.LENGTH_SHORT).show();
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString().trim();
                String pass2 = pass1.getText().toString().trim();
                if(username.isEmpty())
                {
                    user.setError("Email is empty ");
                    user.requestFocus();
                    return;
                }else if (pass2.isEmpty())
                {
                    pass1.setError("Password is empty");
                    pass1.requestFocus();
                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches())
                {
                    Toast.makeText(LoginPage.this, "Please Enter valid email Address", Toast.LENGTH_SHORT).show();
                    return;
                }else if(pass2.length() < 6)
                {
                    Toast.makeText(LoginPage.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    return;
                }else
                {
                   mAuth.signInWithEmailAndPassword(username, pass2).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {
                               firebaseDatabase = FirebaseDatabase.getInstance();
                               myref = firebaseDatabase.getReference("Users");

                               Query query = myref.orderByChild("email").equalTo(username);
                               query.addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                       for (DataSnapshot ds: dataSnapshot.getChildren()){

                                           String access = (String) ds.child("access").getValue();
                                           Log.d("data", access);
                                           if(!access.equalsIgnoreCase("Yes"))
                                           {
                                              mAuth.signOut();
                                               Toast.makeText(LoginPage.this, "Please contact Admin", Toast.LENGTH_SHORT).show();
                                               break;
                                           }else
                                           {
                                               startActivity(new Intent(LoginPage.this, MainActivity.class));
                                           }
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });
                           }else
                           {
                               Toast.makeText(LoginPage.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                }

            }
        });
        /*

         */

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, SignupPage.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
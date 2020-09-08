package com.tuljaisoft.sendwhatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    String url;
    Button send;
    EditText mobileNo, message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        send = findViewById(R.id.send);
        mobileNo = findViewById(R.id.et_mob);
        message = findViewById(R.id.et_message);

        send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                url = "https://api.whatsapp.com/send?phone=+91-" + mobileNo.getText().toString();
                String msg = message.getText().toString();
                Intent direct = new Intent(Intent.ACTION_VIEW, Uri.parse(url + "&text=" + msg));
                direct.setPackage("com.whatsapp");
                startActivity(direct);
            }
        });
    }
}
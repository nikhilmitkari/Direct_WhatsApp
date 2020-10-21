package com.tuljaisoft.sendwhatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


                PackageManager packageManager = getPackageManager();
                url = "https://api.whatsapp.com/send?phone=+91-" + mobileNo.getText().toString();
                String msg = message.getText().toString();
                Intent direct = new Intent(Intent.ACTION_VIEW, Uri.parse(url + "&text=" + msg));
                direct.setPackage("com.whatsapp");
                startActivity(direct);

                /* PackageManager pm=getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = message.getText().toString();

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(MainActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }*/
            }
        });
    }
}

/*
* PackageManager pm=getPackageManager();
    try {

        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        String text = "YOUR TEXT HERE";

        PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
        //Check if package exists or not. If not then code
        //in catch block will be called
        waIntent.setPackage("com.whatsapp");

        waIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(waIntent, "Share with"));

   } catch (NameNotFoundException e) {
        Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                .show();
   }
*/
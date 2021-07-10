package com.tuljaisoft.sendwhatsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    String url;
    Button send;
    EditText mobileNo, message;
    ImageView imageView;
    Button btn_camera, btn_gallery;

    Uri selectedImage = null;

    Button fileButton;
    TextView fileText;

    private Uri filePath = null;
    private static final int FILE_SELECT_CODE = 7;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        send = findViewById(R.id.send);
        mobileNo = findViewById(R.id.et_mob);
        message = findViewById(R.id.et_message);
        imageView = findViewById(R.id.image);
        btn_camera =  findViewById(R.id.btn_camera);
        btn_gallery =  findViewById(R.id.btn_gallery);

        fileButton = findViewById(R.id.btn_choose_file);
        fileText = findViewById(R.id.tv_file_path);

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("*/*");
                startActivityForResult(in, FILE_SELECT_CODE);
            }
        });





        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To take picture from camera

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any action code

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To pick photo from gallery

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }
        });




        send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {

                String toNumber = "+91 "+mobileNo.getText().toString().trim();; // contains spaces.
                toNumber = toNumber.replace("+", "").replace(" ", "");
                String msg = message.getText().toString().trim();

                Intent sendIntent = null;
                if(selectedImage == null && filePath ==null)
                {
                    url = "https://api.whatsapp.com/send?phone=+91-" + mobileNo.getText().toString();
                    sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + "&text=" + msg));
                    sendIntent.setPackage("com.whatsapp");
                }
                else if(filePath == null && selectedImage != null)
                {
                    sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, selectedImage); //Uri.fromFile(selectedImage)
                    sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("image/*");
                }else if(filePath != null && selectedImage == null)
                {
                    sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, filePath); //Uri.fromFile(selectedImage)
                    sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("*/*");
                }else if(filePath != null && selectedImage != null)
                {
                    Toast.makeText(MainActivity.this, "You can not send multiple types of files at the same time ", Toast.LENGTH_SHORT).show();
                    selectedImage = null;
                    filePath = null;
                    url = "https://api.whatsapp.com/send?phone=+91-" + mobileNo.getText().toString();
                    sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + "&text=" + msg));
                    sendIntent.setPackage("com.whatsapp");
                }


                startActivity(sendIntent);
            }
        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    selectedImage = bitmapToUriConverter(imageBitmap);
                    Log.d("from camera", String.valueOf(selectedImage));
                    imageView.setImageURI(selectedImage);
                }

                break;
            case 1:

                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    Log.d("from gallery", String.valueOf(selectedImage));
                    imageView.setImageURI(selectedImage);
                }
                break;

            case FILE_SELECT_CODE:
                if(requestCode == requestCode  && resultCode == RESULT_OK){
                    if(imageReturnedIntent == null)
                    {
                        return;
                    }
                    Uri uri = imageReturnedIntent.getData();
                    filePath  = uri;
                    Log.d("file path", filePath.toString());
                    fileText.setText(filePath.toString());
                }
                break;

                    }

    }


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID+".provider", f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        Log.d("in the bitmap converter", String.valueOf(uri));
        return uri;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d("inside bitmap converter", String.valueOf(inSampleSize));
        return inSampleSize;
    }


    @Override
    public void onBackPressed() {

            new android.app.AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.backbutton))
                    .setPositiveButton(getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_MAIN);
                            i.addCategory(Intent.CATEGORY_HOME);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();

                        }
                    })
                    .setNegativeButton(getString(R.string.no_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
}

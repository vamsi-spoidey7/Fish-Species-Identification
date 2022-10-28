package com.example.fishapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    CardView openCamera,openGallery,profile,logout;
    int imagesize=224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera = findViewById(R.id.openCamera);
        openGallery = findViewById(R.id.openGallery);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.logout);

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .cameraOnly()
                        .crop()
                        .start(10);
            }
        });

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .galleryOnly()
                        .crop()
                        .start(20);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                Toast.makeText(MainActivity.this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void onBackPressed() {
        Toast.makeText(MainActivity.this, "You cannot BackPress here", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

//        if(requestCode==10 && resultCode==RESULT_OK){
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            if(image!=null){
//                int dimension = Math.min(image.getWidth(),image.getHeight());
//                image = ThumbnailUtils.extractThumbnail(image,dimension,dimension);
//                image = Bitmap.createScaledBitmap(image,224,224,false);
//                Intent imgDetails = new Intent(getApplicationContext(),ClassifyActivity.class);
//                imgDetails.putExtra("imgda",image);
//                startActivity(imgDetails);
//            }
//
//        }
//        else if(requestCode==20 && resultCode==RESULT_OK){
//                Bitmap image = (Bitmap) data.getExtras().get("data");
//                if(image!=null){
//                    int dimension = Math.min(image.getWidth(),image.getHeight());
//                    image = ThumbnailUtils.extractThumbnail(image,dimension,dimension);
//                    image = Bitmap.createScaledBitmap(image,224,224,false);
//                    Intent imgDetails = new Intent(getApplicationContext(),ClassifyActivity.class);
//                    imgDetails.putExtra("imgda",image);
//                    startActivity(imgDetails);
//                }
//
//        }else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
//        }



        if(requestCode == 10 && data!=null){
            Uri uri = data.getData();
            if(uri!=null){
                Intent imgDetails = new Intent(getApplicationContext(),ClassifyActivity.class);
                imgDetails.putExtra("img",uri.toString());
                startActivity(imgDetails);
            }
            else{
                Toast.makeText(this, "Image not selected!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == 20 && data!=null){
            Uri uri = data.getData();
            if(uri!=null){
                Intent imgDetails = new Intent(getApplicationContext(),ClassifyActivity.class);
                imgDetails.putExtra("img",uri.toString());
                startActivity(imgDetails);
            }
            else{
                Toast.makeText(this, "Image not selected!", Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Log.d("testing", "tada");
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
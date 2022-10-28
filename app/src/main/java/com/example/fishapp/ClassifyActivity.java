package com.example.fishapp;

import org.tensorflow.lite.support.image.TensorImage;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishapp.ml.Model;
import com.example.fishapp.ml.ModelUnquant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ClassifyActivity extends AppCompatActivity {

    ImageView classifyImage;
    Uri uri;
    Button uploadImg;
    ProgressBar imageUploadProgress;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String uid;
    TextView predictFish;
    StorageReference reference = FirebaseStorage.getInstance().getReference();
    FusedLocationProviderClient fusedLocationProviderClient;
    String country,city,address,latitude,longitude;
    Bitmap bitmap;
    int imageSize = 224;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasiffy);

        classifyImage = findViewById(R.id.classifyImage);
        uploadImg = findViewById(R.id.uploadImg);
        imageUploadProgress = findViewById(R.id.imageUploadProgress);

        predictFish = findViewById(R.id.predictedFish);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Images");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        Intent data = getIntent();
        uri = Uri.parse(data.getStringExtra("img")) ;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        classifyImage.setImageURI(uri);

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                try {
//                    ModelUnquant model = ModelUnquant.newInstance(ClassifyActivity.this);
//
//                    // Creates inputs for reference.
//                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//
//
//
//                    bitmap = Bitmap.createScaledBitmap(bitmap,224,224,true);
//                    inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());
//
//                    // Runs model inference and gets result.
//                    ModelUnquant.Outputs outputs = model.process(inputFeature0);
//                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
//                    byteBuffer.order(ByteOrder.nativeOrder());
//
//                    Log.d("shape", byteBuffer.toString());
//
//                    Toast.makeText(ClassifyActivity.this, ""+outputFeature0.getFloatArray()[0], Toast.LENGTH_SHORT).show();
//
//                    // Releases model resources if no longer used.
//                    model.close();
//                } catch (IOException e) {
//                    // TODO Handle the exception
//                }


                StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
                fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                ImageModel model = new ImageModel(uri.toString(),country,city,address,latitude,longitude);
                                String modelId = databaseReference.push().getKey();
                                assert modelId != null;
                                databaseReference.child(modelId).setValue(model);
                                imageUploadProgress.setVisibility(View.GONE);
//                                Toast.makeText(ClassifyActivity.this, "Red Mullet Fish", Toast.LENGTH_LONG).show();
                                predictFish.setVisibility(View.VISIBLE);
                                predictFish.setText("Red Mullet Fish");
                            }

                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        imageUploadProgress.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageUploadProgress.setVisibility(View.GONE);
                        Toast.makeText(ClassifyActivity.this, "Uploading Failed!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFileExtension(Uri uri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(ClassifyActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try{
                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            latitude = ""+addresses.get(0).getLatitude();
                            longitude = ""+addresses.get(0).getLongitude();
                            address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            country = addresses.get(0).getCountryName();
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(ClassifyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
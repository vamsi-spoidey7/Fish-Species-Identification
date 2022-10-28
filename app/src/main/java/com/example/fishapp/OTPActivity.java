package com.example.fishapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    EditText otpInput1,otpInput2,otpInput3,otpInput4,otpInput5,otpInput6;
    TextView resendOTP,userPhone;
    Button verifyOTP;
    Boolean otpValid = true;
    FirebaseAuth firebaseAuth;
    ProgressBar otpProgress;
    PhoneAuthProvider.ForceResendingToken token;
    String verificationID;
    String phone,email,username,password,uid;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        
        Intent data = getIntent();
        phone = data.getStringExtra("phone");
        Log.d("Great", "onCreate: "+phone);
        username = data.getStringExtra("username");
        email = data.getStringExtra("email");
        password = data.getStringExtra("password");

        otpInput1 = findViewById(R.id.otpInput1);
        otpInput2 = findViewById(R.id.otpInput2);
        otpInput3 = findViewById(R.id.otpInput3);
        otpInput4 = findViewById(R.id.otpInput4);
        otpInput5 = findViewById(R.id.otpInput5);
        otpInput6 = findViewById(R.id.otpInput6);

        setUpOTPInputs();

        otpProgress = findViewById(R.id.otpProgress);
        otpProgress.setVisibility(View.VISIBLE);
        userPhone = findViewById(R.id.userPhone);
        userPhone.setText(phone);

        resendOTP = findViewById(R.id.resendOTP);
        verifyOTP = findViewById(R.id.verifyOTP);

        firebaseAuth = FirebaseAuth.getInstance();

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateField(otpInput1); validateField(otpInput2); validateField(otpInput3); validateField(otpInput4); validateField(otpInput5); validateField(otpInput6);
                otpProgress.setVisibility(View.VISIBLE);
                if(otpValid){
                    String otp = otpInput1.getText().toString()+otpInput2.getText().toString()+otpInput3.getText().toString()+otpInput4.getText().toString()+otpInput5.getText().toString()+otpInput6.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,otp);
                    verifyAuthentication(credential);
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationID = s;
                token = forceResendingToken;
                resendOTP.setVisibility(View.GONE);
                otpProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendOTP.setVisibility(View.VISIBLE);
                otpProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                resendOTP.setVisibility(View.VISIBLE);
                Toast.makeText(OTPActivity.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                otpProgress.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OTPActivity.this, "OTP Verification Failed !!", Toast.LENGTH_SHORT).show();
                otpProgress.setVisibility(View.GONE);
                resendOTP.setVisibility(View.VISIBLE);
            }
        };

        sendOTP(phone);

        
        
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTPMethod(phone);
            }
        });

    }

    public void setUpOTPInputs(){
        otpInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    otpInput2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpInput2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    otpInput3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpInput3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    otpInput4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpInput4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    otpInput5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpInput5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    otpInput6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void sendOTP(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendOTPMethod(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void validateField(EditText field){
        if(field.getText().toString().isEmpty()){
            field.setError("Required");
            otpValid = false;
        }else {
            otpValid = true;
        }
    }

    public void verifyAuthentication(PhoneAuthCredential credential){
        Objects.requireNonNull(firebaseAuth.getCurrentUser()).linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users");
                uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                UserHelperClass userHelperClass = new UserHelperClass(uid,username,email,phone,password);
                reference.child(uid).setValue(userHelperClass);
                Toast.makeText(OTPActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                otpProgress.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                otpProgress.setVisibility(View.GONE);
            }
        });
    }
    public void onBackPressed() {
        Toast.makeText(OTPActivity.this, "You cannot BackPress here", Toast.LENGTH_SHORT).show();
    }
}
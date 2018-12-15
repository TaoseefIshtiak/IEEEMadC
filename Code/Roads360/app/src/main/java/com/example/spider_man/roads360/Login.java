package com.example.spider_man.roads360;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    Button btnSignIn, btnOTP;
    EditText etPhn, etOTP;
    FirebaseAuth mAuth;
    String codeSent;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnOTP = (Button)findViewById(R.id.btnOTP);
        etOTP = (EditText)findViewById(R.id.etOTP);
        etPhn = (EditText)findViewById(R.id.etPhn);

        mAuth = FirebaseAuth.getInstance();

        btnOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // sendVerificationCode();
              //  Toast.makeText(Login.this, "OTP Code Sent to Your Device", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // verifySignInCode();

                Intent intent = new Intent(Login.this, Working.class);
                startActivity(intent);
                
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode,permissions,grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifySignInCode() {

        String code = etOTP.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(Login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Working.class);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Toast.makeText(Login.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }

    private void sendVerificationCode() {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {



            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codeSent =s;
            }
        };

        String phoneNumber = etPhn.getText().toString();
        if(phoneNumber.isEmpty())
        {
            etPhn.setError("Phone Number is required");
            etPhn.requestFocus();
            return;
        }
        else if(phoneNumber.length() <11)
        {
            etPhn.setError("Enter Valid Number");
            etPhn.requestFocus();
            return;

        }
        else
        {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks

        }




    }
}

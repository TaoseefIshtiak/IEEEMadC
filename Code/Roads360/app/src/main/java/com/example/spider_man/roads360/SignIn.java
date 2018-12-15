package com.example.spider_man.roads360;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    EditText etEmail, etPass;
    Button SignIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etEmail = (EditText)findViewById(R.id.etEmailIn);
        etPass = (EditText)findViewById(R.id.etPassIn);

        SignIn = (Button)findViewById(R.id.btnSignIn2);

        mAuth = FirebaseAuth.getInstance();

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void signIn() {
        String finalEmail = etEmail.getText().toString().trim();
        final String finalPass = etPass.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(finalEmail,finalPass)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (finalPass.length() < 6) {
                                etPass.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(SignIn.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent intent = new Intent(SignIn.this, Working.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}

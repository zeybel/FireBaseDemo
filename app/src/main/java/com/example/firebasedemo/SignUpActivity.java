package com.example.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText mEmail, mPassword;
    public Button btnSingUp, btnSignInPage;
    public ProgressBar ProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPassword);
        btnSingUp = findViewById(R.id.btnSignUp);
        btnSignInPage = findViewById(R.id.btnSignInPage);

        ProgressBar = findViewById(R.id.progressBar);

        mEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        mAuth = FirebaseAuth.getInstance();

        btnSingUp.setOnClickListener(this);
        btnSignInPage.setOnClickListener(this);



    }

    private void registerUser() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (email.isEmpty()) {
            mEmail.setError("Email girmeniz gereklidir.");
            mEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Lütfen geçerli bir email adresi giriniz");
            mEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPassword.setError("Şifre girmeniz gereklidir");
            mPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPassword.setError("Şifreniz en az 6 haneli olmalıdır");
            mPassword.requestFocus();
            return;
        }

        ProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Aramıza hoşgeldin!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "Kayıtlı olan bir e-mail girdiniz", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                registerUser();
                break;
            case R.id.btnSignInPage:
                Intent intent = new Intent(SignUpActivity.this , MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}




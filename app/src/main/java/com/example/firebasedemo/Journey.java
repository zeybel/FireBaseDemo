package com.example.firebasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Journey extends AppCompatActivity {

    ImageView mProfileImage;
    TextView mUsernameTextView;
    String userID;
    String userName;

    DatabaseReference mDatabaseRef;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        mProfileImage = findViewById(R.id.profilePhoto);
        mUsernameTextView = findViewById(R.id.userNameTextView);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();



        readOnDatabase();
        //       loadUserInfo();
    }

    private void loadUserInfo() {
//        String userName = mDatabaseRef.child("username").child();

        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getDisplayName() != null) {
                String name = user.getDisplayName();
                mEditText.setText(name);
            }

            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl().toString()).into(mImageView);
            }
        }
    }

    private void readOnDatabase() {

    }
}











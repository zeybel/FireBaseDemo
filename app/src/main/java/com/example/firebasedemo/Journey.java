package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Journey extends AppCompatActivity {

    private static final String TAG = "Journey";

    ImageView mProfileImage;
    TextView mUsernameTextView;

    FirebaseAuth mAuth;
    FirebaseUser currentFirebaseUser;
    String currentUserId;

    private FirebaseDatabase mDatabase;
    private DatabaseReference myDBRef;

    private FirebaseStorage mStorage;
    private StorageReference myStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
        Log.d(TAG, "onCreate: starts");

        mProfileImage = findViewById(R.id.profilePhoto);
        mUsernameTextView = findViewById(R.id.userNameTextView);

        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        myDBRef = mDatabase.getReference();


        readOnDatabase();
    }

    private void readOnDatabase() {
        readFirstName();
        readImage();
    }

    private void readFirstName() {

        if (currentFirebaseUser != null) {
            Log.d(TAG, "readFirstName: starts");

            currentUserId = currentFirebaseUser.getUid();
            Log.d(TAG, "currenuserid: " + currentUserId);

            myDBRef.child("userdetails").child(currentUserId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String userFirstName = (String) snapshot.child("name").getValue();
                                mUsernameTextView.setText(userFirstName);
                            } else {
                                Toast.makeText(Journey.this, "isim hatası", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            if (currentFirebaseUser.getPhotoUrl() != null) {
                Glide.with(this).load(currentFirebaseUser.getPhotoUrl().toString()).into(mProfileImage);
            }
        }

    }

    private void readImage() {

    }
}











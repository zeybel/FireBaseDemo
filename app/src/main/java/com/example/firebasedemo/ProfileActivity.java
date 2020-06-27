package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSEN_IMAGE = 101;
    TextView mTextView;
    ImageView mImageView;
    EditText mEditText;
    Button mButton;
    Uri uriProfileImage;
    private StorageReference mStorageRef;
    String downloadUrl;
    FirebaseAuth mAuth;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProgressBar = findViewById(R.id.progressBar3);
        mTextView = findViewById(R.id.tipTextView);
        mImageView = findViewById(R.id.cameraImage);
        mEditText = findViewById(R.id.nameEditText);
        mButton = findViewById(R.id.btnSave);
        mAuth = FirebaseAuth.getInstance();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
    }

    private void saveUserInfo() {
        String displayName = mEditText.getText().toString();

        if (displayName.isEmpty()) {
            mEditText.setError("İsim girmeniz gerekmektedir");
            mEditText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && downloadUrl != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                    .Builder().setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(downloadUrl))
                    .build();
            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(ProfileActivity.this, "Profil fotoğrafı yüklendi", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSEN_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                mImageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImageToFirebaseStorage() {
        mStorageRef = FirebaseStorage
                .getInstance()
                .getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            mStorageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getMetadata().getReference()
                                    .getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooser = Intent.createChooser(intent, "Profil resmi seçin");
        startActivityForResult(chooser, CHOOSEN_IMAGE);
    }
}
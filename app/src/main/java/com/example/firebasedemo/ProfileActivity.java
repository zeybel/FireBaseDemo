package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSEN_IMAGE = 101;
    EditText mNameEditText, mSurnameEditText, mAgeEditText;
    ImageView mImageView;
    Uri uriProfileImage;
    ProgressBar mProgressBar;
    String profileImageUrl;
    FirebaseAuth mAuth;
    FirebaseUser currentFirebaseUser;

    TextView mTextView;
    Button mUploadButton;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mTextView = findViewById(R.id.tipTextView);
        mImageView = findViewById(R.id.cameraImage);
        mProgressBar = findViewById(R.id.progressBar3);
        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference("profilephotos");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userdetails");

        mNameEditText = findViewById(R.id.nameEditText);
        mSurnameEditText = findViewById(R.id.surnameEditText);
        mAgeEditText = findViewById(R.id.ageEditTextNumber);

        mUploadButton = findViewById(R.id.btnSave);


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUploader();
            }
        });
    }

    private void appJourneyStarts() {
        finish();
        Intent intent = new Intent(this, Journey.class);
        startActivity(intent);
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void fileUploader() {

        String name = mNameEditText.getText().toString();
        String surname = mSurnameEditText.getText().toString();
        String age = mAgeEditText.getText().toString();

        if (name.isEmpty()) {
            mNameEditText.setError("İsim girmeniz gereklidir.");
            mNameEditText.requestFocus();
            return;
        }

        if (surname.isEmpty()) {
            mSurnameEditText.setError("Soyad girmeniz gereklidir");
            mSurnameEditText.requestFocus();
            return;
        }

        if (age.isEmpty()) {
            mAgeEditText.setError("Yaş bilgisi girmeniz gereklidir");
            mAgeEditText.requestFocus();
            return;
        }

        if (uriProfileImage != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            StorageReference ref = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtention(uriProfileImage));

            ref.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, "Profil fotoğrafı başarıyla yüklendi", Toast.LENGTH_LONG).show();

                            Upload upload = new Upload(mNameEditText.getText().toString(),
                                    mSurnameEditText.getText().toString(),
                                    mAgeEditText.getText().toString(),
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                            String userKey = currentFirebaseUser.getUid();
                            mDatabaseRef.child(userKey).setValue(upload);
                            appJourneyStarts();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            Toast.makeText(this, "Fotoğraf seçilmedi.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSEN_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSEN_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uriProfileImage = data.getData();
            mImageView.setImageURI(uriProfileImage);


//            uploadImageToFirebaseStorage();

//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
//                mImageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }


    private void saveUserInfo() {
        String displayName = mNameEditText.getText().toString();

        if (displayName.isEmpty()) {
            mNameEditText.setError("İsim girmeniz gerekmektedir");
            mNameEditText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                    .Builder().setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
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
}
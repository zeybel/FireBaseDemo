package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSEN_IMAGE = 101;
    EditText mEditText;
    ImageView mImageView;
    Uri uriProfileImage;
    ProgressBar mProgressBar;
    String profileImageUrl;
    FirebaseAuth mAuth;

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

        mStorageRef = FirebaseStorage.getInstance().getReference("photos");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("usernames");

        mEditText = findViewById(R.id.nameEditText);
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
//              saveUserInfo();
            }
        });

 //       appJourneyStarts();
  //      loadUserInfo();
    }

    private void appJourneyStarts() {
        finish();
        Intent intent = new Intent(this, Journey.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void fileUploader() {


        if (uriProfileImage != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            StorageReference ref = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtention(uriProfileImage));

            ref.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, "Profil fotoğrafı başarıyla yüklendi", Toast.LENGTH_LONG).show();

                            Upload upload = new Upload(mEditText.getText().toString(),
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String userName = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(userName).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ProfileActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(this, "Fotoğraf seçilmedi.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInfo() {
        String displayName = mEditText.getText().toString();

        if (displayName.isEmpty()) {
            mEditText.setError("İsim girmeniz gerekmektedir");
            mEditText.requestFocus();
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

    public void uploadImageToFirebaseStorage() {
        mStorageRef = FirebaseStorage
                .getInstance()
                .getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mStorageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

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
}
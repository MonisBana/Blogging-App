package com.mab.bloggingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.Manifest;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_STORAGE =100 ;
    private ImageButton mSelectImage;
    private EditText mTitleField;
    private EditText mDescField;
    private Button mSubmitBtn;
    private  Uri mImageUri = null;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    public static final int GALLERY_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mSelectImage = (ImageButton) findViewById(R.id.imageSelect);
        mTitleField = (EditText) findViewById(R.id.titleField);
        mDescField = (EditText) findViewById(R.id.descField);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mProgressDialog = new ProgressDialog(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_READ_STORAGE);

    }

    private void startPosting() {
        mProgressDialog.setMessage("Posting to Blog..");
        mProgressDialog.show();
        final String title_val = mTitleField.getText().toString().trim();
        final String desc_val = mDescField.getText().toString().trim();
        if(!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(desc_val)&&mImageUri!=null){
            StorageReference filepath = mStorageReference.child("Blog_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mDatabaseReference.push();
                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("image").setValue(downloadUrl.toString());
                    mProgressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                }
            });

        }
    }
}

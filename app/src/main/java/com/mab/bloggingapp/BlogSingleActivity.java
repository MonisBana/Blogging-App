package com.mab.bloggingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.PrivateKey;

import static java.lang.System.load;

public class BlogSingleActivity extends AppCompatActivity {
    private String post_key= null;
    private int sPriority;
    private DatabaseReference mDatabaseReference;
    private ImageView mBlogSingleImage;
    private TextView mBlogSingleTitle,mBlogSingleDesc;
    private Button mRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        post_key = getIntent().getStringExtra("BlogId");
        SharedPreferences sp = getSharedPreferences("your_prefs",BlogSingleActivity.MODE_PRIVATE);
        sPriority = sp.getInt("sPriority", -1);
        mBlogSingleImage = (ImageView) findViewById(R.id.imageSelect);
        mBlogSingleTitle = (TextView) findViewById(R.id.titleField);
        mBlogSingleDesc = (TextView) findViewById(R.id.descField);
        mRemoveBtn = (Button) findViewById(R.id.removeBtn);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseReference.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                mBlogSingleTitle.setText(post_title);
                mBlogSingleDesc.setText(post_desc);
                Picasso.with(BlogSingleActivity.this).load(post_image).into(mBlogSingleImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(sPriority ==0){
            mRemoveBtn.setVisibility(View.GONE);
        }
        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference.child(post_key).removeValue();
                Intent mainIntent = new Intent(BlogSingleActivity.this,MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}

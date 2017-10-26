package com.mab.bloggingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.mab.bloggingapp.R.id.action_add;
import static com.mab.bloggingapp.R.id.action_logout;
import static com.mab.bloggingapp.R.id.commentBtn;
import static com.mab.bloggingapp.R.menu.main_menu;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseLike,mDatabaseComment;
    private int Priority;
    private FirebaseAuth mAuth;
    private String Name;
    private EditText mCommentBox;
    private ImageButton mCommentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Priority = intent.getIntExtra("Priority",1);
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mCommentBox = (EditText) findViewById(R.id.commentBox);
        mCommentBtn = (ImageButton) findViewById(R.id.commentBtn);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comment");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mAuth = FirebaseAuth.getInstance();
        Log.d("MAddB", String.valueOf(Priority));
        Priority = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(
                "sPriority", -1);
        Name = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(
                "name","abc");
        Log.d("Named",Name);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setLikeLabel(model.getLikeLabel());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleBlogIntent = new Intent(MainActivity.this,BlogSingleActivity.class);
                        singleBlogIntent.putExtra("BlogId",post_key);
                        singleBlogIntent.putExtra("Priority",Priority);
                        startActivity(singleBlogIntent);



                    }
                });
                viewHolder.mCommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment= String.valueOf(viewHolder.mCommentbox.getText());
                        String Comment = Name +"    "+String.valueOf(viewHolder.mCommentbox.getText());
                        if(!comment.matches("")) {
                            mDatabaseComment.child(post_key).push().setValue(Comment);
                        }
                        Intent commentintent = new Intent(MainActivity.this,Comments.class);
                        commentintent.putExtra("BlogId",post_key);
                        commentintent.putExtra("Name",Name);
                        startActivity(commentintent);
                    }
                });
                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mProcessLike=true;
                        //if(mProcessLike){
                            mDatabaseLike.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(post_key).hasChild("Like")){
                                        long mlikes= (long) dataSnapshot.child(post_key).child("Like").getValue();
                                        int mLike= (int) mlikes;
                                        mDatabaseLike.child(post_key).child("Like").setValue(mLike+1);
                                        viewHolder.mlikeLabel.setText(mLike+1+"");
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                       // }

                        //viewHolder.mlikeLabel.setText(mLikes+"");
                    viewHolder.mLikeBtn.setVisibility(View.GONE);
                        viewHolder.mDislikeBtn.setVisibility(View.VISIBLE);
                    }
                });
                viewHolder.mDislikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabaseLike.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long mlikes= (long) dataSnapshot.child(post_key).child("Like").getValue();
                                int mLikes= (int) mlikes;
                                mDatabaseLike.child(post_key).child("Like").setValue(mLikes-1);
                                viewHolder.mlikeLabel.setText(mLikes-1+"");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        viewHolder.mLikeBtn.setVisibility(View.VISIBLE);
                        viewHolder.mDislikeBtn.setVisibility(View.GONE);
                        //viewHolder.mlikeLabel.setText(mLikes+"");
                    }
                });
           }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }




    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton mLikeBtn;
        ImageButton mDislikeBtn;
        TextView mlikeLabel;
        ImageButton mCommentBtn;
        EditText mCommentbox;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mlikeLabel = (TextView) mView.findViewById(R.id.likesLabel);
            mLikeBtn = (ImageButton) mView.findViewById(R.id.likebtn);
            mDislikeBtn = (ImageButton) mView.findViewById(R.id.dislikeBtn);
            mCommentBtn = (ImageButton) mView.findViewById(R.id.commentBtn);
            mCommentbox = (EditText) mView.findViewById(R.id.commentBox);
        }
        public void setName(String Name){
            TextView name = (TextView) mView.findViewById(R.id.NameField);
            name.setText(Name);
        }
        public void setLikeLabel(String mLikes){
            TextView LikeLabel = (TextView) mView.findViewById(R.id.likesLabel);
            LikeLabel.setText(mLikes);
        }
        public void setTitle(String Title){
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(Title);
        }
        public void setDesc(String Desc){
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(Desc);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(main_menu,menu);
            menu.findItem(R.id.action_add).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId() == action_logout)
        {
            mAuth.signOut();
            Intent SignoutIntent = new Intent(MainActivity.this,GSignin.class);
            startActivity(SignoutIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.mab.bloggingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Comments extends AppCompatActivity {
    private DatabaseReference mDatabaseComments;
    private String post_key;
    private ListView mListView;
    private EditText mCommentBox;
    private ImageButton mCommentBtn;
    private String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        post_key = getIntent().getStringExtra("BlogId");
        Name = getIntent().getStringExtra("Name");
        mDatabaseComments = FirebaseDatabase.getInstance().getReference().child("Comment");
        mListView = (ListView) findViewById(R.id.listView);
        mCommentBox = (EditText) findViewById(R.id.CommentBox);
        mCommentBtn = (ImageButton) findViewById(R.id.CommentBtn);
        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment= String.valueOf(mCommentBox.getText());
                String Comment = Name +"    "+String.valueOf(mCommentBox.getText());
                if(!comment.matches("")) {
                    mDatabaseComments.child(post_key).push().setValue(Comment);
                }
                mCommentBox.setText("");
            }
        });
        final FirebaseListAdapter<String> firebaseListAdapter  = new FirebaseListAdapter<String>(
                this,
                String.class,
                android.R.layout.simple_list_item_1,
                mDatabaseComments.child(post_key)
        ) {
            @Override
            protected void populateView(View v, final String model, int position) {
                final TextView textView =  (TextView) v.findViewById(android.R.id.text1);
                textView.setText(model);
            }
        };
        mListView.setAdapter(firebaseListAdapter);
    }
}

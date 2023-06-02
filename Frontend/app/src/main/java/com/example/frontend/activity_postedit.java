package com.example.frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class activity_postedit extends AppCompatActivity {
    private Button confirmButton;
    private Button backButton;
    private EditText titleText;
    private EditText contentText;
    private Button addImageButton;
    public SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_post_edit);
        confirmButton=findViewById(R.id.confirmButton);
        backButton=findViewById(R.id.backButton);
        titleText=findViewById(R.id.title);
        contentText=findViewById(R.id.content);
        addImageButton=findViewById(R.id.addImageButton);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
    }

    public void onBackClicked(View v){
        setResult(RESULT_CANCELED);
        finish();
    }
    public void onConfirmClicked(View v){
        Intent intent=new Intent();
        Post post=new Post();
        post.setTitle(titleText.getText().toString());
        post.setContent(contentText.getText().toString());
        post.setTime();
        intent.putExtra("newPost", post);
        setResult(RESULT_OK,intent);
        finish();
    }
}

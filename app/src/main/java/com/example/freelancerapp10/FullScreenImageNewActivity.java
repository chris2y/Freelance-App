package com.example.freelancerapp10;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenImageNewActivity extends AppCompatActivity {
    PhotoView fullscreenImageView;
    ImageView clothImage;
    String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_new);
        fullscreenImageView = findViewById(R.id.fullImageView);
        clothImage = findViewById(R.id.xTheIMage);
        imageUrl = getIntent().getStringExtra("image_url");


        Glide.with(this)
                .load(imageUrl)
                .into(fullscreenImageView);

        clothImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}
package com.example.freelancerapp10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.freelancerapp10.adapters.SliderAdapterFullScreen;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class ImageSliderFullScreenActivity extends AppCompatActivity {

    SliderView sliderView;
    ArrayList<String> imageList;
    ImageView backButton,downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider_full_screen);

        backButton = findViewById(R.id.xTheIMage);
        //downloadButton = findViewById(R.id.downloadButtonImage);
        sliderView = findViewById(R.id.detailImageSlider2);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageList = getIntent().getStringArrayListExtra("imageList");
        int position = getIntent().getIntExtra("position", 0);


        SliderAdapterFullScreen sliderAdapterFull = new SliderAdapterFullScreen(this,imageList);

        sliderView.setSliderAdapter(sliderAdapterFull);
        sliderView.setCurrentPagePosition(position);



    }
}
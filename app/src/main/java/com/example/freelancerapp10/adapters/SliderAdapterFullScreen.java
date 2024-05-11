package com.example.freelancerapp10.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.ImageSliderFullScreenActivity;
import com.example.freelancerapp10.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapterFullScreen extends SliderViewAdapter<SliderAdapterFullScreen.SliderAdapterVH> {

    private Context context;
    ArrayList<String> imageUrls;

    public SliderAdapterFullScreen(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        String imageUrl = this.imageUrls.get(position);
        Glide.with(viewHolder.itemView)
                .load(imageUrl)
                .into(viewHolder.imageViewSlide);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    static class SliderAdapterVH extends ViewHolder {

        ImageView imageViewSlide;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewSlide = itemView.findViewById(R.id.image_view);
        }
    }
}

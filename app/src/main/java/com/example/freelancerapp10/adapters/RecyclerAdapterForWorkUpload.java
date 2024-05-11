package com.example.freelancerapp10.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.R;


import java.util.ArrayList;

public class RecyclerAdapterForWorkUpload extends RecyclerView.Adapter<RecyclerAdapterForWorkUpload.ViewHolder> {

    private ArrayList<Uri> uriArrayList;
    private Context context;
    countOfImages countOfImages;

    public RecyclerAdapterForWorkUpload(ArrayList<Uri> uriArrayList, Context context, countOfImages countOfImages) {
        this.uriArrayList = uriArrayList;
        this.context = context;
        this.countOfImages = countOfImages;
    }

    @NonNull
    @Override
    public RecyclerAdapterForWorkUpload.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view,countOfImages);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterForWorkUpload.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri uri = uriArrayList.get(position);
        Glide.with(context).load(uri).into(holder.imageView);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriArrayList.remove(uriArrayList.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                countOfImages.clicked(uriArrayList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,delete;
        countOfImages countOfImages;


        public ViewHolder(@NonNull View itemView, RecyclerAdapterForWorkUpload.countOfImages countOfImages) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.deleteBtn);
            this.countOfImages = countOfImages;
        }
    }

    public interface countOfImages{
        void clicked(int getSize);
    }
}
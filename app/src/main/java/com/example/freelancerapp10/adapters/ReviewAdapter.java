package com.example.freelancerapp10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.ReviewModel;
import com.example.freelancerapp10.model.ReviewModelLoad;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.firebase.Timestamp;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    Context context;
    List<ReviewModel> items;

    public ReviewAdapter(Context context, List<ReviewModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_reviews_row_layout, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ReviewModel currentItem = items.get(position);


        FirebaseUtil.loadNameAndProfileUrl(currentItem.getId(), (fullName, dataProfileImage) -> {
            holder.fullName.setText(fullName);
            Glide.with(context.getApplicationContext())
                    .load(dataProfileImage)
                    .into(holder.profile);
        });



        holder.detail.setText(currentItem.getText());
        holder.ratingBar.setRating(currentItem.getRating());
        //holder.reviewTime.setText(FirebaseUtil.timestampToStringMessage(currentItem.getTimestamp()));
        holder.ratingTextView.setText(String.valueOf(currentItem.getRating()));

        /*holder.profile.setOnClickListener(view -> {
            int position1 = holder.getAdapterPosition();
            String itemKey = items.get(position1).getUserId();
            Intent intent = new Intent(context, ProfileDetailActivity.class);
            intent.putExtra("Key", itemKey);
            context.startActivity(intent);
        });*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fullName, detail, reviewTime,ratingTextView;
        ImageView profile;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.reviewFullNameTextView);
            detail = itemView.findViewById(R.id.reviewDescTextView);
            reviewTime = itemView.findViewById(R.id.reviewTimeTextView);
            profile = itemView.findViewById(R.id.reviewProfile);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);

        }
    }


}

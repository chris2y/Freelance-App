package com.example.freelancerapp10.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.DetailWorkActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.WorkListModel;
import com.example.freelancerapp10.utils.FirebaseUtil;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder> {

    private Context context;
    private List<WorkListModel> items;

    public ListingAdapter(Context context, List<WorkListModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_listing_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WorkListModel currentItem = items.get(position);

        holder.title.setText(currentItem.getTitle());
        holder.postedTime.setText("Posted at " + FirebaseUtil.timestampToStringMessage(currentItem.getTimestamp()));
        holder.price.setText("$" + currentItem.getPrice());
        Glide.with(context.getApplicationContext())
                .load(currentItem.getLogoUrl())
                .into(holder.logo);

        float averageRating = currentItem.getAverage_rating() != 0 ? (float) currentItem.getAverage_rating() : 0.0f;
        int totalRating = (int) currentItem.getTotal_ratings();

        holder.ratingBar.setRating(averageRating);
        holder.ratingTextView.setText(String.format("%.1f", averageRating) + " (" + totalRating + ")");


        holder.itemView.setOnClickListener(view -> {
            int position1 = holder.getAdapterPosition();
            WorkListModel currentItem1 = items.get(position1);
            Intent intent = new Intent(context, DetailWorkActivity.class);
            intent.putExtra("item", currentItem1);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, postedTime, price, ratingTextView;
        ImageView logo;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTextView);
            postedTime = itemView.findViewById(R.id.postedTimeTextView);
            price = itemView.findViewById(R.id.priceTextView);
            logo = itemView.findViewById(R.id.imageViewLogo);

            ratingBar = itemView.findViewById(R.id.ratingBarReview);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);

        }
    }
}

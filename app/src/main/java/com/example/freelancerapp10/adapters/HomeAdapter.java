package com.example.freelancerapp10.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelancerapp10.DetailJobActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.HomeModel;
import com.example.freelancerapp10.utils.FirebaseUtil;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    Context context;
    List<HomeModel> items;


    public HomeAdapter(Context context, List<HomeModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_home_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HomeModel currentItem = items.get(position);

        holder.title.setText(currentItem.getTitle());
        holder.detail.setText(currentItem.getDescription());
        holder.postedTime.setText("Posted: " + FirebaseUtil.getRelativeTimeAgo(currentItem.getTimestamp()));
        holder.price.setText("$" + currentItem.getPrice());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String itemKey = items.get(position).getKey();

                Intent intent = new Intent(context, DetailJobActivity.class);
                intent.putExtra("Key", itemKey);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, detail, postedTime, price;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTextView);
            detail = itemView.findViewById(R.id.descriptionTextView);
            postedTime = itemView.findViewById(R.id.postedTimeTextView);
            price = itemView.findViewById(R.id.priceTextView);

        }
    }


}

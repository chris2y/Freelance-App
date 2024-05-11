package com.example.freelancerapp10.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelancerapp10.AppliersListActivity;
import com.example.freelancerapp10.DetailJobActivity;
import com.example.freelancerapp10.EditPostActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.HomeModel;
import com.example.freelancerapp10.utils.FirebaseUtil;

import java.util.List;

public class PostedAdapter extends RecyclerView.Adapter<PostedAdapter.MyViewHolder> {

    Context context;
    List<HomeModel> items;
    private OnItemRemoveListener removeListener;


    private static boolean isRefreshing = false;

    public interface OnItemRemoveListener {
        void onItemRemoved(int position);
    }


    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        this.removeListener = listener;
    }


    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }



    public PostedAdapter(Context context, List<HomeModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_posted_row_layout, parent, false), items,removeListener);
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

                Intent intent = new Intent(context, AppliersListActivity.class);
                intent.putExtra("Key", itemKey);
                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView title, detail, postedTime, price;
        ImageButton more;
        List<HomeModel> items;
        OnItemRemoveListener removeListener;
        public MyViewHolder(@NonNull View itemView, List<HomeModel> items,OnItemRemoveListener removeListener) {
            super(itemView);
            this.items = items;
            this.removeListener = removeListener;
            title = itemView.findViewById(R.id.titleTextView);
            detail = itemView.findViewById(R.id.descriptionTextView);
            postedTime = itemView.findViewById(R.id.postedTimeTextView);
            price = itemView.findViewById(R.id.priceTextView);
            more = itemView.findViewById(R.id.moreIcon);
            more.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (!isRefreshing){
                showPopupMenu(view);
            }
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
            popupMenu.inflate(R.menu.popupmenupost);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            // Get the position of the clicked item
            int position = getBindingAdapterPosition();
            String key = items.get(position).getKey();


            if (itemId == R.id.edit) {
                if (position >= 0 && position < items.size()) {
                    // Get the FragmentManager from the context
                    Intent intent = new Intent(itemView.getContext(), EditPostActivity.class);
                    intent.putExtra("Key", key);
                    itemView.getContext().startActivity(intent);
                    Log.d("Menu", "Edit");
                }
                return true;
            }

            else if (itemId == R.id.Remove) {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Remove");
                builder.setMessage("Are you sure you want to remove the post?");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseUtil.deleteJobDataFromAllLocation(key);

                    if (removeListener != null) {
                        removeListener.onItemRemoved(getBindingAdapterPosition());
                        Toast.makeText(itemView.getContext(),"Post removed",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("No", (dialog, which) -> {
                }).show();

                Log.d("Menu", "Remove");

                return true;
            }


            else if (itemId == R.id.view) {
                // Check if position is valid and within the list size
                if (position >= 0 && position < items.size()) {
                    // Get the item key based on the position
                    String itemKey = items.get(position).getKey();
                    Intent intent = new Intent(itemView.getContext(), DetailJobActivity.class);
                    intent.putExtra("Key", itemKey);
                    itemView.getContext().startActivity(intent);
                }
                return true;
            } else {
                return false;
            }
        }
}
}
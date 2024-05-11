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

import com.example.freelancerapp10.ApplyActivity;
import com.example.freelancerapp10.DetailJobActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.HomeModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AppliedAdapter extends RecyclerView.Adapter<AppliedAdapter.MyViewHolder> {

    Context context;
    List<HomeModel> items;
    private PostedAdapter.OnItemRemoveListener removeListener;

    private static boolean isRefreshing = false;

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    public interface OnItemRemoveListener {
        void onItemRemoved(int position);
    }


    public void setOnItemRemoveListener(PostedAdapter.OnItemRemoveListener listener) {
        this.removeListener = listener;
    }



    public AppliedAdapter(Context context, List<HomeModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_applied_row_layout, parent, false),  items,removeListener);
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


    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView title, detail, postedTime, price;
        ImageButton more;
        List<HomeModel> items;
        PostedAdapter.OnItemRemoveListener removeListener;


        public MyViewHolder(@NonNull View itemView, List<HomeModel> items, PostedAdapter.OnItemRemoveListener removeListener) {
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
            popupMenu.inflate(R.menu.popupmenuapplied);
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
                    Intent intent = new Intent(itemView.getContext(), ApplyActivity.class);
                    intent.putExtra("itemID",key);
                    intent.putExtra("editProposal","true");
                    itemView.getContext().startActivity(intent);
                    Log.d("Menu", "Edit");
                }
                return true;
            }

            else if (itemId == R.id.Remove) {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Remove");
                builder.setMessage("Are you sure you want to remove the application?");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference("job appliers").child(key).child(FirebaseUtil.currentUserId()).removeValue();
                    FirebaseDatabase.getInstance().getReference("jobs i applied").child(FirebaseUtil.currentUserId()).child(key).removeValue();
                    if (removeListener != null) {
                        removeListener.onItemRemoved(getBindingAdapterPosition());
                        Toast.makeText(itemView.getContext(),"Application removed",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", (dialog, which) -> {

                }).show();
                return true;
            }

            return false;
        }
    }


}

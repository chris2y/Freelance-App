package com.example.freelancerapp10.adapters;


import android.content.Context;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.freelancerapp10.ChatActivity;
import com.example.freelancerapp10.R;

import com.example.freelancerapp10.model.ContractModel;

import com.example.freelancerapp10.model.UserModel;
import com.example.freelancerapp10.utils.AndroidUtil;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.example.freelancerapp10.utils.RateAndReviewDialog;
import com.example.freelancerapp10.utils.ReportDialog;

import java.util.List;



public class ContractsAdapter extends RecyclerView.Adapter<ContractsAdapter.MyViewHolder> {
    Context context;
    List<ContractModel> items;
    private static boolean isRefreshing = false;

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }



    public ContractsAdapter(Context context, List<ContractModel> items) {
        this.context = context;
        this.items = items;

    }



    @NonNull
    @Override
    public ContractsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContractsAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout
                .custom_contracts_row_layout, parent, false),items);
    }




    @Override
    public void onBindViewHolder(@NonNull ContractsAdapter.MyViewHolder holder, int position) {
        ContractModel currentItem = items.get(position);


        holder.jobTitleTextView.setText(currentItem.getTitle());
        holder.jobDescriptionTextView.setText(currentItem.getDescriptionJob());
        holder.priceTextView.setText("$" + currentItem.getPrice());
        holder.proposalDescriptionTextView.setText(currentItem.getProposalDescription());
        holder.contractTimeTextView.setText("Contract date: " + FirebaseUtil.timestampLongToStringMessage((Long) currentItem.getTimestamp()));
        holder.deliveryTimeTextView.setText("Delivery date " + currentItem.getDeliveryDate() + " days");
        holder.chat.setOnClickListener(new View.OnClickListener() {
            String otherUserUid;
            @Override
            public void onClick(View view) {
                if (currentItem.getUserId1().equals(FirebaseUtil.currentUserId())) {
                    otherUserUid = currentItem.getUserId2();
                } else {
                    otherUserUid = currentItem.getUserId1();
                }

                Intent intent = new Intent(context, ChatActivity.class);
                UserModel userModel = new UserModel();
                userModel.setUserId(otherUserUid);
                userModel.setJobId(currentItem.getKey());
                AndroidUtil.passUserModelAsIntents(intent, userModel);

                // Add the FLAG_ACTIVITY_NEW_TASK flag
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView jobTitleTextView, jobDescriptionTextView, priceTextView,
                proposalDescriptionTextView,contractTimeTextView,deliveryTimeTextView;

        Button chat;
        ImageButton more;

        List<ContractModel> items;


        public MyViewHolder(@NonNull View itemView,List<ContractModel> items) {
            super(itemView);
            this.items = items;

            jobTitleTextView = itemView.findViewById(R.id.jobTitleTextView);
            jobDescriptionTextView = itemView.findViewById(R.id.jobDescriptionTextView);
            proposalDescriptionTextView = itemView.findViewById(R.id.proposalDescriptionTextView);
            chat = itemView.findViewById(R.id.chatButton);
            contractTimeTextView = itemView.findViewById(R.id.contractTimeTextView);
            deliveryTimeTextView = itemView.findViewById(R.id.deliveryTimeTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);

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
            popupMenu.inflate(R.menu.popupmenucontract);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();

        }



        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            // Get the position of the clicked item
            int position = getAdapterPosition();

            String otherUser = FirebaseUtil.getOtherUser(items.get(position).getUserId1()
                    ,items.get(position).getUserId2());

            String key = items.get(position).getKey();

            if (itemId == R.id.review) {
                RateAndReviewDialog rateAndReviewDialog = new RateAndReviewDialog(itemView.getContext()
                        ,otherUser,FirebaseUtil.currentUserId(),key);
                rateAndReviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                rateAndReviewDialog.setCancelable(false);
                rateAndReviewDialog.show();
                return true;
            }
            else if (itemId == R.id.projectComplete) {
                showToast("Project Complete item clicked");
                return true;
            }
            else if (itemId == R.id.reportProject) {
                ReportDialog reportDialog = new ReportDialog(itemView.getContext()
                        ,"contract",FirebaseUtil.currentUserId(),otherUser,key);
                reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                reportDialog.setCancelable(false);
                reportDialog.show();
                return true;
            }
            return false;
        }



        private void showToast(String message) {
            Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}

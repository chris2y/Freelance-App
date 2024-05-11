package com.example.freelancerapp10.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.AwardProjectActivity;
import com.example.freelancerapp10.ChatActivity;
import com.example.freelancerapp10.MainActivity;
import com.example.freelancerapp10.PaymentListener;
import com.example.freelancerapp10.ProfileDetailActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.AppliersModel;
import com.example.freelancerapp10.model.UserModel;
import com.example.freelancerapp10.utils.AndroidUtil;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AppliersListAdapter extends RecyclerView.Adapter<AppliersListAdapter.MyViewHolder> {
    Context context;
    List<AppliersModel> items;
    private String itemId;
    private PaymentListener paymentListener;


    public AppliersListAdapter(Context context, List<AppliersModel> items, String itemId, PaymentListener listener) {
        this.context = context;
        this.items = items;
        this.itemId = itemId;
        this.paymentListener = listener;
    }


    @NonNull
    @Override
    public AppliersListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppliersListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout
                .custom_appliers_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppliersListAdapter.MyViewHolder holder, int position) {
        AppliersModel currentItem = items.get(position);

        holder.descriptionTextView.setText(currentItem.getProposalDescription());
        holder.bidTimeTextView.setText("Applied " + FirebaseUtil.getRelativeTimeAgo(currentItem.getTimestamp()));
        holder.deliveryTimeTextView.setText("Will deliver in " + currentItem.getDeliveryDate() + " days");
        holder.bidTextView.setText("$" + currentItem.getBidAmount());


        FirebaseUtil.loadFullName(currentItem.getUserId(),
                fullName -> holder.fullName.setText(fullName));



        FirebaseUtil.loadProfileImage(currentItem.getUserId(), profileImageUrl ->
                Glide.with(context.getApplicationContext())
                .load(profileImageUrl)
                .into(holder.profile));

        holder.profile.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileDetailActivity.class);
            intent.putExtra("userId",currentItem.getUserId());
            context.startActivity(intent);
        });


        holder.awardProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final String contractId = FirebaseDatabase.getInstance().getReference().push().getKey();


                FirebaseUtil.loadNamePhoneEmail(FirebaseUtil.currentUserId(), new FirebaseUtil.namePhoneEmailCallback() {
                    @Override
                    public void onResult(String fullName, String phone, String email) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Award Project");
                        builder.setMessage("To award the project you must first deposit 50% of the deal using Chapa");

                        builder.setPositiveButton("Ok", (dialog, which) -> {
                            paymentListener.initializePayment(currentItem.getBidAmount(), email, fullName, phone,
                                    currentItem.getUserId(), itemId, currentItem.getDeliveryDate() ,  currentItem.getProposalDescription());
                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> {
                            // Add code to handle when the user clicks "Cancel"
                        });

                        builder.show();


                       /* Intent intent = new Intent(context, AwardProjectActivity.class);
                        intent.putExtra("itemId",itemId);
                        intent.putExtra("deliveryDate",currentItem.getDeliveryDate());
                        intent.putExtra("proposalDescription",currentItem.getProposalDescription());
                        intent.putExtra("price",currentItem.getBidAmount());
                        intent.putExtra("userId2",currentItem.getUserId());
                        intent.putExtra("fullName" , fullName);
                        intent.putExtra("email" , email);
                        intent.putExtra("phone" , phone);
                        context.startActivity(intent);*/
                    }
                });

            }
        });




        holder.chat.setOnClickListener(view -> {

            Intent intent = new Intent(context, ChatActivity.class);
            UserModel userModel = new UserModel();
            userModel.setUserId(currentItem.getUserId());
            userModel.setJobId(itemId);
            AndroidUtil.passUserModelAsIntents(intent, userModel);

            // Add the FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        });

    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionTextView,bidTimeTextView, deliveryTimeTextView, bidTextView,fullName;

        Button awardProject,chat;
        ImageView profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            bidTimeTextView = itemView.findViewById(R.id.bidTimeTextView);
            deliveryTimeTextView = itemView.findViewById(R.id.deliveryTimeTextView);
            bidTextView = itemView.findViewById(R.id.bidTextView);
            awardProject = itemView.findViewById(R.id.awardProjectButton);
            chat = itemView.findViewById(R.id.chatButton);
            profile = itemView.findViewById(R.id.applierProfile);
            fullName = itemView.findViewById(R.id.fullNameTextView);
        }

    }
}

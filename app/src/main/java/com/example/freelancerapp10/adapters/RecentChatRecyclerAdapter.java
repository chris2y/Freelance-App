package com.example.freelancerapp10.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.freelancerapp10.ChatActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.ChatroomModel;
import com.example.freelancerapp10.model.UserModel;
import com.example.freelancerapp10.utils.AndroidUtil;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel,
                                       RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;
    public int itemCount = 15;
    public boolean allDataLoaded = false;

    private HashMap<String, UserModel> userCache = new HashMap<>();

    public void loadMoreItems(int additionalItemCount) {
        itemCount += additionalItemCount;
        notifyDataSetChanged();
    }
    public void loadItemsPaused(int additionalItemCount) {
        itemCount = additionalItemCount;
        notifyDataSetChanged();
    }

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        if (position < itemCount) {
            if (  super.getItemCount() < itemCount) {
                // Set the flag if this is the last item in the currently displayed data
                allDataLoaded = true;
            }
        // Check if user details are already cached
        String userId = String.valueOf(FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())); // Replace with the appropriate method to get the user ID
        UserModel cachedUser = userCache.get(userId);

        if (cachedUser != null) {
            // If user details are in the cache, use them
            bindViewHolderWithUserDetails(holder, model, cachedUser);
        } else {
            // If user details are not in the cache, fetch them from Firebase
            FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                UserModel otherUserModel = dataSnapshot.getValue(UserModel.class);
                                // Cache the loaded user details
                                userCache.put(userId, otherUserModel);
                                // Update the ViewHolder
                                bindViewHolderWithUserDetails(holder, model, otherUserModel);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
        }
    }
    }


    private void bindViewHolderWithUserDetails(@NonNull ChatroomModelViewHolder holder, @NonNull ChatroomModel model, @NonNull UserModel otherUserModel) {
        // Update the UI elements in your ViewHolder with user details
        holder.usernameText.setText(otherUserModel.getFullName());

        Glide.with(context)
                .load(otherUserModel.getDataProfileImage())
                .into(holder.profilePic);

        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

        if (lastMessageSentByMe)
            holder.lastMessageText.setText("You : " + model.getLastMessage());
        else
            holder.lastMessageText.setText(model.getLastMessage());

        holder.lastMessageTime.setText(FirebaseUtil.timestampToStringMessage(model.getLastMessageTimestamp()));
        holder.titleMessageText.setText(model.getTitle());

        //Log.d("job", model.getJobId());

        holder.itemView.setOnClickListener(v -> {
            // Handle item click here
            Intent intent = new Intent(context, ChatActivity.class);
            UserModel userModel = new UserModel();
            userModel.setUserId(otherUserModel.getUserId());
            userModel.setJobId(model.getJobId());
            AndroidUtil.passUserModelAsIntents(intent, userModel);
            context.startActivity(intent);
        });
    }


    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return Math.min(super.getItemCount(), itemCount);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        TextView titleMessageText;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            titleMessageText = itemView.findViewById(R.id.title_message_text);
            profilePic = itemView.findViewById(R.id.profile_pic_view);
        }
    }
}

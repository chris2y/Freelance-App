package com.example.freelancerapp10;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.adapters.ChatRecyclerAdapter;
import com.example.freelancerapp10.model.ChatMessageModel;
import com.example.freelancerapp10.model.ChatroomModel;
import com.example.freelancerapp10.model.UserModel;
import com.example.freelancerapp10.utils.AndroidUtil;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    TextInputLayout editText;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId(),otherUser.getJobId());


        editText = findViewById(R.id.chatQuery);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePicture = findViewById(R.id.profile_pic_layout);


        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });

        editText.getEditText().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if touch event is within the bounds of the drawable end
                if (event.getRawX() >= (editText.getEditText().getRight() - editText.getEditText().getCompoundDrawables()[2].getBounds().width())) {

                    String message = editText.getEditText().getText().toString().trim();
                    if (message.isEmpty()) {
                        editText.getEditText().setError("Enter message");
                        return true;
                    }
                    if(chatroomModel==null){
                        CreateChatroomModel();
                    }
                    sendMessageToUser(message);
                    editText.getEditText().setText("");


                    return true;
                }
            }
            // Return false to allow other touch events to be handled
            return false;
        });


        editText.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Get the text from the TextInputEditText

                String question = editText.getEditText().getText().toString().trim();
                if (question.isEmpty()) {
                    editText.getEditText().setError("Enter question");
                    return true;
                }

                String message = editText.getEditText().getText().toString().trim();
                if (message.isEmpty()) {
                    editText.getEditText().setError("Enter message");
                    return true;
                }
                if(chatroomModel==null){
                    CreateChatroomModel();
                }
                sendMessageToUser(message);
                editText.getEditText().setText("");



                return true;
            }
            return false;
        });

        profilePicture.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this,ProfileDetailActivity.class);
            intent.putExtra("userId",otherUser.getUserId());
            startActivity(intent);
        });

        setUserNameAndProfileImage();



        getChatroomModel();
        setupChatRecyclerView();
    }

    private void setUserNameAndProfileImage() {

        FirebaseUtil.loadFullName(otherUser.getUserId(),
                fullName -> otherUsername.setText(fullName));

        FirebaseUtil.loadProfileImage(otherUser.getUserId(),
                profileImageUrl -> Glide.with(getApplicationContext())
                .load(profileImageUrl)
                .into(profilePicture));

    }

    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message){

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());

        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                    }
                });
    }

    void getChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
            }
        });
    }

    private void CreateChatroomModel() {

        String currentUserId = FirebaseUtil.currentUserId();
        String otherUserId = otherUser.getUserId();
        //first time chat
        chatroomModel = new ChatroomModel(
                chatroomId,
                Arrays.asList(currentUserId, otherUserId),
                Timestamp.now(),
                "",
                otherUser.getJobId(),
                otherUser.getTitle()
        );
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

    }

}



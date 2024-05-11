package com.example.freelancerapp10.MainFragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelancerapp10.R;

import com.example.freelancerapp10.adapters.RecentChatRecyclerAdapter;
import com.example.freelancerapp10.model.ChatroomModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MessageFragment extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar,progressBarLoadMore;
    TextView noChat;
    Parcelable recyclerViewState;
    private int savedItemCount = 15;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = rootView.findViewById(R.id.recyler_view1);
        linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        progressBar = rootView.findViewById(R.id.progressbarMessage);
        noChat = rootView.findViewById(R.id.noChatTextView);
        progressBarLoadMore = rootView.findViewById(R.id.progressBarLoadMore);
        setupRecyclerView();
        recyclerView.addOnScrollListener(onScrollListener);

        return rootView;
    }

    void setupRecyclerView() {
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        adapter = new RecentChatRecyclerAdapter(options, rootView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

            // Check if we've reached the end of the list
            if (lastVisibleItem == totalItemCount - 1 && dy > 0) {

                if(!adapter.allDataLoaded){
                    progressBarLoadMore.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> {
                        adapter.loadMoreItems(5);
                        progressBarLoadMore.setVisibility(View.GONE);
                    }, 1500);
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        // Attach a snapshot listener to the query to monitor changes in data
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                // Handle any errors that occur during data loading
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if the data is empty
            if (queryDocumentSnapshots != null && queryDocumentSnapshots.isEmpty()) {
                // If the query is empty, show the "No recent chat" message
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noChat.setVisibility(View.VISIBLE);
            } else {
                // If there are items in the query, hide the "No recent chat" message
                new Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }, 1000);
                noChat.setVisibility(View.GONE);
            }
        });
        adapter.startListening();
    }



    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    public void onPause() {
        super.onPause();
        savedItemCount = adapter.itemCount;
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        new Handler().postDelayed(() ->
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState),
                80);
        adapter.loadItemsPaused(savedItemCount);
    }

}
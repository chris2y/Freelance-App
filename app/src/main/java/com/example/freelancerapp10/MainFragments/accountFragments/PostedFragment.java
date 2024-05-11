package com.example.freelancerapp10.MainFragments.accountFragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.freelancerapp10.R;
import com.example.freelancerapp10.adapters.PostedAdapter;
import com.example.freelancerapp10.model.HomeModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.example.freelancerapp10.utils.HomeDiffCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PostedFragment extends Fragment {

    private View rootView;
    private List<HomeModel> homeItems;
    private RecyclerView recyclerView;
    private PostedAdapter adapter;
    private ProgressBar progressBar,progressBarLoadMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noData;
    String lastTimestamp = "9999999999999";
    Parcelable recyclerViewState;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    DatabaseReference userJobsRef;
    private CompletableFuture<Void> lastLoadedFuture;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_posted, container, false);
        userJobsRef = FirebaseDatabase.getInstance().getReference("jobs i posted")
                .child(FirebaseUtil.currentUserId());
        initView();
        setupRecyclerView();
        loadDataFromDatabase();
        setupSwipeDownToRefresh();
        return rootView;
    }

    private void initView() {
        homeItems = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.progressbarPosted);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshP);
        recyclerView = rootView.findViewById(R.id.recyclerForPosted);
        noData = rootView.findViewById(R.id.noPostTextView);
        progressBarLoadMore = rootView.findViewById(R.id.progressBarLoadMore);
        //next = rootView.findViewById(R.id.next);
        adapter = new PostedAdapter(getContext(), homeItems);
        adapter.setOnItemRemoveListener(this::onItemRemoved);

    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(onScrollListener);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeDownToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.GONE);
            isLoading = false;
            isLastPage = false;
            progressBarLoadMore.setVisibility(View.GONE);
            adapter.setRefreshing(true);
            loadDataFromDatabase();
        });
    }

    private void loadDataFromFirebase(boolean loadMore) {
        isLoading = true;
        Query query = userJobsRef.orderByKey();

        if (loadMore) {
            query = query.endAt(lastTimestamp);
        }

        query.limitToLast(11).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> jobIds = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                        String jobId = jobSnapshot.getKey();
                        jobIds.add(jobId);
                    }

                    if (jobIds.size() < 11) {
                        Toast.makeText(getContext(), "You reached the last item", Toast.LENGTH_SHORT).show();
                        isLastPage = true;
                        isLoading = false;
                        progressBarLoadMore.setVisibility(View.GONE);

                        if (loadMore) {
                            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                            jobIds.remove(jobIds.size() - 1);
                        }

                        loadJobData(jobIds, !loadMore);
                        return;
                    }

                    lastTimestamp = jobIds.get(0);
                    if (loadMore)
                        jobIds.remove(jobIds.size() - 1);
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                    isLoading = false;
                    loadJobData(jobIds, !loadMore);
                } else if(homeItems.isEmpty()) {
                    handleNoData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                isLoading = false;
                progressBarLoadMore.setVisibility(View.GONE);
            }
        });
    }


    private void loadDataFromDatabase() {
        loadDataFromFirebase(false);
    }

    private void loadMoreData() {
        loadDataFromFirebase(true);
    }




    private void loadJobData(List<String> jobIds, boolean clearList) {
        // Cancel the last loaded CompletableFuture if it exists
        if (lastLoadedFuture != null && !lastLoadedFuture.isDone()) {
            lastLoadedFuture.cancel(true);
        }

        DatabaseReference jobDataRef = FirebaseDatabase.getInstance().getReference("Job Data");
        List<CompletableFuture<HomeModel>> futures = new ArrayList<>();


        for (String jobId : jobIds) {
            CompletableFuture<HomeModel> future = new CompletableFuture<>();
            jobDataRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HomeModel homeModel = snapshot.getValue(HomeModel.class);
                    if (snapshot.exists()) {
                        homeModel.setKey(snapshot.getKey());
                        future.complete(homeModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            futures.add(future);
        }

        // Combine all futures into one CompletableFuture
        lastLoadedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        lastLoadedFuture.thenApply(v -> {
            List<HomeModel> loadedItems = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            // Add the loaded items to the existing list
            refreshTheAdapterAndView(loadedItems,clearList);

            return null;
        }).exceptionally(ex -> {
            // Handle exceptions here if necessary
            ex.printStackTrace();
            return null;
        });
    }



    private void refreshTheAdapterAndView(List<HomeModel> updatedList,boolean clearList) {
        Collections.reverse(updatedList);
        HomeDiffCallback diffCallback = new HomeDiffCallback(homeItems, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        if (clearList) {
            homeItems.clear(); // Clear the list if needed
        }
        homeItems.addAll(updatedList);

        progressBar.setVisibility(View.GONE);
        progressBarLoadMore.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (updatedList.isEmpty()&&homeItems.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }

        diffResult.dispatchUpdatesTo(adapter);
        adapter.setRefreshing(false);
        if(!clearList)
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

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
                // Load more data
                if (isLastPage || isLoading) {
                    return;
                }
                progressBarLoadMore.setVisibility(View.VISIBLE);
                loadMoreData();
            }
        }
    };

    public void onItemRemoved(int position) {
        // Remove the item from the adapter's data list
        homeItems.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void handleNoData() {
        homeItems.clear();
        noData.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}

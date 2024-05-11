package com.example.freelancerapp10.MainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freelancerapp10.adapters.HomeAdapter;
import com.example.freelancerapp10.model.HomeModel;
import com.example.freelancerapp10.R;
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

public class HomeFragment extends Fragment {
    private List<HomeModel> mHomeItems;
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar,progressBarRow;
    private View rootView;
    TextView noData;

    long lastTimestamp = 0;
    Parcelable recyclerViewState;
    private boolean isLoading = false;
    private boolean isLastPage = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mHomeItems = new ArrayList<>();
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        noData = rootView.findViewById(R.id.noHomeItemsTextView);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        progressBar = rootView.findViewById(R.id.progressbarHome);
        progressBarRow = rootView.findViewById(R.id.progressBarLoadMore);
        //

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        adapter = new HomeAdapter(getContext(), mHomeItems);
        recyclerView.setAdapter(adapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("Job Data");

        recyclerView.addOnScrollListener(onScrollListener);
        swipeDownToRefresh();
        setupSearchView();

        loadDataFromDatabase();


        return rootView;
    }

    private void swipeDownToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.GONE);
            progressBarRow.setVisibility(View.GONE);
            loadDataFromDatabase();
        });
    }

    private void loadDataFromDatabase() {
        databaseReference.orderByChild("timestamp")
                .limitToLast(11).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HomeModel> updatedList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        HomeModel homeModel = itemSnapshot.getValue(HomeModel.class);
                        homeModel.setKey(itemSnapshot.getKey());
                        updatedList.add(homeModel);
                    }
                    lastTimestamp =  updatedList.get(0).getTimestamp();
                    Collections.reverse(updatedList);
                    updatedList.remove(updatedList.size() - 1);
                    refreshTheAdapterAndView(updatedList);
                    isLoading = false;
                    isLastPage = false;
                } else {
                    handleNoData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void loadMoreData() {
        if (isLastPage || isLoading) {
        return;
        }

        isLoading = true;
        progressBarRow.setVisibility(View.VISIBLE);

        databaseReference.orderByChild("timestamp")
                .endAt(lastTimestamp)
                .limitToLast(11).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<HomeModel> updatedList = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                HomeModel homeModel = itemSnapshot.getValue(HomeModel.class);
                                homeModel.setKey(itemSnapshot.getKey());
                                updatedList.add(homeModel);
                            }

                            if (updatedList.size() < 11 ) {
                                Toast.makeText(getContext(), "You reached last item", Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                                isLoading = false;
                                Collections.reverse(updatedList);
                                progressBarRow.setVisibility(View.GONE);
                                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                                refreshLoadMore(updatedList);
                                return;
                            }

                            lastTimestamp =  updatedList.get(0).getTimestamp();
                            Collections.reverse(updatedList);
                            updatedList.remove(updatedList.size() - 1);
                            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                            refreshLoadMore(updatedList);
                            isLoading = false;
                            progressBarRow.setVisibility(View.GONE);
                        } else {
                            handleNoData();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading = false;
                        progressBarRow.setVisibility(View.GONE);
                    }
                });
    }


    private void refreshLoadMore(List<HomeModel> updatedList) {
        HomeDiffCallback diffCallback = new HomeDiffCallback(mHomeItems, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // Append new data to the existing list
        mHomeItems.addAll(updatedList);

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);


        diffResult.dispatchUpdatesTo(adapter);
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    private void refreshTheAdapterAndView(List<HomeModel> updatedList) {
        HomeDiffCallback diffCallback = new HomeDiffCallback(mHomeItems, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        mHomeItems.clear();
        mHomeItems.addAll(updatedList);

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (mHomeItems.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }

        diffResult.dispatchUpdatesTo(adapter);
    }

    private void handleNoData() {
        mHomeItems.clear();
        noData.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
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
                loadMoreData();
            }
        }
    };

    private void setupSearchView() {
        SearchView searchView = rootView.findViewById(R.id.search_view);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), "Search query submitted: " + query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text changes
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        new Handler().postDelayed(() -> {
            progressBarRow.setVisibility(View.GONE);
        }, 100);
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
package com.example.freelancerapp10.MainFragments.accountFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.freelancerapp10.R;
import com.example.freelancerapp10.adapters.ContractsAdapter;
import com.example.freelancerapp10.model.ContractModel;
import com.example.freelancerapp10.model.HomeModel;
import com.example.freelancerapp10.utils.ContractsDiffCallback;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ContractsFragment extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    List<ContractModel> homeItems;
    ContractsAdapter adapter;
    LinearLayoutManager manager;

    ProgressBar progressBar;
    DatabaseReference userJobsRef;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView noData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_contracts, container, false);
        homeItems = new ArrayList<>();
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshC);
        progressBar = rootView.findViewById(R.id.progressbarContracts);
        noData = rootView.findViewById(R.id.noContractsTextView);
        recyclerView = rootView.findViewById(R.id.recyclerForContracts);
        adapter = new ContractsAdapter(getContext(), homeItems);
        manager = new LinearLayoutManager(getContext());


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        loadDataFromDatabase();
        swipeDownToRefresh();


        return rootView;
    }

    private void swipeDownToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.GONE);
            adapter.setRefreshing(true);
            loadDataFromDatabase();
        });
    }

    private void loadDataFromDatabase() {
        userJobsRef = FirebaseDatabase.getInstance().getReference("my contracts")
                .child(FirebaseUtil.currentUserId());
        userJobsRef.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { // Check if the snapshot exists
                    List<String> contractIds = new ArrayList<>();
                    for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                        String contractId = jobSnapshot.getKey();
                        contractIds.add(contractId);
                        Log.d("datas", "Fetched data: " + contractId);

                    }
                    loadJobData(contractIds);
                } else {
                    // Handle the case when there is no data
                    noData.setVisibility(View.VISIBLE); // Show "No Data" message
                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void loadJobData(List<String> contractIds) {
        DatabaseReference jobDataRef = FirebaseDatabase.getInstance().getReference("contracts");
        List<CompletableFuture<ContractModel>> futures = new ArrayList<>();

        for (String jobId : contractIds) {
            CompletableFuture<ContractModel> future = new CompletableFuture<>();
            jobDataRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ContractModel contractModel = snapshot.getValue(ContractModel.class);
                    if (snapshot.exists()) {
                        contractModel.setKey(snapshot.getKey());
                        future.complete(contractModel);
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
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenApply(v -> {
            List<ContractModel> updatedList = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            refreshTheAdapterAndView(updatedList);

            return null;
        }).exceptionally(ex -> {
            // Handle exceptions here if necessary
            ex.printStackTrace();
            return null;
        });
    }

    private void refreshTheAdapterAndView(List<ContractModel> updatedList) {
        Collections.reverse(updatedList);
        ContractsDiffCallback diffCallback = new ContractsDiffCallback(homeItems, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        // Update the original list with the updated list
        homeItems.clear();
        homeItems.addAll(updatedList);

        swipeRefreshLayout.setRefreshing(false);
        noData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);


        // Dispatch the diffResult to the adapter for efficient updates
        diffResult.dispatchUpdatesTo(adapter);
        adapter.setRefreshing(false);
    }


    @Override
    public void onPause() {
        super.onPause();
        // If the fragment is paused (navigated away), make sure to stop refreshing
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
package com.example.freelancerapp10.MainFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freelancerapp10.ListingListsActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.adapters.ListingAdapter;
import com.example.freelancerapp10.adapters.ListingRecyclerViewAdapter;
import com.example.freelancerapp10.model.WorkListModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListingFragment extends Fragment {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ListingAdapter adapter;
    private List<WorkListModel> itemList;
    private DocumentSnapshot lastVisible;
    private boolean isLastPage = false;
    private Query query;
    final int LOAD_SIZE = 4;
    private ProgressBar progressBar, progressBarRow;
    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView listRecyclerView;
    ArrayList<String> dataModels;
    private NestedScrollView nestedScrollView;

    private ListingRecyclerViewAdapter adapterList;

    TextView latest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listing, container, false);

        SearchView searchView = rootView.findViewById(R.id.search_view);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        adapter = new ListingAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        progressBar = rootView.findViewById(R.id.progressbarListing);
        progressBarRow = rootView.findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);

        listRecyclerView = rootView.findViewById(R.id.listRecyclerView);
        latest = rootView.findViewById(R.id.latestTextView);

        dataModels = new ArrayList<>();

        dataModels.add("Graphics Design");
        dataModels.add("Marketing");
        dataModels.add("Photography");
        dataModels.add("Programing & Tech");
        dataModels.add("Video Editing");
        dataModels.add("Writing & Translation");

        listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterList = new ListingRecyclerViewAdapter(dataModels, getContext());
        listRecyclerView.setAdapter(adapterList);

        query = db.collection("work_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(LOAD_SIZE);

        nestedScrollView = rootView.findViewById(R.id.scrollNestedViewListing);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // Scrolled to bottom
                    loadMoreData();
                }
            }
        });

        loadData();
        swipeDownToRefresh();


        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), "Search query submitted: " + query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return rootView;
    }

    private void swipeDownToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            isLastPage = false;
            progressBar.setVisibility(View.GONE);
            progressBarRow.setVisibility(View.GONE);
            loadData();
        });
    }

    private void loadData() {
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() < LOAD_SIZE) {
                            isLastPage = true;
                        }
                        itemList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            WorkListModel workItem = documentSnapshot.toObject(WorkListModel.class);
                            workItem.setDocId(documentSnapshot.getId());
                            itemList.add(workItem);
                            lastVisible = documentSnapshot;
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        listRecyclerView.setVisibility(View.VISIBLE);
                        latest.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMoreData() {
        if (isLastPage) {
            Toast.makeText(getContext(), "You've reached the last item", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBarRow.setVisibility(View.VISIBLE);
        query.startAfter(lastVisible).limit(LOAD_SIZE).get() // Limit query to LOAD_SIZE
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() < LOAD_SIZE) {
                            isLastPage = true;
                        }
                        // Exclude the last item from being added again
                        int sizeBeforeAdding = itemList.size();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (!documentSnapshot.equals(lastVisible)) { // Exclude the last item
                                WorkListModel workItem = documentSnapshot.toObject(WorkListModel.class);
                                workItem.setDocId(documentSnapshot.getId());
                                itemList.add(workItem);
                                lastVisible = documentSnapshot;
                            }
                        }
                        if (itemList.size() - sizeBeforeAdding < LOAD_SIZE) {
                            isLastPage = true;
                        }
                        progressBarRow.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarRow.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load more data!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLastPage = false;
        progressBarRow.setVisibility(View.GONE);
    }
}

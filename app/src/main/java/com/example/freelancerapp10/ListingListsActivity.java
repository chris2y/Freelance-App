package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

public class ListingListsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ListingAdapter adapter;
    private List<WorkListModel> itemList;
    private DocumentSnapshot lastVisible;
    private boolean isLastPage = false;
    private Query query;
    final int LOAD_SIZE = 5;
    private ProgressBar progressBar,progressBarRow;
    private SwipeRefreshLayout swipeRefreshLayout;

    String clickedItem;

    private NestedScrollView nestedScrollView;
    ImageButton back;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_lists);

        if (getIntent().hasExtra("selected")) {
            clickedItem = getIntent().getStringExtra("selected");
            // Now you have the clicked item's data, you can use it as needed
            // For example, set it to a TextView
            TextView textView = findViewById(R.id.listingTypeText);
            textView.setText(clickedItem);
        }


        back = findViewById(R.id.backButton);

        back.setOnClickListener(view -> onBackPressed());


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        adapter = new ListingAdapter(this, itemList);
        //recyclerView.addOnScrollListener(onScrollListener);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressbarListing);
        progressBarRow = findViewById(R.id.progressBarLoadMore);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        query = db.collection("work_posts")
                .whereEqualTo("workType", clickedItem)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(LOAD_SIZE);

        nestedScrollView = findViewById(R.id.scrollNestedViewListing);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                // Scrolled to bottom
                loadMoreData();
            }
        });

        loadData();
        swipeDownToRefresh();


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
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ListingListsActivity.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }

    private void loadMoreData() {
        if (isLastPage) {
            Toast.makeText(ListingListsActivity.this, "You've reached the last item", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ListingListsActivity.this, "Failed to load more data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
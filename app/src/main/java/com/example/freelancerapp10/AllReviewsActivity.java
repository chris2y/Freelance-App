package com.example.freelancerapp10;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelancerapp10.adapters.ReviewAdapter;
import com.example.freelancerapp10.model.ReviewModel;
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


public class AllReviewsActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Query query;
    private DocumentSnapshot lastVisible;
    private boolean isLastPage = false;
    final int LOAD_SIZE = 15;
    RecyclerView reviewRecyclerView;
    ProgressBar progressBar,progressBarRow;
    private List<ReviewModel> reviewList;
    ReviewAdapter reviewAdapter;
    ImageButton back;

    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);
        reviewRecyclerView = findViewById(R.id.allReviewsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.backButton);
        progressBarRow = findViewById(R.id.progressBarLoadMore);

        nestedScrollView = findViewById(R.id.allReviewNestedScrollView);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        // Scrolled to bottom
                        loadMoreData();
                    }
                });
        reviewList = new ArrayList<>();

        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(AllReviewsActivity.this));
        reviewAdapter = new ReviewAdapter(AllReviewsActivity.this, reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);

        String docId = getIntent().getStringExtra("docId");

        query = db.collection("work_reviews")
                .document(docId)
                .collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(LOAD_SIZE);

        loadReview();
        back.setOnClickListener(view -> onBackPressed());



    }

    private void loadMoreData() {
        if (isLastPage) {
            Toast.makeText(AllReviewsActivity.this, "You've reached the last item", Toast.LENGTH_SHORT).show();
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
                        int sizeBeforeAdding = reviewList.size();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (!documentSnapshot.equals(lastVisible)) { // Exclude the last item
                                ReviewModel reviewModel = documentSnapshot.toObject(ReviewModel.class);
                                reviewList.add(reviewModel);
                                lastVisible = documentSnapshot;
                            }
                        }
                        if (reviewList.size() - sizeBeforeAdding < LOAD_SIZE) {
                            isLastPage = true;
                        }
                        progressBarRow.setVisibility(View.GONE);
                        reviewAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarRow.setVisibility(View.GONE);
                        Toast.makeText(AllReviewsActivity.this, "Failed to load more data!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadReview() {
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() < LOAD_SIZE) {
                            isLastPage = true;
                        }
                        reviewList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ReviewModel reviewModel = documentSnapshot.toObject(ReviewModel.class);
                            reviewList.add(reviewModel);
                        }
                        progressBar.setVisibility(View.GONE);
                        reviewAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(AllReviewsActivity.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
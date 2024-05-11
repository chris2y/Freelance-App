package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.adapters.ReviewAdapter;
import com.example.freelancerapp10.model.HirerModel;
import com.example.freelancerapp10.model.ReviewModel;
import com.example.freelancerapp10.model.ReviewModelLoad;
import com.example.freelancerapp10.model.WorkerModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.example.freelancerapp10.utils.ReportDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileDetailActivity extends AppCompatActivity {
    ImageButton back;
    String userId="";
    ImageView profile;

    private TextView otherUsername;
    private TextView txtFullName;
    private TextView aboutTextView;
    private TextView professionTextView;
    private TextView skillTextView;
    private TextView educationTextView;
    private TextView jobDeliveredTextView;
    private TextView earnedMoneyTextView;
    private TextView jobHiredTextView;
    private TextView spentMoneyTextView;
    private TextView ratingTextView;
    private TextView joinedTextView;
    private RecyclerView reviewRecyclerView;
    LinearLayout worker;
    LinearLayout hirer;
    LinearLayout containerLinear;
    ProgressBar progressBar;
    String imageUrl;
    ImageButton reportButton;
    RatingBar ratingBar;



    private List<ReviewModel> mHomeItems;
    public RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private DatabaseReference databaseReference;
    long lastTimestamp = 0;
    Parcelable recyclerViewState;
    ProgressBar progressBarRow;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    Button loadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        back = findViewById(R.id.back_btn);
        profile = findViewById(R.id.profile_pic_view);
        otherUsername = findViewById(R.id.other_username);
        txtFullName = findViewById(R.id.txtFullName);
        aboutTextView = findViewById(R.id.aboutTextView);
        professionTextView = findViewById(R.id.professionTextView);
        skillTextView = findViewById(R.id.skillTextView);
        educationTextView = findViewById(R.id.educationTextView);
        jobDeliveredTextView = findViewById(R.id.jobDeliveredTextView);
        earnedMoneyTextView = findViewById(R.id.earnedMoneyTextView);
        jobHiredTextView = findViewById(R.id.jobHiredTextView);
        spentMoneyTextView = findViewById(R.id.spentMoneyTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        joinedTextView = findViewById(R.id.joinedTextView);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        worker = findViewById(R.id.workLinearLayout);
        hirer = findViewById(R.id.hireLinearLayout);
        containerLinear = findViewById(R.id.linearContainer);
        progressBar = findViewById(R.id.progressBarUserDetail);
        ratingBar = findViewById(R.id.ratingBarUserDetail);
        loadMore = findViewById(R.id.moreReviewBtn);
        reportButton = findViewById(R.id.reportButton);

        mHomeItems = new ArrayList<>();
        recyclerView = findViewById(R.id.reviewRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
        adapter = new ReviewAdapter(getApplicationContext(), mHomeItems);
        recyclerView.setAdapter(adapter);

        progressBarRow = findViewById(R.id.reviewProgressBar);

        containerLinear.setVisibility(View.GONE);

        loadMore.setOnClickListener(view ->
                loadMoreData());

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        back.setOnClickListener(view -> onBackPressed());
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        Log.d("userId",userId);

        loadReviewFromDatabase(userId);

        FirebaseUtil.loadLookingTo(userId, lookingToCallback -> {
            if(lookingToCallback != null){
                if (lookingToCallback.equals("Hire")) {
                    loadHirer();
                    Log.d("looking","hire");
                } else if (lookingToCallback.equals("Work")) {
                    loadWorker();
                    Log.d("looking","work");
                }

            }
        });

        reportButton.setOnClickListener(view -> {
            ReportDialog reportDialog = new ReportDialog(ProfileDetailActivity.this
                    ,"user",FirebaseUtil.currentUserId(),userId,userId);
            reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
            reportDialog.setCancelable(false);
            reportDialog.show();

        });



        profile.setOnClickListener(view -> {
            Intent intent1 = new Intent(ProfileDetailActivity.this , FullScreenImageNewActivity.class);
            intent1.putExtra("image_url",imageUrl);
            startActivity(intent1);
        });

    }

    private void loadWorker() {
        FirebaseUtil.allUserReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    WorkerModel workerModel = snapshot.getValue(WorkerModel.class);
                    imageUrl = workerModel.getDataProfileImage();

                    displayWorkerData(workerModel);
                    Log.d("looking",workerModel.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadHirer() {

        FirebaseUtil.allUserReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HirerModel hirerModel = snapshot.getValue(HirerModel.class);
                    imageUrl = hirerModel.getDataProfileImage();

                    displayHirerData(hirerModel);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayWorkerData(WorkerModel workerModel) {
        containerLinear.setVisibility(View.VISIBLE);
        hirer.setVisibility(View.GONE);

        Glide.with(getApplicationContext())
                .load(workerModel.getDataProfileImage())
                .into(profile);

        FirebaseUtil.loadRating(workerModel.getUserId(), (avgRating, totalRating) -> {
            ratingTextView.setText(avgRating + "(" + (int) totalRating + ")");
            ratingBar.setRating(avgRating);
        });

        txtFullName.setText(workerModel.getFullName());
        professionTextView.setText(workerModel.getProfession());
        skillTextView.setText(workerModel.getSkill());
        educationTextView.setText(workerModel.getEducation());
        aboutTextView.setText(workerModel.getAboutMe());

        // Check if deliveredJobs is empty or null, if so, set it to "0"
        String deliveredJobs = workerModel.getDeliveredJobs();
        if (deliveredJobs == null || deliveredJobs.isEmpty()) {
            jobDeliveredTextView.setText("0");
        } else {
            jobDeliveredTextView.setText(deliveredJobs);
        }

        // Check if moneyEarned is empty or null, if so, set it to "0"
        String moneyEarned = workerModel.getMoneyEarned();
        if (moneyEarned == null || moneyEarned.isEmpty()) {
            earnedMoneyTextView.setText("0");
        } else {
            earnedMoneyTextView.setText(moneyEarned);
        }

        joinedTextView.setText(FirebaseUtil.timestampLongToStringMessage(workerModel.getCreatedTimestamp()));
        progressBar.setVisibility(View.GONE);
    }
    private void displayHirerData(HirerModel hirerModel) {
        containerLinear.setVisibility(View.VISIBLE);
        worker.setVisibility(View.GONE);

        Glide.with(getApplicationContext())
                .load(hirerModel.getDataProfileImage())
                .into(profile);

        FirebaseUtil.loadRating(hirerModel.getUserId(), (avgRating, totalRating) -> {
            ratingTextView.setText(avgRating + "(" + (int) totalRating + ")");
            ratingBar.setRating(avgRating);
        });

        txtFullName.setText(hirerModel.getFullName());
        jobHiredTextView.setText(String.valueOf(hirerModel.getJobHired()));
        joinedTextView.setText(FirebaseUtil.timestampLongToStringMessage(hirerModel.getCreatedTimestamp()));
        spentMoneyTextView.setText(String.valueOf(hirerModel.getMoneySpent()));
        progressBar.setVisibility(View.GONE);

    }


    private void loadReviewFromDatabase(String userId) {
        databaseReference.child(userId).orderByChild("timestamp")
                .limitToLast(4).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ReviewModelLoad> updatedList = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                ReviewModelLoad reviewModel = itemSnapshot.getValue(ReviewModelLoad.class);
                                reviewModel.setKey(itemSnapshot.getKey());
                                updatedList.add(reviewModel);

                            }

                            if (updatedList.size()<4){
                                Collections.reverse(updatedList);
                                refreshTheAdapterAndView(updatedList);
                                isLoading = false;
                                isLastPage = false;
                                return;
                            }

                            lastTimestamp = (long) updatedList.get(0).getTimestamp();
                            Collections.reverse(updatedList);
                            updatedList.remove(updatedList.size() - 1);
                            refreshTheAdapterAndView(updatedList);
                            isLoading = false;
                            isLastPage = false;
                        } else {


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
            //Toast.makeText(getApplicationContext(), "You reached the last item", Toast.LENGTH_SHORT).show();
            return;
        }
        isLoading = true;
        progressBarRow.setVisibility(View.VISIBLE);
        databaseReference.child(userId)
                .orderByChild("timestamp")
                .endAt(lastTimestamp)
                .limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ReviewModelLoad> moreData = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                ReviewModelLoad reviewModel = itemSnapshot.getValue(ReviewModelLoad.class);
                                reviewModel.setKey(itemSnapshot.getKey());
                                moreData.add(reviewModel);
                            }

                            if (moreData.size() < 10) {
                                Toast.makeText(getApplicationContext(), "You reached the last item", Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                                Collections.reverse(moreData);
                                refreshLoadMore(moreData);
                                progressBarRow.setVisibility(View.GONE);
                                return;
                            }

                            lastTimestamp = (long) moreData.get(0).getTimestamp();
                            Collections.reverse(moreData);
                            refreshLoadMore(moreData); // Add more data to the adapter
                        }
                        isLoading = false;
                        progressBarRow.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading = false;
                        progressBarRow.setVisibility(View.GONE);
                    }
                });
    }


    private void refreshLoadMore(List<ReviewModelLoad> updatedList) {
        // Calculate the difference between the old list and the new list
        //ReviewDiffCallback diffCallback = new ReviewDiffCallback(mHomeItems, updatedList);
        //DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // Clear the old list and add the new data

        //mHomeItems.addAll(updatedList);

        // Notify the adapter of the changes using the calculated diffResult
        //diffResult.dispatchUpdatesTo(adapter);

        // Restore the RecyclerView's state
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        // Hide the progress bar
        progressBar.setVisibility(View.GONE);
    }


    private void refreshTheAdapterAndView(List<ReviewModelLoad> updatedList) {
       /* ReviewDiffCallback diffCallback = new ReviewDiffCallback(mHomeItems, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        mHomeItems.clear();
        mHomeItems.addAll(updatedList);

        progressBar.setVisibility(View.GONE);

        diffResult.dispatchUpdatesTo(adapter);*/
    }


}
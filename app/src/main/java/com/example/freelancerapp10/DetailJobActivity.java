package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.model.JobDetailModel;
import com.example.freelancerapp10.utils.AndroidUtil;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.example.freelancerapp10.utils.ReportDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class DetailJobActivity extends AppCompatActivity {
    ImageButton backButton;
    ImageButton reportButton;
    ImageView userImage;
    TextView detailUsername;
    TextView postedDateTextView;
    TextView seenCounterTextView;
    TextView appliedCounterTextView;
    TextView priceTextView;
    TextView ratingTextView;
    TextView jobHiredTextView;
    TextView spentMoneyTextView;
    TextView timelineDateTextView;
    TextView joinedTextView;
    TextView descriptionTextView;
    TextView titleTextView;

    Button applyNow,edit,remove;

    DatabaseReference databaseReference;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    String key;
    String itemId;
    String userId;
    LinearLayout linearOwnPost;
    String lookingTo = "";
    RatingBar ratingBar;
    private static final int REQUEST_CODE = 99;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_job);
        backButton = findViewById(R.id.backButton);
        userImage = findViewById(R.id.profileImage);
        detailUsername = findViewById(R.id.detailUsername);
        reportButton = findViewById(R.id.reportButton);


        postedDateTextView = findViewById(R.id.postedDateTextView);
        timelineDateTextView = findViewById(R.id.timelineDateTextView);
        seenCounterTextView = findViewById(R.id.seenCounterTextView);
        appliedCounterTextView = findViewById(R.id.appliedCounterTextView);
        priceTextView = findViewById(R.id.priceTextView);

        ratingBar = findViewById(R.id.ratingBarDisplay);
        ratingTextView = findViewById(R.id.ratingTextView);
        jobHiredTextView = findViewById(R.id.jobHiredTextView);
        spentMoneyTextView = findViewById(R.id.spentMoneyTextView);
        joinedTextView = findViewById(R.id.joinedTextView);
        linearOwnPost = findViewById(R.id.ownPostLinear);

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        progressBar = findViewById(R.id.progressbarJobDetail);
        relativeLayout = findViewById(R.id.relativeLayoutJob);

        applyNow = findViewById(R.id.applyButton);
        edit = findViewById(R.id.editButton);
        remove = findViewById(R.id.removeButton);

        reportButton.setOnClickListener(view -> {

            ReportDialog reportDialog = new ReportDialog(DetailJobActivity.this
                    ,"job",FirebaseUtil.currentUserId(),userId,key);
            reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
            reportDialog.setCancelable(false);
            reportDialog.show();

        });



        relativeLayout.setVisibility(View.GONE);

        userImage.setOnClickListener(view -> {
            Intent intent = new Intent(DetailJobActivity.this,ProfileDetailActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
        });

        applyNow.setOnClickListener(view -> {
            if (!FirebaseUtil.isLoggedIn()){
                navigateToLoginOrSignUp();
            }else {
                lookingTo = AndroidUtil.loadLookingTo(getApplicationContext());
                if (checkForLookingTo()) {
                    if(checkUserFilledAllTheData()){
                        Intent intent = new Intent(DetailJobActivity.this, ApplyActivity.class);
                        intent.putExtra("itemID", key);
                        if (applyNow.getText().toString().equals("Edit Proposal")) {
                            intent.putExtra("editProposal", "true");
                        }
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
            }

        });



        backButton.setOnClickListener(view -> onBackPressed());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("Key");

            if (key != null) {
                retrieveAndUpdateJobDetails();
                if (FirebaseUtil.isLoggedIn())
                    checkIfAppliedAlready();
                Log.d("keyss",key);
            }
        }
    }

    private void navigateToLoginOrSignUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobActivity.this);
        builder.setTitle("Login or Signup");
        builder.setMessage("You need to login before you can apply");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(DetailJobActivity.this , LoginSignUpActivity.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
        }).show();

    }

    private boolean checkUserFilledAllTheData() {
        if (AndroidUtil.checkUserDataForNull(getApplicationContext(),lookingTo)){
            return true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobActivity.this);
        builder.setTitle("Apply Specification");
        builder.setMessage("You need to fill all the data before you can apply");

        builder.setPositiveButton("Yes", (dialog, which) -> {
        Intent intent = new Intent(DetailJobActivity.this , EditProfileActivity.class);
        AndroidUtil.UserDataFromSharedPrefAndPassAsIntent(getApplicationContext(),intent);
        startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
        }).show();

        return false;

    }

    private boolean checkForLookingTo() {

        if (lookingTo.equals("Hire")){
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobActivity.this);
            builder.setTitle("Apply Specification");
            builder.setMessage("Your looking to is on \"Hire\",to apply change it to \"Work\" and fill all the data");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                Intent intent = new Intent(DetailJobActivity.this , EditProfileActivity.class);
                AndroidUtil.UserDataFromSharedPrefAndPassAsIntent(getApplicationContext(),intent);
                startActivity(intent);
            });



            builder.setNegativeButton("No", (dialog, which) -> {

            }).show();
            return false;
        }
       return true;
    }


    private void checkIfAppliedAlready() {
        FirebaseUtil.checkIfUserAppliedToJob(key, applied -> {
            if (applied) {
               applyNow.setText("Edit Proposal");

            }
        });

    }



    private void retrieveAndUpdateJobDetails() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Job Data").child(key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    JobDetailModel job = snapshot.getValue(JobDetailModel.class);

                    if (job != null) {
                        itemId = job.getItemId();
                        String title = job.getTitle();
                        String description = job.getDescription();
                        String price = "$" + job.getPrice();
                        String seenCount = String.valueOf(job.getSeenCount() + 1);
                        String postedDate = FirebaseUtil.getRelativeTimeAgo(job.getTimestamp());
                        String timeLine = job.getTimeline();

                        userId = job.getUserId();
                        setUserDetail(userId);


                       if (userId.equals(FirebaseUtil.currentUserId())){
                            applyNow.setVisibility(View.GONE);
                            linearOwnPost.setVisibility(View.VISIBLE);

                            edit.setOnClickListener(view -> {
                                Intent intent = new Intent(DetailJobActivity.this, EditPostActivity.class);
                                intent.putExtra("Key", key);
                                startActivity(intent);

                            });

                            remove.setOnClickListener(view -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailJobActivity.this);
                                builder.setTitle("Remove");
                                builder.setMessage("Are you sure you want to remove the post?");

                                builder.setPositiveButton("Yes", (dialog, which) -> {
                                   FirebaseUtil.deleteJobDataFromAllLocation(key);
                                   finish();
                                });


                                builder.setNegativeButton("No", (dialog, which) -> {

                                }).show();

                            });

                        }

                        titleTextView.setText(title);
                        descriptionTextView.setText(description);
                        priceTextView.setText(price);
                        seenCounterTextView.setText(seenCount);
                        postedDateTextView.setText(postedDate);
                        timelineDateTextView.setText(timeLine);

                        progressBar.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);



                        incrementSeenCount();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void setUserDetail(String uid) {

        if (uid != null ) {
            FirebaseUtil.loadClientDetail(uid, (jobHired, moneySpent, timestamp) -> {
                if (jobHired != 0) {
                    jobHiredTextView.setText(jobHired + " jobs hired");
                }
                if (moneySpent != 0) {
                    spentMoneyTextView.setText("$" + moneySpent + " birr spent");
                }
                if (timestamp != 0) {
                    joinedTextView.setText("Joined at " + FirebaseUtil.timestampLongToStringMessage(timestamp));
                }
            });
            FirebaseUtil.loadProfileImage(uid, profileImageUrl -> Glide.with(getApplicationContext())
                    .load(profileImageUrl)
                    .into(userImage));

            FirebaseUtil.loadRating(uid, (avgRating, totalRating) -> {
                ratingTextView.setText(avgRating + "(" + (int) totalRating + ")");
                ratingBar.setRating(avgRating);
            });

        }
    }

    private void incrementSeenCount() {
        databaseReference.child("seenCount").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentValue = mutableData.getValue(Long.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    // Handle error
                } else {
                    // Handle success
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean proposalEdited = data.getBooleanExtra("proposalEdited", false);

                if (proposalEdited) {
                    // Change the button text to "Edit Proposal"
                    applyNow.setText("Edit Proposal");
                }
            }
        }
    }

}
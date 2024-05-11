package com.example.freelancerapp10.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.ReviewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RateAndReviewDialog extends Dialog {
    String otherUid;
    String myUid;
    String jobId;
    ProgressBar progressBar;

    Button submit;
    TextInputLayout editText;
    RatingBar ratingBar;
    TextView ratingNumber;
    String reviewToast = "Review Successful";
    private DatabaseReference reviewsRef;
    private DatabaseReference ratingsRef;
    private DatabaseReference myReviewRef;
    private ReviewModel myReview;


    public RateAndReviewDialog(@NonNull Context context, String otherUid, String myUid,String jobId) {
        super(context);
        this.otherUid = otherUid;
        this.myUid = myUid;
        this.jobId = jobId;
        reviewsRef = FirebaseDatabase.getInstance().getReference().child("reviews");
        ratingsRef = FirebaseDatabase.getInstance().getReference().child("ratings");
        myReviewRef = FirebaseDatabase.getInstance().getReference().child("my_reviews");
        myReview = null;
        fetchMyReview();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_and_review_dialog);

        submit = findViewById(R.id.submitBtn);
        Button cancel = findViewById(R.id.cancelBtn);
        ratingBar = findViewById(R.id.ratingBar);
        editText = findViewById(R.id.reviewEditTxt);
        progressBar = findViewById(R.id.reviewProgressBar);
        ratingNumber = findViewById(R.id.ratingTextView);



        // Set the dialog's width to MATCH_PARENT and center it horizontally
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        getWindow().setAttributes(layoutParams);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingNumber.setText(String.valueOf(ratingBar.getRating()));
            }
        });

        submit.setOnClickListener(v -> {
            editText.setError(null); // Clear any previous errors
            String reviewText = editText.getEditText().getText().toString().trim();
            int reviewLength = reviewText.length();

            // Check if the review length is less than 20 or greater than 500
             if (reviewLength == 0) {
                editText.setError("Review must not be empty");
            } else if (reviewLength < 20) {
                 editText.setError("Review must be at least 20 characters");
             }else if (reviewLength > 500) {
                editText.setError("Review must be less than 500 characters");
            } else if (ratingBar.getRating() == 0) {
                 // Check if the RatingBar is not rated
                 Toast.makeText(getContext(), "Slide the stars to rate the user", Toast.LENGTH_SHORT).show();
             }else {
                 progressBar.setVisibility(View.VISIBLE);
                 saveReviewAndRating(reviewText, ratingBar.getRating());
             }



        });


        cancel.setOnClickListener(v -> {
            dismiss();
        });


    }
    private void fetchMyReview() {
        myReviewRef.child(myUid).child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User has already reviewed this job
                    myReview = dataSnapshot.getValue(ReviewModel.class);
                    // Handle it accordingly (e.g., show the user's previous review)
                    handleAlreadyReviewedJob();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here if necessary
            }
        });
    }


    private void handleAlreadyReviewedJob() {
        submit.setText("Edit");
        if (myReview != null) {
           ratingBar.setRating(myReview.getRating());
           editText.getEditText().setText(myReview.getText());
           reviewToast = "Edit Successful";
        }
    }

    private void saveReviewAndRating(String reviewText, float rating) {
        // Generate a unique review ID

        // Create a Review object
        ReviewModel review = new ReviewModel(myUid, rating, reviewText);
        ReviewModel reviewMine = new ReviewModel(otherUid, rating, reviewText);
        // Save the review to the "reviews" node under the other user's UID
        reviewsRef.child(otherUid).child(jobId).setValue(review);
        myReviewRef.child(myUid).child(jobId).setValue(reviewMine);

        // Update the average rating in the "ratings" node for the other user
        updateAverageRating();

        Toast.makeText(getContext(), reviewToast , Toast.LENGTH_SHORT).show();

    }


    private void updateAverageRating() {

        DatabaseReference userReviewsRef = reviewsRef.child(otherUid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalRatings = 0;
                float totalRatingValue = 0.0f;

                // Iterate through the user's reviews
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    ReviewModel reviewModel = reviewSnapshot.getValue(ReviewModel.class);
                    totalRatings++;
                    totalRatingValue += reviewModel.getRating();
                }

                // Calculate the average rating
                float averageRating = (totalRatings > 0) ? (totalRatingValue / totalRatings) : 0.0f;

                // Update the average rating in the "ratings" node
                ratingsRef.child(otherUid).child("average_rating").setValue(averageRating);
                ratingsRef.child(otherUid).child("total_ratings").setValue(totalRatings);

                dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        };
        // Attach the ValueEventListener to the user's reviews
        userReviewsRef.addListenerForSingleValueEvent(valueEventListener);
    }

}



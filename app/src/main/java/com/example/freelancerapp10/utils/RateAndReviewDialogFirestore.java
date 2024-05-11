package com.example.freelancerapp10.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RateAndReviewDialogFirestore extends Dialog {
    String otherUid;
    String myUid;
    String jobId;
    ProgressBar progressBar;

    Button submit;
    TextInputLayout editText;
    RatingBar ratingBar;
    TextView ratingNumber;
    String reviewToast = "Review Successful";
    private FirebaseFirestore db;
    private DocumentReference myReviewRef;
    private ReviewModel myReview;

    public RateAndReviewDialogFirestore(@NonNull Context context, String myUid, String otherUid, String jobId) {
        super(context);
        this.myUid = myUid;
        this.jobId = jobId;
        this.otherUid = otherUid;
        db = FirebaseFirestore.getInstance();
        myReviewRef = db.collection("my_reviews").document(myUid);
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

        submit.setOnClickListener(view -> {
            editText.setError(null); // Clear any previous errors
            String reviewText = editText.getEditText().getText().toString().trim();
            int reviewLength = reviewText.length();

            // Check if the review length is less than 20 or greater than 500
            if (reviewLength == 0) {
                editText.setError("Review must not be empty");
            } else if (reviewLength < 20) {
                editText.setError("Review must be at least 20 characters");
            } else if (reviewLength > 500) {
                editText.setError("Review must be less than 500 characters");
            } else if (ratingBar.getRating() == 0) {
                // Check if the RatingBar is not rated
                Toast.makeText(getContext(), "Slide the stars to rate the user", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                saveReviewAndRating(reviewText, ratingBar.getRating());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void saveReviewAndRating(String reviewText, float rating) {
        // Create Review objects
        ReviewModel review = new ReviewModel(myUid, rating, reviewText);
        ReviewModel reviewMine = new ReviewModel(myUid, rating, reviewText);

        CollectionReference workReviewsCollection = db.collection("work_reviews");
        DocumentReference jobReviewDoc = workReviewsCollection.document(jobId);
        jobReviewDoc.collection("reviews").document(myUid).set(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateAverageRating();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        // Save the review to the "my_reviews" collection for the current user
        myReviewRef.collection(jobId).document("review").set(reviewMine)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error if needed
                    }
                });
    }

    private void fetchMyReview() {
        myReviewRef.collection(jobId).document("review").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // User has already reviewed this job
                            myReview = documentSnapshot.toObject(ReviewModel.class);
                            // Handle it accordingly (e.g., show the user's previous review)
                            handleAlreadyReviewedJob();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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

    private void updateAverageRating() {
        FirebaseFirestore.getInstance().collection("work_posts").document(jobId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            double averageRating = documentSnapshot.getDouble("average_rating");
                            double totalRatings = documentSnapshot.getDouble("total_ratings");
                            double newAverageRating;

                            if(submit.getText().toString().equals("Edit")){
                                // Calculate current total score
                                double currentTotalScore = averageRating * totalRatings;

                                // Subtract old rating and add new rating to current total score
                                double newTotalScore = currentTotalScore - myReview.getRating() + ratingBar.getRating();

                                // Calculate new average rating
                                newAverageRating = newTotalScore / totalRatings;

                            }
                            else {
                                double currentTotalScore = averageRating * totalRatings;

                                // Subtract old rating and add new rating to current total score
                                double newTotalScore = currentTotalScore + ratingBar.getRating();

                                // Increment total ratings
                                totalRatings++;

                                // Calculate new average rating
                                newAverageRating = newTotalScore / totalRatings;
                            }

                            // Update Firestore document
                            FirebaseFirestore.getInstance().collection("work_posts").document(jobId)
                                    .update("average_rating", newAverageRating, "total_ratings", totalRatings)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), reviewToast, Toast.LENGTH_SHORT).show();
                                            dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle error if needed
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error if needed
                    }
                });


    }


}
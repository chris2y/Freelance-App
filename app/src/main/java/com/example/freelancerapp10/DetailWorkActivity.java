package com.example.freelancerapp10;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.freelancerapp10.adapters.ReviewAdapter;
import com.example.freelancerapp10.adapters.SliderAdapter;
import com.example.freelancerapp10.model.ReviewModel;
import com.example.freelancerapp10.model.UserModel;
import com.example.freelancerapp10.model.WorkListDetailModel;
import com.example.freelancerapp10.model.WorkListModel;
import com.example.freelancerapp10.utils.AndroidUtil;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailWorkActivity extends AppCompatActivity {
    
    ImageButton back;
    private SliderView sliderView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Query query;

    ArrayList<String> imageUrls = new ArrayList<>();

    TextView title, description, seen, postedDate, timeline, price, fullName, rating, seeAll;
    Button requestOrder,chatButton;
    RatingBar ratingBar;
    RecyclerView reviewRecyclerView;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;

    ImageView profileImage;
    String docId,otherUserId,title1;
    private List<ReviewModel> reviewList;
    ReviewAdapter reviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_work);
        back = findViewById(R.id.backButton);

        back.setOnClickListener(view -> onBackPressed());

        sliderView = findViewById(R.id.sliderView);

        chatButton = findViewById(R.id.chatButton);

        title = findViewById(R.id.titleTextView);
        description = findViewById(R.id.descriptionTextView);
        seen = findViewById(R.id.seenCounterTextView);
        postedDate = findViewById(R.id.postedDateTextView);
        timeline = findViewById(R.id.timelineDateTextView);
        price = findViewById(R.id.priceTextView);
        requestOrder = findViewById(R.id.orderButton);
        ratingBar = findViewById(R.id.ratingBarDisplay);
        rating = findViewById(R.id.ratingTextView);
        fullName = findViewById(R.id.detailUsername);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        seeAll = findViewById(R.id.viewAllReviewTextView);
        progressBar = findViewById(R.id.progressBar);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        profileImage = findViewById(R.id.profileImage);

        reviewList = new ArrayList<>();

        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(DetailWorkActivity.this));
        reviewAdapter = new ReviewAdapter(DetailWorkActivity.this, reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);


        getIntentExtraAndDisplay();

        query = db.collection("work_reviews")
                .document(docId)
                .collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3);

        loadReview();

        checkIfRequestAlreadySent();

        seeAll.setOnClickListener(view ->
                startActivity(new Intent(DetailWorkActivity.this, AllReviewsActivity.class)
                        .putExtra("docId", docId)));

        requestOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (requestOrder.getText().toString().equals("Remove request")) {
                    showRemoveRequestDialog();
                    return;
                }

                showRequestOrderDialogue();

            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailWorkActivity.this, ChatActivity.class);
                UserModel userModel = new UserModel();
                userModel.setUserId(otherUserId);
                userModel.setJobId(docId);
                userModel.setTitle(title1);
                AndroidUtil.passUserModelAsIntents(intent, userModel);
                startActivity(intent);
            }
        });





    }

    private void showRequestOrderDialogue() {
        View view1 = LayoutInflater.from(DetailWorkActivity.this).inflate(R.layout.request_order_dialog, null);

        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(DetailWorkActivity.this);
        builder.setView(view1);
        AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button submitButton = view1.findViewById(R.id.submitBtn);
        Button cancelButton = view1.findViewById(R.id.cancelBtn);
        ProgressBar progressBar1 = view1.findViewById(R.id.requestOrderProgressBar);
        TextInputLayout editText = view1.findViewById(R.id.requestOrderEditTxt);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setError(null); // Clear any previous errors
                String message = editText.getEditText().getText().toString().trim();
                int reportLength = message.length();

                // Check if the review length is less than 20 or greater than 500
                if (reportLength == 0) {
                    editText.setError("Message must not be empty");
                } else if (reportLength < 10) {
                    editText.setError("Message must be at least 10 characters");
                }else if (reportLength > 500) {
                    editText.setError("Message must be less than 500 characters");
                }else {
                    progressBar1.setVisibility(View.VISIBLE);
                    saveRequest(message,alertDialog,progressBar1);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Cancel button click
                alertDialog.dismiss(); // Dismiss dialog on cancel
            }
        });

        alertDialog.show();
    }


    private void saveRequest(String message ,AlertDialog alertDialog, ProgressBar progressBar1) {

        DocumentReference docRef = db.collection("order_requests").document(otherUserId)
                .collection("works").document(docId);

        DocumentReference myDocRef = db.collection("my_orders").document(FirebaseUtil.currentUserId())
                .collection("works").document(docId);

        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("myUserId", otherUserId);
        data.put("workId", docId);
        data.put("otherUserId", docId);
        data.put("timestamp", Timestamp.now());

        myDocRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar1.setVisibility(View.GONE);
                        alertDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alertDialog.dismiss();
                    }
                });

        docRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar1.setVisibility(View.GONE);
                        alertDialog.dismiss();
                        requestOrder.setText("Remove request");
                        Toast.makeText(DetailWorkActivity.this, "Order request sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alertDialog.dismiss();
                        Toast.makeText(DetailWorkActivity.this, "Order request failed", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void checkIfRequestAlreadySent() {
        DocumentReference myDocRef = db.collection("my_orders")
                .document(FirebaseUtil.currentUserId())
                .collection("works")
                .document(docId);
        myDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                           requestOrder.setText("Remove request");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void showRemoveRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Request already sent. Do you want to remove the request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button, remove the request
                        removeRequest(dialog);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button, dismiss the dialog
                        dialog.dismiss();
                    }
                });
// Create the AlertDialog object and return it
        builder.create().show();

    }
    private void removeRequest(DialogInterface dialog) {
        DocumentReference docRef = db.collection("order_requests")
                .document(otherUserId)
                .collection("works")
                .document(docId);

        DocumentReference myDocRef = db.collection("my_orders")
                .document(FirebaseUtil.currentUserId())
                .collection("works")
                .document(docId);

        myDocRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Request removed successfully, proceed to dismiss dialog and progress bar
                        dialog.dismiss();
                        requestOrder.setText("Request order");
                        showToast("Request removed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove request
                        dialog.dismiss();
                        showToast("Failed to remove request");
                    }
                });

        docRef.delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(DetailWorkActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadReview() {
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ReviewModel reviewModel = documentSnapshot.toObject(ReviewModel.class);
                            reviewList.add(reviewModel);
                        }
                        reviewAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(DetailWorkActivity.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getIntentExtraAndDisplay() {

        WorkListModel currentItem = getIntent().getParcelableExtra("item");
        if(currentItem != null) {
            // Use currentItem here
        docId =  currentItem.getDocId();
        title1 =  currentItem.getTitle();
        String price1 =  currentItem.getPrice();
        Timestamp postedDate1 = currentItem.getTimestamp();
        float averageRating = currentItem.getAverage_rating() != 0 ? (float) currentItem.getAverage_rating() : 0.0f;
        int totalRating = (int) currentItem.getTotal_ratings();

        if (totalRating>=3){
            seeAll.setVisibility(View.VISIBLE);
        }else{
            seeAll.setVisibility(View.GONE);
        }

        title.setText(title1);
        price.setText("$" + price1);

        postedDate.setText(FirebaseUtil.timestampToStringMessage(postedDate1));

        ratingBar.setRating(averageRating);
        rating.setText(String.format("%.1f", averageRating) + " (" + totalRating + ")");

        incrementSeenCount(docId);
        loadImageUrlAndLeftData(docId);

        } else {
            // Handle case when currentItem is null
        }
    }

    private void loadImageUrlAndLeftData(String docId) {

        db.collection("work_posts").document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        WorkListDetailModel workItem = documentSnapshot.toObject(WorkListDetailModel.class);
                        if (workItem != null) {
                            timeline.setText(workItem.getTimeline() + " days");
                            description.setText(workItem.getDescription());
                            seen.setText(String.valueOf((int) (workItem.getSeenCount() + 1)));
                            otherUserId = workItem.getUserId();
                            FirebaseUtil.loadNameAndProfileUrl(workItem.getUserId(), (fullName1, dataProfileImage) -> {
                                fullName.setText(fullName1);
                                Glide.with(this).load(dataProfileImage).into(profileImage);
                            });
                            progressBar.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("TAG", "No such document");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "get failed with ", e);
                    }
                });




        db.collection("work_images_url").document(docId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // DocumentSnapshot data
                                imageUrls = (ArrayList<String>) document.get("urls");
                                SliderAdapter sliderAdapter = new SliderAdapter( imageUrls);
                                sliderView.setSliderAdapter(sliderAdapter);
                                sliderView.setAutoCycle(false);

                            } else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void incrementSeenCount(String docId) {
        DocumentReference docRef = db.collection("work_posts").document(docId);

        docRef.update("seenCount", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error
                    }
                });
    }


}
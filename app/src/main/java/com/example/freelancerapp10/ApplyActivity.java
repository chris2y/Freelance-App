package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freelancerapp10.utils.AndroidUtil;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ApplyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText bidAmountEditTxt;
    private EditText deliveryDateEditTxt;
    private EditText describeProposalEditTxt;
    private Button doneButton;
    String proposalDescription;
    String deliveryDate;
    String bidAmount;
    private String itemId;
    private String editProposal="";
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        backButton = findViewById(R.id.backButton);
        bidAmountEditTxt = findViewById(R.id.bidAmountEditTxt);
        deliveryDateEditTxt = findViewById(R.id.deliveryDateEditTxt);
        describeProposalEditTxt = findViewById(R.id.describeProposalEditTxt);
        doneButton = findViewById(R.id.doneButton);
        userId = FirebaseUtil.currentUserId();


       getIntentAndSetEditText();

        backButton.setOnClickListener(view -> {
            onBackPressed();
        });

        doneButton.setOnClickListener(view -> {

            bidAmount = bidAmountEditTxt.getText().toString().trim();
            deliveryDate = deliveryDateEditTxt.getText().toString().trim();
            proposalDescription = describeProposalEditTxt.getText().toString().trim();

            if (TextUtils.isEmpty(bidAmount)) {
                bidAmountEditTxt.setError("Bid amount cannot be empty.");
                return;
            }

            if (TextUtils.isEmpty(deliveryDate)) {
                deliveryDateEditTxt.setError("Delivery date cannot be empty.");
                return;
            }

            if (TextUtils.isEmpty(proposalDescription)) {
                describeProposalEditTxt.setError("Proposal description cannot be empty.");
                return;
            }



            Map<String, Object> submitProposal = new HashMap<>();
            submitProposal.put("bidAmount", bidAmount);
            submitProposal.put("deliveryDate", deliveryDate);
            submitProposal.put("proposalDescription", proposalDescription);
            submitProposal.put("userId", userId);
            submitProposal.put("timestamp", ServerValue.TIMESTAMP);


            FirebaseDatabase.getInstance().getReference("job appliers").child(itemId).child(userId).setValue(submitProposal)
                    .addOnSuccessListener(unused -> {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("itemId", itemId);
                        dataMap.put("timestamp", ServerValue.TIMESTAMP);
                        FirebaseDatabase.getInstance().getReference("jobs i applied").child(userId).child(itemId).setValue(dataMap);
                        showToast("Applied Successfully");
                        Intent resultIntent = new Intent();
                        boolean proposalEdited = true; // Set this to true if the proposal was edited, false if it was submitted
                        resultIntent.putExtra("proposalEdited", proposalEdited);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> showToast("Error Applying"));
        });

    }

    private void getIntentAndSetEditText() {

        Intent intent = getIntent();
        if (intent.hasExtra("editProposal")) {
            editProposal = intent.getStringExtra("editProposal");
        }
        itemId = intent.getStringExtra("itemID");

        if (editProposal.equals("true")){
            loadProposal();

        }
    }

    private void loadProposal() {
        FirebaseDatabase.getInstance().getReference("job appliers").child(itemId).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String bidAmount = snapshot.child("bidAmount").getValue(String.class);
                            String deliveryDate = snapshot.child("deliveryDate").getValue(String.class);
                            String proposalDescription = snapshot.child("proposalDescription").getValue(String.class);

                            if (bidAmount != null && deliveryDate != null && proposalDescription != null) {
                                bidAmountEditTxt.setText(bidAmount);
                                deliveryDateEditTxt.setText(deliveryDate);
                                describeProposalEditTxt.setText(proposalDescription);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
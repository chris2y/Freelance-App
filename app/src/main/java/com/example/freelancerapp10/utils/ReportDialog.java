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
import com.example.freelancerapp10.model.ReportModel;
import com.example.freelancerapp10.model.ReviewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportDialog extends Dialog {

    String myUid;
    String otherUid;
    String jobId;
    String reportType;
    ProgressBar progressBar;

    Button submit;
    TextInputLayout editText;
    TextView reportTypeTextView;
    String reportToast = "Report Successful";
    private DatabaseReference reportsRef;
    private ReportModel myReport;



    public ReportDialog(@NonNull Context context, String reportType, String myUid, String otherUid,String jobId) {
        super(context);
        this.myUid = myUid;
        this.reportType = reportType;
        this.otherUid = otherUid;
        this.jobId = jobId;
        reportsRef = FirebaseDatabase.getInstance().getReference().child("reports");
        myReport = null;
        fetchMyReview();



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_dialog);

        submit = findViewById(R.id.submitBtn);
        Button cancel = findViewById(R.id.cancelBtn);
        editText = findViewById(R.id.reportEditTxt);
        progressBar = findViewById(R.id.reportProgressBar);
        reportTypeTextView = findViewById(R.id.reportTextView);

        // Set the dialog's width to MATCH_PARENT and center it horizontally
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        getWindow().setAttributes(layoutParams);


        submit.setOnClickListener(v -> {
            editText.setError(null); // Clear any previous errors
            String reportText = editText.getEditText().getText().toString().trim();
            int reportLength = reportText.length();

            // Check if the review length is less than 20 or greater than 500
             if (reportLength == 0) {
                editText.setError("Report must not be empty");
            } else if (reportLength < 20) {
                 editText.setError("Report must be at least 20 characters");
             }else if (reportLength > 500) {
                 editText.setError("Report must be less than 500 characters");
             }else {
                 progressBar.setVisibility(View.VISIBLE);
                 saveReport(reportText);
             }



        });


        cancel.setOnClickListener(v -> {
            dismiss();
        });


    }
    private void fetchMyReview() {
        reportsRef.child(reportType).child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User has already reviewed this job
                    myReport = dataSnapshot.getValue(ReportModel.class);
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
        if (myReport != null) {
           editText.getEditText().setText(myReport.getText());
           reportToast = "Edit Successful";
        }
    }

    private void saveReport(String reportText) {
        // Generate a unique review ID

        // Create a Review object
        ReportModel review = new ReportModel(myUid, otherUid, jobId,reportText);
        reportsRef.child(reportType).child(jobId).setValue(review);

        // Update the average rating in the "ratings" node for the other user
        progressBar.setVisibility(View.VISIBLE);
        dismiss();
        Toast.makeText(getContext(), reportToast , Toast.LENGTH_SHORT).show();
    }


}



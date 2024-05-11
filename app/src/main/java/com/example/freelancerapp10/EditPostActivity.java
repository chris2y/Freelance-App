package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.freelancerapp10.model.PostModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    String priceType = "";

    EditText title,skill,description,price,timeline;
    Button done;
    ImageButton back;
    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(R.id.fixedRadio);
        title = findViewById(R.id.titleEditTxt);
        skill = findViewById(R.id.skillEditTxt);
        description = findViewById(R.id.descriptionEditTxt);
        price = findViewById(R.id.priceEditTxt);
        timeline = findViewById(R.id.timelineEditTxt);
        done = findViewById(R.id.postButton);
        back = findViewById(R.id.backButton);

        back.setOnClickListener(view ->
                onBackPressed());


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fixedRadio) {
                priceType = "Price-Fixed";
            } else if (checkedId == R.id.negotiableRadio) {
                priceType = "Price-Negotiable";
            }
        });

        Bundle intent = getIntent().getExtras();
        key = intent.getString("Key");
        Log.d("key",key);

        loadPost();

        done.setOnClickListener(view -> {
            validateAndSaveData();
        });
    }

    private boolean validateAndSaveData() {
        String Title = title.getText().toString().trim();
        String Skill = skill.getText().toString().trim();
        String Description = description.getText().toString().trim();
        String Price = price.getText().toString().trim();
        String Timeline = timeline.getText().toString().trim() + " Days";
        String userId = FirebaseUtil.currentUserId();
        String itemId = FirebaseUtil.allJobCollectionReference().push().getKey();
        boolean isValid = true;

        if (Title.isEmpty()) {
            title.setError("Title is required");
            isValid = false;
        }

        if (Skill.isEmpty()) {
            skill.setError("Skill is required");
            isValid = false;
        }

        if (Description.isEmpty()) {
            description.setError("Description is required");
            isValid = false;
        }

        if (Price.isEmpty()) {
            price.setError("Price is required");
            isValid = false;
        }

        if (Timeline.isEmpty()) {
            timeline.setError("Timeline is required");
            isValid = false;
        }

        if (isValid) {
            Map<String, Object> postData = new HashMap<>();
            postData.put("title", Title);
            postData.put("skill", Skill);
            postData.put("description", Description);
            postData.put("price", Price);
            postData.put("priceType", priceType);
            postData.put("timeline", Timeline);
            postData.put("userId", userId);
            postData.put("itemId", itemId);

            FirebaseUtil.allJobCollectionReference().child(key).updateChildren(postData).addOnSuccessListener(unused -> {
                finish();
                Toast.makeText(getApplicationContext(), "Your post id uploaded", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Error uploading the post", Toast.LENGTH_SHORT).show();
            });
        }

        return isValid;
    }

    private void loadPost() {
        FirebaseDatabase.getInstance().getReference("Job Data").child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String titles = snapshot.child("title").getValue(String.class);
                            String skills = snapshot.child("skill").getValue(String.class);
                            String descriptions = snapshot.child("description").getValue(String.class);
                            String priceTypes = snapshot.child("priceType").getValue(String.class);
                            String prices = snapshot.child("price").getValue(String.class);
                            String timelines = snapshot.child("timeline").getValue(String.class);

                            if (titles != null && skills != null && descriptions != null
                                     && prices != null && timelines != null) {
                                title.setText(titles);
                                skill.setText(skills);
                                description.setText(descriptions);
                                price.setText(prices);
                                timeline.setText(timelines);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }
}
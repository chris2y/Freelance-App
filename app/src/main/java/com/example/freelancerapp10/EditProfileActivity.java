package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.MainFragments.AccountFragment;
import com.example.freelancerapp10.model.UserProfileModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private LinearLayout linearLayoutEditProfileWorker;
    private Button doneButton;
    private ImageButton backImage;

    private String email = "";
    private String phone = "";
    private String profession = "";
    private String education = "";
    private String skill = "";
    private String about = "";
    private String lookingTo = "";
    private String dataProfileImage = "";
    private String fullName = "";


    private EditText fullNameEditText, emailEditText, phoneEditText;
    private EditText professionEditText, educationEditText, skillEditText, aboutMeEditText;

    private StorageReference imageFolder;
    private FirebaseStorage storage;
    private String imageUrlForStoring;
    ValueEventListener eventListener;
    private static final int REQUEST_SELECT_IMAGE = 100;
    private Uri croppedImageUri;
    Uri selectedImageUri;
    ImageView selectImage,profileImageDisplay;
    String profileImagesUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        linearLayoutEditProfileWorker = findViewById(R.id.linearLayoutEditProfileWorker);
        doneButton = findViewById(R.id.doneButton);
        backImage = findViewById(R.id.backButton);
        radioGroup = findViewById(R.id.radioGroup);

        fullNameEditText = findViewById(R.id.fullNameEditTxt);
        emailEditText = findViewById(R.id.emailEditTxt);
        phoneEditText = findViewById(R.id.phoneEditTxt);
        professionEditText = findViewById(R.id.professionEditTxt);
        educationEditText = findViewById(R.id.educationEditTxt);
        skillEditText = findViewById(R.id.skillEditTxt);
        aboutMeEditText = findViewById(R.id.aboutMeEditTxt);

        selectImage = findViewById(R.id.editImage);
        profileImageDisplay = findViewById(R.id.accountimageEdit);





        getBundleExtra();
        declareAndSetRadioGroup();
        loadProfile();

        selectImage.setOnClickListener(view -> selectImageIntent());

        profileImageDisplay.setOnClickListener(view -> {
            if (profileImagesUrl == null) {
                return;
            }
            Intent intent = new Intent(getApplicationContext(), FullScreenImageNewActivity.class);
            intent.putExtra("image_url", profileImagesUrl);
            startActivity(intent);
        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }



    private void loadProfile() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    profileImagesUrl = dataSnapshot.child("dataProfileImage").getValue(String.class);
                    if (profileImagesUrl != null) {
                        loadProfileImage(profileImagesUrl);
                    }
                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        };
        FirebaseUtil.getUsersReference().addValueEventListener(eventListener);
    }


    private void loadProfileImage(String profileImageUrl) {
        Glide.with(getApplicationContext())
                .load(profileImageUrl)
                .thumbnail(0.2f)
                .override(200, 200)
                .into(profileImageDisplay);
    }
    private void selectImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String randomFileName = "cropped_" + timeStamp + "_" + new Random().nextInt(1000);
            // Start the crop activity using UCrop library
            UCrop.of(selectedImageUri, Uri.fromFile(new File(getCacheDir(), randomFileName)))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(720,720)
                    .start(this);

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            croppedImageUri = UCrop.getOutput(data);
            uploadProfileImage();
            Log.d("Croped then passed to Upload","Success");
            // Load the cropped image into an ImageView

        }
    }

    private void uploadProfileImage() {
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        imageFolder = FirebaseStorage.getInstance().getReference().child("New User Profiles");
        StorageReference imageName = imageFolder.child(currentDate + " Image: " + selectedImageUri.getLastPathSegment());

        imageName.putFile(croppedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrlForStoring = uri.toString();
                        if (profileImagesUrl != null) {
                            Log.d("deleteProfileImageFromStorage","Success");
                            deleteProfileImageFromStorage();
                        }
                        else {
                            storeLinks(imageUrlForStoring);
                            Log.d("storeLinks","Success");
                        }

                    }
                });
            }
        });
    }

    private void storeLinks(String imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("dataProfileImage", imageUrl);
        dataProfileImage = imageUrl;

        FirebaseUtil.getUsersReference().updateChildren(data).addOnSuccessListener(unused -> {
            selectedImageUri = null;
            // If data uploaded successfully, show a toast
            Toast.makeText(getApplicationContext(), "Your data has been uploaded successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteProfileImageFromStorage() {
        // Get a reference to the Firebase Storage instance
        storage = FirebaseStorage.getInstance();
        // Create a reference to the image file using its URL
        StorageReference imageRef = storage.getReferenceFromUrl(profileImagesUrl);
        // Delete the image file
        imageRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Image deleted successfully
                            storeLinks(imageUrlForStoring);
                            //deleteImageFromRealtimeDatabase();
                        } else {
                            // Handle the failure case when deleting the image
                            Exception e = task.getException();
                            if (e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                // Handle the case when the object does not exist at the specified location
                                Log.e("ImageDeletionExample", "Image does not exist at the specified location");
                            } else {
                                // Handle other failure cases
                                Log.e("ImageDeletionExample", "Failed to delete image: " + e.getMessage(), e);
                            }
                        }
                    }
                });
    }

    private void declareAndSetRadioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.workRadio) {
                setVisibilityAndSetTexts(View.VISIBLE, "Work");
                workDoneButton();
            } else if (checkedId == R.id.hireRadio) {
                setVisibilityAndSetTexts(View.GONE, "Hire");
                hireDoneButton();
            }
        });

        // Set initial state based on "lookingTo"
        if (lookingTo.equals("Work")) {
            radioGroup.check(R.id.workRadio);
        } else {
            radioGroup.check(R.id.hireRadio);
        }
    }

    private void setVisibilityAndSetTexts(int visibility, String type) {
        linearLayoutEditProfileWorker.setVisibility(visibility);
        lookingTo = type;
        fullNameEditText.setText(fullName);
        emailEditText.setText(email);
        phoneEditText.setText(phone);

        if (type.equals("Work")) {
            professionEditText.setText(profession);
            educationEditText.setText(education);
            skillEditText.setText(skill);
            aboutMeEditText.setText(about);
        }
    }

    private void workDoneButton() {
        doneButton.setOnClickListener(view -> {
            updateData();
            finish();
        });
    }

    private void hireDoneButton() {
        doneButton.setOnClickListener(view -> {
            updateData();
            finish();
        });
    }

    private void updateData() {
        fullName = fullNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        phone = phoneEditText.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("dataEmail", email);
        updates.put("phoneNumber", phone);
        updates.put("lookingTo", lookingTo);



        if (lookingTo.equals("Work")) {
            profession = professionEditText.getText().toString();
            education = educationEditText.getText().toString();
            skill = skillEditText.getText().toString();
            about = aboutMeEditText.getText().toString();

            updates.put("profession", profession);
            updates.put("education", education);
            updates.put("skill", skill);
            updates.put("aboutMe", about);
        }

        saveDataToSharedPreferences();

        FirebaseUtil.getUsersReference().updateChildren(updates)
                .addOnSuccessListener(unused -> showToast("Data updated"))
                .addOnFailureListener(e -> showToast("Error updating the data"));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveDataToSharedPreferences() {
        // Get the shared preferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("myUserData", Context.MODE_PRIVATE);

        // Create an editor to edit the shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put common data in all cases
        editor.putString("fullName", fullName);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("lookingTo", lookingTo);
        editor.putString("dataProfileImage", dataProfileImage);

        // Conditionally put data based on the value of "lookingTo"
        if ("Work".equals(lookingTo)) {
            editor.putString("profession", profession);
            editor.putString("education", education);
            editor.putString("skill", skill);
            editor.putString("about", about);
        }
        // Commit the changes to the shared preferences
        editor.apply();
    }


    private void getBundleExtra() {
        Intent intent = getIntent();
        if (intent.hasExtra("Email")) {
            email = intent.getStringExtra("Email");
        }else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                email = extras.getString("email", "");
                phone = extras.getString("phone", "");
                profession = extras.getString("profession", "");
                education = extras.getString("education", "");
                skill = extras.getString("skill", "");
                about = extras.getString("about", "");
                lookingTo = extras.getString("lookingTo", "");
                dataProfileImage = extras.getString("dataProfileImage", "");
                fullName = extras.getString("fullName", "");
            }

        }

    }


}
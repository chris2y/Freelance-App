package com.example.freelancerapp10.MainFragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.freelancerapp10.FullScreenImageNewActivity;
import com.example.freelancerapp10.LoginSignUpActivity;
import com.example.freelancerapp10.MainFragments.accountFragments.ViewPagerAdapterFragmentForAccount;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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
import java.util.Objects;
import java.util.Random;

public class AccountFragment extends Fragment {
    private ViewPagerAdapterFragmentForAccount viewPagerAdapterFragmentForAccount;
    private SharedPreferences sharedPreferences1;
    private SharedPreferences sharedPreferences2;
    private StorageReference imageFolder;
    private FirebaseStorage storage;
    private String imageUrlForStoring;
    ValueEventListener eventListener;
    private static final int REQUEST_SELECT_IMAGE = 100;
    private Uri croppedImageUri;
    Uri selectedImageUri;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    ImageView selectImage,profileImageDisplay;
    private TextView logout;
    private TextView fullName;
    String profileImagesUrl;
    String fullname;
    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        sharedPreferences1 = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferences2 = requireActivity().getSharedPreferences("myUserData", MODE_PRIVATE);

        viewPager2 = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        logout = rootView.findViewById(R.id.btnLogout);
        selectImage = rootView.findViewById(R.id.editImage);
        profileImageDisplay = rootView.findViewById(R.id.accountimage);
        fullName = rootView.findViewById(R.id.txtFullName);


        setupViewPagerAndTabs();


        selectImage.setOnClickListener(view -> selectImageIntent());

        profileImageDisplay.setOnClickListener(view -> {

            if (profileImagesUrl == null) {
                return;
            }
            Intent intent = new Intent(getContext(), FullScreenImageNewActivity.class);
            intent.putExtra("image_url", profileImagesUrl);
            startActivity(intent);
        });

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    profileImagesUrl = dataSnapshot.child("dataProfileImage").getValue(String.class);
                    fullname = dataSnapshot.child("fullName").getValue(String.class);
                    fullName.setText(fullname);

                    if (profileImagesUrl != null) {
                        loadProfileImage(profileImagesUrl);
                    }
                } else {
                    // The specified user ID does not exist in the database
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        };

        FirebaseUtil.getUsersReference().addValueEventListener(eventListener);


     logout.setOnClickListener(view -> {

         SharedPreferences.Editor editor1 = sharedPreferences1.edit();
         SharedPreferences.Editor editor2 = sharedPreferences2.edit();

         editor2.clear();
         editor2.apply();

         editor1.clear();
         editor1.apply();
         editor1.putBoolean("isLoggedIn", false);


         startActivity(new Intent(getContext(), LoginSignUpActivity.class));
         FirebaseUtil.logout();
         if (getActivity() != null) {
             getActivity().finish();
         }
     });

        return rootView;
}

    private void setupViewPagerAndTabs() {

        viewPagerAdapterFragmentForAccount = new ViewPagerAdapterFragmentForAccount((FragmentActivity) getContext());

        viewPager2.setAdapter(viewPagerAdapterFragmentForAccount);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }


    private void loadProfileImage(String profileImageUrl) {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            Glide.with(getActivity().getApplicationContext())
                    .load(profileImageUrl)
                    .into(profileImageDisplay);
        }
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
            UCrop.of(selectedImageUri, Uri.fromFile(new File(getContext().getCacheDir(), randomFileName)))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(720,720)
                    .start(getActivity(),AccountFragment.this);

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

        FirebaseUtil.getUsersReference().updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                selectedImageUri = null;
                // If data uploaded successfully, show a toast
                Toast.makeText(getContext(), "Your data has been uploaded successfully", Toast.LENGTH_SHORT).show();
            }
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


}

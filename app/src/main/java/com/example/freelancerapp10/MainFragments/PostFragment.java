package com.example.freelancerapp10.MainFragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freelancerapp10.R;
import com.example.freelancerapp10.adapters.RecyclerAdapterForWorkUpload;
import com.example.freelancerapp10.model.PostModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class PostFragment extends Fragment implements RecyclerAdapterForWorkUpload.countOfImages {

    View rootView;
    RadioGroup radioGroup;
    String priceType;

    EditText title,skill,description,price,timeline;
    Button post;

    Button pick;
    TextView selcetednum;
    RecyclerView recyclerView;
    RecyclerAdapterForWorkUpload adapter;
    private static final int READ_PERMISSION = 101;


    ArrayList<Uri> ChooseImageList = new ArrayList<>();
    ArrayList<String> UrlsList = new ArrayList<>();
    ProgressDialog progressDialog;

    String[] itemsList = { "Graphics Design"  , "Marketing" ,
            "Photography", "Programing & Tech" , "Video Editing" , "Writing & Translation" };
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    TextInputLayout priceJ,titleJ,descriptionJ,timelineJ;
    String workType = "";
    String logoUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_post, container, false);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        radioGroup.check(R.id.fixedRadio);
        title = rootView.findViewById(R.id.titleEditTxt);
        skill = rootView.findViewById(R.id.skillEditTxt);
        description = rootView.findViewById(R.id.descriptionEditTxt);
        price = rootView.findViewById(R.id.priceEditTxt);
        timeline = rootView.findViewById(R.id.timelineEditTxt);
        post = rootView.findViewById(R.id.postButton);

        priceJ = rootView.findViewById(R.id.priceTextInputLayoutJ);
        titleJ = rootView.findViewById(R.id.titleTextInputLayoutJ);
        descriptionJ = rootView.findViewById(R.id.descriptionTextInputLayoutJ);
        timelineJ = rootView.findViewById(R.id.timelineTextInputLayoutJ);

        pick = rootView.findViewById(R.id.pickimage);
        selcetednum = rootView.findViewById(R.id.selectedPhotos);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapterForWorkUpload(ChooseImageList, getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        autoCompleteTextView = rootView.findViewById(R.id.autoCompleteText);
        adapterItems = new ArrayAdapter<>(rootView.getContext(), R.layout.list_work, itemsList);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            workType = adapterView.getItemAtPosition(i).toString();
            //Toast.makeText(getContext(), item, Toast.LENGTH_SHORT).show();
        });


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fixedRadio) {
                priceType = "Price-Fixed";
            } else if (checkedId == R.id.negotiableRadio) {
                priceType = "Price-Negotiable";
            }
        });

       /* post.setOnClickListener(view -> {
            validateAndSaveData();
        });*/

        post.setOnClickListener(view -> {
            validateAndSaveDataJ();
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });



        return rootView;
    }

    private void validateAndSaveDataJ() {
        String price = priceJ.getEditText().getText().toString().trim();
        String title = titleJ.getEditText().getText().toString().trim();
        String description = descriptionJ.getEditText().getText().toString().trim();
        String timeline = timelineJ.getEditText().getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(price)) {
            priceJ.setError("Price is required");
            isValid = false;
        } else {
            priceJ.setError(null);
        }

        if (TextUtils.isEmpty(title)) {
            titleJ.setError("Title is required");
            isValid = false;
        } else {
            titleJ.setError(null);
        }

        if (TextUtils.isEmpty(workType)) {
            autoCompleteTextView.setError("Work type is required");
            isValid = false;
        } else {
            autoCompleteTextView.setError(null);
        }

        if (TextUtils.isEmpty(description)) {
            descriptionJ.setError("Description is required");
            isValid = false;
        } else {
            descriptionJ.setError(null);
        }

        if (TextUtils.isEmpty(timeline)) {
            timelineJ.setError("Timeline is required");
            isValid = false;
        } else {
            timelineJ.setError(null);
        }

        if(ChooseImageList.size() == 0){
            isValid = false;
            Toast.makeText(getContext(),"Please select at least 1 image",Toast.LENGTH_SHORT).show();
        }
        if (isValid) {
            uploadDataJ(price , title , timeline , description);
        }
    }



    private void uploadDataJ(String price, String title, String timeline, String description) {
        showProgressDialog("uploading images please wait...");
        if (ChooseImageList.size() > 0) {
            Uri firstImage = ChooseImageList.get(0);
            if (firstImage != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), firstImage);

                    // Crop the bitmap
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int cropSize = Math.min(width, height);
                    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, (width - cropSize) / 2, (height - cropSize) / 2, cropSize, cropSize);

                    // Resize the cropped bitmap to 300x300
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, 220, 220, true);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Set compression quality to 100
                    byte[] data = baos.toByteArray();

                    String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("Job Listing Images");
                    final StorageReference compressedImageName = ImageFolder.child(currentDate + "_compressed_icon.jpg");
                    UploadTask uploadTask = compressedImageName.putBytes(data);
                    uploadTask.addOnSuccessListener(taskSnapshot -> compressedImageName.getDownloadUrl().addOnSuccessListener(uri -> {
                        logoUrl = String.valueOf(uri);
                        uploadRemainingImages(price, title, timeline, description);
                    })).addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Please fill All Field", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }



    private void uploadRemainingImages(String price, String title, String timeline, String description) {
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri individualImage = ChooseImageList.get(i);
            if (individualImage != null) {
                String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                StorageReference imageFolder = FirebaseStorage.getInstance().getReference().child("Job Listing Images");
                final StorageReference imageName = imageFolder.child(currentDate + " Image " + i + ": " + individualImage.getLastPathSegment());

                // Compress the image
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), individualImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos); // Adjust compression quality as needed
                    byte[] data = baos.toByteArray();

                    // Upload compressed image
                    UploadTask uploadTask = imageName.putBytes(data);
                    uploadTask.addOnSuccessListener(taskSnapshot -> imageName.getDownloadUrl().addOnSuccessListener(uri -> {
                        UrlsList.add(String.valueOf(uri));
                        if (UrlsList.size() == ChooseImageList.size()) {
                            storeLinksJ(price, title, timeline, description);
                        }
                    })).addOnFailureListener(exception -> {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), "Uploading data failed" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Please fill All Field", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void storeLinksJ(String price, String title, String timeline, String description) {
        dismissProgressDialog();
        showProgressDialog("uploading data please wait...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("price", price);
        data.put("title", title);
        data.put("timeline", timeline);
        data.put("description", description);
        data.put("timestamp", Timestamp.now());
        data.put("logoUrl", logoUrl);
        data.put("userId", FirebaseUtil.currentUserId());
        data.put("workType", workType);
        data.put("average_rating", 0F);
        data.put("total_ratings", 0F);
        data.put("seenCount", 0F);


        db.collection("work_posts")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    String uploadId = documentReference.getId();
                    storeImageUrls(uploadId);
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Toast.makeText(getContext(), "Uploading data failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void storeImageUrls(String uploadId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("uploadId", uploadId);
        data.put("urls", UrlsList);

        db.collection("work_images_url")
                .document(uploadId)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    clearData();
                    dismissProgressDialog();
                    Toast.makeText(getContext(), "Your data uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    dismissProgressDialog();
                    Toast.makeText(getContext(), "Uploading data failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearData() {
        ChooseImageList.clear();
        logoUrl = "";
        UrlsList.clear();
        adapter.notifyDataSetChanged();
        selcetednum.setText("Selected Images");
        descriptionJ.getEditText().setText("");
        priceJ.getEditText().setText("");
        titleJ.getEditText().setText("");
        timelineJ.getEditText().setText("");
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
            PostModel postModel = new PostModel(Title,Skill,Description,Price,priceType,Timeline,userId,itemId);
            FirebaseUtil.allJobCollectionReference().child(itemId).setValue(postModel).addOnSuccessListener(unused -> {

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("itemId", itemId);
                dataMap.put("timestamp", ServerValue.TIMESTAMP);

                FirebaseDatabase.getInstance().getReference("jobs i posted").child(userId).child(itemId).setValue(dataMap);
                Toast.makeText(getContext(), "Your post id uploaded", Toast.LENGTH_SHORT).show();
                title.setText("");
                skill.setText("");
                description.setText("");
                price.setText("");
                timeline.setText("");
                radioGroup.check(R.id.fixedRadio);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error uploading the post", Toast.LENGTH_SHORT).show();
            });
        }

        return isValid;
    }



    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission((Activity) getContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission.
                ActivityCompat.requestPermissions((Activity) getContext(), new
                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
                pickImageFromGallery();
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }
    }

    int SELECT_PICTURES = 1;
    private void pickImageFromGallery() {
        // here we go to gallery and select Image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
        //**The following line is the important one!
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURES) {
            if(resultCode == Activity.RESULT_OK) {

                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for(int i = 0; i < count; i++) {
                        if(ChooseImageList.size()<4){
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            if (isImageLessThan2MB(imageUri)) {
                                ChooseImageList.add(imageUri);
                            } else {
                                Toast.makeText(getContext(), "Image size exceeds 2 MB", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getContext(),"You can only select 4 images",Toast.LENGTH_SHORT).show();
                        }

                    }
                } else if(data.getData() != null) {
                    if(ChooseImageList.size()<4) {
                        Uri imageUri = data.getData();
                        if (isImageLessThan2MB(imageUri)) {
                            ChooseImageList.add(imageUri);
                        } else {
                            Toast.makeText(getContext(), "Image size exceeds 2 MB", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getContext(),"You can only select 4 images",Toast.LENGTH_SHORT).show();
                    }
                }
                adapter.notifyDataSetChanged();
                selcetednum.setText("Selected Images (" + ChooseImageList.size() + ")");
            } else {
                Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isImageLessThan2MB(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytes = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
                if (totalBytes > 1024 * 1024 * 2) { // If size exceeds 2MB
                    inputStream.close();
                    return false;
                }
            }
            inputStream.close();
            //Toast.makeText(getContext(), "Image size: " + totalBytes / 1024 + "KB", Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void clicked(int getSize) {
        selcetednum.setText("Selected Images ("+ChooseImageList.size()+")");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapterItems = new ArrayAdapter(requireContext(), R.layout.list_work, itemsList);
        autoCompleteTextView.setAdapter(adapterItems);
    }


}
package com.example.freelancerapp10.utils;


import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DatabaseReference allUserReference(){
        return FirebaseDatabase.getInstance().getReference("users");
    }

    public static DatabaseReference getUsersReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(currentUserId());
    }


    public static DatabaseReference allJobCollectionReference(){
        return FirebaseDatabase.getInstance().getReference("Job Data");

    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }



    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2, String jobId) {
        // Combine user UIDs and job ID to create a unique chatroom ID
        List<String> sortedIds = Arrays.asList(userId1, userId2, jobId);
        Collections.sort(sortedIds);
        return TextUtils.join("_", sortedIds);
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static String getOtherUser(String uid1, String uid2) {
        if (uid1.equals(FirebaseUtil.currentUserId())) {
            return uid2;
        } else {
            return uid1;
        }
    }

    public static DatabaseReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserReference().child(userIds.get(1));
        } else {
            return allUserReference().child(userIds.get(0));
        }
    }
    public static String getRelativeTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        CharSequence relativeTimeSpan = DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.MINUTE_IN_MILLIS);
        return relativeTimeSpan.toString();
    }



    public static String timestampLongToStringMessage(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date timestampDate = new Date(timestamp);
        return sdf.format(timestampDate);
    }


    public static String timestampToStringMessage(Timestamp timestamp) {
            Date currentDate = new Date();
            Date timestampDate = timestamp.toDate();

            long timeDifference = currentDate.getTime() - timestampDate.getTime();
            long hoursDifference = timeDifference / (60 * 60 * 1000);

            if (hoursDifference >= 24) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                return sdf.format(timestampDate);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return sdf.format(timestampDate);
            }
        }



    public static void loadFullName(String userId, final fullNameCallback fullNameCallback) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("fullName");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.getValue(String.class);
                    fullNameCallback.onResult(fullName);

                } else {
                    // Use a default image or handle the absence of profile image here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }

    public static void loadNameAndProfileUrl(String userId, final nameProfileCallback data) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String dataProfileImage = dataSnapshot.child("dataProfileImage").getValue(String.class);

                    // Check if any of the values are null before passing them to the callback
                    if (fullName != null && dataProfileImage != null ) {
                        data.onResult(fullName, dataProfileImage);
                    } else {
                        // Handle the case where one or more values are null
                        // You can skip the callback or provide default values
                        data.onResult("", ""); // Passing default values ("") as an example
                    }
                } else {
                    // Handle the case where the data doesn't exist
                    // You can skip the callback or provide default values
                    data.onResult("", ""); // Passing default values ("") as an example
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    public static void loadNamePhoneEmail(String userId, final namePhoneEmailCallback data) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String email = dataSnapshot.child("dataEmail").getValue(String.class);

                    // Check if any of the values are null before passing them to the callback
                    if (fullName != null && phone != null && email != null) {
                        data.onResult(fullName, phone, email);
                    } else {
                        // Handle the case where one or more values are null
                        // You can skip the callback or provide default values
                        data.onResult("", "", ""); // Passing default values ("") as an example
                    }
                } else {
                    // Handle the case where the data doesn't exist
                    // You can skip the callback or provide default values
                    data.onResult("", "", ""); // Passing default values ("") as an example
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }


    public static void loadRatingFireStore(String postId, final ratingCallbackFirestore ratingCallbackFirestore) {
        FirebaseFirestore.getInstance().collection("rating_post").document(postId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve average_rating and total_ratings as Float
                            Float average_rating = documentSnapshot.getDouble("average_rating").floatValue();
                            Float total_ratings = documentSnapshot.getLong("total_ratings").floatValue();
                            float roundedValue = Math.round(average_rating * 10.0f) / 10.0f;
                            if (average_rating != null && total_ratings != null ) {
                                ratingCallbackFirestore.onResult(roundedValue, total_ratings);
                            } else {
                                // Handle the case where one or more values are null
                                // You can skip the callback or provide default values
                                ratingCallbackFirestore.onResult(0, 0); // Passing default values (0L) as an example
                            }

                        } else {
                            // Handle the case where the data doesn't exist
                            ratingCallbackFirestore.onResult(0, 0);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure case
                        ratingCallbackFirestore.onResult(0, 0);
                    }
                });
    }

    public interface ratingCallbackFirestore {
        void onResult(float averageRating, float totalRatings);
    }

    public static void loadRating(String userId, final ratingCallback ratingCallback) {
        FirebaseDatabase.getInstance().getReference("ratings").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Retrieve average_rating and total_ratings as Float
                            Float average_rating = snapshot.child("average_rating").getValue(Float.class);
                            Float total_ratings = snapshot.child("total_ratings").getValue(Float.class);
                            float roundedValue = Math.round(average_rating * 10.0f) / 10.0f;
                            ratingCallback.onResult(roundedValue, total_ratings);
                        } else {
                            ratingCallback.onResult(0, 0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the onCancelled event
                    }
                });
    }

    public static void loadClientDetail(String userId, final clientDetailCallback clientDetailCallback) {
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Long createdTimestamp = snapshot.child("createdTimestamp").getValue(Long.class);
                            Long moneySpent = snapshot.child("moneySpent").getValue(Long.class);
                            Long jobHired = snapshot.child("jobHired").getValue(Long.class);

                            // Check if any of the values are null before passing them to the callback
                            if (createdTimestamp != null && moneySpent != null && jobHired != null) {
                                clientDetailCallback.onResult(jobHired, moneySpent, createdTimestamp);
                            } else {
                                // Handle the case where one or more values are null
                                // You can skip the callback or provide default values
                                clientDetailCallback.onResult(0L, 0L, 0L); // Passing default values (0L) as an example
                            }
                        } else {
                            // Handle the case where the data doesn't exist
                            // You can skip the callback or provide default values
                            clientDetailCallback.onResult(0L, 0L, 0L); // Passing default values (0L) as an example
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the onCancelled event
                    }
                });
    }



    public static void loadLookingTo(String userId, final lookingToCallback lookingToCallback) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("lookingTo");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lookingTo = dataSnapshot.getValue(String.class);
                    lookingToCallback.onResult(lookingTo);

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }

    public static void loadProfileImage(String userId, final profileCallback profileCallback) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("dataProfileImage");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = dataSnapshot.getValue(String.class);
                    profileCallback.onResult(profileImageUrl);

                } else {
                    // Use a default image or handle the absence of profile image here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }



    public static void checkIfUserAppliedToJob(String jobId, final appliedCallback appliedCallback) {
        FirebaseDatabase.getInstance().getReference("job appliers").child(jobId).child(FirebaseUtil.currentUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean applied = snapshot.exists();
                        appliedCallback.onResult(applied);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



    public static void deleteJobDataFromAllLocation(String key){
        final DatabaseReference jobDataRef = FirebaseDatabase.getInstance().getReference("Job Data").child(key);
        final DatabaseReference jobAppliersRef = FirebaseDatabase.getInstance().getReference("job appliers").child(key);
        final DatabaseReference jobsIPostedRef = FirebaseDatabase.getInstance().getReference("jobs i posted")
                .child(FirebaseUtil.currentUserId()).child(key);

        jobDataRef.removeValue((error, ref) -> {
            if (error != null) {
                Log.e("deleted1", "Error deleting job data: " + error.getMessage());
            } else {
                Log.d("deleted1", key);
            }

            jobAppliersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.getKey();
                        if (userId != null) {
                            Log.d("delete2", "User ID: " + userId);

                            DatabaseReference userJobsAppliedRef = FirebaseDatabase.getInstance().getReference("jobs i applied").child(userId).child(key);
                            userJobsAppliedRef.removeValue((error1, ref1) -> {
                                if (error1 != null) {
                                    Log.e("delete2", "Error deleting job application for User ID: " + userId + ", Error: " + error1.getMessage());
                                } else {
                                    Log.d("delete2", "Job application deleted for User ID: " + userId);
                                }
                            });
                        } else {
                            Log.e("delete2", "User ID is null for a child node in 'job appliers'");
                        }
                    }

                    jobAppliersRef.removeValue((error2, ref2) -> {
                        if (error2 != null) {
                            Log.e("deleted3", "Error deleting job appliers: " + error2.getMessage());
                        } else {
                            Log.d("deleted3", key);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("delete2", "Error reading job appliers: " + databaseError.getMessage());
                }
            });

            jobsIPostedRef.removeValue((error3, ref3) -> {
                if (error3 != null) {
                    Log.e("deleted4", "Error deleting job I posted: " + error3.getMessage());
                } else {
                    Log.d("deleted4", key);
                }
            });

        });

    }



    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public interface fullNameCallback {
        void onResult(String fullName);
    }
    public interface lookingToCallback {
        void onResult(String lookingToCallback);
    }
    public interface appliedCallback {
        void onResult(boolean applied);
    }
    public interface profileCallback {
        void onResult(String profileImageUrl);
    }
    public interface ratingCallback {
        void onResult(float avgRating,float totalRating);
    }
    public interface clientDetailCallback {
        void onResult(long jobHired,long moneySpent,long timestamp);
    }
    public interface namePhoneEmailCallback {
        void onResult(String fullName, String phone, String email);
    }

    public interface nameProfileCallback {
        void onResult(String fullName, String dataProfileImage);
    }


}


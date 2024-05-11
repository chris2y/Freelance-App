package com.example.freelancerapp10.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.freelancerapp10.EditProfileActivity;
import com.example.freelancerapp10.model.UserModel;

import org.checkerframework.checker.index.qual.PolyUpperBound;


public class AndroidUtil {

    public static String loadLookingTo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("myUserData", Context.MODE_PRIVATE);
        // Retrieve the "lookingTo" value from shared preferences
        String lookingTo = sharedPreferences.getString("lookingTo", "");
        return lookingTo;
    }

    public static boolean checkUserDataForNull(Context context, String lookingTo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("myUserData", Context.MODE_PRIVATE);

        String fullName = sharedPreferences.getString("fullName", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String dataProfileImage = sharedPreferences.getString("dataProfileImage", "");

        // Check if common values are null or empty
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(dataProfileImage)) {
            return false; // Some common data is null or empty
        }

        if ("Work".equals(lookingTo)) {
            String profession = sharedPreferences.getString("profession", "");
            String education = sharedPreferences.getString("education", "");
            String skill = sharedPreferences.getString("skill", "");
            String about = sharedPreferences.getString("about", "");

            // Check if work-related values are null or empty
            if (TextUtils.isEmpty(profession) || TextUtils.isEmpty(education) || TextUtils.isEmpty(skill) || TextUtils.isEmpty(about)) {
                return false; // Some work-related data is null or empty
            }
        }
        // If all data is present and not null or empty, return true
        return true;
    }


    public static void UserDataFromSharedPrefAndPassAsIntent(Context context,Intent intent) {
        // Get the shared preferences instance
        SharedPreferences sharedPreferences = context.getSharedPreferences("myUserData", Context.MODE_PRIVATE);
        Bundle extras = new Bundle();

        // Retrieve the data from shared preferences
        String fullName = sharedPreferences.getString("fullName", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String lookingTo = sharedPreferences.getString("lookingTo", "");
        String dataProfileImage = sharedPreferences.getString("dataProfileImage", "");
        String profession = sharedPreferences.getString("profession", "");
        String education = sharedPreferences.getString("education", "");
        String skill = sharedPreferences.getString("skill", "");
        String about = sharedPreferences.getString("about", "");

        extras.putString("email", email);
        extras.putString("phone", phone);
        extras.putString("lookingTo", lookingTo);
        extras.putString("dataProfileImage", dataProfileImage);
        extras.putString("fullName", fullName);

        extras.putString("profession", profession);
        extras.putString("education", education);
        extras.putString("skill", skill);
        extras.putString("about", about);


        intent.putExtras(extras);
    }


    public static void passUserModelAsIntents(Intent intent, UserModel model){
       intent.putExtra("dataFullName",model.getFullName());
       intent.putExtra("userId",model.getUserId());
       intent.putExtra("jobId",model.getJobId());
       intent.putExtra("title",model.getTitle());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setFullName(intent.getStringExtra("dataFullName"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setJobId(intent.getStringExtra("jobId"));
        userModel.setTitle(intent.getStringExtra("title"));
        return userModel;
    }
    public static void showToast(Context context, String message){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}

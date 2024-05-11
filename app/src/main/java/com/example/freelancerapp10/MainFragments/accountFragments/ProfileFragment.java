package com.example.freelancerapp10.MainFragments.accountFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import com.example.freelancerapp10.EditProfileActivity;
import com.example.freelancerapp10.R;
import com.example.freelancerapp10.model.UserProfileModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    Button editProfile;
    private TextView txtEmailLoad;
    private TextView txtPhoneLoad;
    private TextView txtLookingTLoad;
    private TextView txtProfessionLoad;
    private TextView txtEducationLoad;
    private TextView txtSkillLoad;
    private TextView txtAboutLoad;

    private String email = "";
    private String phone = "";
    private String profession = "";
    private String education = "";
    private String skill = "";
    private String about = "";
    private String lookingTo = "";
    private String dataProfileImage = "";

    private String fullName = "";
    LinearLayout linearLayoutWork;
    private ScrollView scrollViewContainer;
    private ProgressBar progressBar;




    View view;
    ValueEventListener eventListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        editProfile = view.findViewById(R.id.btnEditProfile);
        txtEmailLoad = view.findViewById(R.id.txtEmailLoad);
        txtPhoneLoad = view.findViewById(R.id.txtPhoneLoad);
        txtProfessionLoad = view.findViewById(R.id.txtProfessionLoad);
        txtEducationLoad = view.findViewById(R.id.txtEducationLoad);
        txtSkillLoad = view.findViewById(R.id.txtSkillLoad);
        txtLookingTLoad = view.findViewById(R.id.txtLookingToLoad);
        txtAboutLoad = view.findViewById(R.id.txtAboutLoad);
        linearLayoutWork = view.findViewById(R.id.linearLayoutWorkView);
        scrollViewContainer = view.findViewById(R.id.scrollViewContainer);
        progressBar = view.findViewById(R.id.progressBar);


        progressBar.setVisibility(View.VISIBLE);
        scrollViewContainer.setVisibility(View.GONE);

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserProfileModel userProfileModel = dataSnapshot.getValue(UserProfileModel.class);
                    if (userProfileModel != null) {
                        email = userProfileModel.getDataEmail();
                        phone = userProfileModel.getPhoneNumber();
                        profession = userProfileModel.getProfession();
                        about = userProfileModel.getAboutMe();
                        skill = userProfileModel.getSkill();
                        education = userProfileModel.getEducation();
                        lookingTo = userProfileModel.getLookingTo();
                        dataProfileImage = userProfileModel.getDataProfileImage();
                        fullName = userProfileModel.getFullName();

                        updateTextViews();

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


        editProfile.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            Bundle extras = new Bundle();

            extras.putString("email", email);
            extras.putString("phone", phone);
            extras.putString("profession", profession);
            extras.putString("education", education);
            extras.putString("skill", skill);
            extras.putString("about", about);
            extras.putString("lookingTo", lookingTo);
            extras.putString("dataProfileImage", dataProfileImage);
            extras.putString("fullName", fullName);

            intent.putExtras(extras);
            startActivity(intent);
        });


        return view;
    }

    private void updateTextViews() {
        if (lookingTo.equals("Work")) {
            linearLayoutWork.setVisibility(View.VISIBLE);
            txtProfessionLoad.setText(profession);
            txtAboutLoad.setText(about);
            txtSkillLoad.setText(skill);
            txtEducationLoad.setText(education);
        } else {
            // If lookingTo is not "Work", hide the ScrollView and show the ProgressBar
            linearLayoutWork.setVisibility(View.GONE);
        }
        txtEmailLoad.setText(email);
        txtPhoneLoad.setText(phone);
        txtLookingTLoad.setText(lookingTo);
        progressBar.setVisibility(View.GONE);
        scrollViewContainer.setVisibility(View.VISIBLE);
    }

}
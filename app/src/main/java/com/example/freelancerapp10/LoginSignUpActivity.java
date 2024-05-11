package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freelancerapp10.model.UserModel;
import com.example.freelancerapp10.model.UserProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginSignUpActivity extends AppCompatActivity {

    private TextView loginTextLol, signUpTextLol, forgotPassTxt, signupTxt, loginTxt;
    private TextInputLayout emailEditText, passwordEditText;
    private Button loginButton,skipButton,signupButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        loginTextLol = findViewById(R.id.txtLoginlol);
        signUpTextLol = findViewById(R.id.txtSignUplol);
        emailEditText = findViewById(R.id.txtEmail);
        passwordEditText = findViewById(R.id.txtPassword);
        loginButton = findViewById(R.id.btnLogin);
        skipButton = findViewById(R.id.btnSkip);
        signupButton = findViewById(R.id.btnSignUp);
        signupTxt = findViewById(R.id.txtSignup);
        forgotPassTxt = findViewById(R.id.txtForgotPass);
        loginTxt = findViewById(R.id.txtLogIn);

        progressBar = findViewById(R.id.progressBarLogin);
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable loginButton, forgotPassTxt, and signupTxt
                loginButton.setVisibility(View.GONE);
                forgotPassTxt.setVisibility(View.GONE);
                signupTxt.setVisibility(View.GONE);
                signUpTextLol.setVisibility(View.GONE);


                loginTextLol.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);
                loginTxt.setVisibility(View.VISIBLE);
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setVisibility(View.VISIBLE);
                forgotPassTxt.setVisibility(View.VISIBLE);
                signupTxt.setVisibility(View.VISIBLE);
                signUpTextLol.setVisibility(View.VISIBLE);

                loginTextLol.setVisibility(View.GONE);
                signupButton.setVisibility(View.GONE);
                loginTxt.setVisibility(View.GONE);
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEditText.setError(null);
                passwordEditText.setError(null);

                String email = emailEditText.getEditText().getText().toString().trim();
                String password = passwordEditText.getEditText().getText().toString().trim();

                if (password.isEmpty() && email.isEmpty()) {
                    passwordEditText.setError("Password cannot be empty");
                    emailEditText.setError("Email can't be empty");
                    return;
                }

                if (email.isEmpty()) {
                    emailEditText.setError("Email can't be empty");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid email format");
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password cannot be empty");
                    return;
                }

                if (password.length() != 8) {
                    passwordEditText.setError("Password must contain 8 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);

                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            user = auth.getCurrentUser();
                            String email1 = user.getEmail();
                            String uid = user.getUid();

                            saveOldDataToSharedPref(uid);

                            // Save the user's information to shared preferences

                            editor.putString("email", email1);
                            editor.putString("uid", uid);
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("authToken", user.getIdToken(false).getResult().getToken()); // Save authentication token
                            editor.apply();

                            Toast.makeText(LoginSignUpActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));

                            finish();
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                loginButton.setVisibility(View.VISIBLE);
                                if (e instanceof FirebaseAuthInvalidUserException) {
                                    emailEditText.setError("User not found");
                                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(LoginSignUpActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });





        signupButton.setOnClickListener(view -> {

            emailEditText.setError(null);
            passwordEditText.setError(null);

            String email = emailEditText.getEditText().getText().toString().trim();
            String password = passwordEditText.getEditText().getText().toString().trim();

            if (password.isEmpty() && email.isEmpty()) {
                passwordEditText.setError("Password cannot be empty");
                emailEditText.setError("Email can't be empty");
                return;
            }

            if (email.isEmpty()) {
                emailEditText.setError("Email cannot be empty");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid email format");
                return;
            }
            if (password.isEmpty()){
                passwordEditText.setError("Password cannot be empty");
                return;
            }
            if (password.length() != 8){
                passwordEditText.setError("Password must contain 8 characters");
            }
            else if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() == 8){
                progressBar.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.GONE);
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            // Save the user's information to shared preferences
                            editor.putString("Email", email);
                            editor.putString("uid", uid);
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("authToken", user.getIdToken(false).getResult().getToken()); // Save authentication token
                            editor.apply();


                            UserModel userModel = new UserModel(email,uid);
                            databaseReference.child(uid).setValue(userModel)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(LoginSignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
                                                    Intent intent = new Intent(LoginSignUpActivity.this, EditProfileActivity.class);
                                                    intent.putExtra("Email", email);
                                                    startActivity(intent);

                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            signupButton.setVisibility(View.VISIBLE);
                                            Toast.makeText(LoginSignUpActivity.this, "SignUp failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            signupButton.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginSignUpActivity.this, "SignUp failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("skip", "true");
                editor.apply();
                startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    private void saveOldDataToSharedPref(String uid) {

        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfileModel userProfileModel = snapshot.getValue(UserProfileModel.class);
                    if (userProfileModel != null) {

                        saveDataToSharedPreferences(userProfileModel);

                    }

                } else {
                    // The specified user ID does not exist in the database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void saveDataToSharedPreferences(UserProfileModel userProfileModel) {
        // Get the shared preferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("myUserData", Context.MODE_PRIVATE);

        // Create an editor to edit the shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put common data in all cases
        editor.putString("fullName", userProfileModel.getFullName());
        editor.putString("email", userProfileModel.getDataEmail());
        editor.putString("phone", userProfileModel.getPhoneNumber());
        editor.putString("lookingTo", userProfileModel.getLookingTo());
        editor.putString("dataProfileImage", userProfileModel.getDataProfileImage());

        editor.putString("profession", userProfileModel.getProfession());
        editor.putString("education", userProfileModel.getEducation());
        editor.putString("skill", userProfileModel.getSkill());
        editor.putString("about", userProfileModel.getAboutMe());
        // Commit the changes to the shared preferences
        editor.apply();
    }
}
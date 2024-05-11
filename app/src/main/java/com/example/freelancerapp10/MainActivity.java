package com.example.freelancerapp10;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.freelancerapp10.MainFragments.AccountFragment;
import com.example.freelancerapp10.MainFragments.HomeFragment;
import com.example.freelancerapp10.MainFragments.ListingFragment;
import com.example.freelancerapp10.MainFragments.MessageFragment;
import com.example.freelancerapp10.MainFragments.PostFragment;
import com.example.freelancerapp10.MainFragments.RedirectToLoginFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99;
    BottomNavigationView bottomNavigationView;


    HomeFragment homeFragment = new HomeFragment();
    MessageFragment messageFragment = new MessageFragment();
    RedirectToLoginFragment redirectToLoginFragment = new RedirectToLoginFragment();

    ListingFragment listingFragment = new ListingFragment();
    AccountFragment accountFragment = new AccountFragment();
    PostFragment postFragment = new PostFragment();
    private SharedPreferences sharedPreferences;
    String skip;
    String authToken;
    boolean isLoggedIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,homeFragment).commit();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isUserAuthenticated()) {
            proceedToLogin();
        }

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_message);
        badgeDrawable.setVisible(false);
        badgeDrawable.setNumber(8);


        // Retrieving the data from the Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data = extras.getString("completed");
            if (data.equals("true")){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Project Awarded");
                builder.setMessage("Project has been awarded successfully, you can find the contract details in Account -> Contracts");
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    // Do something when the "Ok" button is clicked
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Log.d("Data received:", data);
            }
        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // This is the boolean value that you mentioned.

                if (itemId == R.id.nav_home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
                    return true;
                } else if (itemId == R.id.nav_account && isLoggedIn) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, accountFragment).commit();
                    return true;
                } else if (itemId == R.id.nav_listing && isLoggedIn) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, listingFragment).commit();
                    return true;
                } else if (itemId == R.id.nav_post && isLoggedIn) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, postFragment).commit();
                    return true;
                } else if (itemId == R.id.nav_message && isLoggedIn) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, messageFragment).commit();
                    return true;
                } else {
                    // The user is not logged in, so redirect to the login fragment.
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, redirectToLoginFragment).commit();
                    return true;
                }
            }
        });

    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean contractState = data.getBooleanExtra("contractState", false);

                if (contractState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Project Awarded");
                    builder.setMessage("Project has been awarded successfully,you can find find about " +
                            "the contract detail in Account -> Contracts");

                    builder.setPositiveButton("Ok", (dialog, which) -> {
                    }).show();
                    Toast.makeText(getApplicationContext(), "failed to process response", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Project Awarded");
                    builder.setMessage("Project has been awarded successfully,you can find find about " +
                            "the contract detail in Account -> Contracts");

                    builder.setPositiveButton("Ok", (dialog, which) -> {
                    }).show();
                    Toast.makeText(getApplicationContext(), "failed to process response", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/
    private boolean isUserAuthenticated() {
        authToken = sharedPreferences.getString("authToken", null);
        skip = sharedPreferences.getString("skip", null);

        Log.d("Sharedprefvalue", authToken + skip);
        return (authToken == null || authToken.isEmpty()) && (skip == null || skip.isEmpty());
    }

    private void proceedToLogin() {
        startActivity(new Intent(MainActivity.this, LoginSignUpActivity.class));
        finish();
    }
}
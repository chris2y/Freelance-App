package com.example.freelancerapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freelancerapp10.adapters.AppliersListAdapter;
import com.example.freelancerapp10.model.AppliersModel;
import com.example.freelancerapp10.model.UserModel;
import com.example.freelancerapp10.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppliersListActivity extends AppCompatActivity implements PaymentListener{

    List<AppliersModel> appliersItems;
    String itemId;
    RecyclerView recyclerView;
    AppliersListAdapter adapter;
    LinearLayoutManager manager;
    ImageButton back;
    ProgressBar progressBar;
    TextView noData;
    private static final int REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliers_list);
        appliersItems = new ArrayList<>();
        progressBar = findViewById(R.id.progressbarAppliers);
        recyclerView = findViewById(R.id.recyclerForAppliers);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemId = bundle.getString("Key");
            Log.d("itemId" , itemId);
        }

        adapter = new AppliersListAdapter(AppliersListActivity.this, appliersItems, itemId,this);

        manager = new LinearLayoutManager(AppliersListActivity.this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        back = findViewById(R.id.backButton);
        noData = findViewById(R.id.noAppliersTextView);


        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        back.setOnClickListener(view -> {
            onBackPressed();
        });

        loadDataFromDatabase();

    }

    private void loadDataFromDatabase() {
        FirebaseDatabase.getInstance().getReference("job appliers")
                .child(itemId)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                AppliersModel appliersModel = dataSnapshot.getValue(AppliersModel.class);
                                if (appliersModel != null) {
                                    appliersModel.setKey(dataSnapshot.getKey());
                                    appliersItems.add(appliersModel);
                                }
                            }
                            noData.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            appliersItems.clear();
                            progressBar.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    String Uid2,ItemId,DeliveryDate,ProposalDescription,Price;

    private class InitializePaymentTask extends AsyncTask<Void, Void, HashMap<String, String>> {
        String txRef = "";
        String amount;
        String email;
        String fullName;
        String phone;

        InitializePaymentTask(String amount, String email, String fullName, String phone,
                              String uid2,String itemId,String deliveryDate,String proposalDescription) {
            this.amount = amount;
            this.email = email;
            this.fullName = fullName;
            this.phone = "+251" + phone;
            Price = amount;

            Uid2 = uid2;
            ItemId = itemId;
            DeliveryDate = deliveryDate;
            ProposalDescription = proposalDescription;


        }


        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                Random random = new Random();
                int randomNumber = random.nextInt(10000); // Adjust the range as needed

// Convert the random number to a string
                txRef = "chewatatgest-" + randomNumber;
                Log.d("Chapaaaaaa", txRef);

                String lastName = "";
                String currency = "ETB";

                Log.d("Chapaaaaaa", amount);



                // Define your API endpoint and secret key
                String apiUrl = "https://api.chapa.co/v1/transaction/initialize";
                String secretKey = "CHASECK_TEST-LoFk5nGOjxxMywS8XNcUBdegrKpM7KaV"; // Replace with your actual secret key

                MediaType mediaType = MediaType.parse("application/json");


                RequestBody body = RequestBody.create(mediaType, "{\n" +
                        " \"amount\":\"" + amount + "\",\n" +
                        " \"currency\": \"" + currency + "\",\n" +
                        " \"email\": \"" + email + "\",\n" +
                        " \"first_name\": \"" + fullName + "\",\n" +
                        " \"last_name\": \"" + lastName + "\",\n" +
                        " \"phone_number\": \"" + phone + "\",\n" +
                        " \"tx_ref\": \"" + txRef + "\",\n" +
                        " \"callback_url\": \"https://webhook.site/077164d6-29cb-40df-ba29-8a00e59a7e60\",\n" +
                        " \"return_url\": \"https://google.com\",\n" +
                        " \"customization[title]\": \"Freelance\",\n" +
                        " \"customization[description]\": \"I love online payments\"\n" +
                        "}");

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + secretKey)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                if (response != null) {
                    Log.d("ChapaApiResponse", "Response Code: " + response.code());
                    String responseBody = response.body().string();
                    Log.d("ChapaApiResponse", "Response Body: " + responseBody);

                    // Check if the response is successful
                    if (response.isSuccessful()) {
                        // Parse the JSON response to get the checkout URL
                        // You may want to use a JSON library for this, e.g., Jackson or Gson
                        // Here, we'll use a simple substring method for demonstration purposes
                        String checkoutUrl = responseBody.substring(responseBody.indexOf("checkout_url") + 15, responseBody.lastIndexOf("\""));
                        checkoutUrl = checkoutUrl.replace("\\/", "/"); // Unescape the double backslashes

                        HashMap<String, String> result = new HashMap<>();
                        result.put("checkout_url", checkoutUrl);
                        result.put("txRef", txRef);
                        return result;
                    } else {
                        return null; // Failed to initialize payment transaction
                    }
                }

                // Check if the response is successful
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ChapaPaymentError", "Error: " + e.getMessage());
                return null;
            }

            return null; // Added this line to provide a return statement outside the try-catch block
        }

        @Override
        protected void onPostExecute(HashMap<String, String> resultMap) {
            if (resultMap != null) {
                String checkoutUrl = resultMap.get("checkout_url");
                String txRef = resultMap.get("txRef");

                if (checkoutUrl != null) {
                    // Redirect the user to the payment link using a WebView
                    Log.d("Chapaaaaaa", checkoutUrl);

                    Intent intent = new Intent(AppliersListActivity.this, WebViewActivity.class);
                    intent.putExtra("urlPayment", checkoutUrl);
                    intent.putExtra("tx_ref", txRef);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "failed checkout", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "failed to process response", Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    public void initializePayment(String amount, String email, String fullName, String phone,
                                  String uid2,String itemId,String deliveryDate,String proposalDescription) {
        new InitializePaymentTask(amount, email, fullName, phone, uid2, itemId, deliveryDate, proposalDescription).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean paymentState = data.getBooleanExtra("paymentState", false);

                if (paymentState) {

                    awardProject();

                    Toast.makeText(getApplicationContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Payment failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void awardProject() {
        FirebaseUtil.allJobCollectionReference().child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data as a HashMap
                    Map<String, Object> userData = (Map<String, Object>) dataSnapshot.getValue();
                    if (userData != null) {
                        String descriptionJob = (String) userData.get("description");
                        String title = (String) userData.get("title");

                        Map<String, Object> postData = new HashMap<>();
                        postData.put("contractId", itemId);
                        postData.put("deliveryDate", DeliveryDate);
                        postData.put("proposalDescription", ProposalDescription);
                        postData.put("price", Price );
                        postData.put("timestamp", ServerValue.TIMESTAMP);
                        postData.put("userId1", FirebaseUtil.currentUserId());
                        postData.put("userId2", Uid2);
                        postData.put("descriptionJob", descriptionJob);
                        postData.put("title", title);

                        FirebaseDatabase.getInstance().getReference("contracts").child(itemId).setValue(postData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Toast.makeText(getApplicationContext(),"Project awarded",Toast.LENGTH_SHORT).show();
                                    }
                                });

                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("contractId", itemId);
                        dataMap.put("timestamp", ServerValue.TIMESTAMP);

                        FirebaseDatabase.getInstance().getReference("my contracts")
                                .child(Uid2).child(itemId).setValue(dataMap);
                        FirebaseDatabase.getInstance().getReference("my contracts")
                                .child(FirebaseUtil.currentUserId()).child(itemId).setValue(dataMap);

                        FirebaseUtil.deleteJobDataFromAllLocation(itemId);


                        Intent intent = new Intent(AppliersListActivity.this, MainActivity.class);
                        intent.putExtra("completed", "true");
                        startActivity(intent);

                    } else {

                    }
                } else {
                    // Handle the absence of user data here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

}
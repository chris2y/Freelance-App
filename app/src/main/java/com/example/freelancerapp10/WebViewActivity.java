package com.example.freelancerapp10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebViewActivity extends AppCompatActivity {


    EditText urlInput;
    WebView webView;
    ProgressBar progressBar;
    ImageView webBack,webForward,webRefresh,closeBtn;
    public static String txRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        urlInput = findViewById(R.id.url_input);
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        webBack = findViewById(R.id.web_back);
        webForward = findViewById(R.id.web_forward);
        webRefresh = findViewById(R.id.web_refresh);
        closeBtn = findViewById(R.id.close_btn);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);


        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("urlPayment");
        txRef = intent.getStringExtra("tx_ref");
        Log.d("Chapaaaaaa", txRef);
        loadMyUrl(url);
        urlInput.setFocusable(false);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                boolean paymentState = false; // Set this to true if the proposal was edited, false if it was submitted
                resultIntent.putExtra("paymentState", paymentState);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        webBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webView.canGoBack()){
                    webView.goBack();
                }
            }
        });

        webForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webView.canGoForward()){
                    webView.goForward();
                }
            }
        });

        webRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
            }
        });


    }

    void loadMyUrl(String url){
            webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {

        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    class MyWebViewClient extends WebViewClient {
        private static final String TARGET_URL = "https://google.com/"; // Replace with your desired URL
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // Check if the URL matches the specific URL you want to finish the activity on
            String url = request.getUrl().toString();
            if (url.equals(TARGET_URL)) {

                ValidatePaymentTask validatePaymentTask = new ValidatePaymentTask();
                validatePaymentTask.execute();
                return true; // Return true to indicate that the URL is handled
            } else {
                return false; // Continue loading the URL
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            urlInput.setText(webView.getUrl());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }





    private class ValidatePaymentTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String txRef = "https://api.chapa.co/v1/transaction/verify/" + WebViewActivity.txRef;
                Log.d("Chapaaaaaa", txRef);
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                // Use the intended GET method directly when creating the request
                Request request = new Request.Builder()
                        .url(txRef) // Replace with your actual transaction reference
                        .get() // Use the GET method directly
                        .addHeader("Authorization", "Bearer CHASECK_TEST-LoFk5nGOjxxMywS8XNcUBdegrKpM7KaV") // Replace with your actual secret key
                        .build();
                Response response = client.newCall(request).execute();

                return response.isSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isPaymentSuccessful) {
            Intent resultIntent = new Intent();
            boolean paymentState;
            if (isPaymentSuccessful) {
                paymentState = true;
            } else {
                paymentState = false;
            }
            resultIntent.putExtra("paymentState", paymentState);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

    }


}
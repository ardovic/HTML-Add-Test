package com.ardovic.htmladdtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<AddData> {

    private WebView webView;
    private ProgressBar progressBar;
    private AddData addData;
    private ApiInterface apiInterface;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("http://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    PackageManager packageManager = getPackageManager();
                    if (intent.resolveActivity(packageManager) != null) {
                        view.getContext().startActivity(intent);
                        finish();
                    } else {
                        showDialog("No Application can handle your intent");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });


        progressBar = findViewById(R.id.progress);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        app = App.getInstance();
        addData = app.getAppData();


        if (addData == null) {
            TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            try {
                @SuppressLint("MissingPermission") String imsi = mTelephonyMgr.getSubscriberId();
                long id = Long.parseLong(imsi);
                Call<AddData> call = apiInterface.getPageData(id);
                call.enqueue(this);
            }catch (Throwable t) {
                showDialog("Error getting IMSI");
                t.printStackTrace();
            }


        } else {
            respondToAddData(addData);
        }

    }

    @Override
    public void onResponse(Call<AddData> call, Response<AddData> response) {
        addData = (AddData) response.body();

        if (addData != null) {
            App.getInstance().setAppData(addData);
            respondToAddData(addData);
        }

    }

    @Override
    public void onFailure(Call<AddData> call, Throwable t) {
        showDialog("POST request failed");
    }

    public void respondToAddData(AddData addData) {
        if (addData.getStatus().equals("OK")) {
            webView.loadUrl(addData.getUrl());
            webView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            showDialog(addData.getMessage());
        }
    }

    public void showDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        progressBar.setVisibility(View.GONE);
        alertDialog.show();
    }
}

package com.example.jackdaw.iservices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class webviewActivity extends AppCompatActivity
{
    WebView webView;
    TextView txtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        //Toolbar myToolBar = (Toolbar) findViewById(R.id.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String message = getIntent().getStringExtra("message_key");
        //txtView = (TextView) findViewById(R.id.link_text);
        //txtView.setText(message);
        //String msg = txtView.getText().toString();

        webView = (WebView) findViewById(R.id.urlWebView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(message);
    }
}

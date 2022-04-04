package com.bsmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;


public class WebBrowser extends AppCompatActivity {

    private String bsIP;
    private WebBrowser self;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            WebView myWebView = findViewById(R.id.we);
            myWebView.loadUrl("javascript:menuButtonPressed()");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        WebView myWebView = findViewById(R.id.we);
        myWebView.loadUrl("javascript:backButtonPressed()");
    }

    public class WebAppInterface {
        Context mContext;
        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }
        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void exit() {
            finish();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        SharedPreferences mSettings = this.getSharedPreferences("BS_IP", 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Basestation ip:port");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(mSettings.getString("BS_IP", null));
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bsIP = input.getText().toString();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("BS_IP", bsIP);
                editor.commit();
                setContentView(R.layout.activity_main);
                WebView myWebView = findViewById(R.id.we);
                myWebView.setWebChromeClient(new WebChromeClient());
                myWebView.setWebViewClient(new WebViewClient());
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.getSettings().setDomStorageEnabled(true);
                myWebView.getSettings().setDatabaseEnabled(true);
                myWebView.addJavascriptInterface(new WebAppInterface(self), "Android");
                myWebView.loadUrl("http://" + bsIP);
            }
        });

        builder.show();
    }
}

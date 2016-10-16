package com.ob.Obrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.util.Base64;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * Created by vikas on 10/15/16.
 */

public class WebViewActivity extends Activity {
    private static final int INCREASE_FONT_SIZE_MENU_ITEM = Menu.FIRST;
    private static final int DECREASE_FONT_SIZE_MENU_ITEM = Menu.FIRST + 1;
    private static final int FIND_IN_PAGE_MENU_ITEM = Menu.FIRST + 2;
    private static final int CLEAR_CACHE_MENU_ITEM = Menu.FIRST + 3;
    private static final int CLOSE_WEB_VIEW_MENU_ITEM = Menu.FIRST + 4;

    private WebView mWebView;
    private LinearLayout linearLayout;

    private Button findNextButton;
    private Button findCloseButton;
    private EditText findEditText;

    private static datab webDataBase;

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.webrender);

        String title = getIntent().getStringExtra("title");
        mWebView = (WebView) findViewById(R.id.webView1);
        configureWebView(mWebView);
        webDataBase = new datab(getApplicationContext());
        String htmlData = webDataBase.rlink(title);
        String htmlUrl = webDataBase.getBaseUrl(title);
        try{
            htmlData = Base64.encodeToString(htmlData.getBytes("UTF-8"), Base64.DEFAULT);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        mWebView.loadData(htmlData, "text/html; charset=utf-8", "base64");

    }

    private WebViewClient getWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
            String data = webDataBase.rlinkbyurl(url);
            if (data.equalsIgnoreCase("error")) {
                try{
                    data = Base64.encodeToString(data.getBytes("UTF-8"), Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private void configureWebView(WebView mWebView) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setBlockNetworkLoads(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
    }
}

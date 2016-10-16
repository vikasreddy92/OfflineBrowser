package com.ob.Obrowser;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ob.Obrowser.R;

public class webdisp extends Activity {
    /**
     * @uml.property name="wv"
     * @uml.associationEnd
     */
    public static final int menu1 = Menu.FIRST;
    public static final int menu2 = Menu.FIRST + 1;
    public static final int menu3 = Menu.FIRST + 2;
    public static final int menu4 = Menu.FIRST + 4;
    public static final int menu5 = Menu.FIRST + 5;
    public WebView wv;
    private LinearLayout container;
    private Button nextButton, closeButton;
    private EditText findBox;

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.webrender);

        wv = (WebView) findViewById(R.id.webView1);
        final String useragent = "Mozilla/5.0 (Linux; U; Android 2.3.5; en-us; SAMSUNG-SGH-I897 Build/GINGERBREAD) AppleWebKit/533.1+ (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Title");
        alert.setMessage("Give title:");
        // Set an EditText view to get user input

        Intent i = getIntent();
        String value = i.getStringExtra("title");

        final datab d1 = new datab(getApplication());
        final String res1 = d1.rlink(value);
        String base64 = null;
        final String base = d1.getBaseUrl(value);
        wv.getSettings().setUserAgentString(useragent);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setBlockNetworkLoads(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);

        String data = res1;  // the html data
        try {
            base64 = android.util.Base64.encodeToString(data.getBytes("UTF-8"), android.util.Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        wv.loadData(base64, "text/html; charset=utf-8", "base64");
        WebViewClient client = new WebViewClient() {
            // you tell the webclient you want to catch when a url is about to load
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                int duration = Toast.LENGTH_SHORT;
                if ((url.startsWith("http"))) {
                    Log.v("in client should overridideurl with http://", url);
                    Toast.makeText(getApplicationContext(), url, duration).show();
                    String d = d1.rlinkbyurl(url);
                    String base64 = null;
                    if (d.equalsIgnoreCase("error")) {
                        String data = d;  // the html data
                        try {
                            base64 = android.util.Base64.encodeToString(data.getBytes("UTF-8"), android.util.Base64.DEFAULT);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        wv.loadData(base64, "text/html; charset=utf-8", "base64");
                    }
                    String data = d;  // the html data
                    try {
                        base64 = android.util.Base64.encodeToString(data.getBytes("UTF-8"), android.util.Base64.DEFAULT);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    wv.loadData(base64, "text/html; charset=utf-8", "base64");
                } else {
                    Log.v("is not starts with http", url);
                    String res2 = base + url;

                    Toast.makeText(getApplicationContext(), res2, duration).show();
                    String d = d1.rlinkbyurl(res2);
                    String base64 = null;
                    String data = d;  // the html data
                    try {
                        base64 = android.util.Base64.encodeToString(data.getBytes("UTF-8"), android.util.Base64.DEFAULT);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    wv.loadData(base64, "text/html; charset=utf-8", "base64");

                }
                return true;
            }
            // here you execute an action when the URL you want is about to load
              /*  @Override
			  public void onLoadResource(WebView  view, String nurl)
			    {			    		
			        	int duration = Toast.LENGTH_SHORT;
			        	Toast.makeText(getApplicationContext(),"first time", duration).show();				    	
			            // do whatever you want			        
			    }*/
        };
        wv.setWebViewClient(client);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv.canGoBack() == true) {
                        wv.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, menu1, 0, "Increase font size");
        menu.add(0, menu2, 0, "Decrease font size");
        menu.add(0, menu4, 0, "Find on page");
        menu.add(0, menu5, 0, "Clear Cache");
        menu.add(0, menu3, 0, "Close");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case menu1:
                int m = 10;
                //wv.getSettings().setTextSize(WebSettings.TextSize.LARGER);
                wv.getSettings().setDefaultFontSize(wv.getSettings().getDefaultFontSize() + m);
                m = 10;
                break;
            case menu2:
                int n = 10;
                //wv.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
                wv.getSettings().getDefaultFontSize();
                wv.getSettings().setDefaultFontSize(wv.getSettings().getDefaultFontSize() - n);
                n = 10;
                break;
            case menu3:
                finish();
                break;
            case menu4:
                search();
                break;
            case menu5:
                wv.clearCache(true);
                Toast.makeText(getApplicationContext(), "Cache cleared", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    public void search() {
        container = (LinearLayout) findViewById(R.id.layoutId);
        nextButton = new Button(this);
        nextButton.setText("Next");
        nextButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                wv.findNext(true);
            }
        });
        container.addView(nextButton);
        closeButton = new Button(this);
        closeButton.setText("Close");
        closeButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                container.removeAllViews();
                wv.clearMatches();
            }
        });
        container.addView(closeButton);
        findBox = new EditText(this);
        findBox.setMinEms(30);
        findBox.setSingleLine(true);
        findBox.setHint("Search");
        findBox.setOnKeyListener(new android.view.View.OnKeyListener() {
            @SuppressWarnings("deprecation")
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))) {
                    wv.findAll(findBox.getText().toString());
                    try {
                        Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                        m.invoke(wv, true);
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }
        });

        container.addView(findBox);
    }
}
package com.ob.Obrowser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vikas on 10/13/16.
 */

public class AddLinkActivity extends Activity {

    private Button submitButton;
    private Spinner depthSpinner;
    private ArrayAdapter<CharSequence> depthArrayAdapter;
    private Spinner maxLinksSpinner;
    private ArrayAdapter<CharSequence> maxLinksArrayAdapter;
    private ProgressDialog progressDialog;
    private EditText titleEditText;
    private EditText urlEditText;

    private int currentTitleCount = 0;

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.entry_edit_layout);
        init();
    }

    private void init() {
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        urlEditText = (EditText) findViewById(R.id.urlEditText);

        depthSpinner = (Spinner) findViewById(R.id.depthSpinner);
        depthArrayAdapter = ArrayAdapter.createFromResource(this, R.array.depth_array,
                android.R.layout.simple_spinner_dropdown_item);
        depthSpinner.setAdapter(depthArrayAdapter);

        maxLinksSpinner = (Spinner) findViewById(R.id.maxLinksSpinner);
        maxLinksArrayAdapter = ArrayAdapter.createFromResource(this, R.array.max_links_array,
                android.R.layout.simple_spinner_dropdown_item);
        maxLinksSpinner.setAdapter(maxLinksArrayAdapter);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(submitButtonListener);
    }

    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            progressDialog = createProgressDialog(view.getContext());
            progressDialog.show();
            Thread thread = new Thread(runnableThread);
            thread.start();
        }
    };

    private ProgressDialog createProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("File downloading ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        return progressDialog;
    }

    private Runnable runnableThread = new Runnable() {
        int progressBarStatus = 0;
        Handler progressBarHandler = new Handler();

        public void run() {
            Looper.prepare();
            while (progressBarStatus < 100) {
                progressBarStatus = doWork();
                progressBarHandler.post(new Runnable() {
                    public void run() {
                        if (progressBarStatus >= 100) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    };

    private int doWork() {
        int depth = Integer.parseInt(String.valueOf(depthSpinner.getSelectedItem()));
        int maxLinks = Integer.parseInt(String.valueOf(maxLinksSpinner.getSelectedItem()));
        String title = titleEditText.getText().toString();
        String url = urlEditText.getText().toString();

        if (isValidTitle(title)) {
            displayMessage(getApplicationContext(), "Title cannot be empty!");
            return 100;
        }

        if (isValidURL(url)) {
            displayMessage(getApplicationContext(), "Not a valid URL!");
            return 100;
        }

        if (isNetworkAvailable()) {
            displayMessage(getApplicationContext(), "Not connected to network!");
            return 100;
        }

//        if (!url.startsWith("http://") || !url.startsWith("https//")) {
//            url = "http://" + url;
//        }

        ListDBHelper listDBHelper = new ListDBHelper(AddLinkActivity.this);
        listDBHelper.additem(title);

        if (depth == 1) {
            downloadData(url, title, maxLinks);
        } else if (depth == 2) {
            downloadData(url, title, maxLinks);
            downloadInnerLinks(url, title, maxLinks);
        } else if (depth > 2) {
            ArrayList<String> urls = downloadInnerLinks(url, title, maxLinks);
            int currentDepth = 2;
            while (currentDepth < depth) {
                urls = downloadInnerLinks(urls, title, maxLinks);
                currentDepth++;
            }
        }
        return 100;
    }

    private boolean isValidTitle(String title) {
        return (title == null || title.length() == 0);
    }

    private boolean isValidURL(String url) {
        return (url == null || url.length() <= 7);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo == null || !networkInfo.isConnected());
    }

    private void displayMessage(Context applicationContext, String message) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(AddLinkActivity.this, ObrowserActivity.class);
        AddLinkActivity.this.startActivity(i);
    }

    private void downloadData(String url, String title, int maxLinks) {
        HttpClient httpClient = url.startsWith("http://") ? new DefaultHttpClient() :
                                                            new NetworkManager().getClient();
        HttpGet httpGet = getHttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.equals(null) || httpResponse.getStatusLine().getStatusCode() != 200) {
                displayMessage(getApplicationContext(), "Invalid response!");
            }
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            Document document = Jsoup.parse(responseBody);
            Elements hrefElements = document.select("abs[href]");
            int currentLinksCount = 0;
            for (Element hrefElement : hrefElements) {
                if (currentLinksCount > maxLinks) {
                    break;
                }
                String href = hrefElement.attr("abs:href");
                if (href.length() < 7) {
                    continue;
                }
                if (href.startsWith("http://") != true && href.equals(null) != true) {
                    href = "http://" + url + href;
                }

                httpGet = getHttpGet(href);
                httpResponse = httpClient.execute(httpGet);
                String responseText = EntityUtils.toString(httpResponse.getEntity());
                document.head().appendElement("style").attr("type", "text/css").text(responseText);
                responseBody = document.html();
            }
            Webdata wd = new Webdata();
            wd.setData1(responseBody);
            wd.setUrl1(url);
            wd.setTitle1(title + currentTitleCount++);
            Intent sendData = new Intent(getApplicationContext(), insertdata.class);
            sendData.putExtra("webdata", wd);
            startActivity(sendData);
            currentLinksCount++;
        } catch (IOException e) {
            displayMessage(getApplicationContext(), "Could not execute HttpClient.execute" + e.getMessage());
        } catch (Exception e) {
            displayMessage(getApplicationContext(), "Could not save" + e.getMessage());
        } finally {
            Toast.makeText(getApplicationContext(), "Saving failed", Toast.LENGTH_LONG).show();
            httpGet.abort();
        }

    }

    private HttpGet getHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.3.5; en-us; ) " +
                "AppleWebKit/533.1+ (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        httpGet.setHeader("Content-Type", "text/html");
        httpGet.setHeader("Accept", "text/html");
        httpGet.setHeader("Accept-Language", "en-us,en;q=0.8");
        httpGet.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        return httpGet;
    }

    private ArrayList<String> downloadInnerLinks(String url, String title, int maxLinks) {
        ArrayList<String> innerLinks = new ArrayList<String>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements hrefElements = document.select("a[href]");
            int currentLinksCount = 0;
            for (Element hrefElement : hrefElements) {
                if (currentLinksCount > maxLinks) {
                    break;
                }
                String tempURL = hrefElement.attr("abs:href").toString();
                if (URLUtil.isValidUrl(tempURL) && URLUtil.isHttpUrl(tempURL) ||
                        URLUtil.isHttpsUrl(tempURL)) {
                    downloadData(tempURL, title, maxLinks);
                    innerLinks.add(url);
                }
                currentLinksCount++;
            }

        } catch (IOException e) {
            displayMessage(getApplicationContext(), "IOException while downlaoding inner link!");
        }
        return innerLinks;
    }

    private ArrayList<String> downloadInnerLinks(ArrayList<String> urls, String title, int maxLinks) {
        ArrayList<String> innerLinks = new ArrayList<String>();
        int currentLinksCount = 0;
        for (String url : urls) {
            if (currentLinksCount > maxLinks) {
                break;
            }
            innerLinks.addAll(downloadInnerLinks(url, title, maxLinks));
            currentLinksCount++;
        }
        return innerLinks;
    }
}
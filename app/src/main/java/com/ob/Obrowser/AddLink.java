package com.ob.Obrowser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class AddLink extends Activity {
    Elements links; // to store links
    String Fname;
    Elements clinks;
    Elements links_n;
    Elements imglinks;
    org.jsoup.nodes.Element link1;
    HttpClient httpclient;
    HttpGet httppost;
    static String _url;
    String burl;
    String _title;
    String responseBody = "";
    Document doc;
    int depth = 0;
    int read_dep;
    int mlinks;
    int i = 0; // variable appended for title name
    int j = 1;
    Spinner dept_spin;
    Spinner mlinks_spin;
    Bitmap bitmap;
    int duration = Toast.LENGTH_SHORT;
    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    String useragent = "Mozilla/5.0 (Linux; U; Android 2.3.5; en-us; ) AppleWebKit/533.1+ (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    boolean ns;
    int progressBarStatus = 0;
    ProgressDialog progressBar = null;
    public URLUtil validator = new URLUtil();

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.second);
        final Handler progressBarHandler = new Handler();
        //submit button
        Button sm = (Button) findViewById(R.id.button1);
        //depth input Spinner item
        dept_spin = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> dept_adapter = ArrayAdapter.createFromResource(
                this, R.array.depth_array, android.R.layout.simple_spinner_item);
        dept_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept_spin.setAdapter(dept_adapter);
        mlinks_spin = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> ml_adapter = ArrayAdapter.createFromResource(
                this, R.array.max_links_array, android.R.layout.simple_spinner_item);
        ml_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mlinks_spin.setAdapter(ml_adapter);
        sm.setOnClickListener(new View.OnClickListener() {
                                  public void onClick(View v) {
                                      progressBar = new ProgressDialog(v.getContext());
                                      progressBar.setCancelable(true);
                                      progressBar.setMessage("File downloading ...");
                                      progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                      progressBar.setProgress(0);
                                      progressBar.setMax(100);
                                      progressBar.show();
                                      new Thread(new Runnable() {
                                          public void run() {
                                              Looper.prepare();
                                              while (progressBarStatus < 100) {
                                                  // process some tasks
                                                  progressBarStatus = doWork();
                                                  // Update the progress bar
                                                  progressBarHandler.post(new Runnable() {
                                                      public void run() {
                                                          //j  progressDialog.setProgress(progressBarStatus);
                                                          if (progressBarStatus >= 100) {    // close the progress bar dialog
                                                              progressBar.dismiss();
                                                          }
                                                      }
                                                  });
                                              }
                                              // ok, file is downloaded,
                                          }
                                      }).start();

                                  }// end of onClick
                              } //end of View.onClickListener
        );// end of set.onClickListener
    }//end of onCreate

    final int doWork() throws NullPointerException, ParseException {
        final EditText link = (EditText) findViewById(R.id.editText2);
        final EditText title = (EditText) findViewById(R.id.editText1);
        read_dep = Integer.parseInt(String.valueOf(dept_spin.getSelectedItem()));
        mlinks = Integer.parseInt(String.valueOf(mlinks_spin.getSelectedItem()));
        _url = link.getText().toString();
        _title = title.getText().toString();
        ListDBHelper li = new ListDBHelper(AddLink.this);
        li.additem(_title);
        ns = isNetworkAvailable();
        if (ns == false) {
            Log.v("the value of ns", "" + ns);
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getApplicationContext(), "No Internet Connection\nplease try again...", duration).show();
            Intent i = new Intent(AddLink.this, ObrowserActivity.class);
            AddLink.this.startActivity(i);
        } else {
            try {
                if ((_url.equals(null)) == true || (_title.equals(null)) == true)
                    throw new java.lang.NullPointerException();
                else if (_url.startsWith("http://") == false && _url.startsWith("https://") == false)
                    _url = "http://" + _url;
                Log.v("url and title empty", "" + ns);
                Log.v("Network state", "" + ns);
                Log.v("first page", _url);
                Toast.makeText(getApplicationContext(), _title + "\n" + _url, duration).show();
                down(_url);
                Log.v("read_dep value is ", "" + read_dep);
                Log.v("mlinks value is ", "" + mlinks);
                if (read_dep > 0) {
                    depth = 1;
                    Log.v("depth value in first call ", "" + depth);
                    getlinks(_url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Toast.makeText(getApplicationContext(), "Please Enter Url and title,and try...", duration).show();
                AddLink.this.finish();
            }

        }
        return 100;
    }

    public void down(String url) throws ParseException, IOException {
        if (url.startsWith("https://")) {
            Log.v("in https", "");
            NetworkManager nw = new NetworkManager();
            httpclient = nw.getClient();
        } else {
            Log.v("in http", "");
            httpclient = new DefaultHttpClient();
        }
        httppost = new HttpGet(url);
        HttpResponse response = null;
        //httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, useragent);
        httppost.setHeader("User-Agent", useragent);
        httppost.setHeader("Content-Type", "text/html");
        httppost.setHeader("Accept", "text/html");
        httppost.setHeader("Accept-Language", "en-us,en;q=0.8");
        httppost.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        try {
            Log.v("posted url", url);
            response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() != 200 && response.equals(null)) {
                Log.v("Invalid http post", "response status code" + response.getStatusLine().getStatusCode());
                AddLink.this.finish();
            } else {
                responseBody = EntityUtils.toString(response.getEntity());
                Document doc2 = Jsoup.parse(responseBody);
                clinks = doc2.select("link[href]");
//					imglinks=doc2.select("img[src]");
//					for(Element img :imglinks)
//					{
//						BitmapFactory.Options bmOptions;
//						   bmOptions = new BitmapFactory.Options();
//						   bmOptions.inSampleSize = 1;
//						   String url4=img.attr("abs:src");
//						   if(url4.startsWith("http://")==false&& (url4.equals("null"))==false)
//							{
//								url4="http://"+_url+url4;	
//							}
//						   Log.v("url4",url4);
//						   downloadimgs(url4,bmOptions);	
//					}
                Log.v("jsoup getting no. of css pages", "" + clinks.size());
                for (Element clink1 : clinks) {
                    String url3 = clink1.attr("abs:href");
                    if (url3.length() < 4) {
                        break;
                    }
                    Log.v("css url1", "css url1" + url3 + "hi");
                    if (url3.startsWith("http://") != true && (url3.equals("null")) != true) {
                        url3 = "http://" + _url + url3;
                    }
                    Log.v("css url2", "css url2: " + url3);
                    HttpGet hg = new HttpGet(url3);
                    HttpResponse cresp = httpclient.execute(hg);
                    String css1 = EntityUtils.toString(cresp.getEntity());
                    Log.v("css code:", css1);
                    Document doct = Jsoup.parse(responseBody);
                    doct.head().appendElement("style").attr("type", "text/css").text(css1);
                    responseBody = doct.html();
                    Log.v("response Body after replacement", responseBody);
                }
            }
            Webdata wd = new Webdata();
            wd.setData1(responseBody);
            wd.setUrl1(url);
            wd.setTitle1(_title + i);
            i++;
            Intent sendData = new Intent(getApplicationContext(), insertdata.class);
            sendData.putExtra("webdata", wd);
            startActivity(sendData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getlinks(String n_url) throws IOException {
        try {
            doc = Jsoup.connect(n_url).get();
            links = doc.select("a[href]");
            int n = links.size();
            Log.v("jsoup getting no. of links", "" + n);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        for (org.jsoup.nodes.Element link1 : links) {
            //Log.v("trim(link1.text())",link1.attr("abs:href").toString());
            try {
                // Execute HTTP Post Request
                if (URLUtil.isValidUrl(link1.attr("abs:href").toString()) && URLUtil.isHttpUrl(link1.attr("abs:href").toString()) || URLUtil.isHttpsUrl(link1.attr("abs:href").toString()))
                    down(link1.attr("abs:href").toString());
                Log.v("j ", "" + j);
                j++;
                if (j > mlinks) {
                    j = 0;
                    break;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.v("depth", "" + depth);
        if (read_dep > 1 && depth < 2) {
            depth = 2;
            downsuburl(links);
        }
    }

    public void downsuburl(Elements slinks) throws IOException {
        int k = 1;
        Log.v("depth value in suburl ", "" + depth);
        for (Element slink1 : slinks) {
            getlinks(slink1.attr("abs:href").toString());
            Log.v("sublink", slink1.attr("abs:href").toString());
            k++;
            if (k > mlinks) {
                k = 0;
                break;
            }
        }
        depth++;
    }

    public void downloadimgs(String iurl, BitmapFactory.Options options) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(iurl);
        URLConnection conn = url.openConnection();
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            Log.v("image links response code", "" + httpConn.getResponseCode());
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
                Log.v("input stream", "completed");
                OutputStream outStream = null;
                Log.v("url file name", url.getFile());
                File file = new File(extStorageDirectory, url.getFile());
                Log.v("created file", "" + file);
                outStream = new FileOutputStream(file);
                Log.v("image inserted", file.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Invalid url", 1000).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo(); // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        return false;
    }

    @SuppressWarnings("static-access")
    public String getBaseUrl() {
        return this._url;
    }
}
// end of Activity

        	

package com.ob.Obrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class insertdata extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Webdata wd = (Webdata) i.getSerializableExtra("webdata");
        setContentView(R.layout.main);
        if (wd.getTitle1() != null && wd.getData1() != null) {
            Log.v("title", wd.getTitle1());
            Log.v("data", "in insertdata()");
            datab db1 = new datab(this);
            String title1 = wd.getTitle1();
            String url = wd.getUrl1();
            String data = wd.getData1();
            db1.addlink(title1, url, data);
            Log.v("data", "inserted");
            finish();
        } else {
            Log.v("in insertdata class", " no webdata");
        }
    }
}
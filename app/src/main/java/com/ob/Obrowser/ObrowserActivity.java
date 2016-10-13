package com.ob.Obrowser;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ObrowserActivity extends ListActivity {
    /**
     * Called when the activity is first created.
     */
    private static final int menu1 = Menu.FIRST;
    private static final int menu2 = Menu.FIRST + 1;
    private static final int menu3 = Menu.FIRST + 2;
    private String[] values;
    private List<String> list;
    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListDBHelper li = new ListDBHelper(ObrowserActivity.this);
        list = new ArrayList<String>();
        list = li.rlink();
        values = new String[list.size()];
        int i = 0;
        for (String s : list) {
            Log.v("in obrowser", s);
            if (s.equals(null))
                break;
            values[i] = s;
            if (i > list.size())
                break;
            i++;
        }

        // if(values.length==0)
        // {
        // setContentView(R.layout.main);
        // }

        registerForContextMenu(getListView());
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values));
        lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.v("selected item on create",
                        (String) parent.getItemAtPosition(position));
                Intent sendtitle = new Intent(getApplicationContext(),
                        webdisp.class);
                sendtitle.putExtra("title",
                        (String) parent.getItemAtPosition(position) + 0);
                startActivity(sendtitle);
            }
        });
        if (values.length == 0) {
            help();
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                Toast.makeText(this, values[(int) info.id] + " is deleted",
                        Toast.LENGTH_SHORT).show();
                datab db = new datab(this);
                db.delete(values[(int) info.id]);
                ListDBHelper ldb = new ListDBHelper(this);
                ldb.delete(values[(int) info.id]);
                repopulateList();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, menu1, 0, "Add page");
        menu.add(0, menu2, 0, "Quit");
        menu.add(0, menu3, 0, "Help");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case menu1:
                addLink();
                break;
            case menu3:
                help();
                break;
            case menu2:
                finish();
                break;
        }
        return true;
    }

    public void addLink() {
        //Intent i = new Intent(ObrowserActivity.this, AddLink.class);
        Intent i = new Intent(ObrowserActivity.this, AddLinkActivity.class);
        ObrowserActivity.this.startActivity(i);

    }

    public void onResume() {
        super.onResume();
        repopulateList();
    }

    public void repopulateList() {
        ListDBHelper li = new ListDBHelper(ObrowserActivity.this);
        list = new ArrayList<String>();
        list = li.rlink();
        values = new String[list.size()];
        int i = 0;
        for (String s : list) {
            Log.v("in obrowser", s);
            if (s.equals(null))
                break;
            values[i] = s;
            if (i > list.size())
                break;
            i++;
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        lv.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.v("selected item on create",
                        (String) parent.getItemAtPosition(position));
                Intent sendtitle = new Intent(getApplicationContext(),
                        webdisp.class);
                sendtitle.putExtra("title",
                        (String) parent.getItemAtPosition(position) + "0");
                Log.v("OA: Opening title:", (String) parent.getItemAtPosition(position) + "0");
                startActivity(sendtitle);
            }
        });
    }

    public void help() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Help");

        // Setting Dialog Message
        alertDialog.setMessage("Press menu to start with the application\nPress \"Menu -> Add page\" option to download a webpage");
        alertDialog.setCanceledOnTouchOutside(true);
        // Showing Alert Message
        alertDialog.show();

    }
}
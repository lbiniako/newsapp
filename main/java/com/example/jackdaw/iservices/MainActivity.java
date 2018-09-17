package com.example.jackdaw.iservices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    private ImageView iv;
    TextView msg;
    String msgUrl;
    //private LinearLayout ll;

    // URL to get contacts JSON
    private String url = "https://iservices.gr/wp-json/wp/v2/posts?_embed&per_page=15";

    ArrayList<HashMap<String, String>> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        iv = (ImageView) findViewById(R.id.post_image);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                lv.smoothScrollToPosition(0);
            }
        });
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetNews extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Ενημέρωση ειδήσεων...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null)
            {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    //JSONArray news = jsonObj.getJSONArray("posts");
                    JSONArray news = new JSONArray(jsonStr);
                    // looping through All News
                    for (int i = 0; i < news.length(); i++) {
                        JSONObject jo = news.getJSONObject(i);

                        String date = jo.optString("date");

                        String link = jo.optString("link");

                        JSONObject innerTitle = jo.getJSONObject("title");
                        String title = innerTitle.optString("rendered");

                        //Get post image Url
                        JSONObject featureImage = jo.getJSONObject("_embedded");
                        JSONArray featureImageUrl = featureImage.getJSONArray("wp:featuredmedia");
                        JSONObject featureImageObj = featureImageUrl.getJSONObject(0);
                        String source_url = featureImageObj.optString("source_url");
                        if(source_url.isEmpty())
                        {
                            source_url = "drawable://" + R.drawable.placeholder;
                        }

                        // tmp hash map for single news item
                        HashMap<String, String> newsItem = new HashMap<>();

                        // adding each child node to HashMap key => value
                        newsItem.put("title", title);
                        newsItem.put("date", date);
                        newsItem.put("link", link);
                        newsItem.put("source_url", source_url);
                       // contact.put("email", email);
                        //contact.put("mobile", mobile);

                        // adding contact to contact list
                        newsList.add(newsItem);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Σφάλμα με τη σύνδεση στο server. Παρακαλώ δοκιμάστε ξανά!", Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new customAdapter(MainActivity.this, newsList, R.layout.list_item, new String[]{"date","link","title","source_url"}, new int[]{R.id.date, R.id.link, R.id.title, R.id.post_image});

            lv.setAdapter(adapter);
        }

    }


    public void openWVA(View v)
    {
        Intent intent = new Intent(MainActivity.this, webviewActivity.class);
        LinearLayout ll = (LinearLayout)v.getParent();
        msg = (TextView) ll.findViewById(R.id.link);

        msgUrl = msg.getText().toString();
        intent.putExtra("message_key",msgUrl);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item= menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.news_cat, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                switch (selectedItemText)
                {
                    case "Όλα τα νέα":
                        newsList.clear();
                        lv.setAdapter(null);
                        url = "https://iservices.gr/wp-json/wp/v2/posts?_embed&per_page=15";
                        new GetNews().execute();
                        break;
                    case "Νέα της iServices":
                        newsList.clear();
                        lv.setAdapter(null);
                        url = "https://iservices.gr/wp-json/wp/v2/posts?_embed&categories=10&per_page=15";
                        new GetNews().execute();
                        break;
                    case "Νέα του διαδικτύου":
                        newsList.clear();
                        lv.setAdapter(null);
                        url = "https://iservices.gr/wp-json/wp/v2/posts?_embed&categories=9&per_page=15";
                        new GetNews().execute();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }
}
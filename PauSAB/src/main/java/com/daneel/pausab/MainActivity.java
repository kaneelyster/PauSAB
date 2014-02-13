package com.daneel.pausab;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends Activity {

    //TODO: Implement SharedPreferences getters/setters for

    private static final int SHOW_PREFERENCES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Start the  service
    public void startNewService(View view) {

        Intent serviceIntent = new Intent(this, PersistentAgent.class);
        serviceIntent.putExtra("Action", "Start");
        startService(serviceIntent);
    }

    // Stop the  service
    public void stopNewService(View view) {

        stopService(new Intent(this, PersistentAgent.class));
    }

    public void test(View view){
        //refreshDownloadStatus();
        //Toast.makeText(this, "No testing yet", Toast.LENGTH_LONG).show();
        testDownloadPause();
    }

    public void testDownloadPause(){
        String statusText="";
        pauseDownloads status = new pauseDownloads();
        try {
            statusText = status.execute(new String[] {""}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, statusText, Toast.LENGTH_LONG).show();
    }



    public void testDowloadStatus(){
        String statusText="";
        refreshDownloadStatus status = new refreshDownloadStatus();
        try {
            statusText = status.execute(new String[] {""}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, statusText, Toast.LENGTH_LONG).show();
    }

    public void testConnectivity(){
        String online = isOnline()?"ONLINE":"OFFLINE";
        Toast.makeText(this, online, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private class pauseDownloads extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...  urls) {
            String statusText;
            URL url;
            statusText="";
            try{
                String statusFeed = "http://192.168.1.18:8080/api?mode=config&name=set_pause&value=5&apikey=6e578e0f2667a0977a72d04c3a340950";
                url = new URL(statusFeed);

                URLConnection connection;
                connection = url.openConnection();

                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    statusText="OK";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return statusText;
        }
    }

    private class refreshDownloadStatus extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String...  urls) {
        String statusText = "Nothing";
        String speedText = "0 K/s";
        String timeleftText = "";
        //Get the XML

        URL url;
        try{
            String statusFeed = "";
            url = new URL(statusFeed);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                InputStream in = httpConnection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                //Parse the feed
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                NodeList nl = docEle.getElementsByTagName("state");
                if (nl != null && nl.getLength() > 0){
                    Element queue = (Element)nl.item(0);
                    statusText = queue.getTextContent();
                }
                nl = docEle.getElementsByTagName("speed");
                if (nl != null && nl.getLength() > 0){
                    Element queue = (Element)nl.item(0);
                    speedText = queue.getTextContent();
                }
                nl = docEle.getElementsByTagName("timeleft");
                if (nl != null && nl.getLength() > 0){
                    Element queue = (Element)nl.item(0);
                    timeleftText = queue.getTextContent();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return statusText + " at " + speedText + "/s [ETA " + timeleftText + "]";
        //return "nothing";
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? SettingsActivity.class : FragmentSettings.class;
                Intent i = new Intent(this, c);
                startActivityForResult(i, SHOW_PREFERENCES);
                return true;
            case R.id.action_exit:
                //TODO: Exit ActionBar item clicked.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
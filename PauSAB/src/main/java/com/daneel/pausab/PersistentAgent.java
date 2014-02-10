package com.daneel.pausab;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
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
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by daneel on 2014/01/13.
 */
public class PersistentAgent extends Service {

    public static String ACTION_DURATION1 = "com.daneel.pausab.DURATION1";
    public static String ACTION_DURATION2 = "com.daneel.pausab.DURATION2";
    public static String ACTION_DURATION3 = "com.daneel.pausab.DURATION3";
    public static String EXTRA_NOTIFICATIONACTION = "com.daneel.pausab.NOTIFICATIONACTION";
    public Preferences preferences;

    public PersistentAgent() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = new Preferences();
        String statusText = "";
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.getString("Pause") != null){
                String action = bundle.getString("Pause");
                if (action.equals(ACTION_DURATION1)){
                    pauseDownloads(preferences.getDuration1());
                }
                else if (action.equals(ACTION_DURATION2)){
                    pauseDownloads(preferences.getDuration2());
                }
                else if (action.equals(ACTION_DURATION3)){
                    pauseDownloads(preferences.getDuration3());
                }
                //TODO: Check flow/handling on clicks + send to config screen on main notification intent click

            }
            else if (bundle.getString("Action").equals("Start")) {
                refreshDownloadStatus status = new refreshDownloadStatus();
                try {
                    statusText = status.execute(new String[]{""}).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                createNotification(statusText);
//                return Service.START_STICKY;
            }

        }

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service was created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** method for clients */
    public void pauseDownloads(int duration) {
        String statusText="";
        DownloadPause status = new DownloadPause();
        try {
            statusText = status.execute(String.valueOf(duration)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, statusText, Toast.LENGTH_LONG).show();
    }



    public void createNotification(String statusText) {
        // Prepare intents which are triggered if the notification is selected

        Intent mainIntent = new Intent(this, MyBroadcastReceiver.class);
        mainIntent.setAction(EXTRA_NOTIFICATIONACTION);
        PendingIntent pMainIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, mainIntent, 0);

        Intent pauseIntent1 = new Intent(this, MyBroadcastReceiver.class);
        pauseIntent1.setAction(ACTION_DURATION1);
        PendingIntent pIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, pauseIntent1, 0);

        Intent pauseIntent2 = new Intent(this, MyBroadcastReceiver.class);
        pauseIntent2.setAction(ACTION_DURATION2);
        PendingIntent pIntent2 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, pauseIntent2, 0);

        Intent pauseIntent3 = new Intent(this, MyBroadcastReceiver.class);
        pauseIntent3.setAction(ACTION_DURATION3);
        PendingIntent pIntent3 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, pauseIntent3, 0);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.sablogosmall96);
        // Build notification
        // Actions are just fake
        Notification notification = new Notification.Builder(this)
                .setContentTitle(statusText)
                        //.setContentText(statusText)
                        //.setContentInfo("ContentInfo")
                .setTicker("Notification")
                .setContentIntent(pMainIntent)
                .setSmallIcon(R.drawable.sablogosmall)
                .setLargeIcon(bm)
                .addAction(R.drawable.pausebmp32, "5 Min", pIntent1)
                .addAction(R.drawable.pausebmp32, "15 Min", pIntent2)
                .addAction(R.drawable.pausebmp32, "30 Min", pIntent3)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // adding LED lights to notification
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notificationManager.notify(0, notification);
    }


    private class DownloadPause extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...  urls) {
            String statusText;
            URL url;
            String pauseDuration = urls[0];
            statusText="";
            try{
                String statusFeed = "http://"+
                                    preferences.getSERVER_IP()+
                                    ":"+
                                    preferences.getSERVER_PORT()
                                            +"/api?mode=config&name=set_pause&value="+
                                    pauseDuration+
                                    "&apikey="+
                                    preferences.getAPI_KEY();
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
        protected String doInBackground(String... urls) {
            String statusText = "Nothing";
            String speedText = "0 K/s";
            String mbLeftText = "";
            //Get the XML

            URL url;
            try {
                String statusFeed = getString(R.string.SERVERADDRESS);
                url = new URL(statusFeed);

                URLConnection connection;
                connection = url.openConnection();

                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    //Parse the feed
                    Document dom = db.parse(in);
                    Element docEle = dom.getDocumentElement();

                    NodeList nl = docEle.getElementsByTagName("state");
                    if (nl != null && nl.getLength() > 0) {
                        Element queue = (Element) nl.item(0);
                        statusText = queue.getTextContent();
                    }
                    nl = docEle.getElementsByTagName("speed");
                    if (nl != null && nl.getLength() > 0) {
                        Element queue = (Element) nl.item(0);
                        speedText = queue.getTextContent();
                    }
                    nl = docEle.getElementsByTagName("mbleft");
                    if (nl != null && nl.getLength() > 0) {
                        Element queue = (Element) nl.item(0);
                        mbLeftText = queue.getTextContent();
                        mbLeftText = mbLeftText.substring(0, mbLeftText.indexOf("."));
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

            return statusText + " at " + speedText + "/s | " + mbLeftText + " MB";
            //return "nothing";
        }
    }
}

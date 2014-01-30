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
    public static String EXTRA_PAUSEDURATION = "com.daneel.pausab.PAUSEDURATION";
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
                pauseDownloads();
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createNotification(String statusText) {
        // Prepare intents which are triggered if the notification is selected

        Intent mainIntent = new Intent(this, MyBroadcastReceiver.class);
        mainIntent.putExtra(PersistentAgent.EXTRA_NOTIFICATIONACTION, "MainActivity");
        PendingIntent pMainIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, mainIntent, 0);

        Intent pauseIntent1 = new Intent(this, MyBroadcastReceiver.class);
        pauseIntent1.putExtra(PersistentAgent.EXTRA_PAUSEDURATION, "5");
        PendingIntent pIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, pauseIntent1, 0);

        Intent pauseIintent2 = new Intent(this, MyBroadcastReceiver.class);
        pauseIintent2.putExtra(PersistentAgent.EXTRA_PAUSEDURATION, "15");
        PendingIntent pIntent2 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, pauseIintent2, 0);

        Intent pauseIntent3 = new Intent(this, MyBroadcastReceiver.class);
        pauseIntent3.putExtra(PersistentAgent.EXTRA_PAUSEDURATION, "30");
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
    public void pauseDownloads(){
        String statusText="";
        DownloadPause status = new DownloadPause();
        try {
            statusText = status.execute(new String[] {""}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, statusText, Toast.LENGTH_LONG).show();
    }

    private class DownloadPause extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...  urls) {
            String statusText;
            URL url;
            String pauseDuration = "5";
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

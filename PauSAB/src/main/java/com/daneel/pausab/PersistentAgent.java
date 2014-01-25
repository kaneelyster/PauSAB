package com.daneel.pausab;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

    public PersistentAgent(){
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        createNotification(statusText);
        return Service.START_STICKY;
    }

    @Override
    public void onCreate(){
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
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent tweetIntent = new Intent();
        PendingIntent pTweetIntent = PendingIntent.getActivity(this, 0, tweetIntent, 0);

        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.sablogo);
        // Build notification
        // Actions are just fake
        Notification notification = new Notification.Builder(this)
                .setContentTitle("PauSAB")
                .setContentText(statusText)
                .setTicker("Notification")
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.sablogo)
                //.setLargeIcon(bm)
                .addAction(R.drawable.ic_launcher, "5 Min", pIntent)
                .addAction(R.drawable.ic_launcher, "15 Min", pIntent)
                .addAction(R.drawable.ic_launcher, "30 Min", pIntent)
                .setOngoing(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // adding LED lights to notification
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notificationManager.notify(0, notification);
    }

    private class refreshDownloadStatus extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...  urls) {
            String statusText = "Nothing";
            String speedText = "0 K/s";
            String mbLeftText = "";
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
                    nl = docEle.getElementsByTagName("mbleft");
                    if (nl != null && nl.getLength() > 0){
                        Element queue = (Element)nl.item(0);
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

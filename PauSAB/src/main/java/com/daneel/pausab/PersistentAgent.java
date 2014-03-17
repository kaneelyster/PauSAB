package com.daneel.pausab;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
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
    public static String ACTION_MAINACTIVITY = "com.daneel.pausab.MAINACTIVITY";
    public PreferencesStore preferences;

    public PersistentAgent() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = PreferencesStore.getInstance(getApplicationContext());
        String statusText = "";
        Bundle bundle = intent.getExtras();
        boolean connectivity = testConnectivity();
        if (bundle != null) {
            if (bundle.getString("Pause") != null && connectivity){
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
            }
            else if (bundle.getString("Action").equals("Start")) {
                if (connectivity){
                    refreshDownloadStatus status = new refreshDownloadStatus();
                    try {
                        statusText = status.execute(new String[]{""}).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    createNotification(statusText);
                }
                else{
                    clearNotification();
                }

                setRecurringAlarm(intent);
            }
        }
        return Service.START_STICKY;
    }

    public void setRecurringAlarm(Intent intent) {
        Calendar cal = Calendar.getInstance();
        //Intent alarmIntent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarm.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis()+preferences.getRefreshIntervalMinutes()*60*1000, preferences.getRefreshIntervalMinutes()*60*1000, pintent);
        alarm.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis()+preferences.getRefreshIntervalSeconds()*1000, preferences.getRefreshIntervalMinutes()*60*1000, pintent);

        preferences.incUpdateCount();
    }

    public void clearRecurringAlarm(){
        Intent serviceIntent = new Intent(this, PersistentAgent.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, serviceIntent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service was created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        clearNotification();
        clearRecurringAlarm();
    }

    public void clearNotification() {
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(0);
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

    public boolean testConnectivity(){
        String statusText="";
        HostReachability hostStatus = new HostReachability();
        try {
            statusText = hostStatus.execute(new String[] {""}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return statusText.equals(String.valueOf(true));
    }

    private class HostReachability extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...  urls) {
            return String.valueOf(isNetworkOnline() && isServerOnline());
        }
    }

    public boolean isServerOnline() {
        boolean exists = false;
        PreferencesStore preferences;

        preferences = PreferencesStore.getInstance(getApplicationContext());

        try {
            InetAddress serverAddr = InetAddress.getByName(preferences.getSERVER_IP());
            SocketAddress sockaddr = new InetSocketAddress(serverAddr, Integer.decode(preferences.getSERVER_PORT()));
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 2000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);
            exists = true;
        }
        catch (Exception e) {
        }
        return exists;
    }

    public boolean isNetworkOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void createNotification(String statusText) {
        // Prepare intents which are triggered if the notification is selected

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setAction(ACTION_MAINACTIVITY);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(mainIntent);

        //PendingIntent pMainIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, mainIntent, 0);
        PendingIntent pMainIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

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
        Notification notification = new Notification.Builder(this)
                .setContentTitle(statusText)
                        //TODO: Refer to http://developer.android.com/guide/topics/ui/notifiers/notifications.html#HandlingNotifications
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
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;

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
                url = new URL(PreferencesStore.getInstance(getApplicationContext()).getPauseURL(pauseDuration));

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
            String statusText = "Unreachable";
            String speedText = "0 K/s";
            String mbLeftText = "";
            String timeLeftText = "";

            //Get the XML
            URL url;
            try {
                url = new URL(PreferencesStore.getInstance(getApplicationContext()).getStatusURL());

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
                    nl = docEle.getElementsByTagName("pause_int");
                    if (nl != null && nl.getLength() > 0) {
                        Element queue = (Element) nl.item(0);
                        timeLeftText = queue.getTextContent();
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

            if (statusText.equals("Paused")){
                return "Paused for " + timeLeftText+ " | " + mbLeftText + " MB";
            }
            else if (statusText.equals("Downloading") || statusText.equals("IDLE")){
                return statusText + " at " + speedText + "/s | " + mbLeftText + " MB";
            }
            else{
                return "Unreachable";
            }
        }
    }
}

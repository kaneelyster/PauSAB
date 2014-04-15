//package com.daneel.pausab;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.app.Activity;
//import android.content.Intent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//
//public class PauSAB extends Activity {
//
//    public PreferencesStore preferences;
//
//    private static final int SHOW_PREFERENCES = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        preferences = PreferencesStore.getInstance(getApplicationContext());
//
//        String action = getIntent().getAction();
//
//        if (action != null) {
//            if (action.equals("com.daneel.pausab.MAINACTIVITY")){
//                showPreferences();
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    // Start the  service
//    public void startNewService(View view) {
//
////        Intent serviceIntent = new Intent(this, PersistentAgent.class);
////        serviceIntent.putExtra("Action", "Start");
////        startService(serviceIntent);
//        Intent serviceIntent = new Intent(this, ServiceLauncher.class);
//        startActivity(serviceIntent);
//    }
//
//    // Stop the  service
//    public void stopNewService(View view) {
//        stopService(new Intent(this, PersistentAgent.class));
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                showPreferences();
//                return true;
//            case R.id.action_exit:
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    public void showPreferences(){
//        Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? SettingsActivity.class : FragmentSettings.class;
//        Intent i = new Intent(this, c);
//        startActivityForResult(i, SHOW_PREFERENCES);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
////-----------------------------------------------------------------------------------------------------------//
////--------------------- ALL CODE BELOW THIS IS PURELY FOR TESTING/EXPERIMENTAL PURPOSES ---------------------//
////-----------------------------------------------------------------------------------------------------------//
///*
//
//    public void test(View view){
//        //refreshDownloadStatus();
//        //Toast.makeText(this, "No testing yet", Toast.LENGTH_LONG).show();
//        testConnectivity();
//    }
//
//    public boolean testServerConnectivity() {
//        boolean exists = false;
//        PreferencesStore preferences;
//
//        preferences = PreferencesStore.getInstance(getApplicationContext());
//
//        try {
//            InetAddress serverAddr = InetAddress.getByName(preferences.getSERVER_IP());
//            SocketAddress sockaddr = new InetSocketAddress(serverAddr, Integer.decode(preferences.getSERVER_PORT()));
//            // Create an unbound socket
//            Socket sock = new Socket();
//
//            // This method will block no more than timeoutMs.
//            // If the timeout occurs, SocketTimeoutException is thrown.
//            int timeoutMs = 2000;   // 2 seconds
//            sock.connect(sockaddr, timeoutMs);
//            exists = true;
//        }
//        catch (Exception e) {
//        }
//        return exists;
//    }
//
//    public void testConnectivity(){
//        String statusText="";
//        CheckConnectivity status = new CheckConnectivity();
//        try {
//            statusText = status.execute(new String[] {""}).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        Toast.makeText(this, statusText, Toast.LENGTH_LONG).show();
//    }
//
//    private class CheckConnectivity extends AsyncTask<String, Void, String> {
//        String connectivity = "No Network";
//        String serverConnectivity = "Server Unreachable";
//        @Override
//        protected String doInBackground(String...  urls) {
//
//            connectivity = isOnline()?"Network OK":"No Network";
//            serverConnectivity = testServerConnectivity()?"Server OK":"Server Unreachable";
//
//             return connectivity + "\n" + serverConnectivity;
//        }
//    }
//
//    public void testNetworkConnectivity(){
//        String online = isOnline()?"ONLINE":"OFFLINE";
//        Toast.makeText(this, online, Toast.LENGTH_LONG).show();
//    }
//
//
//
//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//            return true;
//        }
//        return false;
//    }
//
//    private class pauseDownloads extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String...  urls) {
//            String statusText;
//            URL url;
//            statusText="";
//            try{
//                String statusFeed = "http://192.168.1.18:8080/api?mode=config&name=set_pause&value=5&apikey=6e578e0f2667a0977a72d04c3a340950";
//                url = new URL(statusFeed);
//
//                URLConnection connection;
//                connection = url.openConnection();
//
//                HttpURLConnection httpConnection = (HttpURLConnection) connection;
//                int responseCode = httpConnection.getResponseCode();
//
//                if (responseCode == HttpURLConnection.HTTP_OK){
//                    statusText="OK";
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return statusText;
//        }
//    }
//*/
//
//}
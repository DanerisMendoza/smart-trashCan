package com.smarttrashcanmobile;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyForegroundService extends Service {

    String globaUrl = "http://192.168.1.4/smart-trashCan/php/";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
        }
        NotificationChannel finalChannel = channel;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            String url = globaUrl + "selectData.php";
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            StringRequest request = new StringRequest(Request.Method.POST, url,
                                    new com.android.volley.Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Notification.Builder notification = null;
                                            try {
                                                JSONObject respObj = new JSONObject(response);
                                                String data = respObj.getString("data");
                                                String status = "";
                                                if (data.equals("1")) {
                                                    status = "Empty";
                                                } else if (data.equals("2")) {
                                                    status = "Half-Full";
                                                }
                                                else {
                                                    status = "Full!";
                                                }
                                                //notification
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        getSystemService(NotificationManager.class).createNotificationChannel(finalChannel);
                                                    }
                                                }

                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                    notification = new Notification.Builder(getApplicationContext(), CHANNELID)
                                                            .setContentText("Smart Trash Can")
                                                            .setContentTitle("Status: "+status)
                                                            .setSmallIcon(R.drawable.ic_launcher_background);
                                                }
                                                startForeground(1001, notification.build());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("post", "smarttrashcan");
                                    return params;
                                }
                            };
                            queue.add(request);
                            Log.e("Service", "Service is running...");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class MyBackgroundService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                Log.e("Service", "Service is running...");
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            ).start();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}

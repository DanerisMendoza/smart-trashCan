package com.smarttrashcanmobile;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyForegroundService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //check if there is internet
        // Get the connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Check if network is available
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (!(networkInfo != null && networkInfo.isConnected())) {
                Toast.makeText(this, "Please Connect to the internet!", Toast.LENGTH_SHORT).show();
                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                    if (processInfo.processName.equals(getApplicationContext().getApplicationInfo().processName)) {
                        activityManager.killBackgroundProcesses(processInfo.processName);
                        android.os.Process.killProcess(processInfo.pid);
                        break;
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please Connect to the internet!", Toast.LENGTH_SHORT).show();
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                if (processInfo.processName.equals(getApplicationContext().getApplicationInfo().processName)) {
                    activityManager.killBackgroundProcesses(processInfo.processName);
                    android.os.Process.killProcess(processInfo.pid);
                    break;
                }
            }
        }
        //Mobile to php
        GlobalClass globalClass = (GlobalClass) getApplicationContext();
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
                () -> {
                    while (true) {
                        String url = "http://ucc-csd-bscs.com/STC/smart-trashCan/selectData.php";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        @SuppressLint({"ResourceType", "SetTextI18n"}) StringRequest request = new StringRequest(Request.Method.POST, url,
                                response -> {
                                    Notification.Builder notification = null;
                                    try {
                                        JSONObject respObj = new JSONObject(response);
                                        String data = respObj.getString("data");
                                        String mode = respObj.getString("mode");
                                        String status;
                                        if (data.equals("1")) {
                                            status = "Empty";
                                        } else if (data.equals("2")) {
                                            status = "Half-Full";
                                        }
                                        else {
                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notifysound);
                                            mp.start();
                                            status = "Full!";
                                        }
                                        globalClass.getTextViewStatus().setText("Status: "+status);
                                        globalClass.getTextViewMode().setText("Mode: "+mode);
                                        //notification
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                getSystemService(NotificationManager.class).createNotificationChannel(finalChannel);
                                            }
                                        }

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            notification = new Notification.Builder(getApplicationContext(), CHANNELID)
                                                    .setContentText("Smart Trash Can")
                                                    .setContentTitle("Status: "+status)
                                                    .setSmallIcon(R.raw.bg);
                                        }
                                        startForeground(1001, notification.build());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }, error -> Toast.makeText(getApplicationContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show()) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
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
                    () -> {
                        while (true) {
                            Log.e("Service", "Service is running...");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
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

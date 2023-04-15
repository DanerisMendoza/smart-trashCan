package com.smarttrashcanmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loadingPB;
    TextView textViewStatus, textViewMode;
    Button buttonMode;
    String globaUrl = "http://192.168.1.5/smart-trashCan/php/";
    String modeGlobal = "";
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingPB = findViewById(R.id.idLoadingPB);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewMode = findViewById(R.id.textViewMode);
        buttonMode = findViewById(R.id.buttonMode);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                readDb();
            }
        }, 0, 1000);

        buttonMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private Handler mHandler = new Handler();

    private void readDb() {
        String url = globaUrl + "selectData.php";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject respObj = new JSONObject(response);
                            String data = respObj.getString("data");
                            String mode = respObj.getString("mode");
                            modeGlobal = mode;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String status = "";
                                    if (data.equals("1")) {
                                        status = "Empty";
                                    }
                                    if (data.equals("2")) {
                                        status = "Half-Full";
                                    }
                                    else if (data.equals("3")) {
                                        NotificationCompat.Builder builder =
                                                new NotificationCompat.Builder(MainActivity.this, "channel_id")
                                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                                        .setContentTitle("Smart Trash Can")
                                                        .setContentText("Your trashcan is FULL!")
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                        NotificationManagerCompat notificationManager =
                                                NotificationManagerCompat.from(MainActivity.this);

                                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        notificationManager.notify(0, builder.build());
                                        status = "Full";
                                    }
                                    textViewStatus.setText("Status: " + status);
                                    textViewMode.setText("mode: " + mode);

                                }
                            });
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
    }


    private void changeMode() {
        String url = globaUrl + "changeMode.php";
        loadingPB.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONObject respObj = new JSONObject(response);


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
                if (modeGlobal.equals("ON")) {
                    modeGlobal = "OFF";
                } else {
                    modeGlobal = "ON";
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("post", "smarttrashcan");
                params.put("mode", modeGlobal);
                return params;
            }
        };
        queue.add(request);
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

}
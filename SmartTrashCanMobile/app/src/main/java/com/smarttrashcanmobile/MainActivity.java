package com.smarttrashcanmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button buttonMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalClass globalClass = (GlobalClass) getApplicationContext();
        globalClass.setTextViewStatus(findViewById(R.id.textViewStatus));
        globalClass.setTextViewMode(findViewById(R.id.textViewMode));
        if(!foregroundServiceRunning()) {
            Intent serviceIntent = new Intent(this, MyForegroundService.class);
            //note: this mean the if block will only workin in android 8 or greater
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
        }
        buttonMode = findViewById(R.id.buttonMode);
        buttonMode.setOnClickListener(view -> {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                if (processInfo.processName.equals(getApplicationContext().getApplicationInfo().processName)) {
                    activityManager.killBackgroundProcesses(processInfo.processName);
                    android.os.Process.killProcess(processInfo.pid);
                    break;
                }
            }
        });
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
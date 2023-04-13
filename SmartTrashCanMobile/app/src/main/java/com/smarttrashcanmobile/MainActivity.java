package com.smarttrashcanmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private ProgressBar loadingPB;
    TextView textViewData,textViewMode;
    Button buttonMode;
    String globaUrl = "http://192.168.1.4/smart_trashCan/php/";
    String modeGlobal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingPB = findViewById(R.id.idLoadingPB);
        textViewData = findViewById(R.id.textViewData);
        textViewMode = findViewById(R.id.textViewMode);
        buttonMode = findViewById(R.id.buttonMode);
        readDb();
        buttonMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeMode();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                readDb();
            }
        });
    }

    private void readDb() {
        String url = globaUrl+"selectData.php";
        loadingPB.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONObject respObj = new JSONObject(response);
                    String data = respObj.getString("data");
                    String mode = respObj.getString("mode");
                    modeGlobal = mode;
                    textViewData.setText("data: "+data);
                    textViewMode.setText("mode: "+mode);
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
        String url = globaUrl+"changeMode.php";
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
                if (modeGlobal.equals("ON")){
                    modeGlobal = "OFF";
                }
                else{
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

}
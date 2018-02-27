package com.cetcme.xkclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.conn_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SocketClient().conn();
            }
        });

        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("data", "123123");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SocketClient.send(jsonObject);
            }
        });
    }
}

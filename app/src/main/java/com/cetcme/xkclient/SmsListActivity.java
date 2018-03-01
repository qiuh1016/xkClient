package com.cetcme.xkclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmsListActivity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);

        getSupportActionBar().hide();

        initTitleView();
        initListView();

    }

    private void initTitleView() {
        QHTitleView qhTitleView = findViewById(R.id.qhTitleView);
        qhTitleView.setTitle("短信列表");
        qhTitleView.setBackView(0);
        qhTitleView.setRightView(0);
        qhTitleView.setClickCallback(new QHTitleView.ClickCallback() {
            @Override
            public void onBackClick() {
                //
            }

            @Override
            public void onRightClick() {
                //
            }
        });
    }

    private void initListView() {
        //设置listView
        listView = findViewById(R.id.sms_list);
        simpleAdapter = new SimpleAdapter(this, getMessageData(), R.layout.cell_sms_list,
                new String[]{"number", "time", "content"},
                new int[]{R.id.number_textView, R.id.time_textView, R.id.content_textView});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplication(), SmsDetailActivity.class);
                intent.putExtra("userAddress", dataList.get(i).get("number").toString());
                startActivity(intent);
            }
        });
    }

    private List<Map<String, Object>> getMessageData() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("number", "123456");
        map1.put("time", "08:22");
        map1.put("content", "收到你的消息");
        dataList.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("number", "123123");
        map2.put("time", "08:45");
        map2.put("content", "您好 有您的快递 请查收您好 有您的快递 请查收您好 有您的快递 请查收您好 有您的快递 请查收");
        dataList.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("number", "321321");
        map3.put("time", "昨天");
        map3.put("content", "Adsadqweqweqw");
        dataList.add(map3);


        return dataList;
    }
}

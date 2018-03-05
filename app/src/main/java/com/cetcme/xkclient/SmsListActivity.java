package com.cetcme.xkclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cetcme.xkclient.event.NewMessageEvent;
import com.cetcme.xkclient.event.SmsEvent;
import com.cetcme.xkclient.utils.DateUtil;
import com.cetcme.xkclient.utils.PreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmsListActivity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList = new ArrayList<>();

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);
        getSupportActionBar().hide();

        initTitleView();
        initListView();

        EventBus.getDefault().register(this);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Message message = new Message().init("333333", "654321", new Date(), "测试新加短信", false, true, false);
//                getNewSms(message);
//            }
//        }, 1000);
    }

    private void initTitleView() {
        QHTitleView qhTitleView = findViewById(R.id.qhTitleView);
        qhTitleView.setTitle("短信列表");
        qhTitleView.setBackView(0);
        qhTitleView.setRightView(R.drawable.sms_add);
        qhTitleView.setClickCallback(new QHTitleView.ClickCallback() {
            @Override
            public void onBackClick() {
                //
                toGetSmsList();
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(getApplication(), SmsDetailActivity.class);
                intent.putExtra("userAddress", getString(R.string.new_sms));
                startActivity(intent);

            }
        });
    }

    private void initListView() {
        //设置listView
        listView = findViewById(R.id.sms_list);
        simpleAdapter = new SimpleAdapter(this, getMessageData(), R.layout.cell_sms_list,
                new String[]{"userAddress", "lastSmsTime", "lastSmsContent"},
                new int[]{R.id.number_textView, R.id.time_textView, R.id.content_textView});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplication(), SmsDetailActivity.class);
                intent.putExtra("userAddress", dataList.get(i).get("userAddress").toString());
                startActivity(intent);
            }
        });
    }

    private List<Map<String, Object>> getMessageData() {
//        // TODO: 获取短信列表
//        String str = "[{\"userAddress\":\"123456\",\"lastSmsTime\":\"Thu Mar 01 16:56:25 GMT+08:00 2018\",\"lastSmsContent\":\"xz\\n fasong\"},{\"userAddress\":\"67876\",\"lastSmsTime\":\"Fri Jan 02 08:21:39 GMT+08:00 2018\",\"lastSmsContent\":\"123556\"},{\"userAddress\":\"654321\",\"lastSmsTime\":\"Fri Jan 02 08:11:48 GMT+08:00 1970\",\"lastSmsContent\":\"Fuchs I\"},{\"userAddress\":\"12451245\",\"lastSmsTime\":\"Fri Jan 02 08:11:19 GMT+08:00 1970\",\"lastSmsContent\":\"such cm\"}]";
//        try {
//            JSONArray jsonArray = new JSONArray(str);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Map<String, Object> map = new HashMap<>();
//                map.put("userAddress", jsonObject.get("userAddress"));
//                map.put("lastSmsTime", DateUtil.modifyDate(jsonObject.get("lastSmsTime").toString()));
//                map.put("lastSmsTimeOriginal", jsonObject.get("lastSmsTime").toString());
//                map.put("lastSmsContent", jsonObject.get("lastSmsContent"));
//                dataList.add(map);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        toGetSmsList();
        return dataList;
    }

    private void toGetSmsList() {
        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put("apiType", "sms_list");
            sendJson.put("userName", PreferencesUtils.getString(SmsListActivity.this, "username"));
            sendJson.put("password", PreferencesUtils.getString(SmsListActivity.this, "password"));
            MyApplication.send(sendJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public static void sortIntMethod(List list) {
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Date date1 = new Date(((Map<String, Object>) o1).get("lastSmsTimeOriginal").toString());
                Date date2 = new Date(((Map<String, Object>) o2).get("lastSmsTimeOriginal").toString());
                if (date1.compareTo(date2) > 0) {
                    return -1;
                } else if (date1.compareTo(date2) == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    private void getNewSms(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("userAddress", message.isSend() ? message.getReceiver() : message.getSender());
        map.put("lastSmsTime", DateUtil.modifyDate(message.getSend_time().toString()));
        map.put("lastSmsTimeOriginal", message.getSend_time().toString());
        map.put("lastSmsContent", message.getContent());

        boolean hasThisAddress = false;
        for (Map<String, Object> hashMap : dataList) {
            if (hashMap.get("userAddress").toString().equals(map.get("userAddress").toString())) {
                hasThisAddress = true;
                hashMap.put("lastSmsTime", map.get("lastSmsTime"));
                hashMap.put("lastSmsTimeOriginal", map.get("lastSmsTimeOriginal"));
                hashMap.put("lastSmsContent", map.get("lastSmsContent"));
                break;
            }
        }
        if (!hasThisAddress) dataList.add(map);
        sortIntMethod(dataList);
        simpleAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(NewMessageEvent messageEvent) {
        getNewSms(messageEvent.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(SmsEvent smsEvent) {
        JSONObject receiveJson = smsEvent.getReceiveJson();
        try {
            String apiType = receiveJson.getString("apiType");
            switch (apiType) {
                case "sms_list":
                    dataList.clear();
                    JSONArray smsList = receiveJson.getJSONArray("data");
                    String myAddress = receiveJson.getString("userAddress");
                    PreferencesUtils.putString(getApplication(), "myAddress", myAddress);

                    for (int i = 0; i < smsList.length(); i++) {
                        JSONObject smsCellJson = smsList.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        map.put("userAddress", smsCellJson.get("userAddress"));
                        map.put("lastSmsTime", DateUtil.modifyDate(smsCellJson.get("lastSmsTime").toString()));
                        map.put("lastSmsTimeOriginal", smsCellJson.get("lastSmsTime").toString());
                        map.put("lastSmsContent", smsCellJson.get("lastSmsContent"));
                        dataList.add(map);
                    }
                    simpleAdapter.notifyDataSetChanged();
                    break;
                case "sms_send":
                    Toast.makeText(this, receiveJson.get("msg").toString(), Toast.LENGTH_SHORT).show();
                    break;
                case "sms_push":
                    Message message = new Message();
                    message.fromJson(receiveJson.getJSONObject("data"));
                    getNewSms(message);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}

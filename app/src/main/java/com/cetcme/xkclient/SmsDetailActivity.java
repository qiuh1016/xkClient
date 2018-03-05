package com.cetcme.xkclient;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cetcme.xkclient.event.NewMessageEvent;
import com.cetcme.xkclient.event.SmsEvent;
import com.cetcme.xkclient.utils.DateUtil;
import com.cetcme.xkclient.utils.PreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.AbsListView.TRANSCRIPT_MODE_NORMAL;

public class SmsDetailActivity extends AppCompatActivity {

    QMUIPullRefreshLayout mPullRefreshLayout;
    ListView mListView;
    private SmsAdapter smsAdapter;
    private List<Message> dataList = new ArrayList<>();
    private QHTitleView qhTitleView;

    private EditText content_editText;
    private EditText receiver_editText;

    private LinearLayout receiver_layout;

    private String userAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);
        getSupportActionBar().hide();

        EventBus.getDefault().register(this);

        userAddress = getIntent().getStringExtra("userAddress");

        receiver_layout = findViewById(R.id.receiver_layout);
        mPullRefreshLayout = findViewById(R.id.pull_to_refresh);
        mListView = findViewById(R.id.listView);
        content_editText = findViewById(R.id.content_editText);
        receiver_editText = findViewById(R.id.receiver_editText);

        receiver_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userAddress = receiver_editText.getText().toString();
                toGetSmsDetail();
            }
        });

        if (!userAddress.equals(getString(R.string.new_sms))) receiver_layout.setVisibility(View.GONE);

        initTitleView();
        initList();
        initSendButton();
    }

    private void initTitleView() {
        qhTitleView = findViewById(R.id.qhTitleView);
        qhTitleView.setTitle(userAddress);
        qhTitleView.setBackView(R.mipmap.icon_back_button);
        qhTitleView.setRightView(0);
        qhTitleView.setClickCallback(new QHTitleView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                //
            }
        });
    }

    private void initList() {
        smsAdapter = new SmsAdapter(getApplicationContext(), getMessageData());
        mListView.setAdapter(smsAdapter);
        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setTranscriptMode(TRANSCRIPT_MODE_NORMAL);

                        // TODO: 获取上一页短信
//                        dataList.add(0, new Message().init("123456", "123456", new Date(), "刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信", false, true, false));

                        mPullRefreshLayout.finishRefresh();
                        smsAdapter.notifyDataSetChanged();
                    }
                }, 200);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(dataList.get(i).getContent());
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String[] items = new String[]{"转发", "删除"};
                new QMUIDialog
                    .MenuDialogBuilder(SmsDetailActivity.this)
                    .addItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(SmsDetailActivity.this, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .show();
                return false;
            }
        });
    }

    private Gson gson = new Gson();

    private List<Message> getMessageData() {

        if (userAddress.equals(getString(R.string.new_sms))) return dataList;


//        String arrayStr = "[{\"content\":\"111neirong1\",\"deleted\":false,\"id\":\"7dabb8ff-d2a8-43ea-835a-3d7a9dc15a9c\",\"isSend\":true,\"read\":true,\"receiver\":\"123456\",\"send_time\":\"Mar 1, 2018 10:15:42\",\"sender\":\"123456\"},{\"content\":\"222neirong2neirong2\",\"deleted\":false,\"id\":\"3ffc9258-df3a-4771-996c-583ef1ae773c\",\"isSend\":false,\"read\":true,\"receiver\":\"123456\",\"send_time\":\"Mar 1, 2018 10:15:42\",\"sender\":\"123456\"}]";
//        Type type1 = new TypeToken<List<Message>>(){}.getType();
//        dataList = gson.fromJson(arrayStr, type1);
//

        // TODO: 获取短信详情
//        Message message = new Message().init("123456", "123456", new Date("2017/3/1 10:21"), "111neirong1", true, true, false);
//        dataList.add(message);
//
//        dataList.add(new Message().init("123456", "123456", new Date(), "222neirong2neirong2", false, true, false));
//
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong3neirong3neirong3", true, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong4neirong4neirong4neirong4", false, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong5neirong5neirong5neirong5neirong5", true, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong6neirong6neirong6neirong6neirong6neirong6", false, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong7neirong7neirong7neirong7neirong7neirong7neirong7", false, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong2neirong2", true, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong3neirong3neirong3", false, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong4neirong4neirong4neirong4", true, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong5neirong5neirong5neirong5neirong5", false, true, false));
//        dataList.add(new Message().init("123456", "123456", new Date(), "neirong6neirong6neirong6neirong6neirong6neirong6", true, true, false));
        toGetSmsDetail();

        return dataList;
    }

    private void toGetSmsDetail() {
        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put("apiType", "sms_detail");
            sendJson.put("userAddress", userAddress);
            sendJson.put("userName", PreferencesUtils.getString(SmsDetailActivity.this, "username"));
            sendJson.put("password", PreferencesUtils.getString(SmsDetailActivity.this, "password"));
            MyApplication.send(sendJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initSendButton() {
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = content_editText.getText().toString();
                if (content.isEmpty()) return;

                if (dataList.size() == 0 || userAddress.equals(getString(R.string.new_sms))) {

                    userAddress = receiver_editText.getText().toString();
                    qhTitleView.setTitle(userAddress);
                    receiver_layout.setVisibility(View.GONE);

                }

                Message newMessage = new Message().init(PreferencesUtils.getString(SmsDetailActivity.this, "myAddress"), userAddress, new Date(), content, true, false, false);
                EventBus.getDefault().post(new NewMessageEvent(newMessage));

                JSONObject sendJson = new JSONObject();
                try {
                    sendJson.put("apiType", "sms_send");
                    sendJson.put("userName", PreferencesUtils.getString(SmsDetailActivity.this, "username"));
                    sendJson.put("password", PreferencesUtils.getString(SmsDetailActivity.this, "password"));
                    sendJson.put("data", newMessage.toJson());
                    MyApplication.send(sendJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dataList.add(newMessage);
                smsAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(dataList.size() - 1);

                content_editText.clearFocus();//取消焦点
                content_editText.setText("");
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(SmsEvent smsEvent) {
        JSONObject receiveJson = smsEvent.getReceiveJson();
        System.out.println("=========sms_detail");
        try {
            String apiType = receiveJson.getString("apiType");
            switch (apiType) {
                case "sms_detail":
                    dataList.clear();
                    JSONArray smsList = receiveJson.getJSONArray("data");
                    for(int i = 0; i < smsList.length(); i++) {
                        JSONObject jsonObject = smsList.getJSONObject(i);
                        Message message = new Message();
                        message.setSender(jsonObject.getString("sender"));
                        message.setReceiver(jsonObject.getString("receiver"));
                        message.setSend_time(new Date(jsonObject.getString("send_time")));
                        message.setContent(jsonObject.getString("content"));
                        message.setRead(jsonObject.getBoolean("read"));
                        message.setDeleted(jsonObject.getBoolean("deleted"));
                        message.setSend(jsonObject.getBoolean("isSend"));
                        dataList.add(message);
                    }
                    smsAdapter.notifyDataSetChanged();
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

package com.cetcme.xkclient.View;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cetcme.xkclient.MyApplication;
import com.cetcme.xkclient.MyClass.Constant;
import com.cetcme.xkclient.MyClass.SmsAdapter;
import com.cetcme.xkclient.R;
import com.cetcme.xkclient.RealmModels.Message;
import com.cetcme.xkclient.Event.NewMessageEvent;
import com.cetcme.xkclient.Event.SmsEvent;
import com.cetcme.xkclient.Utils.PreferencesUtils;
import com.google.gson.Gson;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.AbsListView.TRANSCRIPT_MODE_DISABLED;
import static android.widget.AbsListView.TRANSCRIPT_MODE_NORMAL;

public class SmsDetailActivity extends AppCompatActivity {

    @BindView(R.id.qhTitleView)         QHTitleView qhTitleView;
    @BindView(R.id.listView)            ListView mListView;
    @BindView(R.id.pull_to_refresh)     QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.send_button)         Button send_button;
    @BindView(R.id.receiver_tv)         TextView receiver_tv;
    @BindView(R.id.content_editText)    EditText content_editText;
    @BindView(R.id.receiver_editText)   EditText receiver_editText;
    @BindView(R.id.receiver_layout)     LinearLayout receiver_layout;

    private SmsAdapter smsAdapter;
    private List<Message> dataList = new ArrayList<>();
    private String userAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        userAddress = getIntent().getStringExtra("userAddress");

        receiver_tv.setVisibility(View.GONE);
        receiver_editText.setHint("收件人");

        receiver_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiver_editText.setFocusable(true);
                receiver_editText.setFocusableInTouchMode(true);
                receiver_editText.requestFocus();
            }
        });

        receiver_editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!receiver_editText.getText().toString().isEmpty()) {
                        userAddress = receiver_editText.getText().toString();
                        toGetSmsDetail();
                    }

                }
            }
        });

        if (!userAddress.equals(getString(R.string.new_sms))) {
            receiver_layout.setVisibility(View.GONE);
        } else {
            mPullRefreshLayout.setEnabled(false);
        }

        initTitleView();
        initList();
        initSendButton();
    }

    private void initTitleView() {
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
//                mPullRefreshLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                    }
//                }, 200);

                if (dataList.size() == 0) {
                    mPullRefreshLayout.finishRefresh();
                    return;
                }
                // 获取上一页短信
                JSONObject sendJson = new JSONObject();
                try {
                    sendJson.put("apiType", "sms_detail");
                    sendJson.put("userAddress", userAddress);
                    sendJson.put("userName", PreferencesUtils.getString(SmsDetailActivity.this, "username"));
                    sendJson.put("password", PreferencesUtils.getString(SmsDetailActivity.this, "password"));
                    sendJson.put("countPerPage", Constant.MESSAGE_COUNT_PER_PAGE);
                    sendJson.put("timeBefore", dataList.get(0).getSend_time());
                    MyApplication.send(sendJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final String[] items = new String[]{"复制", "删除"};
                new QMUIDialog
                    .MenuDialogBuilder(SmsDetailActivity.this)
                    .addItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    // 将文本内容放到系统剪贴板里。
                                    cm.setText(dataList.get(i).getContent());
                                    break;
                                case 1:
                                    Toast.makeText(SmsDetailActivity.this, "你选择了 " + items[which] + ", 功能待开发", Toast.LENGTH_SHORT).show();
                                    break;
                            }
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
            sendJson.put("countPerPage", Constant.MESSAGE_COUNT_PER_PAGE);
            sendJson.put("timeBefore", new Date());
            MyApplication.send(sendJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Message newMessage;


    private void initSendButton() {
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = content_editText.getText().toString();
                if (content.isEmpty()) return;

                if (userAddress.equals(getString(R.string.new_sms))) {
                    if (receiver_editText.getText().toString().isEmpty()) {
                        return;
                    } else {
                        userAddress = receiver_editText.getText().toString();
                    }
                }

                if (dataList.size() == 0) {
                    userAddress = receiver_editText.getText().toString();
                    qhTitleView.setTitle(userAddress);
                    receiver_layout.setVisibility(View.GONE);

                }

                newMessage = new Message();
                newMessage.setReceiver(userAddress);
                newMessage.setSender(PreferencesUtils.getString(SmsDetailActivity.this, "myAddress"));
                newMessage.setContent(content);
                newMessage.setSend_time(new Date());
                newMessage.setDeleted(false);
                newMessage.setRead(true);
                newMessage.setSend(true);

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

                EventBus.getDefault().post(new NewMessageEvent(newMessage));
                dataList.add(newMessage);
                smsAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(dataList.size() - 1);

                content_editText.clearFocus();//取消焦点
                content_editText.setText("");
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(send_button.getWindowToken(), 0);

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(SmsEvent smsEvent) {
        JSONObject receiveJson = smsEvent.getReceiveJson();
        try {
            String apiType = receiveJson.getString("apiType");
            switch (apiType) {
                case "sms_detail":

                    // 刚打开页面的时候 到listView底部
                    if (dataList.size() == 0) {
                        mListView.setSelection(mListView.getBottom());
                        mListView.setTranscriptMode(TRANSCRIPT_MODE_DISABLED);
                    }

                    ArrayList<Message> list = new ArrayList<>();
                    JSONArray smsList = receiveJson.getJSONArray("data");
                    if (smsList.length() == 0 && dataList.size() != 0) {
                        Toast.makeText(this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                        mPullRefreshLayout.finishRefresh();
                        return;
                    }
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
                        list.add(message);
                    }
                    dataList.addAll(0, list);
                    smsAdapter.notifyDataSetChanged();
                    qhTitleView.setTitle(userAddress);
                    mPullRefreshLayout.setEnabled(true);
                    mPullRefreshLayout.finishRefresh();
                    receiver_layout.setVisibility(View.GONE);

                    break;
                case "sms_send":
                    int code = receiveJson.getInt("code");
//                    if (code == 0) {
//                        EventBus.getDefault().post(new NewMessageEvent(newMessage));
//                        dataList.add(newMessage);
//                        smsAdapter.notifyDataSetChanged();
//                        mListView.smoothScrollToPosition(dataList.size() - 1);
//
//                        content_editText.clearFocus();//取消焦点
//                        content_editText.setText("");
//                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(send_button.getWindowToken(), 0);
//                    }
                    Toast.makeText(this, receiveJson.get("msg").toString(), Toast.LENGTH_SHORT).show();
                    break;
                case "sms_push":
                    Message message = new Message();
                    message.fromJson(receiveJson.getJSONObject("data"));
                    if (message.getReceiver().equals(userAddress)) {
                        message.setRead(true);
                        dataList.add(message);
                        smsAdapter.notifyDataSetChanged();

                        // 发送已读socket
                        JSONObject sendJson = new JSONObject();
                        try {
                            sendJson.put("apiType", "sms_read");
                            sendJson.put("userName", PreferencesUtils.getString(SmsDetailActivity.this, "username"));
                            sendJson.put("password", PreferencesUtils.getString(SmsDetailActivity.this, "password"));
                            sendJson.put("userAddress", userAddress);
                            MyApplication.send(sendJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case "socketDisconnect":
                    finish();
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

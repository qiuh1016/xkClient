package com.cetcme.xkclient.View;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cetcme.xkclient.MyApplication;
import com.cetcme.xkclient.R;
import com.cetcme.xkclient.RealmModels.Message;
import com.cetcme.xkclient.Event.NewMessageEvent;
import com.cetcme.xkclient.Event.SmsEvent;
import com.cetcme.xkclient.Utils.DateUtil;
import com.cetcme.xkclient.Utils.PreferencesUtils;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SmsListActivity extends AppCompatActivity {

    @BindView(R.id.qhTitleView)     QHTitleView qhTitleView;
    @BindView(R.id.sms_list)        ListView listView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;

    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initTitleView();
        initListView();

    }

    private void initTitleView() {
        qhTitleView.setTitle("短信列表");
        qhTitleView.setBackView(R.mipmap.icon_back_button);
        qhTitleView.setRightView(R.drawable.sms_add);
        qhTitleView.setClickCallback(new QHTitleView.ClickCallback() {
            @Override
            public void onBackClick() {
                showLogoutDialog();
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
        simpleAdapter = new SimpleAdapter(this, getMessageData(), R.layout.cell_sms_list,
                new String[]{"userAddress", "lastSmsTime", "lastSmsContent", "hasUnread"},
                new int[]{R.id.number_textView, R.id.time_textView, R.id.content_textView, R.id.unread_tv});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplication(), SmsDetailActivity.class);
                intent.putExtra("userAddress", dataList.get(i).get("userAddress").toString());
                startActivity(intent);

                if (!dataList.get(i).get("hasUnread").toString().equals("")) {
                    dataList.get(i).put("hasUnread", "");
                    simpleAdapter.notifyDataSetChanged();

                    // 发送已读socket
                    JSONObject sendJson = new JSONObject();
                    try {
                        sendJson.put("apiType", "sms_read");
                        sendJson.put("userName", PreferencesUtils.getString(SmsListActivity.this, "username"));
                        sendJson.put("password", PreferencesUtils.getString(SmsListActivity.this, "password"));
                        sendJson.put("userAddress", dataList.get(i).get("userAddress"));
                        MyApplication.send(sendJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final String[] items = new String[]{"删除"};
                new QMUIDialog
                        .MenuDialogBuilder(SmsListActivity.this)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:

                                        // 发送删除socket
                                        JSONObject sendJson = new JSONObject();
                                        try {
                                            sendJson.put("apiType", "sms_delete");
                                            sendJson.put("userName", PreferencesUtils.getString(SmsListActivity.this, "username"));
                                            sendJson.put("password", PreferencesUtils.getString(SmsListActivity.this, "password"));
                                            sendJson.put("userAddress", dataList.get(i).get("userAddress"));
                                            MyApplication.send(sendJson);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        dataList.remove(i);
                                        simpleAdapter.notifyDataSetChanged();
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
                return false;
            }
        });

        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                toGetSmsList();

                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 10000);
            }
        });
    }

    private List<Map<String, Object>> getMessageData() {
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
        map.put("hasUnread", message.isRead() ? "" : "●");

        boolean hasThisAddress = false;
        for (Map<String, Object> hashMap : dataList) {
            if (hashMap.get("userAddress").toString().equals(map.get("userAddress").toString())) {
                hasThisAddress = true;
                hashMap.put("lastSmsTime", map.get("lastSmsTime"));
                hashMap.put("lastSmsTimeOriginal", map.get("lastSmsTimeOriginal"));
                hashMap.put("lastSmsContent", map.get("lastSmsContent"));
                hashMap.put("hasUnread", map.get("hasUnread"));
                break;
            }
        }
        if (!hasThisAddress) dataList.add(map);
        sortIntMethod(dataList);
        simpleAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void NewMessageEvent(NewMessageEvent messageEvent) {
        getNewSms(messageEvent.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SmsEvent smsEvent) {
        JSONObject receiveJson = smsEvent.getReceiveJson();
        try {
            String apiType = receiveJson.getString("apiType");
            switch (apiType) {
                case "sms_list":

                    mPullRefreshLayout.finishRefresh();

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
                        map.put("hasUnread", smsCellJson.getBoolean("hasUnread") ? "●" : "");
                        dataList.add(map);
                    }
                    simpleAdapter.notifyDataSetChanged();
                    break;
                case "sms_push":
                    Message message = new Message();
                    message.fromJson(receiveJson.getJSONObject("data"));
                    getNewSms(message);

                    if (!isAppOnForeground()) {
                        createInform();
                    } else {
                        final Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
                        vibrator.vibrate(200);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vibrator.vibrate(500);
                            }
                        }, 400);
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

    public void createInform() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //定义一个PendingIntent，当用户点击通知时，跳转到某个Activity(也可以发送广播等)
            Intent intent = new Intent(getApplicationContext(), SmsListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

            //创建一个通知
            Notification notification = null;
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("您有新的短消息")
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
            //用NotificationManager的notify方法通知用户生成标题栏消息通知
            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nManager.notify(100, notification);//id是应用中通知的唯一标识
            //如果拥有相同id的通知已经被提交而且没有被移除，该方法会用更新的信息来替换之前的通知。
        }

    }

    private Boolean isAppOnForeground() {
        ActivityManager activityManager =(ActivityManager) getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName =getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onBackPressed() {
        showLogoutDialog();
    }

    private void showLogoutDialog() {
        new QMUIDialog.MessageDialogBuilder(SmsListActivity.this)
                .setTitle("提示")
                .setMessage("是否退出登录")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "退出", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        finish();
                        MyApplication.socket = null;
                    }
                })
                .show();
    }

}

package com.cetcme.xkclient.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cetcme.xkclient.Event.SmsEvent;
import com.cetcme.xkclient.MyApplication;
import com.cetcme.xkclient.MyClass.Constant;
import com.cetcme.xkclient.MyClass.ModeConstant;
import com.cetcme.xkclient.R;
import com.cetcme.xkclient.Socket.SocketManager;
import com.cetcme.xkclient.UpdateAppManager;
import com.cetcme.xkclient.Utils.PreferencesUtils;
import com.cetcme.xkclient.Utils.WifiUtil;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private QMUITipDialog tipDialog;

    @BindView(R.id.qhTitleView) QHTitleView qhTitleView;
    @BindView(R.id.username_et) EditText username_et;
    @BindView(R.id.password_et) EditText password_et;
    @BindView(R.id.login_btn)   QMUIRoundButton login_btn;
    @BindView(R.id.version_tv)  TextView version_tv;
    @BindView(R.id.wifi_lv)     ListView wifi_lv;

    private MyApplication myApplication;

    // for wifi list view
    List<ScanResult> scanResults = new ArrayList<>();
    WifiManager wifiManager;
    boolean showWifiTip = false;
    private Toast wifiToast;
    public static String version;
    public static String filePath;

    public static boolean downloadDialogShowed = false;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        myApplication = (MyApplication) getApplication();

        initView();
        initTitleView();

        String username = PreferencesUtils.getString(LoginActivity.this, "username");
        String password = PreferencesUtils.getString(LoginActivity.this, "password");
        if (username != null && !username.isEmpty()) {
            username_et.setText(username);
            password_et.setText(password);
        }

        myApplication.loginActivity = this;


        displayCurrentVersionNumber();
        initWifiListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!downloadDialogShowed) {
            boolean update = PreferencesUtils.getBoolean(LoginActivity.this, "update", false);
            if (update && version != null) {
                new UpdateAppManager(LoginActivity.this).showDownloadDialog(version);
            }
        }
    }

    private void initWifiListView() {
        if (ModeConstant.SHOW_WIFI_LIST) {
            wifiToast = Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT);

            //生成广播处理
            IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            //注册广播
            registerReceiver(receiver, intentFilter);

            // wifi
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifiManager != null) {
                scanResults = wifiManager.getScanResults();
                List<Map<String, Object>> dataList = new ArrayList<>();
                for (ScanResult scanResult : scanResults) {
                    if (!scanResult.SSID.isEmpty()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ssid", scanResult.SSID);
                        map.put("level", WifiUtil.getStringLevel(scanResult));
                        if (WifiUtil.getType(scanResult) == WifiUtil.WIFI_CIPHER_NONE) {
                            dataList.add(map);
                        }
                    }
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(LoginActivity.this, dataList, R.layout.cell_wifi, new String[]{"ssid", "level"}, new int[]{R.id.wifi_ssid_tv, R.id.level_tv});
                wifi_lv.setAdapter(simpleAdapter);
            }

            wifi_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (scanResults != null) {
                        showWifiTip = true;
                        wifiManager.disconnect();
                        ScanResult scanResult = scanResults.get(i);
                        WifiConfiguration config = new WifiConfiguration();
                        config.SSID = scanResult.SSID;
                        connect(config);
                    }

                }
            });
        }
    }

    /**
     * Display the current version number
     */
    private void displayCurrentVersionNumber() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
            TextView versionNumber = findViewById(R.id.version_tv);
            versionNumber.setText("©2018 CETCME V" + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                // wifi已成功扫描到可用wifi。
//                List<ScanResult> scanResults = wifiManager.getScanResults();
//                wifiListAdapter.clear();
//                wifiListAdapter.addAll(scanResults);
//            }

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    wifiToast.setText("连接已断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    wifiToast.setText("已连接到网络:" + wifiInfo.getSSID());
                } else {
                    NetworkInfo.DetailedState state = info.getDetailedState();
                    if (state == state.CONNECTING) {
                        wifiToast.setText("连接中...");
                    } else if (state == state.AUTHENTICATING) {
                        wifiToast.setText("正在验证身份信息...");
                    } else if (state == state.OBTAINING_IPADDR) {
                        wifiToast.setText("正在获取IP地址...");
                    } else if (state == state.FAILED) {
                        wifiToast.setText("连接失败");
                    }
                }
                wifiToast.show();
            }

        }



    };

    private void initView() {

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();

                String alertText = "";
                if (username.isEmpty()) {
                    alertText = "用户名为空";
                } else if (password.isEmpty()) {
                    alertText = "密码为空";
                }

                if (alertText.isEmpty()) {
                    PreferencesUtils.putString(LoginActivity.this, "username", username);
                    PreferencesUtils.putString(LoginActivity.this, "password", password);
                    login(username, password);
                } else {
                    tipDialog = new QMUITipDialog.Builder(LoginActivity.this)
                            .setTipWord(alertText)
                            .create();
                    tipDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1000);
                }

            }
        });
    }

    private void initTitleView() {
        qhTitleView.setTitle("用户登录");
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

    private void login(String username, String password) {
        tipDialog = new QMUITipDialog.Builder(LoginActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("登录中")
                .create();
        tipDialog.show();

        myApplication.conn();
    }

    public void loginResult(final boolean loginOK) {
        tipDialog.dismiss();
        tipDialog = new QMUITipDialog.Builder(LoginActivity.this)
                .setIconType(loginOK ? QMUITipDialog.Builder.ICON_TYPE_SUCCESS : QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(loginOK ? "登录成功" : "登录失败")
                .create();
        tipDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
                if (loginOK) {
                    Intent intent = new Intent();
                    intent.setClass(getApplication(), SmsListActivity.class);
                    startActivity(intent);
                }
            }
        }, 1000);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(SmsEvent smsEvent) {
        JSONObject receiveJson = smsEvent.getReceiveJson();
        try {
            String apiType = receiveJson.getString("apiType");
            switch (apiType) {
                case "socketDisconnect":
                    tipDialog.dismiss();
                    boolean update = PreferencesUtils.getBoolean(LoginActivity.this, "update", false);
                    if (!update) {
                        // 不需要更新 就提示断开
                        new QMUIDialog.MessageDialogBuilder(LoginActivity.this)
                                .setTitle("提示")
                                .setMessage("与服务器断开连接")
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (ModeConstant.SHOW_WIFI_LIST) {
            //解除广播
            unregisterReceiver(receiver);
        }
    }


    private void connect(WifiConfiguration config) {
        int wcgID = wifiManager.addNetwork(config);
        wifiManager.enableNetwork(wcgID, true);
    }


}

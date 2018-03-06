package com.cetcme.xkclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cetcme.xkclient.event.SmsEvent;
import com.cetcme.xkclient.utils.DateUtil;
import com.cetcme.xkclient.utils.PreferencesUtils;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private QMUITipDialog tipDialog;

    private EditText username_et;
    private EditText password_et;
    private QMUIRoundButton login_btn;

    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        EventBus.getDefault().register(this);

        initView();
        initTitleView();

        String username = PreferencesUtils.getString(LoginActivity.this, "username");
        String password = PreferencesUtils.getString(LoginActivity.this, "password");
        if (username != null && !username.isEmpty()) {
            username_et.setText(username);
            password_et.setText(password);
        }

        myApplication = (MyApplication) getApplication();
        MyApplication.loginActivity = this;
    }

    private void initView() {
        username_et = findViewById(R.id.username_et);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);

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
        QHTitleView qhTitleView = findViewById(R.id.qhTitleView);
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

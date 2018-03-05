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

import com.cetcme.xkclient.utils.PreferencesUtils;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

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

        initView();
        initTitleView();

        String username = PreferencesUtils.getString(LoginActivity.this, "username");
        String password = PreferencesUtils.getString(LoginActivity.this, "password");
        if (username != null && !username.isEmpty()) {
            username_et.setText(username);
            password_et.setText(password);
        }

        myApplication = (MyApplication) getApplication();
        myApplication.loginActivity = this;
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

//        // 登录逻辑
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                tipDialog.dismiss();
//                tipDialog = new QMUITipDialog.Builder(LoginActivity.this)
//                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
//                        .setTipWord("登录成功")
//                        .create();
//                tipDialog.show();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        tipDialog.dismiss();
//                        Intent intent = new Intent();
//                        intent.setClass(getApplication(), SmsListActivity.class);
//                        startActivity(intent);
//                    }
//                }, 1000);
//            }
//        },1000);
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


}
package com.cetcme.xkclient;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsDetailActivity extends AppCompatActivity {

    QMUIPullRefreshLayout mPullRefreshLayout;
    ListView mListView;
    private SmsAdapter smsAdapter;
    private List<Message> dataList = new ArrayList<>();

    private EditText content_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);
        getSupportActionBar().hide();

        mPullRefreshLayout = findViewById(R.id.pull_to_refresh);
        mListView = findViewById(R.id.listview);
        content_editText = findViewById(R.id.content_editText);

        initTitleView();
        initList();
        initSendButton();
    }

    private void initTitleView() {
        QHTitleView qhTitleView = findViewById(R.id.qhTitleView);
        qhTitleView.setTitle("123123");
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
                        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                        dataList.add(0, new Message().init("123456", "123456", new Date(), "刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信刷新后加的短信", false, true, false));
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
    }

    private List<Message> getMessageData() {
        Message message = new Message().init("123456", "123456", new Date(), "neirong1", true, true, false);
        dataList.add(message);

        dataList.add(new Message().init("123456", "123456", new Date(), "neirong2neirong2", false, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong3neirong3neirong3", true, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong4neirong4neirong4neirong4", false, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong5neirong5neirong5neirong5neirong5", true, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong6neirong6neirong6neirong6neirong6neirong6", false, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong7neirong7neirong7neirong7neirong7neirong7neirong7", false, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong2neirong2", true, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong3neirong3neirong3", false, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong4neirong4neirong4neirong4", true, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong5neirong5neirong5neirong5neirong5", false, true, false));
        dataList.add(new Message().init("123456", "123456", new Date(), "neirong6neirong6neirong6neirong6neirong6neirong6", true, true, false));

        return dataList;
    }

    private void initSendButton() {
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataList.add(new Message().init("123456", "123456", new Date(), content_editText.getText().toString(), true, true, false));
                smsAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(dataList.size() - 1);

                content_editText.clearFocus();//取消焦点
                content_editText.setText("");
            }
        });
    }

}

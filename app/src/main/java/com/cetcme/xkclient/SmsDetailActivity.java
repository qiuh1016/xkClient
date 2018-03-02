package com.cetcme.xkclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.widget.AbsListView.TRANSCRIPT_MODE_NORMAL;

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
        mListView = findViewById(R.id.listView);
        content_editText = findViewById(R.id.content_editText);

        initTitleView();
        initList();
        initSendButton();
    }

    private void initTitleView() {
        String userAddress = getIntent().getStringExtra("userAddress");

//        QMUITopBar mTopBar = findViewById(R.id.topbar);
//        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.QHTitleColor));
//        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
//            }
//        });
//        mTopBar.setTitle(userAddress);

        QHTitleView qhTitleView = findViewById(R.id.qhTitleView);
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

    private Gson gson = new Gson();

    private List<Message> getMessageData() {


//        String arrayStr = "[{\"content\":\"111neirong1\",\"deleted\":false,\"id\":\"7dabb8ff-d2a8-43ea-835a-3d7a9dc15a9c\",\"isSend\":true,\"read\":true,\"receiver\":\"123456\",\"send_time\":\"Mar 1, 2018 10:15:42\",\"sender\":\"123456\"},{\"content\":\"222neirong2neirong2\",\"deleted\":false,\"id\":\"3ffc9258-df3a-4771-996c-583ef1ae773c\",\"isSend\":false,\"read\":true,\"receiver\":\"123456\",\"send_time\":\"Mar 1, 2018 10:15:42\",\"sender\":\"123456\"}]";
//        Type type1 = new TypeToken<List<Message>>(){}.getType();
//        dataList = gson.fromJson(arrayStr, type1);
//

        Message message = new Message().init("123456", "123456", new Date("2017/3/1 10:21"), "111neirong1", true, true, false);
        dataList.add(message);

        dataList.add(new Message().init("123456", "123456", new Date(), "222neirong2neirong2", false, true, false));

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

//        if (dataList.size() < 6) {
//            mListView.setStackFromBottom(false);
//            mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
//        }

        return dataList;
    }

    private void initSendButton() {
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = content_editText.getText().toString();
                if (content.isEmpty()) return;
                dataList.add(new Message().init("123456", "123456", new Date(), content, true, true, false));
                smsAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(dataList.size() - 1);

                content_editText.clearFocus();//取消焦点
                content_editText.setText("");
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

}

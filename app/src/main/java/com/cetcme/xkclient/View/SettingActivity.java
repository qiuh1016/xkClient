package com.cetcme.xkclient.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.cetcme.xkclient.Event.SmsEvent;
import com.cetcme.xkclient.MyApplication;
import com.cetcme.xkclient.MyClass.Constant;
import com.cetcme.xkclient.R;
import com.cetcme.xkclient.Socket.SocketManager;
import com.cetcme.xkclient.Socket.SocketOrder;
import com.cetcme.xkclient.Utils.PreferencesUtils;
import com.cetcme.xkclient.Utils.WifiUtil;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.AbsListView.TRANSCRIPT_MODE_DISABLED;


public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.qhTitleView)     QHTitleView qhTitleView;
    @BindView(R.id.groupListView)   QMUIGroupListView groupListView;

    // for file pick
    int FILE_CODE = 0x99;
    private Handler handler;
    private SocketManager socketManager;

    private Toast tipToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        tipToast = Toast.makeText(SettingActivity.this, "", Toast.LENGTH_SHORT);

        initTitleView();
        initGroupListView();

    }

    private void initTitleView() {
        qhTitleView.setTitle("系统设置");
        qhTitleView.setBackView(R.mipmap.icon_back_button);
        qhTitleView.setRightView(0);
        qhTitleView.setClickCallback(new QHTitleView.ClickCallback() {
            @Override
            public void onBackClick() {
                onBackPressed();
            }

            @Override
            public void onRightClick() {
               //
            }
        });
    }

    private void initGroupListView() {

        QMUICommonListItemView debugBtnSwitchCell = groupListView.createItemView("DEBUG按钮组");
        debugBtnSwitchCell.setOrientation(QMUICommonListItemView.VERTICAL);
        debugBtnSwitchCell.setDetailText("打开关闭终端顶部debug按钮组");
        debugBtnSwitchCell.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        debugBtnSwitchCell.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SocketOrder.openDebugBtn(SettingActivity.this, isChecked);
                tipToast.setText((isChecked ? "打开" : "关闭") + "DEBUG按钮组");
                tipToast.show();
            }
        });

        QMUICommonListItemView setTimeCell = groupListView.createItemView("设置时间");
        setTimeCell.setOrientation(QMUICommonListItemView.VERTICAL);
        setTimeCell.setDetailText("设置终端时间");

        QMUICommonListItemView sendFileCell = groupListView.createItemView("发送文件");
        sendFileCell.setOrientation(QMUICommonListItemView.VERTICAL);
        sendFileCell.setDetailText("发送文件给终端");
        sendFileCell.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    switch (text.toString()) {
                        case "发送文件":
                            openSendFileActivity();
                            break;
                        case "设置时间":
                            SocketOrder.setTime(SettingActivity.this);
                            break;
                    }
                }
            }
        };

        QMUIGroupListView.newSection(SettingActivity.this)
                .addItemView(debugBtnSwitchCell, onClickListener)
                .addItemView(setTimeCell, onClickListener)
                .addItemView(sendFileCell, onClickListener)
                .addTo(groupListView);
    }

    /**
     * demo
     */
    private void initGroupListViewDemo() {
        QMUICommonListItemView normalItem = groupListView.createItemView("Item 1");
        normalItem.setOrientation(QMUICommonListItemView.VERTICAL);

        QMUICommonListItemView itemWithDetail = groupListView.createItemView("Item 2");
        itemWithDetail.setDetailText("在右方的详细信息");

        QMUICommonListItemView itemWithDetailBelow = groupListView.createItemView("Item 3");
        itemWithDetailBelow.setOrientation(QMUICommonListItemView.VERTICAL);
        itemWithDetailBelow.setDetailText("在标题下方的详细信息");

        QMUICommonListItemView itemWithChevron = groupListView.createItemView("Item 4");
        itemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemWithSwitch = groupListView.createItemView("Item 5");
        itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(SettingActivity.this, "checked = " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        QMUICommonListItemView itemWithCustom = groupListView.createItemView("Item 6");
        itemWithCustom.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        QMUILoadingView loadingView = new QMUILoadingView(SettingActivity.this);
        itemWithCustom.addAccessoryCustomView(loadingView);

        QMUICommonListItemView qmuiCommonListItemView = groupListView.createItemView("Item 7");
        qmuiCommonListItemView.setOrientation(QMUICommonListItemView.VERTICAL);
        qmuiCommonListItemView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        qmuiCommonListItemView.setDetailText("123123");

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(SettingActivity.this, text + " is Clicked", Toast.LENGTH_SHORT).show();
                }
            }
        };

        QMUIGroupListView.newSection(SettingActivity.this)
                .setTitle("Section 1: 默认提供的样式")
                .setDescription("Section 1 的描述")
                .addItemView(normalItem, onClickListener)
                .addItemView(itemWithDetail, onClickListener)
                .addItemView(itemWithDetailBelow, onClickListener)
                .addItemView(itemWithChevron, onClickListener)
                .addItemView(itemWithSwitch, onClickListener)
                .addTo(groupListView);

        QMUIGroupListView.newSection(SettingActivity.this)
                .setTitle("Section 2: 自定义右侧 View")
                .addItemView(itemWithCustom, onClickListener)
                .addItemView(qmuiCommonListItemView, onClickListener)
                .addTo(groupListView);
    }

    private void openSendFileActivity() {
        // This always works
        Intent i = new Intent(SettingActivity.this, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
    }

    /**
     * 文件选择器返回
     */
    @SuppressLint("HandlerLeak")
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 0:
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                        String str = "[" + format.format(new Date()) + "]" + msg.obj.toString();
                        tipToast.setText(str);
                        tipToast.show();
                        System.out.println(str);
                        break;
                    case 1:
                        System.out.println("本机IP：" + WifiUtil.GetIpAddress(SettingActivity.this) + " 监听端口:" + msg.obj.toString());
                        break;
                    case 2:
                        tipToast.setText(msg.obj.toString());
                        tipToast.show();
                        break;
                }
            }
        };

        socketManager = new SocketManager(handler);

        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(intent);
            final String ipAddress = Constant.SOCKET_SERVER_IP;
            final int port = Constant.FILE_SOCKET_SERVER_PORT;

            final ArrayList<String> fileNames = new ArrayList<>();
            final ArrayList<String> paths = new ArrayList<>();

            for (Uri uri: files) {
                final File file = Utils.getFileForUri(uri);
                // Do something with the result...

                fileNames.add(file.getName());
                paths.add(file.getPath());
            }

            Message.obtain(handler, 0, "正在发送至" + ipAddress + ":" +  port).sendToTarget();
            Thread sendThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    socketManager.SendFile(fileNames, paths, ipAddress, port);
                }
            });
            sendThread.start();

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(SmsEvent smsEvent) {
        JSONObject receiveJson = smsEvent.getReceiveJson();
        try {
            String apiType = receiveJson.getString("apiType");
            switch (apiType) {
                case "socketDisconnect":
                    finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

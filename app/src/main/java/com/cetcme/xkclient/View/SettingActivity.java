package com.cetcme.xkclient.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.cetcme.xkclient.Event.SmsEvent;
import com.cetcme.xkclient.MyClass.Constant;
import com.cetcme.xkclient.R;
import com.cetcme.xkclient.Socket.SocketManager;
import com.cetcme.xkclient.Socket.SocketOrder;
import com.cetcme.xkclient.Utils.WifiUtil;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class SettingActivity extends SwipeBackActivity {

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

        SocketOrder.getDeviceID(this);
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

    QMUICommonListItemView setNumberCell;

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

        setNumberCell = groupListView.createItemView("设置ID");
        setNumberCell.setOrientation(QMUICommonListItemView.VERTICAL);
        setNumberCell.setDetailText("");
        setNumberCell.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView setTimeCell = groupListView.createItemView("设置时间");
        setTimeCell.setOrientation(QMUICommonListItemView.VERTICAL);
        setTimeCell.setDetailText("设置终端时间");

        QMUICommonListItemView sendFileCell = groupListView.createItemView("发送文件");
        sendFileCell.setOrientation(QMUICommonListItemView.VERTICAL);
        sendFileCell.setDetailText("发送文件给终端");
        sendFileCell.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView RouteCell = groupListView.createItemView("航迹");
        RouteCell.setOrientation(QMUICommonListItemView.VERTICAL);
        RouteCell.setDetailText("查看航迹列表");
        RouteCell.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    switch (text.toString()) {
                        case "发送文件":
                            openSendFileActivity();
                            break;
                        case "设置ID":
                            showEditIDDialog();
                            break;
                        case "设置时间":
                            SocketOrder.setTime(SettingActivity.this);
                            break;
                        case "航迹":
                            startActivity(new Intent(SettingActivity.this, RouteListActivity.class));
                            break;
                    }
                }
            }
        };

        QMUIGroupListView.newSection(SettingActivity.this)
                .addItemView(debugBtnSwitchCell, onClickListener)
                .addItemView(setNumberCell, onClickListener)
                .addItemView(setTimeCell, onClickListener)
                .addItemView(sendFileCell, onClickListener)
                .addItemView(RouteCell, onClickListener)
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

    private void showEditIDDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("设置ID")
                .setPlaceholder("在此输入终端ID")
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() == 8) {
                            SocketOrder.setID(SettingActivity.this, text.toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "请填入正确ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
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
                case "device_id":
                    String deviceID = receiveJson.getString("deviceID");
                    setNumberCell.setDetailText(deviceID);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package com.cetcme.xkclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.cetcme.xkclient.Utils.PreferencesUtils;
import com.cetcme.xkclient.View.LoginActivity;
import com.cetcme.xkclient.View.SmsListActivity;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

/**
 * Created by qiuhong on 5/26/16.
 */
public class UpdateAppManager {

    // 文件分隔符
    private static final String FILE_SEPARATOR = "/";
    // 外存sdcard存放路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + FILE_SEPARATOR +"AutoUpdate" + FILE_SEPARATOR;
    // 下载应用存放全路径
    private static  String FILE_NAME = FILE_PATH + "AutoUpdate.apk";
    // 更新应用版本标记
    private static final int UPDATE_TOKEN = 0x29;
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 0x31;

    private Context context;
    private String message = "检测到本程序有新版本发布，建议您更新！";


    // 下载应用的对话框
    private Dialog dialog;
    // 下载应用的进度条
    private RoundCornerProgressBar progressBar;
    private TextView progressTextView;
    // 进度条的当前刻度值
    private int curProgress;
    // 用户是否取消下载
    private boolean isCancel;
    // 强制更新
    private boolean forceToUpdate;
    // 是否手动检测更新
    private boolean manualCheckUpdate;

    //用户数据
    private String username,password,serverIP,updateContent;

    public UpdateAppManager(Context context) {
        this.context = context;
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TOKEN:
                    progressBar.setProgress(curProgress);
                    progressTextView.setText(curProgress + "/100");
                    break;

                case INSTALL_TOKEN:
                    installApp();
                    break;
            }
        }
    };

    /**
     * 显示提示更新对话框
     */
    public void showNoticeDialog(final String serverVersion) {
        message = "检测到新版本发布(V"+ serverVersion + ")，请切换手机网络下载更新！";
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle("软件版本更新")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("好的", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PreferencesUtils.putBoolean(context, "update", true);
                        // 关闭socket
                        try {
                            SmsListActivity.getInstance().finish();
                            MyApplication.socket.close();
                            MyApplication.socket = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        if (!forceToUpdate) {
            builder.setNegativeButton("以后再说", null);
        }
        builder.create().show();
    }

    /**
     * 显示无更新对话框
     */
    public void showNoUpdateDialog(String currentVersion) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle("当前已为最新版本")
                .setMessage("当前版本：V" + currentVersion + "。")
                .setCancelable(false)
                .setPositiveButton("好的", null);
        builder.create().show();
    }

    public void showDownOkDialog() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle("下载完成")
                .setMessage("新版本下载已完成，登陆终端来安装新版本")
                .setCancelable(false)
                .setPositiveButton("好的", null);
        builder.create().show();
    }

    /**
     * 显示下载进度对话框
     */
    public void showDownloadDialog(String serverVersion) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_bar, null);
        progressBar = view.findViewById(R.id.progressBar);
        progressTextView = view.findViewById(R.id.progressTextView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("新版本下载中,请切换手机网络");
        builder.setView(view);
        builder.setCancelable(false);
        if (!forceToUpdate) {
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isCancel = true;
                    LoginActivity.downloadDialogShowed = false;
                }
            });
        }
        dialog = builder.create();
        dialog.show();
        LoginActivity.downloadDialogShowed = true;
        downloadApp(serverVersion);

    }

    /**
     * 下载新版本应用
     */
    public void downloadApp(final String serverVersion) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                InputStream in = null;
                FileOutputStream out = null;
                HttpURLConnection conn = null;
                try {
                    // TODO: for test
                    String downloadUrl = "http://121.196.225.145:8181/sbaz_new-2.0.1.apk";//"http://121.196.225.145:8181/display-" + serverVersion + ".apk";
                    url = new URL(downloadUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    long fileLength = conn.getContentLength();
                    in = conn.getInputStream();
                    File filePath = new File(FILE_PATH);
                    if(!filePath.exists()) {
                        filePath.mkdir();
                    }
                    FILE_NAME = FILE_PATH +  "app" + serverVersion + ".apk";
                    out = new FileOutputStream(new File(FILE_NAME));
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    long readedLength = 0L;
                    while((len = in.read(buffer)) != -1) {
                        // 用户点击“取消”按钮，下载中断
                        if(isCancel) {
                            break;
                        }
                        out.write(buffer, 0, len);
                        readedLength += len;
                        curProgress = (int) (((float) readedLength / fileLength) * 100);
                        handler.sendEmptyMessage(UPDATE_TOKEN);
                        if(readedLength >= fileLength) {
                            dialog.dismiss();
                            LoginActivity.downloadDialogShowed = false;
                            // 下载完毕，通知安装
                            PreferencesUtils.putBoolean(context, "update", false);
//                            handler.sendEmptyMessage(INSTALL_TOKEN);
                            LoginActivity.filePath = FILE_NAME;
                            showDownOkDialog();
                            break;
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        File appFile = new File(FILE_NAME);
        if(!appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

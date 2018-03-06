package com.cetcme.xkclient;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cetcme.xkclient.event.SmsEvent;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by qiuhong on 01/03/2018.
 */

public class MyApplication extends Application {

    static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    private static int MESSAG_LOGIN_OK = 0x01;
    private static  int MESSAG_LOGIN_FAIL = 0x02;
    private static  int SOCKET_DISCONNECT = 0x00;


    private static String serverIP = "192.168.43.1";
    private static int serverPort = 9999;

    public static LoginActivity loginActivity;

    private static Socket socket;
    private static int BUFFER_SIZE = 1024 * 1024;

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //获得刚才发送的Message对象，然后在这里进行UI操作
            switch (msg.what) {
                case 0x01:
                    loginActivity.loginResult(true);
                    break;
                case 0x02:
                    loginActivity.loginResult(false);
                    break;
            }
        }
    };

    private final static int SOL_TCP = 6;
    private final static int TCP_KEEPIDLE = 4;
    private final static int TCP_KEEPINTVL = 5;
    private final static int TCP_KEEPCNT = 6;

    protected void setKeepaliveSocketOptions(Socket socket, int idleTimeout, int interval, int count) {
        try {
            socket.setKeepAlive(true);
            try {
                Field socketImplField = Class.forName("java.net.Socket").getDeclaredField("impl");
                socketImplField.setAccessible(true);
                if (socketImplField != null) {
                    Object plainSocketImpl = socketImplField.get(socket);
                    Field fileDescriptorField = Class.forName("java.net.SocketImpl").getDeclaredField("fd");
                    if (fileDescriptorField != null) {
                        fileDescriptorField.setAccessible(true);
                        FileDescriptor fileDescriptor = (FileDescriptor) fileDescriptorField.get(plainSocketImpl);
                        Class libCoreClass = Class.forName("libcore.io.Libcore");
                        Field osField = libCoreClass.getDeclaredField("os");
                        osField.setAccessible(true);
                        Object libcoreOs = osField.get(libCoreClass);
                        Method setSocketOptsMethod = Class.forName("libcore.io.ForwardingOs").getDeclaredMethod("setsockoptInt", FileDescriptor.class, int.class, int.class, int.class);
                        if (setSocketOptsMethod != null) {
                            setSocketOptsMethod.invoke(libcoreOs, fileDescriptor, SOL_TCP, TCP_KEEPIDLE, idleTimeout);
                            setSocketOptsMethod.invoke(libcoreOs, fileDescriptor, SOL_TCP, TCP_KEEPINTVL, interval);
                            setSocketOptsMethod.invoke(libcoreOs, fileDescriptor, SOL_TCP, TCP_KEEPCNT, count);
                        }
                    }
                }
            }
            catch (Exception reflectionException) {
                disconnectSocket();
            }
        } catch (SocketException e) {
            disconnectSocket();
        }
    }

    private static void disconnectSocket() {
        System.out.println("=======服务器断开连接");
        new QMUIDialog.MessageDialogBuilder(context)
            .setTitle("提示")
            .setMessage("与服务器断开连接，是否重连？")
            .addAction("取消", new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    dialog.dismiss();
                }
            })
            .addAction(0, "重连", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    conn();
                    dialog.dismiss();
                }
            })
            .show();
    }

    /**
     * 建立服务端连接
     */
    public static void conn() {
        Log.e("JAVA", "to 建立连接：");
        new Thread() {

            @Override
            public void run() {

                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(serverIP, serverPort), 2000);
                    Log.e("JAVA", "建立连接：" + socket);

                    android.os.Message msg = new Message();
                    msg.what = MESSAG_LOGIN_OK;
                    mHandler.sendMessage(msg);
//                    setKeepaliveSocketOptions(socket, TCP_KEEPIDLE, TCP_KEEPINTVL, TCP_KEEPCNT);
                    startReader();
                } catch (UnknownHostException e) {
                    android.os.Message msg = new Message();
                    msg.what = MESSAG_LOGIN_FAIL;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                } catch (IOException e) {
                    android.os.Message msg = new Message();
                    msg.what = MESSAG_LOGIN_FAIL;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 从参数的Socket里获取最新的消息
     */
    private static void startReader() {

        new Thread() {
            @Override
            public void run() {
                try {
                    final InetAddress address = socket.getInetAddress();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    while (true) {

                        System.out.println("*等待服务器数据*" + socket.isConnected());

                        // 读取数据
                        char[] data = new char[BUFFER_SIZE];
                        int len = br.read(data);
                        if (len != -1) {
                            String rexml = String.valueOf(data, 0, len);
                            System.out.println("获取到服务器的信息：" + address + " ");
                            System.out.println(rexml);
                            try {
                                JSONObject receiveJson = new JSONObject(rexml);
                                EventBus.getDefault().post(new SmsEvent(receiveJson));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            socket.close();
                            return;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 发送消息
     */
    public static void send(final JSONObject json) {
        new Thread() {
            @Override
            public void run() {

                try {
                    System.out.println("*to send*");
                    // socket.getInputStream()
                    if (socket == null) {
                        return;
                    }
                    OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

                    writer.write(json.toString());
                    writer.flush();

                    System.out.println("****send: " + json.toString());
                } catch (IOException e) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("apiType", "socketDisconnect");
                        EventBus.getDefault().post(new SmsEvent(jsonObject));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }.start();
    }

}

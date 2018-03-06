package com.cetcme.xkclient;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.*;
import android.os.Message;
import android.util.Log;

import com.cetcme.xkclient.Event.SmsEvent;
import com.cetcme.xkclient.MyClass.Constant;
import com.cetcme.xkclient.View.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    private static int MESSAGE_LOGIN_OK = 0x01;
    private static int MESSAGE_LOGIN_FAIL = 0x02;

    public LoginActivity loginActivity;

    public static Socket socket;
    private static int BUFFER_SIZE = 10 * 1024 * 1024;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
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

    /**
     * 建立服务端连接
     */
    public void conn() {
        Log.e("JAVA", "to 建立连接：");
        new Thread() {

            @Override
            public void run() {

                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(Constant.SOCKET_SERVER_IP, Constant.SOCKET_SERVER_PORT), Constant.SOCKET_CONNECT_TIME_OUT_TIME);
                    Log.e("JAVA", "建立连接：" + socket);

                    android.os.Message msg = new Message();
                    msg.what = MESSAGE_LOGIN_OK;
                    mHandler.sendMessage(msg);
                    startReader();
                    checkConnect();
                } catch (UnknownHostException e) {
                    android.os.Message msg = new Message();
                    msg.what = MESSAGE_LOGIN_FAIL;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                } catch (IOException e) {
                    android.os.Message msg = new Message();
                    msg.what = MESSAGE_LOGIN_FAIL;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String lastReceive = "";

    /**
     * 从参数的Socket里获取最新的消息
     */
    private void startReader() {

        new Thread() {
            @Override
            public void run() {
                try {
                    final InetAddress address = socket.getInetAddress();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    while (true) {

                        System.out.println("*等待服务器数据*");

                        // 读取数据
                        char[] data = new char[BUFFER_SIZE];
                        int len = br.read(data);
                        if (len != -1) {
                            String rexml = String.valueOf(data, 0, len);
                            System.out.println("获取到服务器的信息：" + address + " ");
                            System.out.println(rexml);
                            try {
                                JSONObject receiveJson;
                                if (lastReceive.isEmpty()) {
                                    receiveJson = new JSONObject(rexml);
                                } else {
                                    receiveJson = new JSONObject(lastReceive + rexml);
                                }

                                EventBus.getDefault().post(new SmsEvent(receiveJson));
                                lastReceive = "";
                            } catch (JSONException e) {
                                e.printStackTrace();
                                lastReceive = lastReceive + rexml;
//                                Looper.prepare();
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        lastReceive = "";
//                                    }
//                                }, 1000);
//                                Looper.loop();
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
     * 从参数的Socket里获取最新的消息
     */
    private void checkConnect() {

        new Thread() {
            @Override
            public void run() {

                while (socket != null && !socket.isClosed()) {

                    try {
                        sleep(Constant.SOCKET_HEART_BEAT_TIME);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("apiType", "checkConnect");
                        send(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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
                        socket = null;
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

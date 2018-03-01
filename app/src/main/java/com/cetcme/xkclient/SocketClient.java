package com.cetcme.xkclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient extends Service {

    private String serverIP = "192.168.43.1";
    private int serverPort = 9999;

//    private String serverIP = "localhost";
//    private int serverPort = 3000;

    private static Socket socket;

    private static int BUFFER_SIZE = 1024 * 1024;

    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    public class MsgBinder extends Binder {
        public SocketClient getService() {
            return SocketClient.this;
        }
    }

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
                    socket.connect(new InetSocketAddress(serverIP, serverPort), 2000);
                    Log.e("JAVA", "建立连接：" + socket);

                    startReader();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

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


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}

package com.cetcme.xkclient.Socket;

import android.content.Context;

import com.cetcme.xkclient.MyApplication;
import com.cetcme.xkclient.Utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by qiuhong on 26/03/2018.
 */

public class SocketOrder {

    /**
     * 打开关闭debug按钮组
     * @param context
     * @param open
     */
    public static void openDebugBtn(Context context, boolean open) {
        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put("apiType", "debug");
            sendJson.put("userName", PreferencesUtils.getString(context, "username"));
            sendJson.put("password", PreferencesUtils.getString(context, "password"));
            sendJson.put("code", 0);
            sendJson.put("content", open ? 0 : 1);
            MyApplication.send(sendJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送时间
     * @param context
     */
    public static void setTime(Context context) {
        JSONObject sendJson = new JSONObject();
        try {
            sendJson.put("apiType", "set_time");
            sendJson.put("userName", PreferencesUtils.getString(context, "username"));
            sendJson.put("password", PreferencesUtils.getString(context, "password"));
            sendJson.put("time", new Date());
            MyApplication.send(sendJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

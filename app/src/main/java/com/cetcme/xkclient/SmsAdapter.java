package com.cetcme.xkclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by qiuhong on 28/02/2018.
 */

public class SmsAdapter extends BaseAdapter {

    public LayoutInflater mInflater;
    public List<Message> dataList;

    public SmsAdapter(Context context, List<Message> dataList) {
        this.dataList = dataList;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //得到加载布局的类型
        Message message = dataList.get(i);
//        if (view == null){
            //根据返回类型加载不同的布局文件和创建不同的缓存类ViewHolder
            if (message.isSend()) {
                view = mInflater.inflate(R.layout.cell_sms_send,null);
            } else {
                view = mInflater.inflate(R.layout.cell_sms_receive,null);
            }
            TextView content_textView = view.findViewById(R.id.content_textView);
            TextView time_textView = view.findViewById(R.id.time_textView);
            content_textView.setText(message.getContent());
            time_textView.setText(message.getSend_time().toLocaleString());
//        }
        return view;
    }
}

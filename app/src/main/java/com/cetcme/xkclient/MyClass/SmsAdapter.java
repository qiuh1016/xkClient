package com.cetcme.xkclient.MyClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.cetcme.xkclient.R;
import com.cetcme.xkclient.RealmModels.Message;
import com.cetcme.xkclient.Utils.DateUtil;

import java.util.List;

/**
 * Created by qiuhong on 28/02/2018.
 */

public class SmsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Message> dataList;
    private EditText editText;

    public SmsAdapter(Context context, List<Message> dataList, EditText editText) {
        this.dataList = dataList;
        this.editText = editText;
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
        final Message message = dataList.get(i);
//        if (view == null){
            //根据返回类型加载不同的布局文件和创建不同的缓存类ViewHolder
            if (message.isSend()) {
                view = mInflater.inflate(R.layout.cell_sms_send,null);
                TextView failed_tv = view.findViewById(R.id.failed_tv);
                if (!message.isSendOK()) failed_tv.setVisibility(View.VISIBLE);
                failed_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String content = message.getContent();
                        editText.setText(content);
                        editText.setSelection(content.length());
                    }
                });
            } else {
                view = mInflater.inflate(R.layout.cell_sms_receive,null);
            }
            TextView content_textView = view.findViewById(R.id.content_textView);
            TextView time_textView = view.findViewById(R.id.time_textView);
            content_textView.setText(message.getContent());
            time_textView.setText(DateUtil.modifyDate(message.getSend_time().toString()));

//        }
        return view;
    }

}

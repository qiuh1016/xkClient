package com.cetcme.xkclient.event;

import com.cetcme.xkclient.Message;

/**
 * Created by qiuhong on 05/03/2018.
 */

public class NewMessageEvent {

    private Message message;

    public  NewMessageEvent(Message message){
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void Message(Message message) {
        this.message = message;
    }

}

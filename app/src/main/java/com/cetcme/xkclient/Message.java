package com.cetcme.xkclient;

import java.util.Date;
import java.util.UUID;

/**
 * Created by qiuhong on 12/01/2018.
 */

public class Message {

//    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }

    private String sender;
    private String receiver;
    private Date send_time;
    private String content;
    private boolean read;
    private boolean deleted;
    private boolean isSend;

    Message init(String sender, String receiver, Date send_time, String content, boolean isSend, boolean read, boolean deleted) {
        this.sender = sender;
        this.receiver = receiver;
        this.send_time = send_time;
        this.content = content;
        this.setSend(isSend);
        this.read = read;
        this.deleted = deleted;
        return this;
    }



    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getSend_time() {
        return send_time;
    }

    public void setSend_time(Date send_time) {
        this.send_time = send_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
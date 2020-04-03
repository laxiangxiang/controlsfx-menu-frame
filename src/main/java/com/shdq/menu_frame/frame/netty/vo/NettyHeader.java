package com.shdq.menu_frame.frame.netty.vo;

import java.util.Map;

/**
 * @author shdq-fjy
 */
public class NettyHeader {
    //netty消息校验码
    private int crcCode = 0xabef0101;
    //整个消息长度
    private int length;
    //消息类型:0：业务请求消息；1：业务响应消息；2：业务one way消息；3：握手请求消息；4：握手应答消息；5：心跳请求消息；6：心跳应答消息。
    private byte type;
    //会话id
    private int sessionId;
    //消息优先级
    private byte priority;
    //用于扩展的消息头
    private Map<String ,Object> attachment;

    public NettyHeader() {
    }

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "NettyHeader{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", type=" + type +
                ", sessionId=" + sessionId +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}

package com.shdq.menu_frame.frame.netty.vo;

/**
 * @author shdq-fjy
 */
public class NettyMessage {
    private NettyHeader header;
    private Object body;

    public NettyMessage() {
    }

    public NettyHeader getHeader() {
        return header;
    }

    public void setHeader(NettyHeader header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}

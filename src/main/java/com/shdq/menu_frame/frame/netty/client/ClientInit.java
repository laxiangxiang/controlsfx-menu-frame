package com.shdq.menu_frame.frame.netty.client;

import com.shdq.menu_frame.frame.netty.kryocodec.KryoDecoder;
import com.shdq.menu_frame.frame.netty.kryocodec.KryoEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import javafx.scene.Node;

/**
 * @author shdq-fjy
 */
public class ClientInit extends ChannelInitializer<SocketChannel> {

    private Node node;

    public ClientInit(Node node) {
        this.node = node;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //剥离接收到的长度字段，拿到实际的消息报文字节数组
        ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
        //给发送出去的消息增加长度字段
        ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
        //反序列化
        ch.pipeline().addLast(new KryoDecoder());
        //序列化
        ch.pipeline().addLast(new KryoEncoder());
        //超时检测
        ch.pipeline().addLast("readTimeOutHandler",new ReadTimeoutHandler(10));
        //发出登录请求
        ch.pipeline().addLast("loginAuthHandler",new LoginAuthReqHandler());
        //发出心跳请求
        ch.pipeline().addLast("hearBeatHandler",new HeartBeatReqHandler(node));
    }
}

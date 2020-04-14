package com.shdq.menu_frame.frame.netty.client;

import com.shdq.menu_frame.frame.netty.vo.MessageType;
import com.shdq.menu_frame.frame.netty.vo.NettyHeader;
import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author shdq-fjy
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    /**
     * 建立连接后发出登录请求
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyMessage message = buildLoginReq();
        log.info("客户端请求登录：{},消息：{}",((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress(),message);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        //如果是握手应答消息，需要判断是否认证成功
        if (message.getHeader() != null){
            byte type = message.getHeader().getType();
            if (type == MessageType.LOGIN_RESP.getValue()){
                log.info("服务端响应客户端登录消息：{}",message);
                String loginResult = (String) message.getBody();
                if (!"0".equals(loginResult)){
                    //握手失败，关闭连接
                    ctx.close();
                }else {
                    log.info("Login is ok:"+message);
                    ctx.fireChannelRead(msg);
                }
            }
            if (type == MessageType.HEARTBEAT_RESP.getValue()){
                ctx.fireChannelRead(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.LOGIN_REQ.getValue());
        message.setHeader(header);
        return message;
    }
}

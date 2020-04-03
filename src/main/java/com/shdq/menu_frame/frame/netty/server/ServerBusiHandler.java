package com.shdq.menu_frame.frame.netty.server;

import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 业务处理
 */
public class ServerBusiHandler extends SimpleChannelInboundHandler<NettyMessage>{
    private static final Logger log = LoggerFactory.getLogger(ServerBusiHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
        log.info("{}",msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info(ctx.channel().remoteAddress()+"主动断开了连接。");
    }
}

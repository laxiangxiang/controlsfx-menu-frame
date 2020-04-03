package com.shdq.menu_frame.frame.netty.server;

import com.shdq.menu_frame.frame.netty.vo.MessageType;
import com.shdq.menu_frame.frame.netty.vo.NettyHeader;
import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shdq-fjy
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.getValue()){
            log.info("Receive client heart beat message:--->"+message);
            NettyMessage heartBeat = buildHeartBeat();
            log.info("Send heart beat response message to client:--->"+heartBeat);
            ctx.writeAndFlush(heartBeat);
            ReferenceCountUtil.release(msg);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.HEARTBEAT_RESP.getValue());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

package com.shdq.menu_frame.frame.netty.kryocodec;

import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author shdq-fjy
 */
public class KryoEncoder extends MessageToByteEncoder<NettyMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf out) throws Exception {
        KryoSerializer.serialize(msg,out);
        ctx.flush();
    }
}

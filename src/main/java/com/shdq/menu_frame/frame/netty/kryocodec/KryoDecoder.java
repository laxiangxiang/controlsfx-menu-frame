package com.shdq.menu_frame.frame.netty.kryocodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author shdq-fjy
 */
public class KryoDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object o = KryoSerializer.deserialize(in);
        out.add(o);
    }
}

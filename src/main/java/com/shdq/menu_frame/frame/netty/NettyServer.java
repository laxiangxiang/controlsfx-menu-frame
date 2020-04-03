package com.shdq.menu_frame.frame.netty;

import com.shdq.menu_frame.frame.netty.server.ServerInit;
import com.shdq.menu_frame.frame.netty.vo.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shdq-fjy
 */
public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().startBind();
    }

    public void startBind() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ServerInit());
        serverBootstrap.bind(NettyConstant.REMOTE_PORT).sync();
        log.info("Netty server start:"+NettyConstant.REMOTE_IP+":"+NettyConstant.REMOTE_PORT);
    }
}

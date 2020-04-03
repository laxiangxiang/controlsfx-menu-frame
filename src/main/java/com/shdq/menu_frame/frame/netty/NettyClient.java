package com.shdq.menu_frame.frame.netty;

import com.shdq.menu_frame.frame.netty.client.ClientInit;
import com.shdq.menu_frame.frame.netty.vo.MessageType;
import com.shdq.menu_frame.frame.netty.vo.NettyConstant;
import com.shdq.menu_frame.frame.netty.vo.NettyHeader;
import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author shdq-fjy
 */
public class NettyClient implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Channel channel;
    private EventLoopGroup group = new NioEventLoopGroup();
    //是否用户主动关闭连接
    private volatile boolean userClose = false;
    //连接是否成功关闭的标志
    private volatile boolean connected = false;
    private String ip;
    private int port;
    private Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new NettyClient("127.0.0.1",8989));
        t.start();
        t.join();
    }

    @Override
    public void run() {
        try {
            connect(port,ip);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connect(int port,String ip) throws InterruptedException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000)
                    .handler(new ClientInit(node));
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(ip,port));
            channel = future.sync().channel();
            //连接成功后通知等待线程，连接已建立
            synchronized (this){
                this.connected = true;
                this.notifyAll();
            }
            future.channel().closeFuture().sync();
        }finally {
            //不是用户主动关闭线程，说明网络发生了问题，需要进行重连操作
            if (!userClose){
                System.out.println("连接发生异常，可能发生了服务器异常或网络问题，准备进行重连。。。");
                //再次发起重连操作
                executorService.execute(()->{
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        try {
                            //发起重连操作
                            connect(port,ip);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                });
            }else {
                //用户主动关闭，释放资源
                channel = null;
                group.shutdownGracefully().sync();
                synchronized (this){
                    this.connected = false;
                    this.notifyAll();
                }
            }
        }
    }

    //////////////////////////////////////////////////////////
    //-----------------以下方法供业务方使用--------------------//
    /////////////////////////////////////////////////////////

    public void send(Object message){
        if (channel == null || !channel.isActive()){
            throw new IllegalStateException("和服务器还未建立起有效连接！请稍后再试！");
        }
        NettyMessage message1 = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.SERVICE_REQ.getValue());
        message1.setHeader(header);
        message1.setBody(message);
        channel.writeAndFlush(message1);
    }

    public void close(){
        userClose = true;
        channel.close();
    }

    public boolean isConnected(){
        return connected;
    }
}

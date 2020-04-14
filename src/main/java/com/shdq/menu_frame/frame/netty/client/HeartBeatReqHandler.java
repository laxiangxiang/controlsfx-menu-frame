package com.shdq.menu_frame.frame.netty.client;

import com.shdq.menu_frame.frame.netty.vo.MessageType;
import com.shdq.menu_frame.frame.netty.vo.NettyHeader;
import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shdq-fjy
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatReqHandler.class);
    private volatile ScheduledFuture<?> heartBeat;
    private Image imageOnline = new Image("/images/heart-online.png");
    private Image imageOffline = new Image("/images/heart-offline.png");
    private Node node;

    public HeartBeatReqHandler(Node node) {
        this.node = node;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        //握手或者登录成功，主动发送心跳消息
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.getValue()){
            //定时发送心跳
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx),0,3000, TimeUnit.MILLISECONDS);
            ReferenceCountUtil.release(msg);
        } else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.getValue()) {
            //如果是心跳应答
            log.info("Client receive server heart beat.");
            //给心跳设置动画,如果连接已断开则不设置
            ((ImageView)node).setImage(imageOnline);
            hearImageTransition(node);
            if (!ctx.channel().isActive()){
                //只要是断开连接了，就把心图片设置为灰色的心，并且销毁当前任务
                ((ImageView)node).setImage(imageOffline);
                ctx.executor().shutdownGracefully();
            }
            ReferenceCountUtil.release(msg);
        }else {
            //如果是其它报文
            ctx.fireChannelRead(msg);
        }
    }


    /**
     * 心跳请求任务
     */
    public class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;
        //心跳计数
        private final AtomicInteger heartBeatCount;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
            this.heartBeatCount = new AtomicInteger(0);
        }

        @Override
        public void run() {
            NettyMessage message = buildHeartBeat();
            int count = heartBeatCount.incrementAndGet();
            log.info("Client send heart beat message to server:--->"+count);
            ctx.writeAndFlush(message);
        }

        private NettyMessage buildHeartBeat() {
            NettyMessage message = new NettyMessage();
            NettyHeader header = new NettyHeader();
            header.setType(MessageType.HEARTBEAT_REQ.getValue());
            message.setHeader(header);
            return message;
        }
    }

    private void hearImageTransition(Node node){
        //淡出效果
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), node);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(false);
        //缩放效果
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), node);
        scaleTransition.setToX(1.5);
        scaleTransition.setToY(1.5);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(false);

        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);
        parallelTransition.play();
        parallelTransition.setCycleCount(1);
        parallelTransition.setAutoReverse(false);

        //淡入
        FadeTransition fadeTransitionOut = new FadeTransition(Duration.millis(500), node);
        fadeTransitionOut.setFromValue(0);
        fadeTransitionOut.setToValue(1);
        fadeTransitionOut.setCycleCount(1);
        fadeTransitionOut.setAutoReverse(false);
        //缩小
        ScaleTransition scaleTransitionOut = new ScaleTransition(Duration.millis(500), node);
        scaleTransitionOut.setToX(1);
        scaleTransitionOut.setToY(1);
        scaleTransitionOut.setCycleCount(1);
        scaleTransitionOut.setAutoReverse(false);

        ParallelTransition parallelTransitionOut = new ParallelTransition(fadeTransitionOut,scaleTransitionOut);
        parallelTransitionOut.play();

        SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition,parallelTransitionOut);
        sequentialTransition.setCycleCount(1);
        sequentialTransition.setAutoReverse(false);
        sequentialTransition.play();
    }
}

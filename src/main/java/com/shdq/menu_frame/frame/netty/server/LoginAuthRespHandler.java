package com.shdq.menu_frame.frame.netty.server;

import com.shdq.menu_frame.frame.netty.vo.MessageType;
import com.shdq.menu_frame.frame.netty.vo.NettyHeader;
import com.shdq.menu_frame.frame.netty.vo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shdq-fjy
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(LoginAuthRespHandler.class);
    //用以检测用户是否重复登录的缓存
    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<>();
    //用户登录的白名单
    private String[] whiteList = new String[]{"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        //如果是握手请求消息处理，其它消息透传
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.getValue()){
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            //重复登录拒绝
            if (nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte)-1);
            }else{
                //检测用户是否在白名单中
                InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String WIP : whiteList){
                    if (WIP.equals(ip)){
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildResponse((byte)0) : buildResponse((byte)-1);
                if (isOK){
                    nodeCheck.put(nodeIndex,true);
                }
            }
            log.info("The login response is:"+loginResp+" body["+loginResp.getBody()+"]");
            ctx.writeAndFlush(loginResp);
            //销毁消息
            ReferenceCountUtil.release(msg);
        }else {
            //注释后可演示不往下传递消息的情况，之后的handler收不到消息
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte b) {
        NettyMessage message = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.LOGIN_RESP.getValue());
        message.setHeader(header);
        message.setBody(String.valueOf(b));
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //清除缓存
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}

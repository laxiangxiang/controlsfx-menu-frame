package com.shdq.menu_frame.frame.netty;

import com.shdq.menu_frame.frame.netty.vo.NettyConstant;

import java.util.Scanner;

/**
 * 演示业务方如何调用Netty客户端
 * @author shdq-fjy
 */
public class BusiClient {

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient(NettyConstant.REMOTE_IP,NettyConstant.REMOTE_PORT);
        new Thread(client).start();
        while (!client.isConnected()){
            synchronized (client){
                client.wait();
            }
        }
        System.out.println("网络通信已准备好，可以进行业务操作了。。。。。。");
        Scanner s = new Scanner(System.in);
        while (true){
            String msg = s.next();
            if (msg == null){
                continue;
            }else if ("q".equals(msg.toLowerCase())){
                client.close();
                while (!client.isConnected()){
                    synchronized (client){
                        client.wait();
                    }
                }
                s.close();
                System.exit(1);
            }else {
                client.send(msg);
            }
        }
    }
}

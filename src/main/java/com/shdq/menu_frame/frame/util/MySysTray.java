package com.shdq.menu_frame.frame.util;

import com.shdq.menu_frame.frame.MenuFrame;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;

/**
 * 自定义系统托盘
 * @author shdq-fjy
 */
@Slf4j
public class MySysTray {
    private TrayIcon trayIcon = null;
    private Timeline timeline = new Timeline();
    private Stage stage;
    private CheckUtil checkUtil;

    public MySysTray(Stage stage,CheckUtil checkUtil) {
        this.stage = stage;
        this.checkUtil = checkUtil;
    }

    public void initSystemTray() {
        // 1、创建托盘按钮
        PopupMenu popupMenu = new PopupMenu();
        //如果系统托盘中文乱码（原因是系统托盘默认使用windows系统编码格式，而JVM中的编码格式跟windows默认编码不一致），
        // 肯定是你的JVM的环境变量没有设置这个-Dfile.encoding=GB18030，这个时候如果你用的Eclipse在Run Configurations中的VM arguments中添加即可
        //如果你是打包成EXE程序了，那么你一般不会遇到系统托盘中文乱码问题。
        MenuItem showItem = new MenuItem("显示"); popupMenu.add(showItem);
        showItem.setFont(Font.decode("Arial-BOLD-18"));
        popupMenu.addSeparator();
        MenuItem hideItem = new MenuItem("隐藏"); popupMenu.add(hideItem);
        hideItem.setFont(Font.decode("Arial-BOLD-18"));
        popupMenu.addSeparator();
        MenuItem quitItem = new MenuItem("退出"); popupMenu.add(quitItem);
        quitItem.setFont(Font.decode("Arial-BOLD-18"));
//        Menu menu = new Menu("多项");
//        MenuItem menuItem = new MenuItem("项目1");
//        MenuItem menuItem1 = new MenuItem("项目2");
//        menu.add(menuItem);
//        menu.add(menuItem1);
//        popupMenu.add(menu);
        // 2、创建动作事件监听器（awt的古老操作）
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 多次使用显示和隐藏设置false
                Platform.setImplicitExit(false);
                // 获得按钮并执行相应操作
                MenuItem item = (MenuItem) e.getSource();
                if (item.getLabel().equals("退出")) {
                    // 移除托盘图标
                    SystemTray.getSystemTray().remove(trayIcon);
                    // 关闭应用
                    Platform.exit();
                    //关闭与服务端的连接
                    checkUtil.getNettyClient().close();
                    // 延迟500毫秒关闭进程
                    Timeline timeline = new Timeline();
                    timeline.setCycleCount(1);
                    timeline.setAutoReverse(false);
                    KeyFrame keyFrame = new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            // 彻底退出进程
                            System.exit(0);
                        }
                    });
                    timeline.getKeyFrames().clear();
                    timeline.getKeyFrames().add(keyFrame);
                    timeline.play();
                }

                else if (item.getLabel().equals("显示")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }

                else if (item.getLabel().equals("隐藏")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.hide();
                        }
                    });
                }
            }
        };
        // 3、给按钮添加动作事件监听
        showItem.addActionListener(actionListener);
        quitItem.addActionListener(actionListener);
        hideItem.addActionListener(actionListener);

        try {
            InputStream inputStream = getClass().getResourceAsStream("/images/controlsfx-API.png");
            // 4、我们的托盘图标
            trayIcon = new TrayIcon(ImageIO.read(inputStream), MenuFrame.appName, popupMenu);
            trayIcon.setImageAutoSize(true);
            // 5、鼠标悬浮时的提示信息
            trayIcon.setToolTip(MenuFrame.appName);
            // 6、添加到系统托盘
            SystemTray.getSystemTray().add(trayIcon);
            // 7、给托盘图标添加鼠标监听
            trayIcon.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // 多次使用显示和隐藏设置false
                    Platform.setImplicitExit(false);
                    // 鼠标单击一次
                    if (e.getClickCount() == 2) {
                        timeline.stop();
                        timeline.setCycleCount(1);
                        timeline.setAutoReverse(false);
                        KeyFrame keyFrame = new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                if (stage.isShowing()) {
                                    stage.hide();
                                }else{
                                    stage.show();
                                }
                            }
                        });
                        timeline.getKeyFrames().clear();
                        timeline.getKeyFrames().add(keyFrame);
                        timeline.play();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
            });
        } catch (Exception e) {
            log.debug(e.getCause().getMessage());
            e.printStackTrace();
        }
    }
}

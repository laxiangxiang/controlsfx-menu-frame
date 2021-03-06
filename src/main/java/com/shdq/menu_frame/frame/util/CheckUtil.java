package com.shdq.menu_frame.frame.util;

import com.shdq.menu_frame.frame.MenuFrame;
import com.shdq.menu_frame.frame.netty.NettyClient;
import javafx.animation.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * 资源检查工具类，做初始化工作
 *
 * @author shdq-fjy
 */
@Slf4j
public class CheckUtil {
    private ImageView dbImageView = new ImageView();
    private Label dbLabel = new Label();
    private ProgressIndicator dbProgressIndicator = new ProgressIndicator();
    private Button dbRetryButton = new Button("重试");
    private ImageView serverImageView = new ImageView();
    private Label serverLabel = new Label();
    private ProgressIndicator serverProgressIndicator = new ProgressIndicator();
    private Button heartBeatRetryButton = new Button("重试");
    private NettyClient nettyClient;
    private Node node;
    private LoginUtil loginUtil;
    private boolean dbCheckSuccess;
    private boolean heartBeatCheckSuccess;

    public CheckUtil() {
    }

    public CheckUtil(Node node,LoginUtil loginUtil) {
        this.node = node;
        this.loginUtil = loginUtil;
    }

    public void check() {
        Stage stage = new Stage();
        showCheckInterface(stage);
        Task dbTask = createDBTask();
        Task heartBeatTask = createHeartbeatTask(MenuFrame.serverIp, MenuFrame.serverHeartBeatPort);
        Task waitTask = createWaitTask();
        waitTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean o = (Boolean)newValue;
            if (o) {
                stage.close();
                loginUtil.showLoginInterface();
            }
        });
        check(dbTask, dbLabel, dbProgressIndicator, dbImageView,dbRetryButton);
        dbTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            Double o = (Double) newValue;
            if (o == 1.0 || o == -1.0) {
                if (o == 1.0){
                    dbCheckSuccess = true;
                }else {
                    dbCheckSuccess = false;
                }
                check(heartBeatTask, serverLabel, serverProgressIndicator, serverImageView,heartBeatRetryButton);
            }
        });
        heartBeatTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            double o = (Double)newValue;
            if (o == 1.0 || o == -1.0) {
                if (o==1.0){
                    heartBeatCheckSuccess = true;
                }else {
                    heartBeatCheckSuccess = false;
                }
                check(waitTask);
            }
        });
    }

    private void showCheckInterface(Stage stage) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridPane");
        gridPane.setPadding(new Insets(5, 10, 10, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMinWidth(600);
        gridPane.setMinHeight(400);
        gridPane.setGridLinesVisible(false);
        Label label = new Label();
        label.setText(MenuFrame.appName + " initializing...");
        label.getStyleClass().add("title-label");
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setMargin(label, new Insets(25, 0, 20, 0));
        dbRetryButton.setOnMouseClicked(event -> {
            Task dbTask = createDBTask();
            Task waitTask = createWaitTask();
            waitTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                Boolean o = (Boolean)newValue;
                if (o){
                    stage.close();
                    loginUtil.showLoginInterface();
                }
            });
            dbTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                double o = (Double)newValue;
                if (o == 1.0 || o == -1.0) {
                    if (o == 1.0){
                        dbCheckSuccess = true;
                    }else {
                        dbCheckSuccess = false;
                    }
                    check(waitTask);
                }
            });
            check(dbTask, dbLabel, dbProgressIndicator, dbImageView,dbRetryButton);
        });
        heartBeatRetryButton.setOnMouseClicked(event -> {
            Task heartBeatTask = createHeartbeatTask(MenuFrame.serverIp,MenuFrame.serverHeartBeatPort);
            Task waitTask = createWaitTask();
            waitTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                Boolean o = (Boolean)newValue;
                if (o){
                    stage.close();
                    loginUtil.showLoginInterface();
                }
            });
            heartBeatTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                double o = (Double)newValue;
                if (o == 1.0 || o == -1.0) {
                    if (o==1.0){
                        heartBeatCheckSuccess = true;
                    }else {
                        heartBeatCheckSuccess = false;
                    }
                    check(waitTask);
                }
            });
            check(heartBeatTask, serverLabel, serverProgressIndicator, serverImageView,heartBeatRetryButton);
        });
        dbRetryButton.getStyleClass().add("retry-button");
        heartBeatRetryButton.getStyleClass().add("retry-button");
        HBox dbHBox = createHBox("测试数据库", dbImageView, dbLabel, dbProgressIndicator,dbRetryButton);
        HBox serverHBox = createHBox("测试后台服务", serverImageView, serverLabel, serverProgressIndicator,heartBeatRetryButton);
        GridPane.setHalignment(dbHBox, HPos.CENTER);
        gridPane.add(label, 0, 0);
        gridPane.add(dbHBox, 0, 1);
        gridPane.add(serverHBox, 0, 2);
        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add(this.getClass().getResource("/css/fxsampler.css").toExternalForm());
        stage.setWidth(500);
        stage.setHeight(300);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createHBox(String text, ImageView imageView, Label label, ProgressIndicator progressIndicator,Button retryButton) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        imageView.setImage(new Image("/images/correct.png"));
        imageView.setVisible(false);
        HBox.setMargin(imageView, new Insets(10, 5, 10, 0));
        label.setText(text);
        label.getStyleClass().add("label");
        label.setMinWidth(160);
        label.setVisible(false);
        HBox.setMargin(label, new Insets(10, 5, 10, 5));
        progressIndicator.setMaxHeight(20);
        progressIndicator.setMaxWidth(20);
        progressIndicator.getStyleClass().add("progress");
        progressIndicator.setVisible(false);
        HBox.setMargin(progressIndicator, new Insets(10, 0, 10, 5));
        retryButton.setVisible(false);
        HBox.setMargin(retryButton,new Insets(10,10,0,10));
        hBox.getChildren().addAll(imageView, label, progressIndicator,retryButton);
        return hBox;
    }

    private void check(Task<Boolean> task){
        Service<Boolean> service = new Service() {
            @Override
            protected Task createTask() {
                return task;
            }
        };
        service.start();
    }

    private void check(Task<Double> task, Label label, ProgressIndicator indicator, ImageView imageView,Button retryButton) {
        label.setVisible(true);
        indicator.setVisible(true);
        retryButton.setDisable(false);
        retryButton.setVisible(false);
        imageView.setVisible(false);
        Service<Double> service = new Service() {
            @Override
            protected Task createTask() {
                return task;
            }
        };
        //fixme:如果不注释dbProgressIndicator不显示？？
//        dbProgressIndicator.progressProperty().bind(task.progressProperty());
        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            label.setText(service.getMessage());
            Double result = service.getValue();
            if (result == null) {

            } else if (result == 0) {
                label.setTextFill(Color.gray(0.3));
            } else if (result == 1) {
                label.setTextFill(Color.rgb(19, 167, 68));
                imageView.setVisible(true);
                imageViewTransition(imageView);
                imageView.setImage(new Image("/images/correct.png"));
                indicator.setVisible(false);
                retryButton.setVisible(false);
            } else if (result == -1) {
                label.setTextFill(Color.rgb(255, 0, 0));
                imageView.setVisible(true);
                imageViewTransition(imageView);
                imageView.setImage(new Image("/images/wrong.png"));
                indicator.setVisible(false);
                retryButton.setVisible(true);
                retryButtonTransition(retryButton);
            }
        });
        service.start();
    }

    private Task<Double> createDBTask() {
        return new Task() {
            @Override
            protected Double call() throws Exception {
                updateValue(0.0);
                updateProgress(0, 1);
                updateMessage("正在解析数据库地址...");
                try {
                    //判断是要连接什么数据库
                    updateProgress(0, 1);
                    DBPoolUtil.getConnection();
                    Thread.sleep(5000);
                    updateValue(1.0);
                    updateProgress(1, 1);
                    updateMessage("数据库准备就绪！");
                    return 1.0;
                } catch (SQLException e) {
                    updateValue(-1.0);
                    updateProgress(-1, 1);
                    updateMessage("数据库连接失败！");
                    return -1.0;
                }
            }
        };
    }

    private Task<Double> createHeartbeatTask(String ip, int port) {
        return new Task() {
            @Override
            protected Double call() {
                updateValue(0.0);
                updateProgress(0, 1);
                updateMessage("正在准备建立心跳连接...");
                //新启动一个线程，否则javaFX Application线程结束不了
                nettyClient = new NettyClient(ip, port);
                nettyClient.setNode(node);
                Thread t = new Thread(nettyClient);
                try {
                    t.start();
                    //等待客户端连接后的通知，不管成功与否,这里不需要循环判断连接与否
//                    while (!nettyClient.isConnected()) {
                    synchronized (nettyClient) {
                        nettyClient.wait(5000);
                    }
//                    }
                    if (nettyClient.isConnected()){
                        Thread.sleep(5000);
                        updateValue(1.0);
                        updateProgress(1, 1);
                        updateMessage("与服务端心跳连接成功！");
                        return 1.0;
                    }else {
                        updateValue(-1.0);
                        updateProgress(-1, 1);
                        updateMessage("与服务端连接异常。");
                        return -1.0;
                    }
                } catch (Exception e) {
                    updateValue(-1.0);
                    updateProgress(-1, 1);
                    updateMessage("与服务端连接异常。");
                    return -1.0;
                }
            }
        };
    }

    /**
     * 判断之前任务执行是否成功的任务，没有成功的话不关闭当前界面，也不显示下一个界面，手动重试直到都成功
     * @return
     */
    private Task<Boolean> createWaitTask(){
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (dbCheckSuccess && heartBeatCheckSuccess){
                    Thread.sleep(2000);
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 图标飞入效果
     * @param imageView
     */
    private void imageViewTransition(ImageView imageView) {
        //淡入效果
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), imageView);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(false);
//        fadeTransition.play();
        //平移效果
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500), imageView);
        translateTransition.setFromX(-200);
        translateTransition.setToX(0);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
//        translateTransition.play();
        //旋转效果
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), imageView);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);
        rotateTransition.setAutoReverse(false);
//        rotateTransition.play();
        //缩放效果
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(2000), imageView);
        scaleTransition.setToX(2);
        scaleTransition.setToY(2);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(false);
//        scaleTransition.play();
        //并行执行动画
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, translateTransition);
        parallelTransition.play();
        parallelTransition.setCycleCount(1);
        parallelTransition.setAutoReverse(false);
    }

    /**
     * 重试按钮
     * @param node
     */
    private void retryButtonTransition(Node node){
        SequentialTransition sequentialTransition = new SequentialTransition();
        int xStart = 100;
        int durationTime = 250;
        for (int i = 0; i < 8; i++){
            TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(durationTime), node);
            translateTransition1.setFromX(xStart);
            translateTransition1.setToX(0);
            translateTransition1.setCycleCount(1);
            translateTransition1.setAutoReverse(false);
            xStart = xStart /2;
            durationTime = durationTime / 2;
            TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(durationTime), node);
            translateTransition2.setFromX(0);
            translateTransition2.setToX(xStart);
            translateTransition2.setCycleCount(1);
            translateTransition2.setAutoReverse(false);
            sequentialTransition.getChildren().addAll(translateTransition1,translateTransition2);
        }
        sequentialTransition.setAutoReverse(false);
        sequentialTransition.setCycleCount(1);
        sequentialTransition.play();
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }
}

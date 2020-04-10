package com.shdq.menu_frame.frame.util;

import com.alibaba.fastjson.JSON;
import com.shdq.menu_frame.frame.MenuFrame;
import com.shdq.menu_frame.http.Result;
import com.shdq.menu_frame.model.User;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.HashMap;

/**
 * @author shdq-fjy
 */
public class LoginUtil {
    private MenuFrame menuFrame;
    private ImageView userImageView;
    private Label userLabel;

    public LoginUtil(MenuFrame menuFrame, ImageView userImageView, Label userLabel) {
        this.menuFrame = menuFrame;
        this.userImageView = userImageView;
        this.userLabel = userLabel;
    }

    protected void showLoginInterface() {
        Stage stage = new Stage();
        GridPane pane = new GridPane();
        pane.getStyleClass().add("gridPane");
        pane.setPadding(new Insets(5, 10, 10, 10));
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPrefWidth(600);
        pane.setPrefHeight(400);
        pane.setGridLinesVisible(false);
        Label loginLabel = new Label();
        loginLabel.setText("请登录");
        loginLabel.getStyleClass().add("title-label");
        Label usernameLabel = new Label();
        usernameLabel.setText("用户名：");
        usernameLabel.getStyleClass().add("input-label");
        usernameLabel.setPrefWidth(100);
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("请输入用户名");
        usernameTextField.setOnKeyTyped(event -> usernameTextField.getStyleClass().remove("non-input-text-field"));
        Label passwordLabel = new Label();
        HBox usernameBox = new HBox();
        usernameBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(usernameLabel, new Insets(10, 5, 10, 5));
        HBox.setMargin(usernameTextField, new Insets(10, 0, 10, 5));
        usernameBox.getChildren().addAll(usernameLabel, usernameTextField);
        passwordLabel.setText("密码：");
        passwordLabel.getStyleClass().add("input-label");
        passwordLabel.setPrefWidth(100);
        TextField passwordTextField = new PasswordField();
        passwordTextField.setPromptText("请输入密码");
        passwordTextField.setOnKeyTyped(event -> passwordTextField.getStyleClass().remove("non-input-text-field"));
        HBox passwordBox = new HBox();
        passwordBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(passwordLabel, new Insets(10, 5, 10, 5));
        HBox.setMargin(passwordTextField, new Insets(10, 0, 10, 5));
        passwordBox.getChildren().addAll(passwordLabel, passwordTextField);
        Button button = new Button();
        button.setText("登陆");
        button.getStyleClass().add("login-button");
        ProgressIndicator progress = new ProgressIndicator();
        progress.setVisible(false);
        progress.getStyleClass().add("progress");
        Label infoLabel = new Label();
        infoLabel.setVisible(false);
        infoLabel.getStyleClass().add("info-label");
        button.setOnMouseClicked(event -> {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();
            if (StringUtils.isBlank(username)) {
                usernameTextField.getStyleClass().add("non-input-text-field");
                usernameTextField.setText("");
                nonInputTextFieldTransition(usernameTextField);
                return;
            }
            if (StringUtils.isBlank(password)) {
                passwordTextField.getStyleClass().add("non-input-text-field");
                passwordTextField.setText("");
                nonInputTextFieldTransition(passwordTextField);
                return;
            }
            button.setVisible(false);
            progress.setVisible(true);
            Task task = createAuthenticationTask(username, password);
            task.valueProperty().addListener((observable, oldValue, newValue) -> {
                Double o = (Double) newValue;
                if (o == 1) {
                    try {
                        String name = ((User)menuFrame.user).getUserName();
                        userLabel.setText(name);
                        File file = ResourceUtils.getFile("/images/"+name+".png");
                        if (file.exists()){
                            userImageView.setImage(new Image("/images/"+name+".png"));
                        }else {
                            userImageView.setImage(new Image("/images/defaultUserAvatar.png"));
                        }
                        userLabel.setVisible(true);
                        userImageView.setVisible(true);
                        menuFrame.initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stage.close();
                }
            });
            authenticate(task, infoLabel, button, progress, stage);
        });
        GridPane.setHalignment(loginLabel, HPos.CENTER);
        GridPane.setHgrow(loginLabel, Priority.ALWAYS);
        GridPane.setMargin(loginLabel, new Insets(25, 0, 20, 0));
        GridPane.setHalignment(passwordBox, HPos.CENTER);
        GridPane.setHalignment(usernameBox, HPos.CENTER);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setHalignment(progress, HPos.CENTER);
        GridPane.setHalignment(infoLabel, HPos.CENTER);
        pane.add(loginLabel, 0, 0);
        pane.add(usernameBox, 0, 1);
        pane.add(passwordBox, 0, 2);
        pane.add(button, 0, 3);
        pane.add(progress, 0, 3);
        pane.add(infoLabel, 0, 4);
        stage.setWidth(500);
        stage.setHeight(300);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("/css/fxsampler.css");
        stage.setScene(scene);
        stage.show();
    }

    private void authenticate(Task task, Label infoLabel, Button loginButton, ProgressIndicator progressIndicator, Stage stage) {
        Service<Double> service = new Service<Double>() {
            @Override
            protected Task<Double> createTask() {
                return task;
            }
        };
        //fixme:如果不注释dbProgressIndicator不显示？？
//        progressIndicator.progressProperty().bind(task.progressProperty());
        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            infoLabel.setVisible(true);
            progressIndicator.setVisible(true);
            loginButton.setVisible(false);
            infoLabel.setText(service.getMessage());
            Double result = service.getValue();
            if (result == 0) {
                infoLabel.setTextFill(Color.gray(0.3));
                return;
            } else if (result == 1) {
                infoLabel.setTextFill(Color.rgb(19, 167, 68));
                progressIndicator.setVisible(false);
                loginButton.setVisible(true);
                return;
            } else if (result == -1) {
                infoLabel.setTextFill(Color.rgb(255, 0, 0));
                progressIndicator.setVisible(false);
                loginButton.setVisible(true);
                return;
            }
        });
        service.start();
    }

    private Task<Double> createAuthenticationTask(String username, String password) {
        String url = "http://localhost:8080/user/get?username=" + username + "&password=" + password;
        return new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                updateValue(0.0);
                updateProgress(0, 1);
                updateMessage("正在认证...");
                updateProgress(0, 1);
                Result result = RestTemplateUtil.getRestTemplate().getForObject(url, Result.class);
                int code = result.getCode();
                if (code != 200) {
                    updateValue(-1.0);
                    updateProgress(-1, 1);
                    updateMessage(code + " " + result.getMessage());
                    return -1.0;
                }
                HashMap<String, Object> userMap = (HashMap<String, Object>) result.getData();
                menuFrame.user = JSON.parseObject(JSON.toJSONString(userMap), User.class);
                if (menuFrame.user == null) {
                    updateValue(-1.0);
                    updateProgress(-1, 1);
                    updateMessage(result.getMessage());
                    return -1.0;
                } else {
                    updateValue(1.0);
                    updateProgress(1, 1);
                    updateMessage(result.getMessage());
                    return 1.0;
                }
            }
        };
    }

    /**
     * 输入框来回抖动效果
     */
    private void nonInputTextFieldTransition(Node node) {
        //平移效果
        TranslateTransition translateTransition1 = new TranslateTransition(Duration.millis(10), node);
        translateTransition1.setFromX(-2);
        translateTransition1.setToX(0);
        translateTransition1.setCycleCount(1);
        translateTransition1.setAutoReverse(false);
        TranslateTransition translateTransition2 = new TranslateTransition(Duration.millis(10), node);
        translateTransition2.setFromX(-2);
        translateTransition2.setToX(2);
        translateTransition2.setCycleCount(1);
        translateTransition2.setAutoReverse(false);
        TranslateTransition translateTransition3 = new TranslateTransition(Duration.millis(10), node);
        translateTransition3.setFromX(2);
        translateTransition3.setToX(0);
        translateTransition3.setCycleCount(1);
        translateTransition3.setAutoReverse(false);
        SequentialTransition transition = new SequentialTransition(translateTransition1, translateTransition2, translateTransition3);
        transition.setCycleCount(5);
        transition.setAutoReverse(false);
        transition.play();
    }
}

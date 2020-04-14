package com.shdq.menu_frame.frame.util;

import com.shdq.menu_frame.frame.MenuFrame;
import com.shdq.menu_frame.frame.model.Project;
import com.shdq.menu_frame.http.Result;
import com.shdq.menu_frame.model.User;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author shdq-fjy
 */
public class UserMenuUtil {

    private ContextMenu contextMenu = new ContextMenu();

    private boolean isMenuOnShow = false;

    public boolean isMenuOnShow() {
        return isMenuOnShow;
    }

    public void showUserMenu(ImageView imageView, MenuFrame menuFrame){
        isMenuOnShow = true;
        contextMenu.getItems().clear();
        MenuItem menuItem1 = new MenuItem("menu1");
        menuItem1.setOnAction(event -> {

        });
        MenuItem menuItem2 = new MenuItem("menu2");
        menuItem2.setOnAction(event -> {

        });
        Menu settingMenu = new Menu("设置");
        MenuItem upLoadImage = new MenuItem("上传头像");
        upLoadImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择图片（建议使用大小为32*32格式为png的图片）");
            fileChooser.setInitialDirectory(new File(PathUtil.getFilePath()));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("所有文件","*"),
                    new FileChooser.ExtensionFilter("PNG(*.png)","*.png")
            );
            File file = fileChooser.showOpenDialog(menuFrame.stage);
            if (file != null){
                if (file.getPath().endsWith(".png")){
                    //访问上传用户图片后台接口
                    Result result = RestTemplateUtil.fileUpload(file,((User)menuFrame.user).getUserName());
                    if (result.getCode() == 200){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(result.getMessage());
                        alert.setContentText("上传成功。");
                        alert.showAndWait();
                        //设置为新上传的头像
                        menuFrame.userImageView.setImage(new Image("http://localhost:8080/static/"+"head-"+((User)menuFrame.user).getUserName()+".png"));
                    }else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText(result.getMessage());
                        alert.setContentText(result.getMessage()+"\n\t"+result.getCode());
                        alert.showAndWait();
                    }
                }
            }
        });
        MenuItem userInfo = new MenuItem("用户信息");
        userInfo.setOnAction(event -> {

        });
        settingMenu.getItems().addAll(upLoadImage,userInfo);
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem existLogin = new MenuItem("退出登陆");
        existLogin.setOnAction(event -> {
            contextMenu.hide();
            if (!MenuFrame.showOverViewOnRoot){
                menuFrame.menuTreeView.setRoot(null);
                menuFrame.homeButton.requestFocus();
            }else {
                menuFrame.menuTreeView.setRoot(((new Project()).getMenuTree().getRoot().createTreeItem()));
            }
            menuFrame.searchBox.setDisable(true);
            menuFrame.userImageView.setImage(new Image("/images/defaultUserAvatarUnlogin.png"));
            menuFrame.userImageView.setOnMouseClicked(event1 -> {
                if (!isMenuOnShow){
                    showUserMenuUnLogin(imageView,menuFrame);
                }else {
                    hidden();
                }
            });
            menuFrame.userLabel.setText("未登录");
            menuFrame.changeToHomeTab();
        });
        contextMenu.getItems().addAll(menuItem1,menuItem2,settingMenu,separatorMenuItem,existLogin);
        contextMenu.show(imageView, Side.BOTTOM,-30,0);
    }

    private void showUserMenuUnLogin(ImageView imageView, MenuFrame menuFrame){
        isMenuOnShow = true;
        contextMenu.getItems().clear();
        MenuItem menuItem1 = new MenuItem("menu1");
        menuItem1.setDisable(true);
        MenuItem menuItem2 = new MenuItem("menu2");
        menuItem2.setDisable(true);
        Menu settingMenu = new Menu("设置");
        MenuItem upLoadImage = new MenuItem("上传头像");
        upLoadImage.setDisable(true);
        MenuItem userInfo = new MenuItem("用户信息");
        userInfo.setDisable(true);
        settingMenu.getItems().addAll(upLoadImage,userInfo);
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem login = new MenuItem("登陆");
        login.setOnAction(event -> {
            contextMenu.hide();
            menuFrame.loginUtil.showLoginInterface();
            menuFrame.searchBox.setDisable(false);
        });
        contextMenu.getItems().addAll(menuItem1,menuItem2,settingMenu,separatorMenuItem,login);
        contextMenu.show(imageView, Side.BOTTOM,-30,0);
    }

    public void hidden(){
        isMenuOnShow = false;
        contextMenu.hide();
    }
}

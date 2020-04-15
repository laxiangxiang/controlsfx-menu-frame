package com.shdq.menu_frame.frame.util;

import com.alibaba.fastjson.JSON;
import com.shdq.menu_frame.frame.MenuFrame;
import com.shdq.menu_frame.frame.model.Project;
import com.shdq.menu_frame.http.Result;
import com.shdq.menu_frame.model.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;

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
            hidden();
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
            hidden();
            showUserInfoInterface(menuFrame);
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

    private void showUserInfoInterface(MenuFrame menuFrame) {
        hidden();
        Stage stage  = new Stage();
        GridPane pane = new GridPane();
        Label titleLabel = new Label();
        titleLabel.setText("用户信息");
        titleLabel.getStyleClass().add("title-label");
        Label usernameLabel = new Label();
        usernameLabel.setMinWidth(100);
        usernameLabel.getStyleClass().add("input-label");
        usernameLabel.setText("用户名：");
        TextField usernameTextField = new TextField();
        usernameTextField.setDisable(true);
        HBox usernameBox = new HBox();
        usernameBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(usernameLabel, new Insets(10, 5, 10, 5));
        HBox.setMargin(usernameTextField, new Insets(10, 0, 10, 5));
        usernameBox.getChildren().addAll(usernameLabel,usernameTextField);
        Label userNoLabel = new Label();
        userNoLabel.setMinWidth(100);
        userNoLabel.getStyleClass().add("input-label");
        userNoLabel.setText("用户编号：");
        TextField userNoTextField = new TextField();
        userNoTextField.setDisable(true);
        HBox userNoBox = new HBox();
        userNoBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(userNoLabel, new Insets(10, 5, 10, 5));
        HBox.setMargin(userNoTextField, new Insets(10, 0, 10, 5));
        userNoBox.getChildren().addAll(userNoLabel,userNoTextField);
        Label roleLabel = new Label("角色列表");
        TableView<RoleModelProperty> roleTableView = new TableView<>();
        TableView<PermissionModelProperty> permissionModelPropertyTableView = new TableView<>();
        TableView<MenuModelProperty> menuModelPropertyTableView = new TableView<>();
        roleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //点击选中的角色名称，在permissionTableView显示角色下的权限
            if (newValue == null){
                return;
            }
            RoleModelProperty roleModelProperty = (RoleModelProperty)newValue;
            Result result = (Result) RestTemplateUtil.getRestTemplate().getForObject("http://localhost:8080/role/getRole?roleId="+roleModelProperty.getId(),Result.class);
            HashMap<String,Object> roleMap = (HashMap<String, Object>) result.getData();
            Role role = JSON.parseObject(JSON.toJSONString(roleMap),Role.class);
            ObservableList<PermissionModelProperty> permissionModelProperties = FXCollections.observableArrayList();
            role.getPermissions().forEach(permission -> {
                permissionModelProperties.add(new PermissionModelProperty(permission.getId(),permission.getPermissionName(),permission.getPermissionNo()));
            });
            permissionModelPropertyTableView.setItems(permissionModelProperties);
        });
        TableColumn<RoleModelProperty,String> roleStringTableColumn1 = new TableColumn<>();
        roleStringTableColumn1.setCellValueFactory(param -> param.getValue().nameProperty());
        TableColumn<RoleModelProperty,String> roleStringTableColumn2 = new TableColumn<>();
        roleStringTableColumn2.setCellValueFactory(param -> param.getValue().noProperty());
        roleStringTableColumn1.setText("角色名称");
        roleStringTableColumn2.setText("角色编号");
        roleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roleTableView.getColumns().addAll(roleStringTableColumn1,roleStringTableColumn2);
        VBox roleVBox = new VBox();
        roleVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(roleLabel,Priority.ALWAYS);
        VBox.setVgrow(roleTableView,Priority.ALWAYS);
        VBox.setMargin(roleLabel,new Insets(0,0,5,0));
        roleVBox.getChildren().addAll(roleLabel,roleTableView);
        Label permissionLabel = new Label("权限列表");
        permissionModelPropertyTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //点击选中的权限名称，在menuTableView显示权限下的菜单
            if (newValue == null){
                return;
            }
            PermissionModelProperty permissionModelProperty = (PermissionModelProperty) newValue;
            Result result = (Result)RestTemplateUtil.getRestTemplate().getForObject("http://localhost:8080/permission/getPermission?permissionId="+permissionModelProperty.getId(),Result.class);
            Map<String,Object> permissionMap = (Map<String, Object>) result.getData();
            Permission permission = JSON.parseObject(JSON.toJSONString(permissionMap),Permission.class);
            List<ParentMenu> parentMenus = permission.getParentMenus();
            List<com.shdq.menu_frame.frame.entity.Menu> menus = new ArrayList<>();
            parentMenus.forEach(parentMenu -> {
                Result result1 = (Result)RestTemplateUtil.getRestTemplate().getForObject("http://localhost:8080/parent/menu/getParentMenu?parentMenuId="+parentMenu.getId(),Result.class);
                ParentMenu parentMenu1 = JSON.parseObject(JSON.toJSONString(result1.getData()),ParentMenu.class);
                parentMenu1.getChildMenus().forEach(childMenu -> parentMenu1.getSubMenus().add((com.shdq.menu_frame.frame.entity.Menu)childMenu));
                menus.add(parentMenu1);
            });
            ObservableList<MenuModelProperty> menuModelProperties = FXCollections.observableArrayList();
            Map<String, com.shdq.menu_frame.frame.entity.Menu> menuMap = new HashMap<>();
            new MenuScanner().parseMenu(menus,menuMap,new StringBuffer());
            Set<Map.Entry<String, com.shdq.menu_frame.frame.entity.Menu>> entrySet = menuMap.entrySet();
            Iterator iterator = entrySet.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, com.shdq.menu_frame.frame.entity.Menu> entry = (Map.Entry<String, com.shdq.menu_frame.frame.entity.Menu>)iterator.next();
                String menuName = entry.getKey();
                MenuModelProperty menuModelProperty;
                menuModelProperty = new MenuModelProperty(menuName);
                menuModelProperties.add(menuModelProperty);
            }
            menuModelPropertyTableView.setItems(menuModelProperties);
        });
        TableColumn<PermissionModelProperty,String> permissionModelPropertyStringTableColumn1 = new TableColumn<>();
        permissionModelPropertyStringTableColumn1.setCellValueFactory(param -> param.getValue().permissionNameProperty());
        TableColumn<PermissionModelProperty,String> permissionModelPropertyStringTableColumn2 = new TableColumn<>();
        permissionModelPropertyStringTableColumn2.setCellValueFactory(param -> param.getValue().permissionNoProperty());
        permissionModelPropertyStringTableColumn1.setText("权限名称");
        permissionModelPropertyStringTableColumn2.setText("权限编号");
        permissionModelPropertyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        permissionModelPropertyTableView.getColumns().addAll(permissionModelPropertyStringTableColumn1,permissionModelPropertyStringTableColumn2);
        VBox permissionVBox = new VBox();
        permissionVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(permissionLabel,Priority.ALWAYS);
        VBox.setVgrow(permissionModelPropertyTableView,Priority.ALWAYS);
        VBox.setMargin(permissionLabel,new Insets(0,0,5,0));
        permissionVBox.getChildren().addAll(permissionLabel,permissionModelPropertyTableView);
        Label menuLabel = new Label("菜单列表");
        menuModelPropertyTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //点击菜单项没有操作
        });
        TableColumn<MenuModelProperty,String> menuModelPropertyStringTableColumn1 = new TableColumn<>();
        menuModelPropertyStringTableColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MenuModelProperty, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MenuModelProperty, String> param) {
                return param.getValue().nameProperty();
            }
        });
        menuModelPropertyStringTableColumn1.setText("菜单名称");
        menuModelPropertyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        menuModelPropertyTableView.getColumns().addAll(menuModelPropertyStringTableColumn1);
        VBox menuVBox = new VBox();
        menuVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(menuLabel,Priority.ALWAYS);
        VBox.setVgrow(menuModelPropertyTableView,Priority.ALWAYS);
        VBox.setMargin(menuLabel,new Insets(0,0,5,0));
        menuVBox.getChildren().addAll(menuLabel,menuModelPropertyTableView);
        HBox tableViewHBox = new HBox();
        tableViewHBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(roleVBox, new Insets(10, 5, 10, 0));
        HBox.setMargin(permissionVBox, new Insets(10, 5, 10, 5));
        HBox.setMargin(menuVBox, new Insets(10, 0, 10, 5));
        tableViewHBox.getChildren().addAll(roleVBox,permissionVBox,menuVBox);
        pane.setPadding(new Insets(5, 10, 10, 10));
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setGridLinesVisible(false);
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        GridPane.setHgrow(titleLabel, Priority.ALWAYS);
        GridPane.setVgrow(titleLabel,Priority.ALWAYS);
        GridPane.setHgrow(usernameBox, Priority.ALWAYS);
        GridPane.setVgrow(usernameBox,Priority.ALWAYS);
        GridPane.setHgrow(userNoBox, Priority.ALWAYS);
        GridPane.setVgrow(userNoBox,Priority.ALWAYS);
        GridPane.setHgrow(tableViewHBox, Priority.ALWAYS);
        GridPane.setVgrow(tableViewHBox,Priority.ALWAYS);
        GridPane.setMargin(titleLabel, new Insets(25, 0, 20, 0));
        GridPane.setHalignment(usernameBox, HPos.CENTER);
        GridPane.setHalignment(userNoBox, HPos.CENTER);
        GridPane.setHalignment(tableViewHBox, HPos.CENTER);
        pane.add(titleLabel,0,0);
        pane.add(usernameBox,0,1);
        pane.add(userNoBox,0,2);
        pane.add(tableViewHBox,0,3);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("/css/fxsampler.css");
        stage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setTitle("用户信息");
        if (StringUtils.isBlank(MenuFrame.logoPath)){
            stage.getIcons().add(new Image(MenuFrame.defaultLogoPath));
        }else {
            stage.getIcons().add(new Image(MenuFrame.logoPath));
        }
        stage.setWidth(screenBounds.getWidth() * 0.50);
        stage.setHeight(screenBounds.getHeight() * .50);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(menuFrame.stage);
        stage.show();
        User user = (User)menuFrame.user;
        usernameTextField.setText(user.getUserName());
        userNoTextField.setText(user.getUserNo());
        ObservableList<RoleModelProperty> roleModelProperties = FXCollections.observableArrayList();
        user.getRoles().forEach(role -> {
            roleModelProperties.add(new RoleModelProperty(role.getId(),role.getRoleName(),role.getRoleNo()));
        });
        roleTableView.setItems(roleModelProperties);
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
            hidden();
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

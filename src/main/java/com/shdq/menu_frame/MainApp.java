package com.shdq.menu_frame;

import com.shdq.menu_frame.frame.entity.Menu;
import com.shdq.menu_frame.frame.MenuFrame;
import com.shdq.menu_frame.frame.model.HomePage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author shdq-fjy
 */
public class MainApp extends MenuFrame {

    public static void main(String[] args) {
        appName = "My Daemon Framework";
        mainMenuName = "主页概览";
        serverIp = "127.0.0.1";
        serverHeartBeatPort=8989;
        showOverViewOnRoot = false;
        launch(args);
    }

    @Override
    public void initMenuListData(List<Menu> menus) {
        //没有实现方法块，使用默认的菜单展示
    }

    @Override
    public HomePage getDefaultHomePage() {
        Label label = new Label("Welcome to My controlsfx Frame!");
        label.setStyle("-fx-font-size: 2em; -fx-padding: 0 0 0 5;");
        Label label1 = new Label("Explore the available and interesting parts by clicking on the options to the left.");
        label1.setStyle("-fx-font-size: 1.25em; -fx-padding: 0 0 0 5;");
        HomePage page = new HomePage("Welcome!", new VBox(5, label, label1));
        return page;
    }

    @Override
    public void maintainMenuSortMap(Map<String,List<String>> menuMap) {
        menuMap.put("ControlsFX API", Arrays.asList("3", "/images/controlsfx-API.png"));
        menuMap.put("设备管理",Arrays.asList("2", "/images/equipment.png"));
        menuMap.put("设备管理.设备OEE",Arrays.asList("2.1",""));
        menuMap.put("设备管理.设备故障率",Arrays.asList("2.2",""));
        menuMap.put("质量管理",Arrays.asList("1", "/images/quality.png"));
        menuMap.put("质量管理.产品合格率",Arrays.asList("1.1",""));
        menuMap.put(mainMenuName,Arrays.asList("0", "/images/home.png"));
    }
}

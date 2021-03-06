package com.shdq.menu_frame;

import com.alibaba.fastjson.JSON;
import com.shdq.menu_frame.frame.entity.Menu;
import com.shdq.menu_frame.frame.MenuFrame;
import com.shdq.menu_frame.frame.model.HomePage;
import com.shdq.menu_frame.frame.util.RestTemplateUtil;
import com.shdq.menu_frame.http.Result;
import com.shdq.menu_frame.model.ChildMenu;
import com.shdq.menu_frame.model.ParentMenu;
import com.shdq.menu_frame.model.Permission;
import com.shdq.menu_frame.model.User;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.*;

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

    //没有实现方法块，使用默认的菜单展示
    @Override
    public void initMenuListData(List<Menu> menus) {
        if (user != null){
            //todo:用户验证通过，获取用户权限下可查看的菜单
            Set<Permission> permissions = new HashSet<>();
            Set<ParentMenu> parentMenus = new HashSet<>();
            Set<ParentMenu> parentMenus1 = new HashSet<>();
            ((User)user).getRoles().forEach(role -> {
                Result result = (Result)RestTemplateUtil.getRestTemplate().getForObject("http://localhost:8080/permission/getPermission?permissionId="+role.getId(),Result.class);
                Map<String,Object> permissionMap = (Map<String, Object>) result.getData();
                Permission permission = JSON.parseObject(JSON.toJSONString(permissionMap),Permission.class);
                permissions.add(permission);
                parentMenus.addAll(permission.getParentMenus());
            });
            parentMenus.forEach(parentMenu -> {
                Result result1 = (Result)RestTemplateUtil.getRestTemplate().getForObject("http://localhost:8080/parent/menu/getParentMenu?parentMenuId="+parentMenu.getId(),Result.class);
                ParentMenu parentMenu1 = JSON.parseObject(JSON.toJSONString(result1.getData()),ParentMenu.class);
                parentMenu1.getChildMenus().forEach(childMenu -> parentMenu1.getSubMenus().add((com.shdq.menu_frame.frame.entity.Menu)childMenu));
                parentMenus1.add(parentMenu1);
            });
            menus.addAll(0,parentMenus1);
        }
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

        menuMap.put("parent_menu_3", Arrays.asList("3", "/images/equipment.png"));
        menuMap.put("parent_menu_3.child_menu_3_1",Arrays.asList("",""));
        menuMap.put("parent_menu_1",Arrays.asList("1", "/images/controlsfx-API.png"));
        menuMap.put("parent_menu_1.child_menu_1_1",Arrays.asList("1.1",""));
        menuMap.put("parent_menu_1.child_menu_1_2",Arrays.asList("1.2",""));
        menuMap.put("parent_menu_2",Arrays.asList("2", "/images/quality.png"));
        menuMap.put("parent_menu_2.child_menu_2_1",Arrays.asList("2.1",""));
        menuMap.put("parent_menu_2.child_menu_2_2",Arrays.asList("2.2",""));
        menuMap.put(mainMenuName,Arrays.asList("0", "/images/home.png"));
    }
}

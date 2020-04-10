package com.shdq.menu_frame.frame;

import com.shdq.menu_frame.frame.model.*;
import com.shdq.menu_frame.frame.util.*;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 可以继承此类，重写默认概览页面方法获取自己的默认主页
 */
public abstract class MenuFrame extends Application {
    private Project project;
    private Stage stage;
    private GridPane grid;
    private com.shdq.menu_frame.frame.model.Menu selectedMenu;
    private TreeView<com.shdq.menu_frame.frame.model.Menu> menuTreeView;
    private TextField searchBox;
    private Button homeButton;
    private TreeItem<com.shdq.menu_frame.frame.model.Menu> root;
    private AnchorPane pane;
    private TabPane tabPane;
    private ImageView heartImageView;
    //list第一个item为序号，第二个item为菜单图标路径
    private Map<String,List<String>> menuMap = new HashMap<>();
    //主页菜单，根菜单名称
    public static String mainMenuName = "overView";
    public static String appName = "FXSampler!";
    public static String logoPath;
    public static String serverIp = "";
    public static int serverHeartBeatPort;
    //是否不在treeView中展示overView菜单选项，true：在treeview中显示，作为root treeItem；false：采用单独按钮展示
    public static boolean showOverViewOnRoot = true;
    private static String defaultLogoPath = "/images/controlsfx-logo.png";
    //登录成功后的用户对象，通过用户来获取用户角色，权限，菜单等
    public Object user;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        heartImageView = new ImageView(new Image("/images/heart-offline.png"));//心跳图标
        CheckUtil checkUtil = new CheckUtil(heartImageView,this);
        checkUtil.check();
        maintainMenuSortMap(menuMap);
        initializationModule();
        showInterface(checkUtil);
        if (!showOverViewOnRoot){
            homeButton.requestFocus();
        }
        changeToHomeTab();
    }

    public final void initData(){
        project = new MenuScanner().discoverMenus(this);
        buildSampleTree(null);
        homeButton.setText(project.getMenuTree().getRoot().getMenu().getMenuName());
    }

    private void buildSampleTree(String searchText) {
        root = project.getMenuTree().getRoot().createTreeItem();
        // 借助这棵新建的完整树，我们将基于搜索文本进行过滤
        if (searchText != null) {
            pruneSampleTree(root, searchText);
            // FIXME 我认为TreeView中的怪异错误
            menuTreeView.setRoot(null);
        }
        menuTreeView.setRoot(root);
        if (menuMap.isEmpty() || menuMap.size() < project.getMenuTree().size()){
            sort(root, (o1, o2) -> o1.getValue().getMenuName().compareTo(o2.getValue().getMenuName()));
        }else {
            sort(root, (o1, o2) -> menuMap.get(o1.getValue().getMenuName()).get(0).compareTo(menuMap.get(o2.getValue().getMenuName()).get(0)));
        }
        List<TreeItem<com.shdq.menu_frame.frame.model.Menu>> items = menuTreeView.getRoot().getChildren();
        if (!items.isEmpty()) {
            TreeItem<com.shdq.menu_frame.frame.model.Menu> firstItem = items.get(0);
            menuTreeView.getSelectionModel().select(firstItem);
        }
    }

    private void sort(TreeItem<com.shdq.menu_frame.frame.model.Menu> node, Comparator<TreeItem<com.shdq.menu_frame.frame.model.Menu>> comparator) {
        node.getChildren().sort(comparator);
        for (TreeItem<com.shdq.menu_frame.frame.model.Menu> child : node.getChildren()) {
            sort(child, comparator);
        }
    }

    private boolean pruneSampleTree(TreeItem<com.shdq.menu_frame.frame.model.Menu> treeItem, String searchText) {
        // 一直到叶节点，然后检查它们是否与搜索文本匹配。如果匹配，它们留下。如果没有，我们将其删除。
        // 当弹出时，我们检查分支节点是否仍然有子节点，否则，我们也将其删除
        if (searchText == null) {
            return true;
        }
        if (treeItem.isLeaf()) {
            // 检查匹配。如果匹配，则返回true；如果删除则返回false
            return treeItem.getValue().getMenuName().toUpperCase().contains(searchText.toUpperCase());
        } else {
            List<TreeItem<com.shdq.menu_frame.frame.model.Menu>> toRemove = new ArrayList<>();
            for (TreeItem<com.shdq.menu_frame.frame.model.Menu> child : treeItem.getChildren()) {
                boolean keep = pruneSampleTree(child, searchText);
                if (!keep) {
                    toRemove.add(child);
                }
            }
            // 删除不相关的项
            treeItem.getChildren().removeAll(toRemove);
            // 如果此分支有子级，则返回true，否则返回false
            return !treeItem.getChildren().isEmpty();
        }
    }

    /**
     * 初始化界面各个组件和布局
     */
    private void initializationModule(){
        // 简单的布局：菜单TreeView在左侧，内容显示区域在右侧
        grid = new GridPane();
        grid.setPadding(new Insets(5, 10, 10, 10));
        grid.setHgap(10);
        grid.setVgap(10);
        // --- 左手边
        // search box
        searchBox = new TextField();
        searchBox.setPromptText("Search");
        searchBox.getStyleClass().add("search-box");
        searchBox.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                buildSampleTree(searchBox.getText());
            }
        });
        //不在treeView中显示根节点，把根节点设置到按钮中
        homeButton = new Button();
        if (!showOverViewOnRoot){
            String iconPath = menuMap.get(mainMenuName).get(1);
            if (StringUtils.isNotBlank(iconPath)) {
                homeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(iconPath))));
            }
            homeButton.setMinWidth(150);
            homeButton.setMaxWidth(150);
            homeButton.setPrefHeight(20);
            homeButton.setOnMouseClicked(event -> {
                if (event.getClickCount() <= 1){
                    changeToHomeTab();
                }
            });
        }
        // treeView
        menuTreeView = new TreeView<>(root);
        menuTreeView.setShowRoot(showOverViewOnRoot);
        menuTreeView.getStyleClass().add("samples-tree");
        menuTreeView.setMinWidth(200);
        menuTreeView.setMaxWidth(200);
        menuTreeView.setCellFactory(new Callback<TreeView<com.shdq.menu_frame.frame.model.Menu>, TreeCell<com.shdq.menu_frame.frame.model.Menu>>() {
            @Override
            public TreeCell<com.shdq.menu_frame.frame.model.Menu> call(TreeView<com.shdq.menu_frame.frame.model.Menu> param) {
                return new TreeCell<com.shdq.menu_frame.frame.model.Menu>() {
                    @Override
                    protected void updateItem(com.shdq.menu_frame.frame.model.Menu item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText("");
                            setGraphic(null);
                        } else {
                            String[] menuPath = item.getMenuName().split("\\.");
                            String menu = menuPath[menuPath.length - 1];
                            setText(menu);
                            for (String key : menuMap.keySet()){
                                if (key.contains(menu)){
                                    String iconPath = menuMap.get(key).get(1);
                                    if (StringUtils.isNotBlank(iconPath)){
                                        setGraphic(new ImageView(new Image(getClass().getResourceAsStream(iconPath))));
                                    }
                                }
                            }
                        }
                    }
                };
            }
        });
        menuTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<com.shdq.menu_frame.frame.model.Menu>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<com.shdq.menu_frame.frame.model.Menu>> observable, TreeItem<com.shdq.menu_frame.frame.model.Menu> oldValue, TreeItem<com.shdq.menu_frame.frame.model.Menu> newValue) {
                if (newValue == null) {
                    return;
                } else if (newValue.getValue() instanceof EmptyMenu) {
                    com.shdq.menu_frame.frame.model.Menu selectedMenu = newValue.getValue();
                    if (selectedMenu.getMenuName().equals(mainMenuName)) {
                        changeToHomeTab();
                    }
                    return;
                }
                selectedMenu = newValue.getValue();
                changeMenu();
            }
        });
        // 右边
        pane = new AnchorPane();
        pane.getChildren().add(heartImageView);
        AnchorPane.setRightAnchor(heartImageView,0.0);
        AnchorPane.setTopAnchor(heartImageView,10.0);
        pane.getStyleClass().add("anchorPane");
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        tabPane.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                updateTab();
            }
        });
        GridPane.setMargin(searchBox, new Insets(5, 0, 0, 0));
        grid.add(searchBox, 0, 0);
        if (!showOverViewOnRoot){
            GridPane.setMargin(homeButton,new Insets(0,25,0,25));
            grid.add(homeButton,0,1);
            GridPane.setVgrow(menuTreeView,Priority.ALWAYS);
            grid.add(menuTreeView,0,2);

            GridPane.setHgrow(pane,Priority.ALWAYS);
            GridPane.setMargin(pane,new Insets(0,0,0,0));
            grid.add(pane,1,0);

            GridPane.setHgrow(tabPane,Priority.ALWAYS);
            GridPane.setVgrow(tabPane,Priority.ALWAYS);
            grid.add(tabPane,1,1,1,2);
        }else {
            GridPane.setVgrow(menuTreeView, Priority.ALWAYS);
            grid.add(menuTreeView, 0, 1);
            grid.add(pane,1,0);
            GridPane.setHgrow(tabPane, Priority.ALWAYS);
            GridPane.setVgrow(tabPane, Priority.ALWAYS);
            grid.add(tabPane, 1, 1);
        }
    }

    /**
     * 显示主界面
     * @param checkUtil
     */
    private void showInterface(CheckUtil checkUtil){
        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("/css/fxsampler.css").toExternalForm());
        stage.close();
        if (StringUtils.isBlank(logoPath)){
            stage.getIcons().add(new Image(defaultLogoPath));
        }else {
            stage.getIcons().add(new Image(logoPath));
        }
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        // 将宽度/高度值设置为用户屏幕分辨率的75％
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenBounds.getWidth() * 0.75);
        stage.setHeight(screenBounds.getHeight() * .75);
        stage.setTitle(appName);
        //展示系统托盘
        new MySysTray(stage,checkUtil).initSystemTray();
        stage.show();
    }

    private void changeMenu() {
        if (selectedMenu == null) {
            return;
        }
        setMenuTabs();
        updateTab();
    }

    private void setMenuTabs() {
        tabPane.getTabs().clear();
        MenuBase menuBase = (MenuBase) selectedMenu;
        String[] menuPath = selectedMenu.getMenuName().split("\\.");
        if (menuBase.ifNeeded){
            Tab firstTab = new Tab(menuPath[menuPath.length - 1]);
            firstTab.setContent(menuBase.buildFirstTab());
            tabPane.getTabs().add(firstTab);
        }
        List<String> tabs = menuBase.tabs;
        if (tabs != null && !tabs.isEmpty()) {
            for (String tab : tabs) {
                tabPane.getTabs().add(new Tab(tab));
            }
        }
    }

    private void updateTab() {
        // 只会更新选定的标签-将其他标签保留在之前的状态，直到被选中为止
        if (selectedMenu != null) {
            ((MenuBase) selectedMenu).updateTab(tabPane);
        }
    }

    private void changeToHomeTab() {
        HomePage page = getDefaultHomePage();
        Tab tab = new Tab(page.getTitle());
        tab.setContent(page.getContent());
        tabPane.getTabs().setAll(tab);
    }

    /**
     * 重写此方法以不同方式加载menu
     * @param menus
     */
    public abstract void initMenuListData(List<com.shdq.menu_frame.frame.entity.Menu> menus);
    /**
     * 重写此方法创建自定义概览（首）页
     * @return
     */
    public abstract HomePage getDefaultHomePage();

    /**
     * 维护一个菜单map，包含菜单序号，菜单的图标路径；通过这个map可以调整菜单显示顺序和显示的图标
     * 如果没有提供，使用菜单名称进行排序显示
     * @return
     */
    public abstract void maintainMenuSortMap(Map<String,List<String>> menuSortMap);
}

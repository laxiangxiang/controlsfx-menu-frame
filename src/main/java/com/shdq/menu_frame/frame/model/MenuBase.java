package com.shdq.menu_frame.frame.model;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;

/**
 * 叶子菜单，中间节点菜单基类
 * @author shdq-fjy
 */
public abstract class MenuBase extends Application implements Menu {
    //每一个子菜单页下包含的模块内容（有几个tab页），
    // 如果你的子菜单只需要一个Tab页显示则可以不设置，可以选择重写buildCustomizeFirstTab方法，tab名称默认为菜单名称
    public List<String> tabs;
    //菜单名称，包含父菜单名称，用“.”分隔
    public String menuName;
    //菜单描述
    public String menuDesc;
    //是否显示第一个默认tab页面
    public boolean ifNeeded = true;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(getMenuName());
        Scene scene = new Scene((Parent) tabPageViewTest(), 800, 800);
        scene.getStylesheets().add(MenuBase.class.getResource("/css/fxsampler.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public String getMenuDescription() {
        return menuDesc;
    }

    public double getControlPanelDividerPosition() {
        return 0.6;
    }

    /**
     * 创建菜单的第一个默认Tab页
     */
    public final Node buildFirstTab() {
        if (ifNeeded) {
            Node node = buildCustomizeFirstTab();
            if (node == null) {
                return buildDefaultFirstTab();
            }
            return node;
        }
        return null;
    }

    /**
     * 默认的firstTab为splitPane布局，左边内容通过getPanel方法获得，右边内容通过getControlPanel获得
     * @return
     */
    private final Node buildDefaultFirstTab() {
        SplitPane splitPane = new SplitPane();
        // 我们保证构建顺序是面板，然后是控制面板.
        final Node samplePanel = this.getPanel();
        final Node controlPanel = this.getControlPanel();
        splitPane.setDividerPosition(0, this.getControlPanelDividerPosition());
        if (samplePanel != null) {
            splitPane.getItems().add(samplePanel);
        }
        final VBox rightPanel = new VBox();
        rightPanel.getStyleClass().add("right-panel");
        rightPanel.setMaxHeight(Double.MAX_VALUE);
        boolean addRightPanel = false;
        Label sampleName = new Label(this.getMenuName());
        sampleName.getStyleClass().add("menu-name");
        rightPanel.getChildren().add(sampleName);
        final String menuName = this.getMenuName();
        if (menuName != null && !menuName.isEmpty()) {
            Label projectNameTitleLabel = new Label("Project: ");
            projectNameTitleLabel.getStyleClass().add("project-name-title");
            Label projectNameLabel = new Label(menuName);
            projectNameLabel.getStyleClass().add("project-name");
            projectNameLabel.setWrapText(true);
            TextFlow textFlow = new TextFlow(projectNameTitleLabel, projectNameLabel);
            rightPanel.getChildren().add(textFlow);
        }
        // --- description
        final String description = this.getMenuDescription();
        if (description != null && !description.isEmpty()) {
            Label descriptionLabel = new Label(description);
            descriptionLabel.getStyleClass().add("description");
            descriptionLabel.setWrapText(true);
            rightPanel.getChildren().add(descriptionLabel);
            addRightPanel = true;
        }
        if (controlPanel != null) {
            rightPanel.getChildren().add(new Separator());
            controlPanel.getStyleClass().add("control-panel");
            rightPanel.getChildren().add(controlPanel);
            VBox.setVgrow(controlPanel, Priority.ALWAYS);
            addRightPanel = true;
        }
        if (addRightPanel) {
            ScrollPane scrollPane = new ScrollPane(rightPanel);
            scrollPane.setMaxHeight(Double.MAX_VALUE);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            SplitPane.setResizableWithParent(scrollPane, false);
            splitPane.getItems().add(scrollPane);
        }
        return splitPane;
    }

    /**
     * 默认firstTab页面的左边内容
     * @return
     */
    public Node getPanel(){
        return null;
    }

    /**
     * 默认firstTab页面的右边内容
     * @return
     */
    public Node getControlPanel(){
        return null;
    }

    /*
     * 重写此方法创建自定义的菜单首个tab页面
     * @return
     */
    public Node buildCustomizeFirstTab() {
        return null;
    }

    /**
     * 重写此方法方便单个页面效果测试
     * @return
     */
    public Node tabPageViewTest(){
        return buildFirstTab();
    }

    /**
     * 根据当前选择的菜单跟新tabPane的tab页面
     * @param tabPane
     */
    public final void updateTab(TabPane tabPane){
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        List<Tab> tabs = tabPane.getTabs();
        tabs.forEach(tab -> {
            if (tab.getText().equals(selectedTab.getText())){
                setContent(selectedTab);
            }
        });
    }

    /**
     * 设置每一个Tab页面的内容,重写此方法给选中的Tab页面设置tab内容
     * @param selectedTab
     */
    public abstract void setContent(Tab selectedTab);
}

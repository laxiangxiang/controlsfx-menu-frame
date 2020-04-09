package com.shdq.menu_frame.menuimplement;

import com.shdq.menu_frame.frame.model.MenuBase;
import com.shdq.menu_frame.frame.util.ResourceUtil;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;

import java.util.Arrays;

/**
 * @author shdq-fjy
 */
public class Parent1Child1 extends MenuBase {
    public Parent1Child1() {
        menuName = "parent_menu_1.child_menu_1_1";
        menuDesc = "ControlsFX API 文档";
        tabs = Arrays.asList("Java Doc");
        ifNeeded = false;
    }

    @Override
    public void setContent(Tab selectedTab) {
        String tab = selectedTab.getText();
        if (tab.equals("Java Doc")){
            WebView javaDocWebView = new WebView();
            javaDocWebView.getEngine().load(ResourceUtil.JAVADOC_BASE);
            selectedTab.setContent(javaDocWebView);
        }
    }
}

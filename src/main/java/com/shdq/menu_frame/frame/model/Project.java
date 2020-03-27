package com.shdq.menu_frame.frame.model;

import com.shdq.menu_frame.frame.MenuFrame;

/**
 * @author shdq-fjy
 */
public class Project {
    private final MenuTree menuTree;

    public Project() {
        this.menuTree = new MenuTree(new EmptyMenu(MenuFrame.mainMenuName));
    }

    public void addMenu(String menuPath, Menu menu) {
        String[] menuPaths = menuPath.split("\\.");
        menuTree.addMenu(menuPaths, menu);
    }
    
    public MenuTree getMenuTree() {
        return menuTree;
    }
}

package com.shdq.menu_frame.frame.model;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shdq-fjy
 */
public class MenuTree {
    //树的根节点
    private TreeNode root;
    //所有节点（中间菜单和叶子菜单）的计数，不包括根菜单节点
    private int count = 0;

    public MenuTree(Menu rootMenu) {
        root = new TreeNode(null, rootMenu.getMenuName(), rootMenu);
    }
    
    public TreeNode getRoot() {
        return root;
    }
    
    public int size() {
        return count;
    }

    /**
     *根据菜单的名称解析出的菜单路径逐层添加菜单节点
     * @param menuPaths
     * @param menu
     */
    public void addMenu(String[] menuPaths, Menu menu) {
        if (menuPaths.length == 0) {
            root.addMenu(menu);
            count++;
            return;
        }
        TreeNode n = root;
        for (String menuPath : menuPaths) {
            if (n.containsChild(menuPath)) {
                n = n.getChild(menuPath);
            } else {
                TreeNode newNode;
                if (menuPath.equals(menuPaths[menuPaths.length - 1])){
                    newNode = new TreeNode(menu,menuPath);
                }else {
                    newNode = new TreeNode(menuPath);
                }
                n.addNode(newNode);
                count++;
                n = newNode;
            }
        }
    }
    
    @Override
    public String toString() {
        return root.toString();
    }

    public static class TreeNode {
        private final Menu menu;
        private final String menuPath;
        private final TreeNode parent;
        private List<TreeNode> children;
        
        public TreeNode(String menuPath) {
            this(null, menuPath, null);
        }

        public TreeNode(Menu menu,String menuPath){
            this(null,menuPath,menu);
        }

        public TreeNode(TreeNode parent, String menuPath, Menu menu) {
            this.children = new ArrayList<>();
            this.menu = menu;
            this.parent = parent;
            this.menuPath = menuPath;
        }
        
        public boolean containsChild(String menuPath) {
            if (menuPath == null) return false;
            for (TreeNode n : children) {
                if (menuPath.equals(n.menuPath)) {
                    return true;
                }
            }
            return false;
        }
        
        public TreeNode getChild(String menuPath) {
            if (menuPath == null) return null;
            for (TreeNode n : children) {
                if (menuPath.equals(n.menuPath)) {
                    return n;
                }
            }
            return null;
        }

        public void addMenu(Menu menu) {
            children.add(new TreeNode(this, menu.getMenuName(), menu));
        }
        
        public void addNode(TreeNode n) {
            children.add(n);
        }
        
        public Menu getMenu() {
            return menu;
        }
        
        public String getMenuPath() {
            return menuPath;
        }
        
        public TreeItem<Menu> createTreeItem() {
            TreeItem<Menu> treeItem = null;
            if (menu != null) {
                treeItem = new TreeItem<Menu>(menu);
                treeItem.setExpanded(true);
            }
            else if (menuPath != null) {
                treeItem = new TreeItem<Menu>(new EmptyMenu(menuPath));
                treeItem.setExpanded(false);
            }
            // 递归添加子项
            for (TreeNode n : children) {
                treeItem.getChildren().add(n.createTreeItem());
            }
            return treeItem;
        }
        
        @Override public String toString() {
            if (menu != null) {
                return " Menu [ menuName: " + menu.getMenuName() + ", children: " + children + " ]";
            } else {
                return " Menu [ MenuPath: " + menuPath + ", children: " + children + " ]";
            }
        }
    }
}
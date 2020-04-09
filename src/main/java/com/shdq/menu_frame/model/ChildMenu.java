package com.shdq.menu_frame.model;

import com.alibaba.fastjson.JSON;
import com.shdq.menu_frame.frame.entity.Menu;

public class ChildMenu extends Menu {
    private Integer id;

    private String childMenuName;

    private String childMenuNo;

    private Integer parentMenuId;

    public ChildMenu(String childMenuName) {
        super(childMenuName);
    }

    public Integer getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(Integer parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    private ParentMenu parentMenu;

    public ParentMenu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(ParentMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return child_menu_name
     */
    public String getChildMenuName() {
        return childMenuName;
    }

    /**
     * @param childMenuName
     */
    public void setChildMenuName(String childMenuName) {
        this.childMenuName = childMenuName;
    }

    /**
     * @return child_menu_no
     */
    public String getChildMenuNo() {
        return childMenuNo;
    }

    /**
     * @param childMenuNo
     */
    public void setChildMenuNo(String childMenuNo) {
        this.childMenuNo = childMenuNo;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
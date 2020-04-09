package com.shdq.menu_frame.model;

import com.alibaba.fastjson.JSON;
import com.shdq.menu_frame.frame.entity.Menu;

import java.util.List;

public class ParentMenu extends Menu {
    private Integer id;
    private String parentMenuName;

    private String parentMenuNo;

    private List<Permission> permissions;

    private List<ChildMenu> childMenus;

    public ParentMenu(String parentMenuName,List<ChildMenu> childMenus) {
        super(parentMenuName);
        childMenus.forEach(childMenu -> subMenus.add((Menu)childMenu));
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public List<ChildMenu> getChildMenus() {
        return childMenus;
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
     * @return parent_menu_name
     */
    public String getParentMenuName() {
        return parentMenuName;
    }

    /**
     * @param parentMenuName
     */
    public void setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
    }

    /**
     * @return parent_menu_no
     */
    public String getParentMenuNo() {
        return parentMenuNo;
    }

    /**
     * @param parentMenuNo
     */
    public void setParentMenuNo(String parentMenuNo) {
        this.parentMenuNo = parentMenuNo;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void setChildMenus(List<ChildMenu> childMenus) {
        this.childMenus = childMenus;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
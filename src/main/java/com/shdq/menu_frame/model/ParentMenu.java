package com.shdq.menu_frame.model;

import com.alibaba.fastjson.JSON;
import com.shdq.menu_frame.frame.entity.Menu;

import java.util.List;
import java.util.Objects;

public class ParentMenu extends Menu {
    private Integer id;
    private String parentMenuName;

    private String parentMenuNo;

    private List<Permission> permissions;

    private List<ChildMenu> childMenus;

    public ParentMenu(Integer id, String parentMenuName, String parentMenuNo, List<Permission> permissions, List<ChildMenu> childMenus) {
        super(parentMenuName);
        this.id = id;
        this.parentMenuName = parentMenuName;
        this.parentMenuNo = parentMenuNo;
        this.permissions = permissions;
        this.childMenus = childMenus;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParentMenu)) return false;
        if (!super.equals(o)) return false;
        ParentMenu that = (ParentMenu) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
package com.shdq.menu_frame.model;

import java.util.List;
import java.util.Objects;

public class Permission {
    private Integer id;

    private String permissionName;

    private String permissionNo;

    private List<Role> roles;

    private List<ParentMenu> parentMenus;

    public List<Role> getRoles() {
        return roles;
    }

    public List<ParentMenu> getParentMenus() {
        return parentMenus;
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
     * @return permission_name
     */
    public String getPermissionName() {
        return permissionName;
    }

    /**
     * @param permissionName
     */
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    /**
     * @return permission_no
     */
    public String getPermissionNo() {
        return permissionNo;
    }

    /**
     * @param permissionNo
     */
    public void setPermissionNo(String permissionNo) {
        this.permissionNo = permissionNo;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setParentMenus(List<ParentMenu> parentMenus) {
        this.parentMenus = parentMenus;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", permissionName='" + permissionName + '\'' +
                ", permissionNo='" + permissionNo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
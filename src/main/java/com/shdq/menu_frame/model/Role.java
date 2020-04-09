package com.shdq.menu_frame.model;

import java.util.List;

public class Role {
    private Integer id;

    private String roleName;

    private String roleNo;

    private List<User> users;

    private List<Permission> permissions;

    public List<User> getUsers() {
        return users;
    }

    public List<Permission> getPermissions() {
        return permissions;
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
     * @return role_name
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return role_no
     */
    public String getRoleNo() {
        return roleNo;
    }

    /**
     * @param roleNo
     */
    public void setRoleNo(String roleNo) {
        this.roleNo = roleNo;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", roleNo='" + roleNo + '\'' +
                '}';
    }
}
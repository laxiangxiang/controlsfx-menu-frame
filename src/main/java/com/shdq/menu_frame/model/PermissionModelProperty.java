package com.shdq.menu_frame.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author shdq-fjy
 */
public class PermissionModelProperty {
    private IntegerProperty id;
    private StringProperty permissionName;
    private StringProperty permissionNo;

    public PermissionModelProperty(int id, String permissionName, String permissionNo) {
        this.id = new SimpleIntegerProperty(id);
        this.permissionName = new SimpleStringProperty(permissionName);
        this.permissionNo = new SimpleStringProperty(permissionNo);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getPermissionName() {
        return permissionName.get();
    }

    public StringProperty permissionNameProperty() {
        return permissionName;
    }

    public String getPermissionNo() {
        return permissionNo.get();
    }

    public StringProperty permissionNoProperty() {
        return permissionNo;
    }
}

package com.shdq.menu_frame.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author shdq-fjy
 */
public class RoleModelProperty {
    private IntegerProperty id;
    private StringProperty name;
    private StringProperty no;

    public RoleModelProperty(int id, String name, String no) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.no = new SimpleStringProperty(no);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getNo() {
        return no.get();
    }

    public StringProperty noProperty() {
        return no;
    }
}

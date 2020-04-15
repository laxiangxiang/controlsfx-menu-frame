package com.shdq.menu_frame.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author shdq-fjy
 */
public class MenuModelProperty {
    private StringProperty name;
    public MenuModelProperty(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }
}

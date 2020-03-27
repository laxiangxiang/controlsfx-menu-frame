package com.shdq.menu_frame.frame.model;

/**
 * @author shdq-fjy
 */
public class EmptyMenu implements Menu {
    private final String name;
    public EmptyMenu(String name) {
        this.name = name;
    }

    @Override
    public String getMenuName() {
        return name;
    }

    @Override
    public String getMenuDescription() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
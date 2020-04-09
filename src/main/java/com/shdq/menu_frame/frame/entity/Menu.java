package com.shdq.menu_frame.frame.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shdq-fjy
 */
@Data
public abstract class Menu {
    //菜单名称，必须和实现MenuBase的菜单类名称保持一致（不包含父菜单名称）
    public String name;
    public Menu parent;
    public List<Menu> subMenus;

    public Menu(String name) {
        this.name = name;
        subMenus = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Menu{" +
                "name='" + name + '\'' +
                ", parent=" + parent +
                ", subMenus=" + subMenus +
                '}';
    }
}

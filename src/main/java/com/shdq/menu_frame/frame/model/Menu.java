package com.shdq.menu_frame.frame.model;

/**
 * @author shdq-fjy
 */
public interface Menu {
    /**
     * 获取菜单名称
     * @return
     */
    String getMenuName();

    /**
     * 菜单描述
     * @return
     */
    String getMenuDescription();

    /**
     * 菜单是否可见
     * @return
     */
    boolean isVisible();
}
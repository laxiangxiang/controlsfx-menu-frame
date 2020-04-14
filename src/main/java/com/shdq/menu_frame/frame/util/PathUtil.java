package com.shdq.menu_frame.frame.util;

import javafx.scene.control.Alert;

import java.io.File;

/**
 * @author shdq-fjy
 */
public class PathUtil {

    /**
     * 从注册表中获取设置的文件保存路径
     *
     * @return
     */
    public static String getFilePath() {
        String value = CommonUtil.getDataFromPreferences(CommonUtil.DEFAULT_FILE_PATH_KEY);
        if (value == null) {
            return CommonUtil.default_file_path;
        } else {
            File file = new File(value);
            if (!file.exists()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("不存在当前路径");
                alert.setContentText("您设置的默认文件保存路径（"+file.getAbsolutePath()+"）不存在。" +
                        "可能被误删除，将使用系统默认路径:"+new File(CommonUtil.default_file_path).getAbsolutePath());
                alert.showAndWait();
                return CommonUtil.default_file_path;
            }
            return value;
        }
    }

    /**
     * 将设置的文件保存路径保存到注册表中
     */
    public static void setFilePath(String filePath) {
        CommonUtil.setDataToPreferences(CommonUtil.DEFAULT_FILE_PATH_KEY, filePath);
    }
}

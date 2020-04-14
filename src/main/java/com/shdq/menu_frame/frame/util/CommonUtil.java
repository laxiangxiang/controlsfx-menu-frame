package com.shdq.menu_frame.frame.util;

import com.shdq.menu_frame.frame.MenuFrame;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

/**
 * @author shdq-fjy
 */
public class CommonUtil {
    public static final String BAT_FILE_PATH = "src/main/resources/newPreference.bat";
    private static Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    public static double primaryStage_x;
    public static double primaryStage_y;
    public static final String default_file_path = "C:\\YAML-File-Generator";
    public static final String DEFAULT_FILE_PATH_KEY = "filePath";

    public static String getDataFromPreferences(String key) {
//        HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\shdq\/O/P/C/U/A_/Y/M/L_gen_platform
        Preferences prefs;
        try {
            prefs = Preferences.userNodeForPackage(MenuFrame.class);
        }catch (Exception e){
            JavaCMDUtil.runBatWithoutCmd(BAT_FILE_PATH);
            prefs = Preferences.userNodeForPackage(MenuFrame.class);
        }

//       HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Prefs\com\shdq\/O/P/C/U/A_/Y/M/L_gen_platform
//        Preferences prefs = Preferences.systemNodeForPackage(MenuFrame.class);
        String value = prefs.get(key, null);
        return value;
    }

    public static void setDataToPreferences(String key, String value) {
        Preferences prefs;
        try {
            prefs = Preferences.userNodeForPackage(MenuFrame.class);
        }catch (Exception e){
            JavaCMDUtil.runBatWithoutCmd(BAT_FILE_PATH);
            prefs = Preferences.userNodeForPackage(MenuFrame.class);
        }
        prefs.put(key, value);
    }

    public static void setMainWindowCoordinate(Stage primaryStage){
        primaryStage_x = (screenBounds.getWidth() - primaryStage.getWidth()) / 2;
        primaryStage_y = (screenBounds.getHeight() - primaryStage.getHeight()) / 2;
        primaryStage.setX(primaryStage_x);
        primaryStage.setY(primaryStage_y);
    }

    public static void setNewWindowCoordinate(Stage stage){
        primaryStage_x += 30;
        primaryStage_y += 30;
        stage.setX(primaryStage_x);
        stage.setY(primaryStage_y);
    }

    public static void setDialogWindowCoordinate(Stage primaryStage,Stage dialogStage){
        dialogStage.setX(primaryStage.getX()+30);
        dialogStage.setY(primaryStage.getY()-10);
    }

    public static void setProgressBarWindowCoordinate(Stage primaryStage,Stage progressStage){
        progressStage.setX(primaryStage.getX() + 150);
        progressStage.setY(primaryStage.getY() + 250);
    }
}

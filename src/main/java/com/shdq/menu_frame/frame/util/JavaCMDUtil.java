package com.shdq.menu_frame.frame.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * java的runtime不会加载系统的环境变量，需要自己手动设置，在.bat文件中 set path=C:\Program Files\R\R-3.4.2\bin
 * java调用.bat时，当前路径是jar包上级目录或者是项目的根目录
 */
public class JavaCMDUtil {

    /**
     * 出现cmd窗口
     * @param batPath
     */
    public static void runBat(String batPath){
        String cmd = "cmd /c start "+batPath;
        try {
            Process ps = Runtime.getRuntime().exec(cmd);
            ps.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不出现cmd窗口
     * @param batPath
     */
    public static void runBatWithoutCmd(String batPath){
        try {
            Process ps = Runtime.getRuntime().exec(batPath);
            InputStream in = ps.getInputStream();
            int c;
            while ((c = in.read()) != -1){
//                System.out.println(c);
            }
            in.close();
            ps.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

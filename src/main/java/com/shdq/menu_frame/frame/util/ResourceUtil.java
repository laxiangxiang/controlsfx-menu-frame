package com.shdq.menu_frame.frame.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author shdq-fjy
 */
public class ResourceUtil {
    public static final String JAVADOC_BASE = "http://docs.controlsfx.org/";
    public String getResource(String resourceName, Class<?> baseClass) {
        Class<?> clz = baseClass == null? getClass(): baseClass;
        return getResource(clz.getResourceAsStream(resourceName));
    }

    private static String getResource(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getSourceCode(String sourceURL) {
        try {
            // 尝试通过Web或本地文件系统加载
            URL url = new URL(sourceURL);
            InputStream is = url.openStream();
            return getResource(is);
        } catch (IOException e) {
            // 无操作-网址可能无效，不重复
        }
        return getResource(sourceURL, getClass());
    }

    private String formatSourceCode(String sourceURL) {
        String src;
        if (sourceURL == null) {
            src = "No sample source available";
        } else {
            src = "Sample Source not found";
            try {
                src = getSourceCode(sourceURL);
            } catch(Throwable ex){
                ex.printStackTrace();
            }
        }
        //用"&lt;"转义“ <”，以确保SyntaxHighlighter正确渲染
        src = src.replace("<", "&lt;");
        String template = getResource("/fxsampler/util/SourceCodeTemplate.html", null);
        return template.replace("<source/>", src);
    }

    private String formatCss(String cssUrl) {
        String src;
        if (cssUrl == null) {
            src = "No CSS source available";
        } else {
            src = "Css not found";
            try {
                src = new String(
                        Files.readAllBytes( Paths.get(getClass().getResource(cssUrl).toURI()) )
                );
            } catch(Throwable ex){
                ex.printStackTrace();
            }
        }
        //用"&lt;"转义“ <”，以确保SyntaxHighlighter正确渲染
        src = src.replace("<", "&lt;");
        String template = getResource("/fxsampler/util/CssTemplate.html", null);
        return template.replace("<source/>", src);
    }
}

package com.shdq.menu_frame.frame.util;

import com.shdq.menu_frame.frame.MenuFrame;
import com.shdq.menu_frame.frame.entity.MyMenu;
import com.shdq.menu_frame.frame.model.EmptyMenu;
import com.shdq.menu_frame.frame.model.Menu;
import com.shdq.menu_frame.frame.model.Project;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * @author shdq-fjy
 */
public class MenuScanner {
    private static List<com.shdq.menu_frame.frame.entity.Menu> menus = new ArrayList<>();
    private static List<String> ILLEGAL_CLASS_NAMES = new ArrayList<>();
    static {
        ILLEGAL_CLASS_NAMES.add("/com/javafx/main/Main.class");
        ILLEGAL_CLASS_NAMES.add("/com/javafx/main/NoJavaFXFallback.class");
    }

    /**
     * 所有菜单类都实现MenuBase抽象类，都是Menu子类
     * @return
     */
    public Project discoverMenus(MenuFrame frame) {
        Class<?>[] results = new Class[] { };
        try {
            results = loadFromPathScanning();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, com.shdq.menu_frame.frame.entity.Menu> menuMap = new HashMap<>();
        Project project = new Project();
        frame.initMenuListData(menus);
        if (menus.isEmpty()){
            System.out.println("no any menu loaded");
            System.out.println("use default menu for reveal");
            com.shdq.menu_frame.frame.entity.Menu menu1 = new MyMenu("设备管理");
            com.shdq.menu_frame.frame.entity.Menu menu1_1 = new MyMenu("设备OEE");
            com.shdq.menu_frame.frame.entity.Menu menu1_2 = new MyMenu("设备故障率");
            com.shdq.menu_frame.frame.entity.Menu menu2 = new MyMenu("质量管理");
            com.shdq.menu_frame.frame.entity.Menu menu2_1 = new MyMenu("产品合格率");
            com.shdq.menu_frame.frame.entity.Menu menu3 = new MyMenu("ControlsFX API");
            menu1.getSubMenus().add(menu1_1);
            menu1.getSubMenus().add(menu1_2);
            menu2.getSubMenus().add(menu2_1);
            menus.add(menu1);
            menus.add(menu2);
            menus.add(menu3);
        }
        parseMenu(menus,menuMap,new StringBuffer());
        for (Class<?> menuClass : results) {
            if (! Menu.class.isAssignableFrom(menuClass)) {
                continue;
            }
            if (menuClass.isInterface()) {
                continue;
            }
            if (Modifier.isAbstract(menuClass.getModifiers())) {
                continue;
            }
            if (menuClass == EmptyMenu.class) {
                continue;
            }
            Menu menu = null;
            try {
                menu = (Menu)menuClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (menu == null || ! menu.isVisible()) {
                continue;
            }
            Set<Map.Entry<String, com.shdq.menu_frame.frame.entity.Menu>> entry = menuMap.entrySet();
            Iterator iterator = entry.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, com.shdq.menu_frame.frame.entity.Menu> entry1 = (Map.Entry<String, com.shdq.menu_frame.frame.entity.Menu>) iterator.next();
                if (entry1.getKey().trim().equals(menu.getMenuName().trim())){
                    project.addMenu(entry1.getKey().trim(),menu);
                }
            }
        }
        return project;
    }

    /**
     * 只适合处理最多到二级菜单
     * @param menuList
     * @param menuMap
     * @param sb
     */
    public void parseMenu(List<com.shdq.menu_frame.frame.entity.Menu> menuList, Map<String, com.shdq.menu_frame.frame.entity.Menu> menuMap, StringBuffer sb){
        for (com.shdq.menu_frame.frame.entity.Menu menu1 : menuList){
            List<com.shdq.menu_frame.frame.entity.Menu> menus = menu1.getSubMenus();
            if (!menus.isEmpty()){
                sb.append(menu1.getName()).append(".");
                parseMenu(menus,menuMap,sb);
                sb.setLength(0);
            }else {
                sb.append(menu1.getName());
                menuMap.put(sb.toString(),menu1);
                String s = sb.toString().substring(0,sb.toString().lastIndexOf(".")+1);
                sb.setLength(0);
                sb.append(s);
                continue ;
            }
        }
    }

    private Class<?>[] loadFromPathScanning() throws IOException, ClassNotFoundException {
        final List<File> dirs = new ArrayList<>();
        final List<File> jars = new ArrayList<>();
        // 扫描类路径
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = "";
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if (url.toExternalForm().contains("/jre/")) continue;
            // 仅识别“file”和“ jar” URL，其他schema将被忽略
            String protocol = url.getProtocol().toLowerCase();
            if ("file".equals(protocol)) {
                dirs.add(new File(url.getFile()));
            } else if ("jar".equals(protocol)) {
                String fileName = new URL(url.getFile()).getFile();
                // JAR URL规范必须包含字符串“！/”，该字符串将JAR文件的名称与其中包含的资源的路径分隔开，即使该路径为空。
                int sep = fileName.indexOf("!/");
                if (sep > 0) {
                    jars.add(new File(fileName.substring(0, sep)));
                }
            }
        }
        // 并扫描当前工作目录
        final Path workingDirectory = new File("").toPath();
        scanPath(workingDirectory, dirs, jars);
        // 首先处理目录，然后处理jar，以便类优先于内置的jar（这使IDE中的快速开发更加容易）
        final Set<Class<?>> classes = new LinkedHashSet<>();
        for (File directory : dirs) {
            classes.addAll(findClassesInDirectory(directory));
        }
        for (File jar : jars) {
            String fullPath = jar.getAbsolutePath();
            if (fullPath.endsWith("jfxrt.jar")) continue;
            classes.addAll(findClassesInJar(new File(fullPath)));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private void scanPath(Path workingDirectory, final List<File> dirs, final List<File> jars) throws IOException {
        Files.walkFileTree(workingDirectory, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                final File file = path.toFile();
                final String fullPath = file.getAbsolutePath();
                final String name = file.toString();
                if (fullPath.endsWith("jfxrt.jar") || name.contains("jre")) {
                    return FileVisitResult.CONTINUE;
                }
                if (file.isDirectory()) {
                    dirs.add(file);
                } else if (name.toLowerCase().endsWith(".jar")) {
                    jars.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult visitFileFailed(Path file, IOException ex) {
                System.err.println(ex + " Skipping...");
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private List<Class<?>> findClassesInDirectory(File directory) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            System.out.println("Directory does not exist: " + directory.getAbsolutePath());
            return classes;
        }
        processPath(directory.toPath(), classes);
        return classes;
    }

    private List<Class<?>> findClassesInJar(File jarFile) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!jarFile.exists()) {
            System.out.println("Jar file does not exist here: " + jarFile.getAbsolutePath());
            return classes;
        }
        FileSystem jarFileSystem = FileSystems.newFileSystem(jarFile.toPath(), null);
        processPath(jarFileSystem.getPath("/"), classes);
        return classes;
    }

    private void processPath(Path path, final List<Class<?>> classes) throws IOException {
        final String root = path.toString();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String name = file.toString();
                if (name.endsWith(".class") && ! ILLEGAL_CLASS_NAMES.contains(name)) {
                    // 在所有情况下，删除根路径以使类名正确
                    name = name.substring(root.length());
                    Class<?> clazz = processClassName(name);
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException ex) {
                System.err.println(ex + " Skipping...");
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private Class<?> processClassName(final String name) {
        String className = name.replace("\\", ".");
        className = className.replace("/", ".");
        // 一些清理代码
        if (className.contains("$")) {
            // 我们不在乎样本作为内部类，所以我们跳出来
            return null;
        }
        if (className.contains(".bin")) {
            className = className.substring(className.indexOf(".bin") + 4);
            className = className.replace(".bin", "");
        }
        if (className.startsWith(".")) {
            className = className.substring(1);
        }
        if (className.endsWith(".class")) {
            className = className.substring(0, className.length() - 6);
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (Throwable e) {
            // 可抛出，可能是类无法实例化的各种不良原因
            // System.out.println("ERROR: Class name: " + className);
            // System.out.println("ERROR: Initial filename: " + name);
            // e.printStackTrace();
        }
        return clazz;
    }
}

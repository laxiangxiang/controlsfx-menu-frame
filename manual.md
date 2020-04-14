# 我的后台监控桌面软件菜单界面框架使用指南

## 1.项目目录结构

```text

src
    +---main
    |   +---java
    |   |   \---com
    |   |       \---shdq
    |   |           \---menu_frame
    |   |               |   MenuFrame.java    入口主类，继承MenuFrame类，示例用。
    |   |               |
    |   |               \---frame
    |   |                   |   MenuFrame.java  框架主入口抽象类，使用时需要继承此类实现抽象方法即可
    |   |                   |
    |   |                   +---defaultmenu  默认提供的展示用的菜单，自己的菜单需要自己实现
    |   |                   |       ControlsfxJavaDOC.java
    |   |                   |       EquipmentFailureRate.java
    |   |                   |       EquipmentOEE.java
    |   |                   |       ProductPassRate.java
    |   |                   |
    |   |                   +---entity  
    |   |                   |       Menu.java   实体菜单抽象类。自己的菜单类需要继承此类，否则无法使用此界面框架
    |   |                   |       MyMenu.java 框架默认实现菜单类，展示默认菜单项时使用
    |   |                   |
    |   |                   +---model   框架定义的各种基础类
    |   |                   |       EmptyMenu.java  空菜单类，实现Menu接口。最顶级菜单项（根菜单）和中间节点菜单使用此类
    |   |                   |       HomePage.java   主页对象。选中根菜单时显示的页面
    |   |                   |       Menu.java       菜单接口，定义根菜单和子菜单的共同行为
    |   |                   |       MenuBase.java   子菜单（叶子菜单）抽象类，自定义的菜单都是叶子菜单都需要继承此类
    |   |                   |       MenuTree.java   菜单树类，其中定义了TreeNode树节点内部类
    |   |                   |       Project.java    MenuTree封装对象
    |   |                   |
    |   |                   \---util
    |   |                           MenuScanner.java    菜单扫描工具类。从项目中获取所有菜单类并实例化，获取从自定途径菜单，与菜单对象进行比较与匹配，最终组装成MenuTree，设置到Project对象中。
    |   |                           ResourceUtil.java
    |   |
    |   \---resources
    |       |   log4j.properties
    |       |
    |       +---css
    |       |       fxsampler.css   框架样式文件，相关组件样式可以在此文件中修改
    |       |
    |       \---image   菜单图标
    |               controlsfx-API.png
    |               controlsfx-logo.png
    |               equipment.png
    |               home.png
    |               quality.png
    |
    \---test
        \---java
                Test.java
```

## 2.框架使用介绍

* 菜单类介绍

    Menu接口
    ~~~java
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
    ~~~

    EmptyMenu类
    ~~~java
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
    ~~~

    MenuBase抽象类
    ~~~java
        public abstract class MenuBase extends Application implements Menu {
        //每一个子菜单页下包含的模块内容（tab页名称list）
        // 如果你的子菜单只需要一个Tab页显示则可以不设置，可以选择重写buildCustomizeFirstTab方法，tab名称默认为菜单名称
        public List<String> tabs;
        //菜单名称，包含父菜单名称，用“.”分隔
        public String menuName;
        //菜单描述
        public String menuDesc;
        //是否显示第一个默认tab页面
        public boolean ifNeeded = true;

        /**
        * 继承Application类，方便子菜单类页面的单独测试
        */
        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setTitle(getMenuName());
            Scene scene = new Scene((Parent) tabPageViewTest(), 800, 800);
            scene.getStylesheets().add(MenuBase.class.getResource("/css/fxsampler.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        @Override
        public boolean isVisible() {
            return true;
        }

        @Override
        public String getMenuName() {
            return menuName;
        }

        @Override
        public String getMenuDescription() {
            return menuDesc;
        }

        public double getControlPanelDividerPosition() {
            return 0.6;
        }

        /**
        * 创建菜单的第一个默认Tab页
        */
        public final Node buildFirstTab() {
            if (ifNeeded) {
                Node node = buildCustomizeFirstTab();
                if (node == null) {
                    return buildDefaultFirstTab();
                }
                return node;
            }
            return null;
        }

        /**
        * 默认的firstTab为splitPane布局，左边内容通过getPanel方法获得，右边内容通过getControlPanel获得
        * @return
        */
        private final Node buildDefaultFirstTab() {
            SplitPane splitPane = new SplitPane();
            // 我们保证构建顺序是面板，然后是控制面板.
            final Node samplePanel = this.getPanel();
            final Node controlPanel = this.getControlPanel();
            splitPane.setDividerPosition(0, this.getControlPanelDividerPosition());
            if (samplePanel != null) {
                splitPane.getItems().add(samplePanel);
            }
            final VBox rightPanel = new VBox();
            rightPanel.getStyleClass().add("right-panel");
            rightPanel.setMaxHeight(Double.MAX_VALUE);
            boolean addRightPanel = false;
            Label sampleName = new Label(this.getMenuName());
            sampleName.getStyleClass().add("menu-name");
            rightPanel.getChildren().add(sampleName);
            final String menuName = this.getMenuName();
            if (menuName != null && !menuName.isEmpty()) {
                Label projectNameTitleLabel = new Label("Project: ");
                projectNameTitleLabel.getStyleClass().add("project-name-title");
                Label projectNameLabel = new Label(menuName);
                projectNameLabel.getStyleClass().add("project-name");
                projectNameLabel.setWrapText(true);
                TextFlow textFlow = new TextFlow(projectNameTitleLabel, projectNameLabel);
                rightPanel.getChildren().add(textFlow);
            }
            // --- description
            final String description = this.getMenuDescription();
            if (description != null && !description.isEmpty()) {
                Label descriptionLabel = new Label(description);
                descriptionLabel.getStyleClass().add("description");
                descriptionLabel.setWrapText(true);
                rightPanel.getChildren().add(descriptionLabel);
                addRightPanel = true;
            }
            if (controlPanel != null) {
                rightPanel.getChildren().add(new Separator());
                controlPanel.getStyleClass().add("control-panel");
                rightPanel.getChildren().add(controlPanel);
                VBox.setVgrow(controlPanel, Priority.ALWAYS);
                addRightPanel = true;
            }
            if (addRightPanel) {
                ScrollPane scrollPane = new ScrollPane(rightPanel);
                scrollPane.setMaxHeight(Double.MAX_VALUE);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                SplitPane.setResizableWithParent(scrollPane, false);
                splitPane.getItems().add(scrollPane);
            }
            return splitPane;
        }

        /**
        * 默认firstTab页面的左边内容
        * @return
        */
        public Node getPanel(){
            return null;
        }

        /**
        * 默认firstTab页面的右边内容
        * @return
        */
        public Node getControlPanel(){
            return null;
        }

        /*
        * 重写此方法创建自定义的菜单首个tab页面
        * @return
        */
        public Node buildCustomizeFirstTab() {
            return null;
        }

        /**
        * 重写此方法方便单个页面效果测试，默认测试第一个tab页面
        * @return
        */
        public Node tabPageViewTest(){
            return buildFirstTab();
        }

        /**
        * 根据当前选择的菜单跟新tabPane的tab页面
        * @param tabPane
        */
        public final void updateTab(TabPane tabPane){
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            List<Tab> tabs = tabPane.getTabs();
            tabs.forEach(tab -> {
                if (tab.getText().equals(selectedTab.getText())){
                    setContent(selectedTab);
                }
            });
        }

        /**
        * 设置每一个Tab页面的内容，重写此方法给选中的Tab页面设置tab内容
        * @param selectedTab
        */
        public abstract void setContent(Tab selectedTab);
    }
    ~~~

    MenuTree类
    ~~~java
        public class MenuTree {
        //树的根节点
        private TreeNode root;
        //所有节点（中间菜单和叶子菜单）的计数，不包括根菜单节点
        private int count = 0;

        public MenuTree(Menu rootMenu) {
            root = new TreeNode(null, rootMenu.getMenuName(), rootMenu);
        }
        
        public TreeNode getRoot() {
            return root;
        }
        
        public int size() {
            return count;
        }

        /**
        *根据菜单的名称解析出的菜单路径逐层添加菜单节点
        * @param menuPaths
        * @param menu
        */
        public void addMenu(String[] menuPaths, Menu menu) {
            if (menuPaths.length == 0) {
                root.addMenu(menu);
                count++;
                return;
            }
            TreeNode n = root;
            for (String menuPath : menuPaths) {
                if (n.containsChild(menuPath)) {
                    n = n.getChild(menuPath);
                } else {
                    TreeNode newNode;
                    if (menuPath.equals(menuPaths[menuPaths.length - 1])){
                        newNode = new TreeNode(menu,menuPath);
                    }else {
                        newNode = new TreeNode(menuPath);
                    }
                    n.addNode(newNode);
                    count++;
                    n = newNode;
                }
            }
        }
        
        @Override
        public String toString() {
            return root.toString();
        }

        public static class TreeNode {
            private final Menu menu;
            private final String menuPath;
            private final TreeNode parent;
            private List<TreeNode> children;
            
            public TreeNode(String menuPath) {
                this(null, menuPath, null);
            }

            public TreeNode(Menu menu,String menuPath){
                this(null,menuPath,menu);
            }

            public TreeNode(TreeNode parent, String menuPath, Menu menu) {
                this.children = new ArrayList<>();
                this.menu = menu;
                this.parent = parent;
                this.menuPath = menuPath;
            }
            
            public boolean containsChild(String menuPath) {
                if (menuPath == null) return false;
                for (TreeNode n : children) {
                    if (menuPath.equals(n.menuPath)) {
                        return true;
                    }
                }
                return false;
            }
            
            public TreeNode getChild(String menuPath) {
                if (menuPath == null) return null;
                for (TreeNode n : children) {
                    if (menuPath.equals(n.menuPath)) {
                        return n;
                    }
                }
                return null;
            }

            public void addMenu(Menu menu) {
                children.add(new TreeNode(this, menu.getMenuName(), menu));
            }
            
            public void addNode(TreeNode n) {
                children.add(n);
            }
            
            public Menu getMenu() {
                return menu;
            }
            
            public String getMenuPath() {
                return menuPath;
            }
            
            public TreeItem<Menu> createTreeItem() {
                TreeItem<Menu> treeItem = null;
                if (menu != null) {
                    treeItem = new TreeItem<Menu>(menu);
                    treeItem.setExpanded(true);
                }
                else if (menuPath != null) {
                    treeItem = new TreeItem<Menu>(new EmptyMenu(menuPath));
                    treeItem.setExpanded(false);
                }
                // 递归添加子项
                for (TreeNode n : children) {
                    treeItem.getChildren().add(n.createTreeItem());
                }
                return treeItem;
            }
            
            @Override public String toString() {
                if (menu != null) {
                    return " Menu [ menuName: " + menu.getMenuName() + ", children: " + children + " ]";
                } else {
                    return " Menu [ MenuPath: " + menuPath + ", children: " + children + " ]";
                }
            }
        }
    }
    ~~~

    Project类
    ~~~java
        public class Project {
            private final MenuTree menuTree;

            public Project() {
                this.menuTree = new MenuTree(new EmptyMenu(MenuFrame.mainMenuName));
            }

            public void addMenu(String menuPath, Menu menu) {
                String[] menuPaths = menuPath.split("\\.");
                menuTree.addMenu(menuPaths, menu);
            }
            
            public MenuTree getMenuTree() {
                return menuTree;
            }
        }
    ~~~

* 菜单实现

    实现自定义的菜单只要继承```MenuBase```类，重写MenuBase未实现的抽象方法即可。
    > 1.创建一个无参构造方法，方法内设置从```MenuBase```继承来的属性。```menuName```：菜单名称，包含父（父父）菜单的名称不包括根菜单以“.”分隔，名称要与后续需要维护的```menuMap```中的key一致。```menuDesc```：菜单描述。```tabs```：当前菜单要显示的tab页名称list。```ifNeeded```：是否要显示默认的tab页。

    > 2.如果你的菜单页面只需要一个Tab页面，你可以设置```ifNeeded```为```true```，```tabs```不需要设置。现在你可以选择使用默认的tab布局还是自定义的内容布局。如果选择默认tab页面（是一个分为左右两边的SplitPane）你可以重写```getPanel```（左边）和```getControlPanel```（右边）方法。要使用自定义页面你可以选择重写```buildCustomizeFirstTab```方法。只需要一个Tab页面你还可以设置```ifNeeded```为```false```，设置tabs添加一个元素，内容为tab页名称，接着实现```setContent```方法编写自己的页面内容。
        
    > 3.如果需要多个tab页面，同样可以选择是否需要一个默认的页面，同上设置。同时实现```setContent```方法，给所有的tab页面设置内容。

    > 4.菜单页面的单独展示测试你可以重写```tabPageViewTest```方法，方法内编写你的测试内容。

* 入口MenuFrame类介绍

    ```java
        public abstract class MenuFrame extends Application {
            private Project project;
            private Stage stage;
            private GridPane grid;
            private com.shdq.menu_frame.frame.model.Menu selectedMenu;
            private TreeView<com.shdq.menu_frame.frame.model.Menu> menuTreeView;
            private TreeItem<com.shdq.menu_frame.frame.model.Menu> root;
            private TabPane tabPane;
            //list第一个item为序号，第二个item为菜单图标路径
            private Map<String,List<String>> menuMap = new HashMap<>();
            //主页菜单，根菜单名称
            public static String mainMenuName = "overView";
            public static String appName = "FXSampler!";
            public static String logoPath;
            //是否不在treeView中展示overView菜单选项，true：在treeview中显示，作为root treeItem；false：采用单独按钮展示
            public static boolean showOverViewOnRoot = true;
            private static String defaultLogoPath = "/image/controlsfx-logo.png";
            @Override
            public void start(final Stage primaryStage) throws Exception {
                this.stage = primaryStage;
                if (StringUtils.isBlank(logoPath)){
                    primaryStage.getIcons().add(new Image(defaultLogoPath));
                }else {
                    primaryStage.getIcons().add(new Image(logoPath));
                }
                project = new MenuScanner().discoverMenus(this);
                maintainMenuSortMap(menuMap);
                buildSampleTree(null);
                // 简单的布局：菜单TreeView在左侧，内容显示区域在右侧
                grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setPadding(new Insets(5, 10, 10, 10));
                grid.setHgap(10);
                grid.setVgap(10);
                // --- 左手边
                // search box
                final TextField searchBox = new TextField();
                searchBox.setPromptText("Search");
                searchBox.getStyleClass().add("search-box");
                searchBox.textProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable o) {
                        buildSampleTree(searchBox.getText());
                    }
                });
                //不在treeView中显示根节点，把根节点设置到按钮中
                final Button homeButton = new Button();
                if (!showOverViewOnRoot){
                    homeButton.setText(project.getMenuTree().getRoot().getMenu().getMenuName());
                    homeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(menuMap.get(mainMenuName).get(1)))));
                    homeButton.setMinWidth(150);
                    homeButton.setMaxWidth(150);
                    homeButton.setPrefHeight(20);
                    homeButton.setOnMouseClicked(event -> {
                        if (event.getClickCount() <= 1){
                            changeToHomeTab();
                        }
                    });
                }
                // treeView
                menuTreeView = new TreeView<>(root);
                menuTreeView.setShowRoot(showOverViewOnRoot);
                menuTreeView.getStyleClass().add("samples-tree");
                menuTreeView.setMinWidth(200);
                menuTreeView.setMaxWidth(200);
                menuTreeView.setCellFactory(new Callback<TreeView<com.shdq.menu_frame.frame.model.Menu>, TreeCell<com.shdq.menu_frame.frame.model.Menu>>() {
                    @Override
                    public TreeCell<com.shdq.menu_frame.frame.model.Menu> call(TreeView<com.shdq.menu_frame.frame.model.Menu> param) {
                        return new TreeCell<com.shdq.menu_frame.frame.model.Menu>() {
                            @Override
                            protected void updateItem(com.shdq.menu_frame.frame.model.Menu item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setText("");
                                    setGraphic(null);
                                } else {
                                    String[] menuPath = item.getMenuName().split("\\.");
                                    String menu = menuPath[menuPath.length - 1];
                                    setText(menu);
                                    for (String key : menuMap.keySet()){
                                        if (key.contains(menu)){
                                            String iconPath = menuMap.get(key).get(1);
                                            if (StringUtils.isNotBlank(iconPath)){
                                                setGraphic(new ImageView(new Image(getClass().getResourceAsStream(iconPath))));
                                            }
                                        }
                                    }
                                }
                            }
                        };
                    }
                });
                menuTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<com.shdq.menu_frame.frame.model.Menu>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<com.shdq.menu_frame.frame.model.Menu>> observable, TreeItem<com.shdq.menu_frame.frame.model.Menu> oldValue, TreeItem<com.shdq.menu_frame.frame.model.Menu> newValue) {
                        if (newValue == null) {
                            return;
                        } else if (newValue.getValue() instanceof EmptyMenu) {
                            com.shdq.menu_frame.frame.model.Menu selectedMenu = newValue.getValue();
                            if (selectedMenu.getMenuName().equals(mainMenuName)) {
                                changeToHomeTab();
                            }
                            return;
                        }
                        selectedMenu = newValue.getValue();
                        changeMenu();
                    }
                });
                // 右边
                tabPane = new TabPane();
                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
                tabPane.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable arg0) {
                        updateTab();
                    }
                });
                List<TreeItem<com.shdq.menu_frame.frame.model.Menu>> items = menuTreeView.getRoot().getChildren();
                if (!items.isEmpty()) {
                    TreeItem<com.shdq.menu_frame.frame.model.Menu> firstItem = items.get(0);
                    menuTreeView.getSelectionModel().select(firstItem);
                } else {
                    changeToHomeTab();
                }
                GridPane.setMargin(searchBox, new Insets(5, 0, 0, 0));
                grid.add(searchBox, 0, 0);
                if (!showOverViewOnRoot){
                    GridPane.setMargin(homeButton,new Insets(0,25,0,25));
                    grid.add(homeButton,0,1);
                    GridPane.setVgrow(menuTreeView,Priority.ALWAYS);
                    grid.add(menuTreeView,0,2);
                    GridPane.setHgrow(tabPane,Priority.ALWAYS);
                    GridPane.setVgrow(tabPane,Priority.ALWAYS);
                    grid.add(tabPane,1,0,1,3);
                }else {
                    GridPane.setVgrow(menuTreeView, Priority.ALWAYS);
                    grid.add(menuTreeView, 0, 1);
                    GridPane.setHgrow(tabPane, Priority.ALWAYS);
                    GridPane.setVgrow(tabPane, Priority.ALWAYS);
                    grid.add(tabPane, 1, 0, 1, 2);
                }
                Scene scene = new Scene(grid);
                scene.getStylesheets().add(getClass().getResource("/css/fxsampler.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.setMinWidth(1000);
                primaryStage.setMinHeight(600);
                // 将宽度/高度值设置为用户屏幕分辨率的75％
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                primaryStage.setWidth(screenBounds.getWidth() * 0.75);
                primaryStage.setHeight(screenBounds.getHeight() * .75);
                primaryStage.setTitle(appName);
                primaryStage.show();
                if (showOverViewOnRoot){
                    //默认选中根节点
                    menuTreeView.getSelectionModel().select(menuTreeView.getRoot());
                }else {
                    homeButton.requestFocus();
                }
                changeToHomeTab();
            }

            protected void buildSampleTree(String searchText) {
                root = project.getMenuTree().getRoot().createTreeItem();
                // 借助这棵新建的完整树，我们将基于搜索文本进行过滤
                if (searchText != null) {
                    pruneSampleTree(root, searchText);
                    // FIXME 我认为TreeView中的怪异错误
                    menuTreeView.setRoot(null);
                    menuTreeView.setRoot(root);
                }
                if (menuMap.isEmpty() || menuMap.size() < project.getMenuTree().size()){
                    sort(root, (o1, o2) -> o1.getValue().getMenuName().compareTo(o2.getValue().getMenuName()));
                }else {
                    sort(root, (o1, o2) -> menuMap.get(o1.getValue().getMenuName()).get(0).compareTo(menuMap.get(o2.getValue().getMenuName()).get(0)));
                }
            }

            private void sort(TreeItem<com.shdq.menu_frame.frame.model.Menu> node, Comparator<TreeItem<com.shdq.menu_frame.frame.model.Menu>> comparator) {
                node.getChildren().sort(comparator);
                for (TreeItem<com.shdq.menu_frame.frame.model.Menu> child : node.getChildren()) {
                    sort(child, comparator);
                }
            }

            private boolean pruneSampleTree(TreeItem<com.shdq.menu_frame.frame.model.Menu> treeItem, String searchText) {
                // 一直到叶节点，然后检查它们是否与搜索文本匹配。如果匹配，它们留下。如果没有，我们将其删除。
                // 当弹出时，我们检查分支节点是否仍然有子节点，否则，我们也将其删除
                if (searchText == null) {
                    return true;
                }
                if (treeItem.isLeaf()) {
                    // 检查匹配。如果匹配，则返回true；如果删除则返回false
                    return treeItem.getValue().getMenuName().toUpperCase().contains(searchText.toUpperCase());
                } else {
                    List<TreeItem<com.shdq.menu_frame.frame.model.Menu>> toRemove = new ArrayList<>();
                    for (TreeItem<com.shdq.menu_frame.frame.model.Menu> child : treeItem.getChildren()) {
                        boolean keep = pruneSampleTree(child, searchText);
                        if (!keep) {
                            toRemove.add(child);
                        }
                    }
                    // 删除不相关的项
                    treeItem.getChildren().removeAll(toRemove);
                    // 如果此分支有子级，则返回true，否则返回false
                    return !treeItem.getChildren().isEmpty();
                }
            }

            protected void changeMenu() {
                if (selectedMenu == null) {
                    return;
                }
                setMenuTabs();
                updateTab();
            }

            protected void setMenuTabs() {
                tabPane.getTabs().clear();
                MenuBase menuBase = (MenuBase) selectedMenu;
                String[] menuPath = selectedMenu.getMenuName().split("\\.");
                if (menuBase.ifNeeded){
                    Tab firstTab = new Tab(menuPath[menuPath.length - 1]);
                    firstTab.setContent(menuBase.buildFirstTab());
                    tabPane.getTabs().add(firstTab);
                }
                List<String> tabs = menuBase.tabs;
                if (tabs != null && !tabs.isEmpty()) {
                    for (String tab : tabs) {
                        tabPane.getTabs().add(new Tab(tab));
                    }
                }
            }

            private void updateTab() {
                // 只会更新选定的标签-将其他标签保留在之前的状态，直到被选中为止
                if (selectedMenu != null) {
                    ((MenuBase) selectedMenu).updateTab(tabPane);
                }
            }

            private void changeToHomeTab() {
                HomePage page = getDefaultHomePage();
                Tab tab = new Tab(page.getTitle());
                tab.setContent(page.getContent());
                tabPane.getTabs().setAll(tab);
            }

            /**
            * 重写此方法以不同方式加载menu
            * @param menus
            */
            public abstract void initMenuListData(List<com.shdq.menu_frame.frame.entity.Menu> menus);
            /**
            * 重写此方法创建自定义概览（首）页
            * @return
            */
            public abstract HomePage getDefaultHomePage();

            /**
            * 维护一个菜单map，包含菜单序号，菜单的图标路径；通过这个map可以调整菜单显示顺序和显示的图标
            * 如果没有提供，使用菜单名称进行排序显示
            * @return
            */
            public abstract void maintainMenuSortMap(Map<String,List<String>> menuSortMap);
    ```

* 入口类实现

    应用程序入口类需要继承MenuFrame类。
    > 1.继承了```MenuFrame```类只需要重写三个方法即可。```initMenuListData```方法（不实现则使用内部的展示菜单进行展示），外部菜单的加载方式。例如从数据库加载来的菜单列表，框架外部的菜单类应该实现框架内```com.shdq.menu_frame.frame.entity.Menu```接口。把加载到的菜单放入menus中。在```MenuScanner```类中会将menus中的对象进行解析为一个菜单全路径名（包含父、父父菜单名称，但不包含根菜单名称，以“.”分隔）和菜单对象的map；```MenuScanner```会对项目进行扫描，获取所有的class并筛选出实现了MenuBase类的类（自己定义的叶子菜单类），在上一步解析出的map进行匹配，匹配成功则加入```Project```的MenuTree中。

    > 2.```getDefaultHomePage```方法，设置根菜单的主页页面展示。不实现则采用默认主页展示。

    > 3.```maintainMenuSortMap```方法。维护一个全部菜单项的map，用来设置菜单显示顺序，菜单的图标内容，包括根节点菜单，中间节点菜单，叶子节点菜单。key为菜单的全路径名（包括父，父父菜单名称）不包括根菜单名称已“.”分隔，value为String列表，默认列表第一项为排序序号，第二项为图标路径。注意如果map没有包含上所有的菜单则排序不起作用，采用菜单名称排序，如果没有图标则设置为空字符串，不能不设或设置为```null```。

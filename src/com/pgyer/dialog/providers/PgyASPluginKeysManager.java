package com.pgyer.dialog.providers;

/**
 * Created by Tao9jiu on 16/1/5.
 */

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.pgyer.dialog.Constants;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.util.Calendar;
import java.util.Iterator;


@State(
        name = "PgyASPluginKeysManager", storages = {
@Storage(
        id = "other",
        file = "$APP_CONFIG$/" + Constants.PERSISTENCE_FILE_NAME
)
})

public class PgyASPluginKeysManager implements PersistentStateComponent<Element>,CompilationStatusListener {

    //xml parsing constant used as a root tag for this class
    //用作这个类的根标记的解析常量
    public static final String XML_ROOT_NAME_Key_MANAGER = "KeyManager";
    // xml parsing constants used for team
    public static final String XML_ROOT_NAME_TEAM_MANAGER = "TeamManager";
    public static final String XML_TEAM_MANAGER_TEAM = "Team";
    public static final String XML_TEAM_MANAGER_NAME = "name";
    public static final String XML_TEAM_MANAGER_TOKEN = "token";
    public static final String XML_TEAM_MANAGER_DISTRIBUTION = "distribution";
    public static final String XML_TEAM_MANAGER_COMPONENT = "component";
    // xml parsing constants used for api key && ukey
    public static final String XML_ROOT_NAME_API_KEY = "ApiKey";
    public static final String XML_ROOT_NAME_UKEY = "uKey";
    // xml parsing constant used for the apk file path
    public static final String XML_ROOT_NAME_APK_FILE_PATH = "ApkFilePath";
    public static final String XML_ROOT_NAME_APK_MD5_VAL = "MD5";
    public static final String XML_ROOT_NAME_APK_FLAG_VAL = "FLAG";
    public static final String XML_ROOT_NAME_APK_LANGUAGE_VAL = "LANGUAGE";

    public static final String XML_ROOT_NAME_APK_UPLOAD_FLAG_VAL = "UPLOAD_FLAG";
    public static final String XML_ROOT_NAME_SELECTED_MODULE_NAME = "SelectedModuleName";
    public static final String XML_ROOT_NAME_SELECTED_PROJECT_NAME = "SelectedProjectName";

    //* Maximum number of milliseconds since the last compile time before the user can send the build to Test Flight (currently 5 minutes)
    //自上次编译时间以来，用户可发送测试飞行（当前5分钟）后的最大毫秒数

    public static final int MAX_MILLISECONDS_SINCE_LAST_COMPLIE = 100 * 60 * 5;

    private static Calendar lastCompileTime = Calendar.getInstance();

    private static PgyASPluginKeysManager sInstance = null;

    private String uKey;
    private String api_key;

    private String apkFilePath;
    private String md5;
    private String language;
    private String uploadFlag;
    private String flag;

    private String selectedModuleName;
    private String selectedProjectName;

    public PgyASPluginKeysManager() {

    }

    public static PgyASPluginKeysManager instance() {

        if (sInstance == null) {
            sInstance = ServiceManager.getService(PgyASPluginKeysManager.class);

            /**  给服务添加项目监听 */

            ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {
                @Override
                public void projectOpened(Project project) {
                    CompilerManager.getInstance(project)
                            .addCompilationStatusListener(sInstance);
                }

                @Override
                public boolean canCloseProject(Project project) {
                    return true;
                }

                @Override
                public void projectClosed(Project project) {
                }

                @Override
                public void projectClosing(Project project) {
                    CompilerManager.getInstance(project)
                            .removeCompilationStatusListener(sInstance);
                }
            });

            /** 将服务监听 实例化 给当前打开的项目 **/
            CompilerManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0])
                    .addCompilationStatusListener(sInstance);

        }
        return sInstance;
    }

    @Nullable
    @Override
    public Element getState() {

        //create the class root tag
        Element rootTag = new Element(XML_ROOT_NAME_Key_MANAGER);

        //create the team root xml tag
        Element teamRootTag = new Element(XML_ROOT_NAME_TEAM_MANAGER);

        //add the team elements
        rootTag.addContent(teamRootTag);

        if (uKey != null) {
            //set the ukey key
            Element ukeyKeyTag = new Element(XML_ROOT_NAME_UKEY).setText(uKey);
            rootTag.addContent(ukeyKeyTag);
        }
        if (api_key != null) {
            Element apiKeyTag = new Element(XML_ROOT_NAME_API_KEY).setText(api_key);
            rootTag.addContent(apiKeyTag);
        }
        if (apkFilePath != null) {
            Element filePathTag = new Element(XML_ROOT_NAME_APK_FILE_PATH).setText(apkFilePath);
            rootTag.addContent(filePathTag);
        }
        if (md5 != null) {
            Element filePathTag = new Element(XML_ROOT_NAME_APK_MD5_VAL).setText(md5);
            rootTag.addContent(filePathTag);
        }
        if (language != null) {
            Element filePathTag = new Element(XML_ROOT_NAME_APK_LANGUAGE_VAL).setText(language);
            rootTag.addContent(filePathTag);
        }
        if (uploadFlag != null) {
            Element filePathTag = new Element(XML_ROOT_NAME_APK_UPLOAD_FLAG_VAL).setText(uploadFlag);
            rootTag.addContent(filePathTag);
        }
        if (flag != null) {
            Element filePath = new Element(XML_ROOT_NAME_APK_FLAG_VAL).setText(flag);
            rootTag.addContent(filePath);
        }
        if (selectedModuleName != null) {
            Element moduleName = new Element(XML_TEAM_MANAGER_NAME).setText(selectedModuleName);
            rootTag.addContent(moduleName);
        }
        if (selectedProjectName != null) {
            Element projectName = new Element(XML_ROOT_NAME_SELECTED_PROJECT_NAME).setText(selectedProjectName);
            rootTag.addContent(selectedProjectName);
        }

        return rootTag;
    }

    @Override
    public void loadState(Element componentTag) {

        // 疑问,什么时候设置的  XML_TEAM_MANAGER_COMPONENT ?
        if (componentTag.getName().equals(XML_TEAM_MANAGER_COMPONENT)) {

            Iterator rootIterator = componentTag.getDescendants();

            // loop through all the root elements and parse them accordingly
            while (rootIterator.hasNext()) {

                Object element = rootIterator.next();

                if (!(element instanceof Element)) {
                    continue;
                }

                Element rootElement = (Element) element;

                if (rootElement.getName().equals(XML_ROOT_NAME_API_KEY)) {
                    api_key = parseApiKey(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_UKEY)) {
                    // parse the api key
                    uKey = parseuKey(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_APK_FILE_PATH)) {
                    // parse the apk file path
                    apkFilePath = parseApkFilePath(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_SELECTED_MODULE_NAME)) {
                    // parse the user selected module name
                    selectedModuleName = parseUserSelectedModuleName(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_SELECTED_PROJECT_NAME)) {
                    // parse the user selected project name
                    selectedProjectName = parseUserSelectedProjectName(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_APK_MD5_VAL)) {
                    // parse the apk file path
                    md5 = parseMd5(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_APK_LANGUAGE_VAL)) {
                    // parse the apk file path
                    language = parseLanguage(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_APK_UPLOAD_FLAG_VAL)) {
                    // parse the apk file path
                    uploadFlag = parseUploadFlag(rootElement);

                } else if (rootElement.getName().equals(XML_ROOT_NAME_APK_FLAG_VAL)) {
                    // parse the apk file path
                    flag = parseFlag(rootElement);
                }
            }
        }
    }

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {

        if (errors < 1) {
            //获取当前的状态栏

            StatusBar statusBar = WindowManager.getInstance()
                    .getStatusBar(ProjectManager.getInstance().getOpenProjects()[0]);

            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("编译完成发送到蒲公英 <a href = 'open'></a> 打开蒲公英插件",
                            MessageType.INFO, new HyperlinkListener() {
                                @Override
                                public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                                    if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                        ToolWindowManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0])
                                                .getToolWindow("Pgyer Plugin").show(null);
                                    }
                                }
                            })
                    .setFadeoutTime(4000)
                    .createBalloon()
                    .show(RelativePoint.getNorthEastOf(statusBar.getComponent()), Balloon.Position.atRight);
        }
    }


    @Override
    public void fileGenerated(String s, String s1) {

    }

/**********************************************数据分析  parse(load) set get *************************************************************************/
    /**
     * Parse the user's selected project name xml element
     *
     * @param element the user's selected project element
     * @return parsed value
     */
    public String parseUserSelectedProjectName(Element element) {
        return element.getText();
    }

    /**
     * Parse the user's selected module name xml element
     *
     * @param element the user's selected module element
     * @return parsed value
     */
    public String parseUserSelectedModuleName(Element element) {

        return element.getText();

    }

    /**
     * Parse the apk file path xml element
     *
     * @param element apk file path element
     * @return parsed file path
     */
    public String parseApkFilePath(Element element) {
        return element.getText();
    }

    public String parseMd5(Element element) {
        return element.getText();
    }

    public String parseLanguage(Element element) {
        return element.getText();
    }


    public String parseFlag(Element element) {
        return element.getText();
    }

    public String parseUploadFlag(Element element) {
        return element.getText();
    }

    /**
     * Parse the api key xml element
     *
     * @param element the root element of the api key
     * @return parsed api key
     */
    public String parseApiKey(Element element) {
        return element.getText();
    }

    /**
     * Returns the api key for test flight authentication
     *
     * @return api_key
     */
    public String getApiKey() {
        return api_key;
    }

    /**
     * Set the api key used for the test flight authentication
     *
     * @param api_Key api key
     */
    public void setApiKey(String api_Key) {
        this.api_key = api_Key;
    }

    /**
     * Parse the api key xml element
     *
     * @param element the root element of the api key
     * @return parsed api key
     */
    public String parseuKey(Element element) {
        return element.getText();
    }

    /**
     * Returns the api key for test flight authentication
     *
     * @return api_key
     */
    public String getuKey() {
        return uKey;
    }

    /**
     * Set the api key used for the test flight authentication
     *
     * @param uKey u key
     */
    public void setuKey(String uKey) {
        this.uKey = uKey;
    }

    /**
     * Returns the apk file path
     *
     * @return apk file path
     */
    public String getApkFilePath() {
        return apkFilePath;
    }

    /**
     * Set the apk file path
     *
     * @param apkFilePath apk file path
     */
    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }


    public String getMd5() {
        return md5;
    }

    public void setMd5(String m) {
        this.md5 = m;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String m) {
        this.language = m;
    }

    public String getUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(String flag) {
        this.uploadFlag = flag;
    }

    public String getFlag() {
        return flag;
    }


    public void setFlag(String fl) {
        this.flag = fl;
    }

    /**
     * Returns the user's selected module name, null if none was saved so far
     *
     * @return user's selected module name
     */
    public String getSelectedModuleName() {
        return selectedModuleName;
    }

    /**
     * Set the user's selected module name, used to get the apk file path for the module
     *
     * @param selectedModuleName selected module name
     */
    public void setSelectedModuleName(String selectedModuleName) {
        this.selectedModuleName = selectedModuleName;
    }

    /**
     * Returns the selected project name
     *
     * @return user's selected project name
     */
    public String getSelectedProjectName() {
        return selectedProjectName;
    }

    /**
     * Set the selected project name
     *
     * @param selectedProjectName user selected project name
     */
    public void setSelectedProjectName(String selectedProjectName) {
        this.selectedProjectName = selectedProjectName;
    }

    public static Calendar getLastCompileTime() {
        return lastCompileTime;
    }

    public static void setLastCompileTime(Calendar lastCompileTime) {
        PgyASPluginKeysManager.lastCompileTime = lastCompileTime;
    }
}
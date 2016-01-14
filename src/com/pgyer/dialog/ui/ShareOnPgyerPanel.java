package com.pgyer.dialog.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.pgyer.dialog.dialoges.ShareOnPgyerDialog;
import com.pgyer.dialog.providers.ApkInformation;
import com.pgyer.dialog.providers.PgyASPluginKeysManager;
import com.pgyer.dialog.utils.ModulesManager;
import com.pgyer.dialog.utils.SearchFile;
import com.pgyer.dialog.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Tao9jiu on 16/1/5.
 */
public class ShareOnPgyerPanel {
    private JTextField appkeyinput;
    private JTextField ukeyinput;
    private JComboBox projectcombo;
    private JButton settingBtn;
    private JLabel labelappkey;
    private JLabel labelukey;
    private JLabel getappkey;
    private JLabel getukey;
    private JPanel panel;
    private JLabel apkPath;
    private JTextArea uoloadLog;
    private JLabel tips;
    public ShareOnPgyerDialog shareOnPgyerDialog;
    public Color mainColor;
    public String apkAbsolutePath;
    public ApkInformation apkInformation;

    public ShareOnPgyerPanel(final ShareOnPgyerDialog shareOnPgyerDialog) {

        this.shareOnPgyerDialog = shareOnPgyerDialog;
        mainColor = new Color(26,188,156);

        apkInformation = ApkInformation.getInstance();

        getappkey.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                browserUrl("http://www.pgyer.com/account/index");
                getappkey.setForeground(mainColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        getukey.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                browserUrl("http://www.pgyer.com/account/index");
                getukey.setForeground(mainColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        /***********************************与 setupValuesOnUI()  做了类似的事情
         *                                   给 projectCombo  添加选择model  以及数量
         *                                   这里添加可以检测到每次新建项目的module************************************************************************************/
        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {
            @Override
            public void projectOpened(Project project) {

                // get the best matching module for this project and set it's file path
                Module previouslySelectedModule = ModulesManager.instance().getMostImportantModule();
                String filePath = previouslySelectedModule.getModuleFilePath();
                filePath = parsefilePath(filePath);
                apkAbsolutePath = filePath;
                apkInformation.initPath(apkAbsolutePath);
                apkInformation.setFilePath(apkAbsolutePath);
                apkPath.setText(splitPath(filePath));
                //新建项目,则跟新选择按钮列表
                PgyASPluginKeysManager.instance().setSelectedModuleName(previouslySelectedModule.getName());
                String[] modules = ModulesManager.instance().getAllModuleNames();
                if (modules != null) {
                    // set the model of the modules
                    projectcombo.setModel(new DefaultComboBoxModel(ModulesManager.instance().getAllModuleNames()));
                }
                // set the selection
                projectcombo.setSelectedIndex(ModulesManager.instance().getSelectedModuleIndex(previouslySelectedModule.getName()));
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

            }
        });

        setupValuesOnUI();
        projectcombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PgyASPluginKeysManager.instance().setSelectedModuleName((String) projectcombo.getSelectedItem());
                /* update the apk path**/
                Module module = ModulesManager.instance().getModuleByName((String) projectcombo.getSelectedItem());
                String filePath = ModulesManager.instance().getAndroidApkPath(module);
                filePath = parsefilePath(filePath);
                apkAbsolutePath = filePath;
                apkInformation.initPath(apkAbsolutePath);
                apkInformation.setFilePath(apkAbsolutePath);
                SearchFile.getInstance().initPath(apkInformation.filePath);
                apkPath.setText(splitPath(filePath));
                // update the build version fields too
                updateBuildVersionFields();                                        //未完成,后续添加,初步判定没用
            }
        });

        settingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // create a new file type with the apk extension to be used for file type filtering
                FileType type = FileTypeManager.getInstance().getFileTypeByExtension("apk");
                // create a descriptor for the file chooser
                FileChooserDescriptor descriptor = Utils.createSingleFileDescriptor(type);
                descriptor.setTitle("APK File");
                descriptor.setDescription("选择APK文件上传到蒲公英");
                // by default open the first opened project root directory
                VirtualFile fileToSelect = ProjectManager.getInstance().getOpenProjects()[0].getBaseDir();
                // open the file chooser
                FileChooser.chooseFiles(descriptor, null, fileToSelect, new FileChooser.FileChooserConsumer() {
                    @Override
                    public void cancelled() {
                        // do nothing for now...
                    }

                    @Override
                    public void consume(java.util.List<VirtualFile> virtualFiles) {

                        String filePath = virtualFiles.get(0).getPath();
                        // the file was selected so add it to the text field
                        File file = new File(filePath);
                        if (!file.exists() || filePath.toLowerCase().indexOf(".apk") < 0) {
                            filePath = "";
                        }
                        apkAbsolutePath = filePath;
                        settingBtn.setText("重新选择文件");
                        apkPath.setText(splitPath(filePath));
                        apkInformation.initPath(apkAbsolutePath);
                        apkInformation.setFilePath(apkAbsolutePath);
                        SearchFile.getInstance().initPath(apkInformation.filePath);
                        // save the file path
                        PgyASPluginKeysManager.instance().setApkFilePath(filePath);
                        updateBuildVersionFields();
                    }
                });
            }
        });
    }
    public String getAppkeyInput(){

        return appkeyinput.getText().trim();
    }

    public String getUkeyinput(){
        return ukeyinput.getText().trim();
    }

    public void setAppkeyinput(String appkey){
        appkeyinput.setText(appkey);
    }

    public void setuKeyinput(String ukey){
        ukeyinput.setText(ukey);
    }
    public JPanel getPanel(){
        return panel;
    }

    public String getuploadLog(){
        return uoloadLog.getText().trim();
    }

    public void browserUrl(String url){
        try {
            Desktop.getDesktop().browse(new URI( url));
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Set the default or previously saved values on the UI components
     */
    public void setupValuesOnUI() {

        Module previouslySelectedModule;

        // if the apk file path was not saved previously by the user, set the saved module apk file path or the best matching module
        previouslySelectedModule = ModulesManager.instance().getModuleByName(PgyASPluginKeysManager.instance().getSelectedModuleName());

        if (previouslySelectedModule != null) {
            String filePath = ModulesManager.instance().getAndroidApkPath(previouslySelectedModule);
            filePath = parsefilePath(filePath);
            apkAbsolutePath = filePath;

            apkPath.setText(splitPath(filePath));
            apkInformation.initPath(apkAbsolutePath);
            SearchFile.getInstance().initPath(apkInformation.filePath);
        } else {
            // get the best matching module for this project and set it's file path
            previouslySelectedModule = ModulesManager.instance().getMostImportantModule();
            String filePath = ModulesManager.instance().getAndroidApkPath(previouslySelectedModule);
            // the file was selected so add it to the text field
            filePath = parsefilePath(filePath);
            apkAbsolutePath = filePath;
            apkPath.setText(splitPath(filePath));
            apkInformation.initPath(apkAbsolutePath);
            SearchFile.getInstance().initPath(apkInformation.filePath);
        }

        // set the model of the modules
        projectcombo.setModel(new DefaultComboBoxModel(ModulesManager.instance().getAllModuleNames()));

        // set the selection
        /************************************************这块添加一个判断,不然在 本地文件中读取的modul为null时报错*********************************************************************/
        projectcombo.setSelectedIndex(ModulesManager.instance().getSelectedModuleIndex(previouslySelectedModule.getName()));
        updateBuildVersionFields();
    }
    /*********** 判断 apk文件存在,不存在返回"" ***********/
    public String parsefilePath(String filePath){
        if(filePath == null) {
            filePath = "";
        }
        // the file was selected so add it to the text field
        File file = new File(filePath) ;

        if(!file.exists() || filePath.toLowerCase().indexOf(".apk") < 0)    {
            filePath = "";
        }
        return filePath;
    }

    /*** 简化apk路径 **/
    public String splitPath(String filep){
        String rt = "";
        if(filep == null){
            filep = "";
        }
        if(filep.length()>30){
            filep ="..."+filep.substring(filep.length()-30,filep.length());
        }
        return filep  ;
    }

    /**
     * Updates the build version(code and name) fields
     *
     *  初步判定没有用
     */
    public void updateBuildVersionFields() {
        Module module = ModulesManager.instance().getModuleByName((String) projectcombo.getSelectedItem());
        // update the code and name text fields manifest build version code and name values

        String[] apk = new String[3];
        if(apkAbsolutePath != null){
            apkInformation.initPath(apkAbsolutePath);
        }
    }

    public String getApkPath() {
        return apkPath.getText();
    }
}

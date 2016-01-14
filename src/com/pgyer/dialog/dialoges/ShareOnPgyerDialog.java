package com.pgyer.dialog.dialoges;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.pgyer.dialog.providers.ApkInformation;
import com.pgyer.dialog.providers.PgyASPluginKeysManager;
import com.pgyer.dialog.providers.UploadService;
import com.pgyer.dialog.ui.ShareOnPgyerPanel;
import git4idea.DialogManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Tao9jiu on 16/1/6.
 */
public class ShareOnPgyerDialog extends DialogWrapper {
    public ShareOnPgyerPanel shareOnPgyerPanel;

    public ShareOnPgyerDialog(@Nullable Project project) {

        super(project);
        this.setResizable(false);
        shareOnPgyerPanel = new ShareOnPgyerPanel(this);
        if(PgyASPluginKeysManager.instance().getApiKey() != null){
            shareOnPgyerPanel.setAppkeyinput(PgyASPluginKeysManager.instance().getApiKey());
        }

        if(PgyASPluginKeysManager.instance().getuKey() != null){
            shareOnPgyerPanel.setuKeyinput(PgyASPluginKeysManager.instance().getuKey());
        }

        setOKButtonText("下一步");
        setCancelButtonText("取消");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return shareOnPgyerPanel.getPanel();
    }

    @Override
    public void doOKAction() {

        super.doOKAction();
        final Project project = ProjectManager.getInstance().getDefaultProject();
        InformationOfPgyerDialog informationOfPgyerDialog = new InformationOfPgyerDialog(project);
        informationOfPgyerDialog.setTitle("上传到蒲公英");
        String appkey = this.shareOnPgyerPanel.getAppkeyInput();
        String uploadLog = this.shareOnPgyerPanel.getuploadLog();

        if(appkey != null && appkey.length()>3){
            PgyASPluginKeysManager.instance().setApiKey(appkey);
        }

        String ukey = this.shareOnPgyerPanel.getUkeyinput();

        if(ukey != null && ukey.length()>3){
            PgyASPluginKeysManager.instance().setuKey(ukey);
        }

        if(uploadLog != null){
            ApkInformation.getInstance().setTextlog(uploadLog);
        }




        if (ApkInformation.getInstance().getFilePath() != null && shareOnPgyerPanel.getApkPath() != "") {
            DialogManager.show(informationOfPgyerDialog);
        } else {
                Messages.showErrorDialog("请选择正确的文件", "错误提示");
        }


    }
}

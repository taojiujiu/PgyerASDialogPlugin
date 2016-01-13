package com.pgyer.dialog.dialoges;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.pgyer.dialog.ui.InformationOfPgyerPanel;
import git4idea.DialogManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Tao9jiu on 16/1/6.
 */
public class InformationOfPgyerDialog extends DialogWrapper {
    public InformationOfPgyerPanel informationOfPgyerPanel;
    public InformationOfPgyerDialog(@Nullable Project project) {

        super(project);
        this.setResizable(false);
        informationOfPgyerPanel = new InformationOfPgyerPanel(this);
//        appInformationPanel = new AppInformationPanel(this);
        setCancelButtonText("上一步");
        setOKButtonText("关闭");
            init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return informationOfPgyerPanel.getPanel();
//        return  appInformationPanel.getPanel();
    }


    @Override
    public void doCancelAction() {
        super.doCancelAction();
        final Project project = ProjectManager.getInstance().getDefaultProject();
        ShareOnPgyerDialog shareOnPgyerDialog = new ShareOnPgyerDialog(project);

        DialogManager.show(shareOnPgyerDialog);
    }
}

package com.pgyer.dialog.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.pgyer.dialog.dialoges.InformationOfPgyerDialog;
import com.pgyer.dialog.dialoges.ShareOnPgyerDialog;
import com.pgyer.dialog.providers.ApkInformation;
import git4idea.DialogManager;

/**
 * Created by Tao9jiu on 16/1/5.
 */
public class ShareOnPgyerAction extends AnAction {
    public ApkInformation apkInformation = ApkInformation.getInstance();


    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        ShareOnPgyerDialog shareOnPgyerDialog = new ShareOnPgyerDialog(project);


        shareOnPgyerDialog.setTitle("蒲公英");
        DialogManager.show(shareOnPgyerDialog);


//        InformationOfPgyerDialog informationOfPgyerDialog = new InformationOfPgyerDialog(project);
//        DialogManager.show(informationOfPgyerDialog);


    }
}

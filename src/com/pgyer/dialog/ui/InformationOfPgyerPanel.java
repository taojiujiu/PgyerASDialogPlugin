package com.pgyer.dialog.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.pgyer.dialog.actions.ShareOnPgyerAction;
import com.pgyer.dialog.dialoges.InformationOfPgyerDialog;
import com.pgyer.dialog.dialoges.ShareOnPgyerDialog;
import com.pgyer.dialog.providers.ApkInformation;
import com.pgyer.dialog.providers.PgyASPluginKeysManager;
import com.pgyer.dialog.providers.UploadService;
import git4idea.DialogManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Tao9jiu on 16/1/5.
 */
public class InformationOfPgyerPanel implements  UploadService.UploadServiceDelegate{
    private JProgressBar progressBar;
    private JButton upload;
    private JLabel appname;
    private JLabel appcodename;
    private JLabel shortUrl;
    private JLabel check;
    private JPanel panel;
    private JLabel shorttiltle;
    private JPanel icon;
    private JButton backbutton;

    public JLabel iconimage;

    public InformationOfPgyerDialog informationOfPgyerDialog;
    public UploadService uploadService;
    public ApkInformation apkInformation;

    public InformationOfPgyerPanel(final InformationOfPgyerDialog informationOfPgyerDialog) {

        this.informationOfPgyerDialog = informationOfPgyerDialog;
        uploadService  = new UploadService();
        apkInformation = ApkInformation.getInstance();
        shorttiltle.setVisible(false);
        shortUrl.setVisible(false);
        check.setVisible(false);

        if(apkInformation != null){
            appname.setText(apkInformation.getName());
            appcodename.setText(apkInformation.getVersionName());



            progressBar.setVisible(false);
            if(apkInformation.getaShort()!= null){
                shortUrl.setText(apkInformation.getaShort());
                shorttiltle.setVisible(true);
                shortUrl.setVisible(true);
                upload.setVisible(true);
                check.setText("已经上传");
                check.setVisible(true);
            }
            upload.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    performUploadValidation();


                }
            });

            shortUrl.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                    browserUrl(shortUrl.getText());
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        }

    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void onUploadFinished(final boolean finishedSuccessful) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {


                if(!finishedSuccessful){
                    progressBar.setVisible(false);
                    upload.setEnabled(true);
                    check.setVisible(true);
                    check.setText("上传失败，请回到上一步检查APIKEY和UKEY是否合法");

                    return;
                }

                progressBar.setVisible(false);
                upload.setEnabled(false);
                shortUrl.setVisible(true);
                shorttiltle.setVisible(true);
                check.setVisible(true);
                check.setText("上传成功");
                shortUrl.setText(ApkInformation.getInstance().getaShort());



            }
        });
    }

    @Override
    public void onPackageSizeComputed(final long totalSize) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setMaximum((int) totalSize);
            }
        });
    }

    @Override
    public void onProgressChanged(final long progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                progressBar.setValue((int) progress);
            }
        });
    }


    public void browserUrl(String url){
        try {
            Desktop.getDesktop().browse(new URI( url));
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            Utils.postErrorNoticeTOSlack(e1);
        } catch (URISyntaxException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            Utils.postErrorNoticeTOSlack(e1);
        }
    }

    /**
     * Performs validation before uploading the build to test flight, if everything is in order, the build is sent
     */
    public void performUploadValidation() {


        uploadBuild();


    }

    /***** 上传蒲公英功能 ****/
    public void uploadBuild(){
        progressBar.setValue(0);
        progressBar.setVisible(true);

        upload.setEnabled(false);

//

        uploadService.uploadAPKfile(PgyASPluginKeysManager.instance().getApiKey(), PgyASPluginKeysManager.instance().getuKey(),
                apkInformation.getFilePath(),ApkInformation.getInstance().getTextlog(), InformationOfPgyerPanel.this);
    }



}

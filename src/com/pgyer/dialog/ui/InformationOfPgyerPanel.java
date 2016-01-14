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
import com.pgyer.dialog.utils.SearchFile;
import git4idea.DialogManager;
import sun.awt.image.ToolkitImage;

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
    public JLabel iconimage;
    private JLabel identifer;

    public InformationOfPgyerDialog informationOfPgyerDialog;
    public UploadService uploadService;
    public ApkInformation apkInformation;

    public InformationOfPgyerPanel(final InformationOfPgyerDialog informationOfPgyerDialog) {

        this.informationOfPgyerDialog = informationOfPgyerDialog;
        uploadService  = new UploadService();
        apkInformation = ApkInformation.getInstance();

        icon.setVisible(true);
        shorttiltle.setVisible(false);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
//                Image image = Toolkit.getDefaultToolkit().getImage("/Users/Tao9jiu/Downloads/app.png");
                if(apkInformation.getIcon() != null) {
                    String iconpath = SearchFile.getInstance().queryIcon(apkInformation.getIcon());
                    Image image = Toolkit.getDefaultToolkit().getImage(iconpath);
                    ImageIcon icon_ = new ImageIcon(image);
                    icon_.setImage(icon_.getImage().getScaledInstance(70, 70, Image.SCALE_AREA_AVERAGING));
                    iconimage.setIcon(icon_);
                }else {
                    ImageIcon icon_ = new ImageIcon("icon.png");
                    icon_.setImage(icon_.getImage().getScaledInstance(70, 70, Image.SCALE_AREA_AVERAGING));
                    iconimage.setIcon(icon_);

                }
                iconimage.setVisible(true);
            }
        });

        shorttiltle.setVisible(false);
        shortUrl.setVisible(false);
        check.setVisible(false);

        if(apkInformation != null){
            appname.setText(apkInformation.getName());
            appcodename.setText(apkInformation.getVersionName()+" ( "+apkInformation.getVersionCode()+" ) ");
            identifer.setText(apkInformation.getBundleId());
            progressBar.setVisible(false);

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
                    browserUrl(apkInformation.getaShort());
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
        icon.validate();

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
                shortUrl.setText(ApkInformation.getInstance().getaShort() + "  点击打开");
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
        } catch (URISyntaxException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

        uploadService.uploadAPKfile(PgyASPluginKeysManager.instance().getApiKey(), PgyASPluginKeysManager.instance().getuKey(),
                apkInformation.getFilePath(), ApkInformation.getInstance().getTextlog(), InformationOfPgyerPanel.this);
    }


}

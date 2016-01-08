package com.pgyer.dialog.ui;

import com.pgyer.dialog.providers.ApkInformation;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Tao9jiu on 16/1/7.
 */
public class IconPanel extends JPanel {
    /* 17 */   private int width = 80;
    /* 18 */   private int height = 80;
    /*    */   private static IconPanel iconPanel;
    /* 20 */   ImageIcon icon = new ImageIcon(ApkInformation.getInstance().getIcon());
    /*    */
/*    */   public IconPanel() {
/* 23 */     setSize(this.width, this.height);
/* 24 */     iconPanel = this;
/*    */   }
    /*    */
/*    */   protected void paintComponent(Graphics g)
/*    */   {
/* 29 */     Image img = this.icon.getImage();
/* 30 */     g.drawImage(img, 0, 0, getSize().width, getSize().height, this);
/*    */   }
    /*    */
/*    */   public void setIcon(String path)
/*    */   {
/* 36 */     this.icon = new ImageIcon(path);
/* 37 */     repaint();
/*    */   }
    /*    */   public static IconPanel getInstance() {
/* 40 */     if (iconPanel == null) return new IconPanel();
/* 41 */     return iconPanel;
/*    */   }
/*    */ }
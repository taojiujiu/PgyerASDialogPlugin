package com.pgyer.dialog.ui;

import com.pgyer.dialog.providers.ApkInformation;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Tao9jiu on 16/1/7.
 */
public class IconPanel extends JPanel {
      private int width = 80;
      private int height = 80;
      private static IconPanel iconPanel;
      ImageIcon icon = new ImageIcon("images/icon.png");
//        ImageIcon icon = new ImageIcon("/images/icon.png");
   public IconPanel() {
     setSize(this.width, this.height);
     iconPanel = this;
   }

   @Override
   protected void paintComponent(Graphics g)
   {
     Image img = this.icon.getImage();
     g.drawImage(img, 0, 0, getSize().width, getSize().height, this);
   }
   public void setIcon(String path)
   {
     this.icon = new ImageIcon(path);
     repaint();
   }
    public static IconPanel getInstance() {
     if (iconPanel == null) return new IconPanel();
     return iconPanel;
   }
 }
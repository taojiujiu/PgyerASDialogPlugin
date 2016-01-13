package com.pgyer.dialog.utils;

import com.pgyer.dialog.providers.ApkInformation;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

/**
 * Created by Tao9jiu on 16/1/12.
 */
public class FileOperate
{
    private static FileOperate fileOperate;

    public FileOperate()
    {
        fileOperate = this;
    }

    public static FileOperate getInstance() {
        if (fileOperate == null)
            return new FileOperate();

        return fileOperate;
    }

    public void newFolder(String folderPath)
    {
        String filePath;
        try
        {
            filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!(myFilePath.exists()))
                myFilePath.mkdir();
        }
        catch (Exception e)
        {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
        }
    }

    public void newFile(String filePathAndName, String fileContent)
    {
        String filePath;
        try
        {
            filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!(myFilePath.exists()))
                myFilePath.createNewFile();

            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            String strContent = fileContent;
            myFile.println(strContent);
            resultFile.close();
        }
        catch (Exception e)
        {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
        }
    }

    public Boolean isExist(String path)
    {
        File file = new File(path);
        return Boolean.valueOf(file.exists());
    }

    public void delFile(String filePathAndName)
    {
        String filePath;
        try
        {
            filePath = filePathAndName;
            filePath = filePath.toString();
            File myDelFile = new File(filePath);
            myDelFile.delete();
        }
        catch (Exception e)
        {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
        }
    }

    public void delFolder(String folderPath)
    {
        try
        {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        }
        catch (Exception e)
        {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();
        }
    }

    public void delAllFile(String path)
    {
        File file = new File(path);
        if (!(file.exists()))
            return;

        if (!(file.isDirectory()))
            return;

        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; ++i) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            }
            else
                temp = new File(path + File.separator + tempList[i]);

            if (temp.isFile())
                temp.delete();

            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    public void copyFile(String oldPath, String newPath)
    {
        int bytesum;
        try
        {
            bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];

                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    public void copyFolder(String oldPath, String newPath)
    {
        try
        {
            new File(newPath).mkdirs();
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; ++i) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                }
                else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + temp.getName().toString());

                    byte[] b = new byte[5120];
                    int len;

                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory())
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
            }
        }
        catch (Exception e)
        {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }

    public void moveFile(String oldPath, String newPath)
    {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }

    public void moveFolder(String oldPath, String newPath)
    {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    public String fileChoose()
    {
        JFileChooser fileChooser = new JFileChooser(ApkInformation.getInstance().filePath);
        FileNameExtensionFilter ff = new FileNameExtensionFilter(null, new String[] { "apk" });
        fileChooser.setFileFilter(ff);
        int option = fileChooser.showOpenDialog(null);
        if (option == 0)
        {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            return path;
        }
        return null;
    }
}
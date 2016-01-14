package com.pgyer.dialog.utils;

import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tao9jiu on 16/1/12.
 */
public class SearchFile
{
    public String url;
    public File iconFile;
    public ZipFile zipFile;
    private static SearchFile searchFile;
    private String beforePath;

    public SearchFile(String url)
    {
        this.url = url;
        searchFile = this;
    }

    public SearchFile() {
        searchFile = this;
    }
    public static SearchFile getInstance() {
        if (searchFile == null) return new SearchFile();
        return searchFile;
    }

    public void initPath(String url) {
        this.url = url;
    }

    public InputStreamBody query(String name) {
        int length = 0;
        byte[] b = new byte[1024];
        InputStreamBody fileContent = null;
        try
        {
            this.zipFile = new ZipFile(new File(this.url));
            Enumeration enumeration = this.zipFile.entries();
            ZipEntry zipEntry = null;

            while (enumeration.hasMoreElements()) {
                zipEntry = (ZipEntry)enumeration.nextElement();
                if ((!zipEntry.isDirectory()) &&
                        (name.equals(zipEntry.getName())))
                {
                    InputStream inputStream = this.zipFile.getInputStream(zipEntry);
                    fileContent = new InputStreamBody(inputStream, name);
                }

            }

        }
        catch (IOException e)
        {
        }
        return fileContent;
    }

    public String queryIcon(String name)
    {
        int length = 0;
        byte[] b = new byte[1024];
        InputStreamBody fileContent = null;
        String path = null;
        try
        {
            this.zipFile = new ZipFile(new File(this.url));
            Enumeration enumeration = this.zipFile.entries();
            ZipEntry zipEntry = null;

            while (enumeration.hasMoreElements()) {
                zipEntry = (ZipEntry)enumeration.nextElement();
                if ((!zipEntry.isDirectory()) &&
                        (name.equals(zipEntry.getName()))) {
                    if (this.beforePath != null) {
                        FileOperate.getInstance().delFile(this.beforePath);
                    }
                    if(Storage.getAppDataFolder().exists()){
                        FileOperate.getInstance().delAllFile(Storage.getAppDataFolder().getPath());
                    }
                    path = Storage.getAppDataFolder().getPath() + "/ax" + System.currentTimeMillis() + ".png";
                    this.beforePath = path;
                    OutputStream outputStream = new FileOutputStream(path);
                    InputStream inputStream = this.zipFile.getInputStream(zipEntry);
                    while ((length = inputStream.read(b)) > 0) {
                        outputStream.write(b, 0, length);
                    }
                    outputStream.close();
                    inputStream.close();
                }
            }
        }
        catch (IOException e)
        {
        }
        return path;
    }
}
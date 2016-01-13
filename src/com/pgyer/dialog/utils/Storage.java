package com.pgyer.dialog.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tao9jiu on 16/1/12.
 */
public class Storage
{
    public static File getAppDataFolder()
    {
        String os = System.getProperty("os.name").toUpperCase();
        String dataFile = (os.contains("MAC")) ? "Library/Caches/pgyer" : ".pgyer";
        File file = new File(System.getProperty("user.home"), dataFile);
        file.mkdirs();
        return     file;
    }

    public static File getTemIconFolder() {
        String os = System.getProperty("os.name").toUpperCase();
        String dataFile = (os.contains("MAC")) ? "Library/Caches/pgyer/temp" : ".pgyer/temp";
        FileOperate.getInstance().newFolder(dataFile);

        return new File(System.getProperty("user.home"), dataFile);
    }

    public static String getXmlPath() {
        String folderPath = getAppDataFolder().getPath();
        FileOperate.getInstance().newFolder(folderPath);
        String path = folderPath + "/" + "key.xml";

        if (FileOperate.getInstance().isExist(path).booleanValue()) {
            return path;
        }

        String content = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n<RESULT>\n    <VALUE>　　\n        <KEY>NAME</KEY>　　\n        <VAL>AA</VAL>\n    </VALUE>\n    <VALUE>　　\n        <KEY>PATH</KEY> 　\n        <VAL>AAAA</VAL>\n    </VALUE>\n</RESULT>";

        FileOperate.getInstance().newFile(path, content);
        return path;
    }
}
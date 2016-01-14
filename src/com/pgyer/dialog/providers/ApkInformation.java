package com.pgyer.dialog.providers;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Tao9jiu on 16/1/5.
 */
public class ApkInformation {
    public String name;
    public String icon;
    public String bundleId;
    public String aShort;
    public String versionName;
    public String versionCode;
    public String filePath;
    public String textlog;
    public static ApkInformation apkInformation;

    public ApkInformation(){
        apkInformation = this;
    }

    public ApkInformation(String url){
        apkInformation = this;
        this.filePath = url;
        parseApk(url);
    }

    public static ApkInformation getInstance(){
        if(apkInformation == null)
            return new ApkInformation();
        return apkInformation;
    }

    public void initPath(String url){
        this.filePath = url;
        if(this.filePath.isEmpty())  return;
        parseApk(this.filePath);
        setFilePath(url);
    }

    public void parseApk(String url){
        ApkParser apkParser = null;
        try {
            apkParser = new ApkParser(new File(url));
            ApkMeta apkMeta = apkParser.getApkMeta();
            this.versionName = apkMeta.getVersionName();
            this.versionCode = apkMeta.getVersionCode().toString();
            this.bundleId = apkMeta.getPackageName();
            this.name = apkMeta.getLabel();
            this.icon = apkMeta.getIcon().getPath();
            apkParser.close();
        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getaShort() {
        return aShort;
    }

    public void setaShort(String aShort) {
        this.aShort = aShort;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTextlog() {
        return textlog;
    }

    public void setTextlog(String textlog) {
        this.textlog = textlog;
    }
    public String getBundleId() {
        return bundleId;
    }
}

package com.scorpion.brightnessmanager;

public class BrightnessModel {

    private String appLabel;
    private String icon;
    private String pkgName;
    private boolean autoBrightness;
    private int brightnessValue;

    public BrightnessModel() {
    }

    public BrightnessModel(String appLabel,  String pkgName,String icon, boolean autoBrightness, int brightnessValue) {
        this.appLabel = appLabel;
        this.icon = icon;
        this.pkgName = pkgName;
        this.autoBrightness = autoBrightness;
        this.brightnessValue = brightnessValue;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public boolean isAutoBrightness() {
        return autoBrightness;
    }

    public void setAutoBrightness(boolean autoBrightness) {
        this.autoBrightness = autoBrightness;
    }

    public int getBrightnessValue() {
        return brightnessValue;
    }

    public void setBrightnessValue(int brightnessValue) {
        this.brightnessValue = brightnessValue;
    }
}

package com.scorpion.brightnessmanager.model;

public class VolumeModel {

    private String appLabel;
    private String icon;
    private String pkgName;
    int mode,ring,media,alarm,voiceCall,notification;


    public VolumeModel() {
    }

    public VolumeModel(String appLabel, String icon, String pkgName, int mode, int ring, int media, int alarm, int voiceCall, int notification) {
        this.appLabel = appLabel;
        this.icon = icon;
        this.pkgName = pkgName;
        this.mode = mode;
        this.ring = ring;
        this.media = media;
        this.alarm = alarm;
        this.voiceCall = voiceCall;
        this.notification = notification;
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getRing() {
        return ring;
    }

    public void setRing(int ring) {
        this.ring = ring;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public int getVoiceCall() {
        return voiceCall;
    }

    public void setVoiceCall(int voiceCall) {
        this.voiceCall = voiceCall;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }
}

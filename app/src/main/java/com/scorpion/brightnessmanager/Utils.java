package com.scorpion.brightnessmanager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {

    public static boolean idLoad = false;
    public static boolean isAd = false ;
    public static boolean isProLive = false ;
    public static int AdPos = 5;
    public static int isServiceOnBrightness = 0;
    public static int isServiceOnRotation = 0;
    public static int isServiceOnVolume = 0;
    public static int AdmobFacebook = 2;
    public static int timesInterAd = 3;
    public static int totalInterCount = 0;

    public static String getApplicationLabel(Context context, String str) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(str, 0);
        } catch (NameNotFoundException unused) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }
}

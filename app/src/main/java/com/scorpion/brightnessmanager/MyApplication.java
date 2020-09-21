package com.scorpion.brightnessmanager;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
       AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("40f9cb24-c0f1-4506-b029-aed65d2c186f");
        FBInterstitial.getInstance().loadFBInterstitial(this);

    }
}

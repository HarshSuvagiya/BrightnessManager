package com.scorpion.brightnessmanager;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
       AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("3f0e8804-8776-4c51-949a-13256a7d2e9c");
        FBInterstitial.getInstance().loadFBInterstitial(this);

    }
}
